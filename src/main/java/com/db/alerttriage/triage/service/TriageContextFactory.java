package com.db.alerttriage.triage.service;

import com.db.alerttriage.alert.entity.Alert;
import com.db.alerttriage.inventory.model.AssetFacts;
import com.db.alerttriage.inventory.service.InventoryService;
import com.db.alerttriage.triage.model.TriageContext;
import com.db.alerttriage.vulnerability.service.VulnerabilityService;
import com.db.alerttriage.vulnerability.model.VulnerabilityAdvisory;
import org.springframework.stereotype.Service;

@Service
public class TriageContextFactory {

    private final InventoryService inventoryService;
    private final VulnerabilityService vulnerabilityService;

    public TriageContextFactory(InventoryService inventoryService
            ,VulnerabilityService vulnerabilityService){
        this.inventoryService = inventoryService;
        this.vulnerabilityService = vulnerabilityService;
    }

    public TriageContext create(Alert alert){

        AssetFacts assetFacts = inventoryService.findByHostname(
                alert.getHostname()
        ).orElseThrow(() -> new
        IllegalStateException("Asset not found in inventory: " + alert.getHostname())
        );

        VulnerabilityAdvisory advisory = vulnerabilityService
                .findByCveId(alert.getCveId());

        return new  TriageContext(
                alert.getCveId(),

                alert.getHostname(),

                alert.getCvssScore(),

                alert.getDescription(),

                assetFacts ,

                advisory
        );

    }
}
