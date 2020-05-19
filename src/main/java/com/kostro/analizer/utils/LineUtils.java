package com.kostro.analizer.utils;

import com.kostro.analizer.analize.Line;
import com.kostro.analizer.db.model.LineEntity;
import com.kostro.analizer.wallet.Candle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class LineUtils {
    private static final Logger log = LoggerFactory.getLogger(CandleUtils.class);

    public static Line from(LineEntity entity) {
        return new Line(entity.getStartDate(), entity.getStartValue(), entity.getEndDate(), entity.getEndValue(), entity.getResolution(), entity.getMargin());
    }

    public static LineEntity from(Line line) {
        LineEntity entity = new LineEntity();
        entity.setStartDate(line.getStartDate());
        entity.setStartValue(line.getStartValue());
        entity.setEndDate(line.getEndDate());
        entity.setEndValue(line.getEndValue());
        entity.setResolution(line.getResolution());
        entity.setMargin(line.getMargin());
        return entity;
    }

    public static void analize(Line line, Candle candle) {
        Duration duration = Duration.between(line.getStartDate(), line.getEndDate());
        // 21 + 9 = 30
        long steps = duration.toHours()/(line.getResolution()/3600);
        //9450 - 9975 -> -525 / 30-> -17,5
        double step = (line.getEndValue() - line.getStartValue())/steps;
        duration = Duration.between(line.getStartDate(), candle.getTime());
        if (candle.getClose() > line.getStartValue()- (step * duration.toHours())) {
            log.info();
        }
    }
}
