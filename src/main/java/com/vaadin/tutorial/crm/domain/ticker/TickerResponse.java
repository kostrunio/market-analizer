package com.vaadin.tutorial.crm.domain.ticker;

import java.util.Map;

public class TickerResponse {
    private String status;
    private Map<String, Ticker> items;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Ticker> getItems() {
        return items;
    }

    public void setItems(Map<String, Ticker> items) {
        this.items = items;
    }
}
