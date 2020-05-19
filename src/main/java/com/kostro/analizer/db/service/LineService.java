package com.kostro.analizer.db.service;

import com.kostro.analizer.analize.Line;
import com.kostro.analizer.db.model.CandleEntity;
import com.kostro.analizer.db.model.LineEntity;
import com.kostro.analizer.db.repository.CandlesRepository;
import com.kostro.analizer.db.repository.LineRepository;
import com.kostro.analizer.utils.CandleUtils;
import com.kostro.analizer.utils.LineUtils;
import com.kostro.analizer.wallet.Candle;
import com.kostro.analizer.wallet.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {

    private static final Logger log = LoggerFactory.getLogger(LineService.class);

    private LineRepository repository;

    public LineService(LineRepository repository) {
        this.repository = repository;
    }

    public void save(LineEntity entity) {
        repository.save(entity);
    }

    public List<Line> findInRange(LocalDateTime date, Resolution resolution) {
        List<Line> lines = new ArrayList<>();
        for (LineEntity entity : repository.findInRange(date, resolution.getSecs()))
            lines.add(LineUtils.from(entity));
        return lines;
    }
}
