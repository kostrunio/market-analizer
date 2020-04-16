package com.vaadin.tutorial.crm.domain;

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

    public String getTimestamp(int i) {
        return items.get(i).get(0).toString();
    }

    public Double getOpen(int i) {
        if (items.get(i).get(1) instanceof Map)
            return Double.parseDouble(((Map<String, String>)items.get(i).get(1)).get("o"));
        return null;
    }

    public Double getClose(int i) {
        if (items.get(i).get(1) instanceof Map)
            return Double.parseDouble(((Map<String, String>)items.get(i).get(1)).get("c"));
        return null;
    }

    public Double getHigh(int i) {
        if (items.get(i).get(1) instanceof Map)
            return Double.parseDouble(((Map<String, String>)items.get(i).get(1)).get("h"));
        return null;
    }

    public Double getLow(int i) {
        if (items.size() > i && items.get(i).get(1) instanceof Map)
            return Double.parseDouble(((Map<String, String>)items.get(i).get(1)).get("l"));
        return null;
    }
}
