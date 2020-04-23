package com.kostro.analizer.db.service;

import com.kostro.analizer.db.model.CandelEntity;
import com.kostro.analizer.db.repository.CandelsRepository;
import com.kostro.analizer.scheduler.Scheduler;
import com.kostro.analizer.utils.CandelUtils;
import com.kostro.analizer.wallet.Candel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CandleService {

    private static final Logger log = LoggerFactory.getLogger(CandleService.class);

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

    public List<Candel> find(LocalDateTime startDate, LocalDateTime endDate, int resolution) {
        List<Candel> candles = new ArrayList<>();
        for(CandelEntity entity : repository.find(startDate, endDate, resolution)) {
            candles.add(CandelUtils.from(entity));
        }
        return candles;
    }

    public void refreshCandels(List<Candel> candels) {
        for (Candel candel : candels) {
            CandelEntity entity = repository.findByTimeAndResolution(candel.getTime(), candel.getResolution());
            entity = CandelUtils.from(candel, entity != null ? entity.getId() : null);
            repository.save(entity);
        }
    }

    public LocalDateTime getLastDate() {
        return repository.findLastCandel();
    }
}
