package com.kostro.analizer.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Configuration")
public class ConfigurationEntity extends AbstractEntity implements Cloneable {

    @Column(name = "c_name")
    @NotNull
    private String name;

    @Column(name = "c_value")
    @NotNull
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
