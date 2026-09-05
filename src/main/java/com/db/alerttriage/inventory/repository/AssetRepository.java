package com.db.alerttriage.inventory.Repository;

import com.db.alerttriage.inventory.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset,Long> {
    Optional<Asset> findByHostname(String hostName);

}
