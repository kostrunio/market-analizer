package com.kostro.analizer.db.service;

import com.kostro.analizer.db.model.CandleEntity;
import com.kostro.analizer.db.repository.CandlesRepository;
import com.kostro.analizer.utils.CandleUtils;
import com.kostro.analizer.wallet.Candle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CandleService {
    
    private final LocalDateTime FIRST_CANDLE = LocalDateTime.of(2020, 8, 01, 00, 00, 00);

    private CandlesRepository repository;

    private static ConcurrentMap<String, LocalDateTime> lastCandle = new ConcurrentHashMap<>();

    @Autowired
    public CandleService(CandlesRepository repository) {
        this.repository = repository;
    }

    public void save(CandleEntity entity) {
        repository.save(entity);
    }

    public CandleEntity find(Long id) {
        return repository.findById(id).get();
    }

    public List<CandleEntity> findAll() {
        return repository.findAll();
    }

    public List<Candle> find(QueryParams params) {
        List<CandleEntity> entities = findCandles(params);
        return CandleUtils.asList(entities);
    }

    private List<CandleEntity> findCandles(QueryParams params) {
        if (params.volume > 0) {
            return repository.find(params.market, params.startDate, params.endDate, params.resolution.getSecs(), params.volume);
        } else {
            return repository.find(params.market, params.startDate, params.endDate, params.resolution.getSecs());
        }
    }

    public void refreshCandles(String market, List<Candle> candles) {
        for (Candle candle : candles) {
            CandleEntity entity = repository.findByMarketAndTimeAndResolution(market, candle.getTime(), candle.getResolution(market));
            entity = CandleUtils.of(market, candle, entity != null ? entity.getId() : null);
            repository.save(entity);
        }
    }

    public LocalDateTime getLastCandle(String market) {
        lastCandle.putIfAbsent(market, getLastDate(market));
        return lastCandle.getOrDefault(market, FIRST_CANDLE);
    }

    public void setLastCandle(String market, LocalDateTime lastCandle) {
        log.debug("lastCandle: " + lastCandle);
        CandleService.lastCandle.put(market, lastCandle);
    }

    private LocalDateTime getLastDate(String market) {
        return repository.findLastCandle(market);
    }

    public List<Candle> findLastHuge(String market, LocalDateTime time, int resolution, int limit, int numberOfTransactions) {
        return repository.findLast(market, time, resolution, limit, numberOfTransactions)
                .stream()
                .map(entity -> CandleUtils.of(entity))
                .collect(Collectors.toList());
    }
}
