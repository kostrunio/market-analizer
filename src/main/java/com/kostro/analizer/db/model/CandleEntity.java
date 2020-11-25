package com.kostro.analizer.db.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Candles")
public class CandleEntity extends AbstractEntity implements Cloneable {

    @Column(name = "c_time")
    @NotNull
    private LocalDateTime time;

    @Column(name = "c_resolution")
    @NotNull
    private int resolution;

    @Column(name = "c_open")
    @NotNull
    private double open;

    @Column(name = "c_high")
    @NotNull
    private double high;

    @Column(name = "c_low")
    @NotNull
    private double low;

    @Column(name = "c_close")
    @NotNull
    private double close;

    @Column(name = "c_volume")
    @NotNull
    private double volume;

    @Column(name = "c_co")
    @NotNull
    private double co;

}
