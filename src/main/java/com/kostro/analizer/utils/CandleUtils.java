package com.kostro.analizer.utils;

import com.kostro.analizer.db.model.CandleEntity;
import com.kostro.analizer.json.bitbay.domain.candle.CandleResponse;
import com.kostro.analizer.wallet.Candle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CandleUtils {
    private static final Logger log = LoggerFactory.getLogger(CandleUtils.class);

    public static  Map<LocalDateTime, Candle> createCandles(List<Candle> Candles) {
        Map<LocalDateTime, Candle> map = new HashMap<>();
        for (Candle Candle : Candles) {
            map.put(Candle.getTime(), Candle);
        }
        return map;
    }

    public static List<Candle> prepareCandles(List<Candle> Candles, int resolution, LocalDateTime fromDate, LocalDateTime toDate) {
        List<Candle> list = new ArrayList<>();
        List<Candle> sortedCandles = Candles.stream().sorted(Comparator.comparing(Candle::getTime)).collect(Collectors.toList());
        Candle newCandle = null;
        LocalDateTime date = fromDate;
        for (int i = 0; i < sortedCandles.size();) {
            Candle Candle = sortedCandles.get(i);
            if (Candle.getTime().isBefore(date.plusSeconds(resolution))) {
                if (newCandle == null) {
                    newCandle = new Candle(date, resolution, Candle.getOpen(), Candle.getHigh(), Candle.getLow(), Candle.getClose(), Candle.getVolume());
                    i++;
                    continue;
                }
                updateCandle(newCandle, Candle);
                i++;
                continue;
            } else if (newCandle != null) {
                list.add(newCandle);
                newCandle = null;
            }
            date = date.plusSeconds(resolution);
        }
        if (newCandle != null) list.add(newCandle);
        return list;
    }

    private static void updateCandle(Candle newCandle, Candle Candle) {
        if (newCandle.getHigh() < Candle.getHigh()) newCandle.setHigh(Candle.getHigh());
        if (newCandle.getLow() > Candle.getLow()) newCandle.setLow(Candle.getLow());
        newCandle.setClose(Candle.getClose());
        newCandle.setVolume(newCandle.getVolume()+Candle.getVolume());
    }

    public static CandleEntity from (Candle Candle, Long id) {
        CandleEntity entity = from(Candle);
        entity.setId(id);
        return entity;
    }
    public static CandleEntity from(Candle Candle) {
        CandleEntity entity = new CandleEntity();
        entity.setTime(Candle.getTime());
        entity.setResolution(Candle.getResolution());
        entity.setOpen(Candle.getOpen());
        entity.setHigh(Candle.getHigh());
        entity.setLow(Candle.getLow());
        entity.setClose(Candle.getClose());
        entity.setVolume(Candle.getVolume());
        return entity;
    }

    public static Candle from(CandleEntity candleEntity) {
        return new Candle(candleEntity.getTime(), candleEntity.getResolution(), candleEntity.getOpen(), candleEntity.getHigh(), candleEntity.getLow(), candleEntity.getClose(), candleEntity.getVolume());
    }
}
