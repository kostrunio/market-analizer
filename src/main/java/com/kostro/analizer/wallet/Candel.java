package com.kostro.analizer.wallet;

public class Candel {
    private String timestamp;
    private int resolution;
    private double open;
    private double close;
    private double low;
    private double high;
    private double volume;

    public Candel(String timestamp, int resolution, double open, double close, double low, double high, double volume) {
        this.timestamp = timestamp;
        this.resolution = resolution;
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
        this.volume = volume;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getResolution() {
        return resolution;
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

    public double getVolume() {
        return volume;
    }
}
