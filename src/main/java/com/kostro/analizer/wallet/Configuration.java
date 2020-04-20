package com.kostro.analizer.wallet;

public class Configuration {
    private int offerLong;
    private int startDate;
    private int periodLong;
    private double buy;
    private double sellFailure;
    private double sellSuccess;
    private double result;

    public Configuration(double buy, double sellFailure, double sellSuccess, double result) {
        this.buy = buy;
        this.sellFailure = sellFailure;
        this.sellSuccess = sellSuccess;
        this.result = result;
    }

    public double getBuy() {
        return buy;
    }

    public double getSellFailure() {
        return sellFailure;
    }

    public double getSellSuccess() {
        return sellSuccess;
    }

    public double getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "buy=" + buy +
                ", sellFailure=" + sellFailure +
                ", sellSuccess=" + sellSuccess +
                ", result=" + result +
                '}';
    }
}
