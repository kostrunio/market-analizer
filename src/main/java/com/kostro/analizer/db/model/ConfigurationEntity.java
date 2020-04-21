package com.kostro.analizer.db.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Configuration")
public class ConfigurationEntity extends AbstractEntity implements Cloneable {


}
