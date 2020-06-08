package com.kostro.analizer.analize;

import java.time.LocalDateTime;

public class Line {

    private LocalDateTime startDate;
    private double startValue;
    private LocalDateTime endDate;
    private double endValue;
    private int resolution;
    private double margin;

    public Line(LocalDateTime startDate, double startValue, LocalDateTime endDate, double endValue, int resolution, double margin) {
        this.startDate = startDate;
        this.startValue = startValue;
        this.endDate = endDate;
        this.endValue = endValue;
        this.resolution = resolution;
        this.margin = margin;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public double getStartValue() {
        return startValue;
    }

    public void setStartValue(double startValue) {
        this.startValue = startValue;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public double getEndValue() {
        return endValue;
    }

    public void setEndValue(double endValue) {
        this.endValue = endValue;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }
}
