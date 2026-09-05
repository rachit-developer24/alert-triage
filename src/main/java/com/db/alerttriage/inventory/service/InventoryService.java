package com.db.alerttriage.inventory.service;

import com.db.alerttriage.inventory.Repository.AssetRepository;
import com.db.alerttriage.inventory.entity.Asset;
import com.db.alerttriage.inventory.mapper.AssetMapper;
import com.db.alerttriage.inventory.model.AssetFacts;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {

    private final AssetRepository assetRepository;

    public InventoryService(AssetRepository assetRepository){
        this.assetRepository = assetRepository;
    }

    public Optional<AssetFacts> findByHostname(String hostname){
        return assetRepository.findByHostname(hostname)
                .map(AssetMapper::toFacts);
    };
}
