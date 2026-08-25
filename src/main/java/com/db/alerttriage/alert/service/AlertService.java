package com.db.alerttriage.alert.service;

import com.db.alerttriage.alert.dto.CreateAlertRequest;
import com.db.alerttriage.alert.dto.CreateAlertResult;
import com.db.alerttriage.alert.entity.Alert;
import com.db.alerttriage.alert.entity.AlertStatus;
import com.db.alerttriage.alert.entity.Severity;
import com.db.alerttriage.alert.repository.AlertRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private static final Logger log =
            LoggerFactory.getLogger(AlertService.class);

    // Must match V2__add_unique_active_alert_index.sql
    private static final String ACTIVE_ALERT_UNIQUE_CONSTRAINT =
            "uq_active_alert_cve_hostname";

    private final AlertRepository alertRepository;
    private final AlertWriter alertWriter;

    public AlertService(
            AlertRepository alertRepository,
            AlertWriter alertWriter
    ) {
        this.alertRepository = alertRepository;
        this.alertWriter = alertWriter;
    }

    public CreateAlertResult createAlert(CreateAlertRequest request) {

        Optional<Alert> existingAlert =
                alertRepository.findByCveIdAndHostnameAndStatusNot(
                        request.cveId(),
                        request.hostname(),
                        AlertStatus.CLOSED
                );

        if (existingAlert.isPresent()) {
            return new CreateAlertResult(
                    existingAlert.get(),
                    false
            );
        }

        Severity severity =
                Severity.fromCvss(request.cvssScore());

        Alert alert = new Alert(
                request.sourceAlertId(),
                request.cveId(),
                request.hostname(),
                request.cvssScore(),
                severity,
                null, // TODO: enrich from inventory/CMDB
                request.description(),
                request.detectedAt(),
                Instant.now(),
                AlertStatus.NEW
        );

        try {

            Alert savedAlert = alertWriter.save(alert);

            return new CreateAlertResult(
                    savedAlert,
                    true
            );

        } catch (DataIntegrityViolationException ex) {

            if (!isActiveAlertDuplicate(ex)) {
                throw ex;
            }

            Alert existing = alertRepository
                    .findByCveIdAndHostnameAndStatusNot(
                            request.cveId(),
                            request.hostname(),
                            AlertStatus.CLOSED
                    )
                    .orElseThrow(() -> ex);

            log.info(
                    "event=duplicate_race cve={} host={} winner_id={}",
                    request.cveId(),
                    request.hostname(),
                    existing.getId()
            );

            return new CreateAlertResult(
                    existing,
                    false
            );
        }
    }

    @Transactional(readOnly = true)
    public List<Alert> getAlerts() {
        return alertRepository.findAllByOrderByReceivedAtDesc();
    }

    private boolean isActiveAlertDuplicate(Throwable exception) {

        Throwable current = exception;

        while (current != null) {

            if (current instanceof ConstraintViolationException constraintViolation) {
                return ACTIVE_ALERT_UNIQUE_CONSTRAINT.equals(
                        constraintViolation.getConstraintName()
                );
            }

            current = current.getCause();
        }

        return false;
    }
}
