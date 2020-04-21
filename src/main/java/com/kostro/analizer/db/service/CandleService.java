package com.kostro.analizer.db.service;

import com.kostro.analizer.db.model.CandelEntity;
import com.kostro.analizer.db.repository.CandelsRepository;
import com.kostro.analizer.wallet.Candel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CandleService {

    private static final Logger LOGGER = Logger.getLogger(CandleService.class.getName());

    private CandelsRepository repository;

    public CandleService(CandelsRepository repository) {
        this.repository = repository;
    }

    public void save(CandelEntity entity) {
        repository.save(entity);
    }

    public CandelEntity find(Long id) {
        return repository.findById(id).get();
    }

    public List<CandelEntity> findAll() {
        return repository.findAll();
    }

    public static CandelEntity from(Candel candel) {
        CandelEntity entity = new CandelEntity();
        entity.setTime(candel.getTime());
        entity.setResolution(candel.getResolution());
        entity.setOpen(candel.getOpen());
        entity.setHigh(candel.getHigh());
        entity.setLow(candel.getLow());
        entity.setClose(candel.getClose());
        entity.setVolume(candel.getVolume());
        return entity;
    }

    public static Candel from(CandelEntity candleEntity) {
        return new Candel(candleEntity.getTime(), candleEntity.getResolution(), candleEntity.getOpen(), candleEntity.getHigh(), candleEntity.getLow(), candleEntity.getClose(), candleEntity.getVolume());
    }

    public List<Candel> find(LocalDateTime startDate, LocalDateTime endDate, int resolution) {
        List<Candel> candles = new ArrayList<>();
        for(CandelEntity entity : repository.find(startDate, endDate, resolution)) {
            candles.add(from(entity));
        }
        return candles;
    }
}
