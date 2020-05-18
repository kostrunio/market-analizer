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
}
