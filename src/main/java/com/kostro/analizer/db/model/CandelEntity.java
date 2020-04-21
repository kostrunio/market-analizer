package com.kostro.analizer.db.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "Candels")
public class CandelEntity extends AbstractEntity implements Cloneable {

    @Column(name = "c_time")
    @NotNull
    @NotEmpty
    private LocalDateTime time;

    @Column(name = "c_resolution")
    @NotNull
    @NotEmpty
    private int resolution;

    @Column(name = "c_open")
    @NotNull
    @NotEmpty
    private double open;

    @Column(name = "c_high")
    @NotNull
    @NotEmpty
    private double high;

    @Column(name = "c_low")
    @NotNull
    @NotEmpty
    private double low;

    @Column(name = "c_close")
    @NotNull
    @NotEmpty
    private double close;

    @Column(name = "c_volume")
    @NotNull
    @NotEmpty
    private double volume;

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
