ALTER TABLE triage_decisions
    ADD COLUMN review_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    ADD COLUMN reviewed_by VARCHAR(255),
    ADD COLUMN reviewed_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN review_comment VARCHAR(1000);