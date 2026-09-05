# alert-triage

Ingest vulnerability alerts for network devices, enrich them with authoritative asset and
threat-intelligence data, triage them against a written patch policy, and keep an audit trail
every decision can be replayed from. Practice project for a Deutsche Bank network automation
team (vulnerability / patch / config / release management).

## Stack

- Java 21, Spring Boot 4.0.7, Maven wrapper (`./mvnw`)
- PostgreSQL 16 in Docker Compose
- Flyway for schema, Hibernate `ddl-auto=validate` — **never** `update` or `create`
- Testcontainers 2.0.5 (note: 2.x artifact names are `testcontainers-postgresql`,
  not `postgresql` — tutorials online show the 1.x names)

**Stay on Spring Boot 4.0.7.** The only reason to downgrade was Spring AI, and we are not
using it. Boot 4 renames catch people out: `spring-boot-starter-web` is `-webmvc`, the four
`*-test` starters replace `spring-boot-starter-test`, and `spring-boot-starter-aop` does not
exist — it is `spring-boot-starter-aspectj`.

## Run it

```bash
docker compose up -d      # needs .env (gitignored)
./mvnw spring-boot:run    # reads .env via spring.config.import
```

Config comes from `.env` through `spring.config.import=optional:file:.env[.properties]`.
Real environment variables take precedence, so deployed environments need no code change.

## Conventions — follow these, do not invent alternatives

- Constructor injection only. No `@Autowired` on fields.
- DTOs and domain facts are `record`s. Entities are classes with a `protected` no-arg
  constructor, getters only, no setters.
- The entity is never the API contract, and never crosses a package boundary. Map to a
  record at the edge.
- Enums for closed sets. Never strings. Mirror them with a CHECK constraint in the migration.
- Schema changes are new Flyway migrations. Never edit an applied migration.
- Critical data-integrity invariants are enforced in the database as well as in code
  (see `V2` — a partial unique index for the duplicate rule).
- `BigDecimal` for scores, `Instant` for timestamps, `TIMESTAMPTZ` in SQL.
  No floats, no `LocalDateTime`.
- Lowercase package names.
- No SQL join between `alerts` and `assets`. Inventory is a separate bounded context and
  will become a REST client to a real CMDB. A join makes that swap a rewrite.

## Hard rules

- **No credentials in the repo. Ever.** `.env` is gitignored and stays that way.
- Nothing writes to a network device. This service is read-only by design.
- Every triage decision writes an audit row: who, what, when, on what evidence,
  under which rule version.
- The model reads and explains. Deterministic code decides. A human approves anything
  that writes.
- Below the confidence threshold, or on stale/unknown inventory: return
  `INSUFFICIENT_DATA`. Never guess.
- CI never calls a real model. `FakeTriageModel` is the default implementation.

## Triage architecture — LOCKED

Do not redesign this. New ideas go in `docs/slice-03.md` and wait.

**Who owns which fact:**

| Fact | Source |
|---|---|
| environment, internetExposed, installedVersion, businessCriticality | inventory |
| knownExploited | CISA KEV — structured, not prose |
| versionAffected | installed vs CPE range, compared **in code** |
| cvssScore | the alert |
| policyClause | exact lookup keyed on severity band + environment |
| remediation, exploit prerequisites, mitigating controls, evidence quotes | **the model** |

The model reads unstructured advisory prose and nothing else. It never sees asset facts —
that would leak internal infrastructure data to an external API and let its output influence
inputs that must be deterministic.

**Pipeline:** ingest → asset lookup (unknown or stale → stop) → intel lookup →
version check (not affected → stop, no model call) → AI reader → validate → rules →
persist + audit → human approval → ticket/notify

**Outcomes:** `ACT` · `ATTEND` · `TRACK` · `NOT_AFFECTED` · `INSUFFICIENT_DATA`

`INSUFFICIENT_DATA` is a gate before the decision tree, not a leaf of it.
Confidence is computed from provenance, never self-reported by the model.

**LLM tooling:** `com.anthropic:anthropic-java` behind the `TriageModel` interface.
Not Spring AI (structured output is best-effort by its own docs), not LangChain4j
(Boot 4 starters are beta), no vector database (six policy clauses — exact lookup is
better on every axis), no tool calling (the model has no actions, which removes a whole
class of prompt-injection risk).

## Working agreement — important

**You may generate:** boilerplate and mechanical code — DTO records, mappers, test
scaffolding and fixtures, migration skeletons, config, Dockerfile, CI workflow, javadoc,
curl commands.

