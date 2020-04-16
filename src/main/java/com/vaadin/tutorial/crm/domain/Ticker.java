package com.vaadin.tutorial.crm.domain;

public class Ticker {

    private Market market;
    private String time;
    private Double highestBid;
    private Double lowestAsk;
    private Double rate;
    private Double previousRate;

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(Double highestBid) {
        this.highestBid = highestBid;
    }

    public Double getLowestAsk() {
        return lowestAsk;
    }

    public void setLowestAsk(Double lowestAsk) {
        this.lowestAsk = lowestAsk;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getPreviousRate() {
        return previousRate;
    }

    public void setPreviousRate(Double previousRate) {
        this.previousRate = previousRate;
    }
}
