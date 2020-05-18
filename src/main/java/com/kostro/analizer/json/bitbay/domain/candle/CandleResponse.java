package com.kostro.analizer.json.bitbay.domain.candle;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

public class CandleResponse {
    private String status;
    private List<List<Object>> items;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<List<Object>> getItems() {
        return items;
    }

    public void setItems(List<List<Object>> items) {
        this.items = items;
    }

    public Double getOpen(LocalDateTime time) {
        Map<String, String> candle = findCandle(time);
        if (candle != null)
            return Double.parseDouble(candle.get("o"));
        return null;
    }

    public Double getClose(LocalDateTime time) {
        Map<String, String> candle = findCandle(time);
        if (candle != null)
            return Double.parseDouble(candle.get("c"));
        return null;
    }

    public Double getHigh(LocalDateTime time) {
        Map<String, String> candle = findCandle(time);
        if (candle != null)
            return Double.parseDouble(candle.get("h"));
        return null;
    }

    public Double getLow(LocalDateTime time) {
        Map<String, String> candle = findCandle(time);
        if (candle != null)
            return Double.parseDouble(candle.get("l"));
        return null;
    }

    private Map<String, String> findCandle(LocalDateTime time) {
        String timestamp = time.toEpochSecond(ZoneOffset.of("+2"))+"000";
        for (List<Object> item : items) {
            if (item.get(0).toString().equals(timestamp))
                return (Map<String, String>) item.get(1);
        }
        return null;
    }
}
