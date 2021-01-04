package com.kostro.analizer.utils;

import com.kostro.analizer.db.model.CandleEntity;
import com.kostro.analizer.wallet.Candle;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CandleUtils {

    public static  Map<LocalDateTime, Candle> createCandles(List<Candle> candles) {
        Map<LocalDateTime, Candle> map = new HashMap<>();
        for (Candle candle : candles) {
            map.put(candle.getTime(), candle);
        }
        return map;
    }

    public static List<Candle> prepareCandles(List<Candle> candles, int resolution, LocalDateTime fromDate) {
        List<Candle> list = new ArrayList<>();
        List<Candle> sortedCandles = candles.stream().sorted(Comparator.comparing(Candle::getTime)).collect(Collectors.toList());
        Candle newCandle = null;
        LocalDateTime date = fromDate;
        for (int i = 0; i < sortedCandles.size();) {
            Candle candle = sortedCandles.get(i);
            if (candle.getTime().isBefore(date.plusSeconds(resolution))) {
                if (newCandle == null) {
                    newCandle = new Candle(date, resolution, candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVolume());
                    i++;
                    continue;
                }
                updateCandle(newCandle, candle);
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

    private static void updateCandle(Candle newCandle, Candle candle) {
        if (newCandle.getHigh() < candle.getHigh()) newCandle.setHigh(candle.getHigh());
        if (newCandle.getLow() > candle.getLow()) newCandle.setLow(candle.getLow());
        newCandle.setClose(candle.getClose());
        newCandle.setVolume(newCandle.getVolume()+candle.getVolume());
    }

    public static CandleEntity from(String market, Candle candle, Long id) {
        CandleEntity entity = from(market, candle);
        entity.setId(id);
        return entity;
    }
    public static CandleEntity from(String market, Candle candle) {
        CandleEntity entity = new CandleEntity();
        entity.setMarket(market);
        entity.setTime(candle.getTime());
        entity.setResolution(candle.getResolution(market));
        entity.setOpen(candle.getOpen());
        entity.setHigh(candle.getHigh());
        entity.setLow(candle.getLow());
        entity.setClose(candle.getClose());
        entity.setVolume(candle.getVolume());
        return entity;
    }

    public static Candle from(CandleEntity candleEntity) {
        return new Candle(candleEntity.getTime(), candleEntity.getResolution(), candleEntity.getOpen(), candleEntity.getHigh(), candleEntity.getLow(), candleEntity.getClose(), candleEntity.getVolume());
    }
}
