package com.kostro.analizer.db.repository;

import com.kostro.analizer.db.model.CandelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface CandelsRepository extends JpaRepository<CandelEntity, Long> {

    @Query(value = "select * from Candels c where c.c_time between :startDate and :endDate and c.c_resolution = :resolution", nativeQuery = true)
    public List<CandelEntity> find(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   @Param("resolution") int resolution);

    CandelEntity findByTimeAndResolution(LocalDateTime time, int resolution);

    @Query(value = "select c.c_time from Candels c order by c.c_time desc limit 1", nativeQuery = true)
    LocalDateTime findLastCandel();

    @Query(value = "select * from Candels c where c.c_time between :startDate and :endDate and c.c_resolution = :resolution and c.c_volume > :limit", nativeQuery = true)
    public List<CandelEntity> findWithLimit(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   @Param("resolution") int resolution,
                                   @Param("limit") double limit);
}
