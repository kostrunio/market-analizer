package com.kostro.analizer.db.model;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "Configuration")
public class ConfigurationEntity extends AbstractEntity implements Cloneable {

    @Column(name = "c_market")
    @NotNull
    private String market;

    @Column(name = "c_name")
    @NotNull
    private String name;

    @Column(name = "c_value")
    @NotNull
    private String value;
}
