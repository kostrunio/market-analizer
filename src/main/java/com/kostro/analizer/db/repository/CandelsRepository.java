package com.kostro.analizer.db.repository;

import com.kostro.analizer.db.model.CandelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface CandelsRepository extends JpaRepository<CandelEntity, Long> {

    @Query(value = "select * from Candels c where c.c_time between :startDate and :endDate and c.c_resolution = :resolution", nativeQuery = true)
    public List<CandelEntity> find(@Param("startDate") Timestamp startDate,
                                   @Param("endDate") Timestamp endDate,
                                   @Param("resolution") int resolution);
}
