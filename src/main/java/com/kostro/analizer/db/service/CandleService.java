package com.kostro.analizer.db.service;

import com.kostro.analizer.db.model.CandleEntity;
import com.kostro.analizer.db.repository.CandlesRepository;
import com.kostro.analizer.utils.CandleUtils;
import com.kostro.analizer.wallet.Candle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CandleService {

    private CandlesRepository repository;

    private static Map<String, LocalDateTime> lastCandle = new HashMap<>();

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

    public List<Candle> find(String market, LocalDateTime startDate, LocalDateTime endDate, int resolution, double limit) {
        List<Candle> candles = new ArrayList<>();
        for(CandleEntity entity : repository.findWithLimit(market, startDate, endDate, resolution, limit)) {
            candles.add(CandleUtils.from(entity));
        }
        return candles;
    }
    public List<Candle> find(String market, LocalDateTime startDate, LocalDateTime endDate, int resolution) {
        List<Candle> candles = new ArrayList<>();
        for(CandleEntity entity : repository.find(market, startDate, endDate, resolution)) {
            candles.add(CandleUtils.from(entity));
        }
        return candles;
    }

    public void refreshCandles(String market, List<Candle> candles) {
        for (Candle candle : candles) {
            CandleEntity entity = repository.findByMarketAndTimeAndResolution(market, candle.getTime(), candle.getResolution(market));
            entity = CandleUtils.from(market, candle, entity != null ? entity.getId() : null);
            repository.save(entity);
        }
    }

    public LocalDateTime getLastCandle(String market) {
        if (!lastCandle.containsKey(market)) {
            lastCandle.put(market, getLastDate(market));
            if (lastCandle.get(market) != null)
                return lastCandle.get(market).minusMinutes(5);
            else
                lastCandle.put(market, LocalDateTime.of(2021, 1, 01, 00, 00, 00));
        }
        return lastCandle.get(market);
    }

    public void setLastCandle(String market, LocalDateTime lastCandle) {
        log.debug("lastCandle: " + lastCandle);
        CandleService.lastCandle.put(market, lastCandle);
    }

    private LocalDateTime getLastDate(String market) {
        return repository.findLastCandle(market);
    }

    public List<Candle> findLastHuge(String market, LocalDateTime time, int resolution, int limit, int numberOfTransactions) {
        List<Candle> candles = new ArrayList<>();
        for(CandleEntity entity : repository.findLast(market, time, resolution, limit, numberOfTransactions)) {
            candles.add(CandleUtils.from(entity));
        }
        return candles;
    }
}
