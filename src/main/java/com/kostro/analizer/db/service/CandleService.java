package com.kostro.analizer.db.service;

import com.kostro.analizer.db.model.CandleEntity;
import com.kostro.analizer.db.repository.CandlesRepository;
import com.kostro.analizer.utils.CandleUtils;
import com.kostro.analizer.wallet.Candle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CandleService {

    private static final Logger log = LoggerFactory.getLogger(CandleService.class);

    private CandlesRepository repository;

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

    public void refreshCandles(List<Candle> Candles) {
        for (Candle Candle : Candles) {
            CandleEntity entity = repository.findByTimeAndResolution(Candle.getTime(), Candle.getResolution());
            entity = CandleUtils.from(Candle, entity != null ? entity.getId() : null);
            repository.save(entity);
        }
    }

    public LocalDateTime getLastDate() {
        return repository.findLastCandle();
    }
}
