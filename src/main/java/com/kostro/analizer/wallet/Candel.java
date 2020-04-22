package com.kostro.analizer.wallet;

import java.time.LocalDateTime;

public class Candel {
    private LocalDateTime time;
    private int resolution;
    private double open;
    private double close;
    private double low;
    private double high;
    private double volume;

    public Candel(LocalDateTime time, int resolution, double open, double close, double low, double high, double volume) {
        this.time = time;
        this.resolution = resolution;
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
        this.volume = volume;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public int getResolution() {
        return resolution;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
