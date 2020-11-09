package com.kostro.analizer.db.service;

import com.kostro.analizer.db.model.CandleEntity;
import com.kostro.analizer.db.repository.CandlesRepository;
import com.kostro.analizer.utils.CandleUtils;
import com.kostro.analizer.wallet.Candle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CandleService {

    private static final Logger log = LoggerFactory.getLogger(CandleService.class);

    private CandlesRepository repository;

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

    public List<Candle> find(LocalDateTime startDate, LocalDateTime endDate, int resolution, double limit) {
        List<Candle> candles = new ArrayList<>();
        for(CandleEntity entity : repository.findWithLimit(startDate, endDate, resolution, limit)) {
            candles.add(CandleUtils.from(entity));
        }
        return candles;
    }
    public List<Candle> find(LocalDateTime startDate, LocalDateTime endDate, int resolution) {
        List<Candle> candles = new ArrayList<>();
        for(CandleEntity entity : repository.find(startDate, endDate, resolution)) {
            candles.add(CandleUtils.from(entity));
        }
        return candles;
    }

    public void refreshCandles(List<Candle> candles) {
        for (Candle candle : candles) {
            CandleEntity entity = repository.findByTimeAndResolution(candle.getTime(), candle.getResolution());
            entity = CandleUtils.from(candle, entity != null ? entity.getId() : null);
            repository.save(entity);
        }
    }

    private static LocalDateTime lastCandle;

    public LocalDateTime getLastCandle() {
        if (lastCandle == null) {
            lastCandle = getLastDate();
            if (lastCandle != null)
                lastCandle = lastCandle.minusMinutes(5);
        }
        if (lastCandle == null)
            lastCandle = LocalDateTime.of(2020, 10, 01, 00, 00, 00);
        return lastCandle;
    }

    public void setLastCandle(LocalDateTime lastCandle) {
        this.lastCandle = lastCandle;
    }

    public LocalDateTime getLastDate() {
        return repository.findLastCandle();
    }

    public List<Candle> findLastHuge(LocalDateTime time, int resolution, int limit, int numberOfTransactions) {
        List<Candle> candles = new ArrayList<>();
        for(CandleEntity entity : repository.findLast(time, resolution, limit, numberOfTransactions)) {
            candles.add(CandleUtils.from(entity));
        }
        return candles;
    }
}
