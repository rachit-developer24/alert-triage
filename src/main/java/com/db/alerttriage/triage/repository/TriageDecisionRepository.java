package com.db.alerttriage.triage.repository;

import com.db.alerttriage.triage.entity.TriageDecisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TriageDecisionRepository
        extends JpaRepository<TriageDecisionEntity, Long> {

    Optional<TriageDecisionEntity> findByAlertId(Long alertId);
}