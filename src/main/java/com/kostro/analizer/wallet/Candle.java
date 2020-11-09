package com.kostro.analizer.wallet;

import java.time.LocalDateTime;

public class Candle {
    private LocalDateTime time;
    private int resolution;
    private double open;
    private double close;
    private double low;
    private double high;
    private double volume;

    public Candle(LocalDateTime time, int resolution, double open, double high, double low, double close, double volume) {
        this.time = time;
        this.resolution = resolution;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
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

    @Override
    public String toString() {
        return time + "[" +
                "o=" + open +
                ", c=" + close +
                ", l=" + low +
                ", h=" + high +
                ", v=" + volume +
                "]";
    }
}
