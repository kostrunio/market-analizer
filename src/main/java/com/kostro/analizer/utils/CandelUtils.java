package com.kostro.analizer.utils;

import com.kostro.analizer.db.model.CandelEntity;
import com.kostro.analizer.json.domain.candle.CandleResponse;
import com.kostro.analizer.wallet.Candel;
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

public class CandelUtils {
    private static final Logger log = LoggerFactory.getLogger(CandelUtils.class);

    public static List<Candel> createCandels(CandleResponse response, int resolution) {
        List<Candel> candels = new ArrayList<>();
        if (response.getItems() != null)
            for (List<Object> item : response.getItems()) {
                String timestamp = item.get(0).toString();
                double open = Double.parseDouble(((Map<String, String>)item.get(1)).get("o"));
                double close = Double.parseDouble(((Map<String, String>)item.get(1)).get("c"));
                double low = Double.parseDouble(((Map<String, String>)item.get(1)).get("l"));
                double high = Double.parseDouble(((Map<String, String>)item.get(1)).get("h"));
                double volume = Double.parseDouble(((Map<String, String>)item.get(1)).get("v"));
                candels.add(new Candel(LocalDateTime.ofEpochSecond(Long.parseLong(timestamp.substring(0, 10)), Integer.parseInt(timestamp.substring(11)), ZoneOffset.of("+2")), resolution, open, high, low, close, volume));
            }
        return candels;
    }

    public static  Map<LocalDateTime, Candel> createCandels(List<Candel> candels) {
        Map<LocalDateTime, Candel> map = new HashMap<>();
        for (Candel candel : candels) {
            map.put(candel.getTime(), candel);
        }
        return map;
    }

    public static List<Candel> prepareCandels(List<Candel> candels, int resolution, LocalDateTime fromDate, LocalDateTime toDate) {
        List<Candel> list = new ArrayList<>();
        List<Candel> sortedCandels = candels.stream().sorted(Comparator.comparing(Candel::getTime)).collect(Collectors.toList());
        Candel newCandel = null;
        LocalDateTime date = fromDate;
        for (int i = 0; i < sortedCandels.size();) {
            Candel candel = sortedCandels.get(i);
            if (candel.getTime().isBefore(date.plusSeconds(resolution))) {
                if (newCandel == null) {
                    newCandel = new Candel(date, resolution, candel.getOpen(), candel.getHigh(), candel.getLow(), candel.getClose(), candel.getVolume());
                    i++;
                    continue;
                }
                updateCandel(newCandel, candel);
                i++;
                continue;
            } else if (newCandel != null) {
                list.add(newCandel);
                newCandel = null;
            }
            date = date.plusSeconds(resolution);
        }
        if (newCandel != null) list.add(newCandel);
        return list;
    }

    private static void updateCandel(Candel newCandel, Candel candel) {
        if (newCandel.getHigh() < candel.getHigh()) newCandel.setHigh(candel.getHigh());
        if (newCandel.getLow() > candel.getLow()) newCandel.setLow(candel.getLow());
        newCandel.setClose(candel.getClose());
        newCandel.setVolume(newCandel.getVolume()+candel.getVolume());
    }

    public static CandelEntity from (Candel candel, Long id) {
        CandelEntity entity = from(candel);
        entity.setId(id);
        return entity;
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
}
