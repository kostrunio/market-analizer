package com.kostro.analizer.json.bitbay.domain.ticker;

import java.time.LocalDateTime;

public class Market {
    private String code;
    private First first;
    private Second second;
    private LocalDateTime time;
    private Double highestBid;
    private Double lowestAsk;
    private Double rate;
    private Double previousRate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public First getFirst() {
        return first;
    }

    public void setFirst(First first) {
        this.first = first;
    }

    public Second getSecond() {
        return second;
    }

    public void setSecond(Second second) {
        this.second = second;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
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
