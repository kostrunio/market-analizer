package com.kostro.analizer.db.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
