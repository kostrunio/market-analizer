package com.kostro.analizer.db.repository;

import com.kostro.analizer.db.model.ConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<ConfigurationEntity, Long> {
}
