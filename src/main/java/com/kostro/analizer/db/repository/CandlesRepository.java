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

    @Query(value = "select * from Candles c where c.c_market = :market and c.c_time between :startDate and :endDate and c.c_resolution = :resolution", nativeQuery = true)
    List<CandleEntity> find(@Param("market") String market,
                            @Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate,
                            @Param("resolution") int resolution);

    @Query(value = "select * from Candles c where c.c_market = :market and c.c_time between :startDate and :endDate and c.c_resolution = :resolution and c.c_volume > :volume", nativeQuery = true)
    List<CandleEntity> find(@Param("market") String market,
                            @Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate,
                            @Param("resolution") int resolution,
                            @Param("volume") double volume);

    CandleEntity findByMarketAndTimeAndResolution(String market, LocalDateTime time, int resolution);

    @Query(value = "select c.c_time from Candles c where c.c_market = :market order by c.c_time desc limit 1", nativeQuery = true)
    LocalDateTime findLastCandle(@Param("market") String market);

    @Query(value = "select * from Candles c where c.c_market = :market and c.c_time <= :startDate and c.c_resolution = :resolution and c.c_volume > :volume order by id desc limit :numberOfTransactions", nativeQuery = true)
    List<CandleEntity> findLast(@Param("market") String market,
                                @Param("startDate") LocalDateTime time,
                                @Param("resolution") int resolution,
                                @Param("volume") int volume,
                                @Param("numberOfTransactions") int numberOfTransactions);
}
