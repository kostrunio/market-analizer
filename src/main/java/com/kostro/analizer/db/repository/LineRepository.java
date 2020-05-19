package com.kostro.analizer.db.repository;

import com.kostro.analizer.db.model.ConfigurationEntity;
import com.kostro.analizer.db.model.LineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LineRepository extends JpaRepository<LineEntity, Long> {

    @Query(value = "select * from Lines l where :date between l.startDate and l.endDate and l.resolution = :resolution", nativeQuery = true)
    List<LineEntity> findInRange(@Param("date") LocalDateTime date, @Param("resolution") double resolution);
}