**I write myself:** service logic, business rules, controller behaviour, the triage rules —
anything involving a decision or a trade-off. Ask me questions and review what I produce
instead of writing it for me.

**Always:** explain what you generated and why, in plain terms. Flag the edge cases you
skipped. If I cannot explain a line, it does not ship — so do not hand me lines I would
not understand.

**Verify before asserting.** Check the repo and run the command rather than recalling.
Version claims about this project have been wrong three times; `./mvnw dependency:tree`
beats any tutorial.

Prefer small diffs. One thing at a time.

## Current state

**Slice 01 — alert ingestion. Done and verified.**
`POST /api/alerts` -> 201, duplicate (same cveId + hostname) -> 200 with the existing
alert, invalid -> 400, `GET /api/alerts` -> 200 newest-first. Duplicate rule enforced by
a partial unique index (V2); the concurrent-duplicate race is caught and resolved to the
winner. Acceptance criteria in `docs/slice-01.md`, written Given/When/Then.

**Inventory — done.** `Asset`, `Environment`, `DeviceType`, `BusinessCriticality`,
`AssetRepository`, `AssetMapper`, `AssetFacts`, `InventoryService`. V3 removes the
hardcoded `environment` from alerts, V4 seeds assets. Device facts are structured and
come from our own database, never from a model.

**Triage — skeleton in place, not enforced yet.** `TriageModel` interface with a
`FakeTriageModel`, `AdvisoryFacts`, `EvidenceQuote`, `TriageAction`, `TriageContext`,
`TriageDecision`, `FactsValidator`, `TriageContextFactory`, `TriageDecisionService`,
`TriageService`, `TriageDecisionEntity` + repository (V5). Anthropic structured-output
spike lives in test sources.

**Tests** are Testcontainers-based, so Docker must be running or every test fails with
"Failed to load ApplicationContext".

---

# Requirements to done

Target: finished by Fri 4 Sep, then this project is closed.
Order matters. Do not start a block before the one above it is green.

## Block 1 — housekeeping

- Commits split into reviewable pieces, never one large blob
- `CLAUDE.md` tracked in git
- Package names all lowercase (`inventory/repository`, `vulnerability/service`)

## Block 2 — ship it

- `/actuator/health` endpoint
- Multi-stage Dockerfile that builds from source
- CI on push: build + test, Postgres service container, fake model only
- Image published to a registry
- Deployed and reachable at a public URL
- Verified live: POST an alert, GET it back
- Deploy chain written out end to end in the README

## Block 3 — guardrails

- Every model output validated against its schema before use. Reject, never repair
- A finding with no evidence quote is rejected
- The model may only use text present in its input
- Prompt-injection handling on free-text fields; `description` arrives from outside
- Input size limits
- Model output can never directly trigger a write or an action

## Block 4 — confidence

- Every finding carries a confidence score
- Below the threshold, return "insufficient data". Never a guess
- The threshold is configuration, not a literal in code

## Block 5 — decide, audit, survive

- Deterministic code makes the decision. The model only reads and explains
- Every decision writes an audit row: who, what, when, on what evidence
- A human approval step exists before anything is actioned
- Circuit breaker falls back to rules-only when the model is unavailable
- RAG over the patch policy using pgvector; the retrieved clause is cited in the output

## Block 6 — evals

- 25 golden cases checked into the repo
- Measured and recorded: citation accuracy, hallucination rate, false positives/negatives
- Evals runnable as a single command
- CI runs against the fake model, never the real one

## Block 7 — handover and demo

- README: what it does, what it needs to run, how it deploys
- Runbook: what breaks, how you notice, what to do
- A recorded 10-minute walkthrough: what it does, what worked, what did not, what next
- The walkthrough leads with the guardrails and shows the system refusing to answer

---

# Questions I must be able to answer out loud, no notes

Pair programming at Deutsche Bank will be "why this line?". These are mine to defend.

- Why a database constraint for the duplicate rule instead of check-then-save
- What exactly happens to the second transaction in that race
- Why the constant is on the left of `.equals()`
- What happens if the exception cause chain is circular
- Why we return at the first `ConstraintViolationException` — correct, or a bug?
- Why `BigDecimal` and not `double` for a CVSS score
- Why `Instant` and not `LocalDateTime`
- Whether the concurrency test is genuinely deterministic

---

# Definition of done

Deployed at a public URL. The agent explains and cites. It refuses when it is not sure.
It falls back to rules when the model is down. Every decision is audited. There are eval
numbers. And every line can be explained out loud.

Anything beyond that is polishing. Stop and move to the next project.
