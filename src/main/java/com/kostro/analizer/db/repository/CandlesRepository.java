package com.kostro.analizer.db.repository;

import com.kostro.analizer.db.model.CandleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CandlesRepository extends JpaRepository<CandleEntity, Long> {

    @Query(value = "select * from Candles c where c.c_time between :startDate and :endDate and c.c_resolution = :resolution", nativeQuery = true)
    List<CandleEntity> find(@Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate,
                            @Param("resolution") int resolution);

    CandleEntity findByTimeAndResolution(LocalDateTime time, int resolution);

    @Query(value = "select c.c_time from Candles c order by c.c_time desc limit 1", nativeQuery = true)
    LocalDateTime findLastCandle();

    @Query(value = "select * from Candles c where c.c_time between :startDate and :endDate and c.c_resolution = :resolution and c.c_volume > :limit", nativeQuery = true)
    List<CandleEntity> findWithLimit(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("resolution") int resolution,
                                     @Param("limit") double limit);

    @Query(value = "select * from Candles c where c.c_time <= :startDate and c.c_resolution = :resolution and c.c_volume > :limit order by id desc limit :numberOfTransactions", nativeQuery = true)
    List<CandleEntity> findLast(@Param("startDate") LocalDateTime time, @Param("resolution") int resolution,
                                @Param("limit") int limit,
                                @Param("numberOfTransactions") int numberOfTransactions);
}
