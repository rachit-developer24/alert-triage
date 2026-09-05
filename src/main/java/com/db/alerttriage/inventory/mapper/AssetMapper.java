package com.db.alerttriage.inventory.mapper;

import com.db.alerttriage.inventory.entity.Asset;
import com.db.alerttriage.inventory.model.AssetFacts;

public final class AssetMapper {

    private AssetMapper() {
    }

    public static AssetFacts toFacts(Asset asset) {
        return new AssetFacts(
                asset.getHostname(),
                asset.getIpAddress(),
                asset.getEnvironment(),
                asset.isInternetExposed(),
                asset.getBusinessCriticality(),
                asset.getDeviceType(),
                asset.getVendor(),
                asset.getModel(),
                asset.getInstalledVersion(),
                asset.getOwnerTeam(),
                asset.getSite(),
                asset.getLastSeenAt()
        );
    }
}