package com.db.alerttriage.controller;

import com.db.alerttriage.alert.dto.AlertResponse;
import com.db.alerttriage.alert.dto.CreateAlertRequest;
import com.db.alerttriage.alert.dto.CreateAlertResult;
import com.db.alerttriage.alert.entity.Alert;
import com.db.alerttriage.alert.service.AlertService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService){
        this.alertService = alertService;
    }

    @PostMapping
    public ResponseEntity<AlertResponse>createAlert(@Valid @RequestBody
                                                        CreateAlertRequest request){

        CreateAlertResult result = alertService.createAlert(request);
        AlertResponse response = toResponse(result.alert());

        if (result.created()){
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(response);
        }

        return ResponseEntity.ok(response);


    };

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts(){
    List<AlertResponse> responses = alertService.getAlerts()
            .stream()
            .map(this::toResponse)
            .toList();

    return ResponseEntity.ok(responses);

    };

    private AlertResponse toResponse(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getSourceAlertId(),
                alert.getCveId(),
                alert.getHostname(),
                alert.getCvssScore(),
                alert.getSeverity(),
                alert.getEnvironment(),
                alert.getDescription(),
                alert.getDetectedAt(),
                alert.getReceivedAt(),
                alert.getStatus()

        );

    }



}
