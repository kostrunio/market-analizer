package com.vaadin.tutorial.crm.domain.ticker;

public class Transaction {

    private String currency;
    private Double minOffer;
    private Integer scale;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getMinOffer() {
        return minOffer;
    }

    public void setMinOffer(Double minOffer) {
        this.minOffer = minOffer;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }
}
