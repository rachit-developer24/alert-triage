package com.db.alerttriage.alert.service;

import com.db.alerttriage.alert.dto.CreateAlertRequest;
import com.db.alerttriage.alert.dto.CreateAlertResult;
import com.db.alerttriage.alert.repository.AlertRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("fake")
@Sql("/test-assets.sql")
class AlertServiceConcurrencyTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16");

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertRepository alertRepository;

    @Test
    void concurrentDuplicateCreatesOnlyOneAlert() throws Exception {

        CreateAlertRequest request = new CreateAlertRequest(
                "scan-race-1",
                "CVE-2026-9999",
                "router-race-01",
                new BigDecimal("9.1"),
                "Concurrent vulnerability test",
                Instant.parse("2026-08-25T00:00:00Z")
        );

        CountDownLatch startGate = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<CreateAlertResult> task = () -> {
            startGate.await();
            return alertService.createAlert(request);
        };

        Future<CreateAlertResult> firstFuture = executor.submit(task);
        Future<CreateAlertResult> secondFuture = executor.submit(task);

        startGate.countDown();

        CreateAlertResult first = firstFuture.get();
        CreateAlertResult second = secondFuture.get();

        executor.shutdown();

        long createdCount = Stream.of(first, second)
                .filter(CreateAlertResult::created)
                .count();

        assertThat(createdCount).isEqualTo(1);

        assertThat(first.alert().getId())
                .isEqualTo(second.alert().getId());

        assertThat(alertRepository.count())
                .isEqualTo(1);
    }
}
