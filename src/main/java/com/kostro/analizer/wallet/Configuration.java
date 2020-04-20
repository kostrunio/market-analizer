package com.kostro.analizer.wallet;

import java.time.LocalDateTime;

public class Configuration {
    private int offerLong;
    private int startDay;
    private int periodLong;
    private double buy;
    private double sellFailure;
    private double sellSuccess;
    private double result;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Configuration(int offerLong, int startDay, int periodLong, double buy, double sellFailure, double sellSuccess, double result, LocalDateTime startDate, LocalDateTime endDate) {
        this.offerLong = offerLong;
        this.startDay = startDay;
        this.periodLong = periodLong;
        this.buy = buy;
        this.sellFailure = sellFailure;
        this.sellSuccess = sellSuccess;
        this.result = result;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getOfferLong() {
        return offerLong;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getPeriodLong() {
        return periodLong;
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

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "offerLong=" + offerLong +
                ", startDay=" + startDay +
                ", periodLong=" + periodLong +
                ", buy=" + buy +
                ", sellFailure=" + sellFailure +
                ", sellSuccess=" + sellSuccess +
                ", result=" + result +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
