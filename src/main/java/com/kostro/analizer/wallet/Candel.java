package com.kostro.analizer.wallet;

public class Candel {
    private String timestamp;
    private double open;
    private double close;
    private double low;
    private double high;

    public Candel(String timestamp, double open, double close, double low, double high) {
        this.timestamp = timestamp;
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }

    public double getLow() {
        return low;
    }

    public double getHigh() {
        return high;
    }
}
