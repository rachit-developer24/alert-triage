package com.db.alerttriage.alert.service;

import com.db.alerttriage.alert.dto.CreateAlertRequest;
import com.db.alerttriage.alert.dto.CreateAlertResult;
import com.db.alerttriage.alert.entity.Alert;
import com.db.alerttriage.alert.entity.AlertStatus;
import com.db.alerttriage.alert.entity.Severity;
import com.db.alerttriage.alert.repository.AlertRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Service
public class AlertService {

     private final AlertRepository alertRepository;

     public AlertService(AlertRepository alertRepository){
         this.alertRepository = alertRepository;
     }


     @Transactional
     public CreateAlertResult createAlert(CreateAlertRequest request){

         Optional<Alert> existingAlert = alertRepository.findByCveIdAndHostnameAndStatusNot(
                 request.cveId(),
                 request.hostname(),
                 AlertStatus.CLOSED
         );
         if (existingAlert.isPresent()){
             return new  CreateAlertResult(existingAlert.get(),
                     false);
         }

       Severity severity = Severity.fromCvss(request.cvssScore());

         Alert alert = new Alert(
                 request.sourceAlertId(),
                 request.cveId(),
                 request.hostname(),
                 request.cvssScore(),
                 severity,
                 null,// TODO: later enrich from inventory/CMDB
                 request.description(),
                 request.detectedAt(),
                 Instant.now(),
                 AlertStatus.NEW
         );
        Alert savedAlert = alertRepository.save(alert);

        return new  CreateAlertResult(savedAlert,
               true );

     };



}
