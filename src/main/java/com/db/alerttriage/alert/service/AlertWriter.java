package com.db.alerttriage.alert.service;

import com.db.alerttriage.alert.entity.Alert;
import com.db.alerttriage.alert.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service
public class AlertWriter {

    private final AlertRepository alertRepository;

    public AlertWriter(AlertRepository alertRepository){
        this.alertRepository = alertRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Alert save(Alert alert){
       return alertRepository.saveAndFlush(alert);
    };


}
