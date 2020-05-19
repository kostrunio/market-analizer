package com.kostro.analizer.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "Lines")
public class LineEntity extends AbstractEntity implements Cloneable {

    @Column(name = "l_startDate")
    @NotNull
    private LocalDateTime startDate;

    @Column(name = "l_startValue")
    @NotNull
    private double startValue;

    @Column(name = "l_endDate")
    @NotNull
    private LocalDateTime endDate;

    @Column(name = "l_endValue")
    @NotNull
    private double endValue;

    @Column(name = "l_resolution")
    @NotNull
    private long resolution;

    @Column(name = "l_margin")
    @NotNull
    private double margin;

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

    public long getResolution() {
        return resolution;
    }

    public void setResolution(long resolution) {
        this.resolution = resolution;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }
}
