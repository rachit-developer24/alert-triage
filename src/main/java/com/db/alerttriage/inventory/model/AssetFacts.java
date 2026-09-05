package com.db.alerttriage.inventory.model;

import com.db.alerttriage.inventory.entity.BusinessCriticality;
import com.db.alerttriage.inventory.entity.DeviceType;
import com.db.alerttriage.inventory.entity.Environment;

import java.time.Instant;

public record AssetFacts(
        String hostname,
        String ipAddress,
        Environment environment,
        boolean internetExposed,
        BusinessCriticality businessCriticality,
        DeviceType deviceType,
        String vendor,
        String model,
        String installedVersion,
        String ownerTeam,
        String site,
        Instant lastSeenAt
) {
}