package com.db.alerttriage.alert.repository;

import com.db.alerttriage.alert.entity.Alert;
import com.db.alerttriage.alert.entity.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert,Long> {
    List<Alert> findAllByOrderByReceivedAtDesc();

    Optional<Alert>findByCveIdAndHostnameAndStatusNot(
            String cveId,
            String hostname,
            AlertStatus status

    );


}
