package com.kostro.analizer.wallet;

import java.time.LocalDateTime;

public class Transaction {
    private LocalDateTime buyDate;
    private double buyPrice;
    private LocalDateTime sellDate;
    private double sellPrice;
    private boolean success;

    public Transaction(LocalDateTime buyDate, double buyPrice) {
        this.buyDate = buyDate;
        this.buyPrice = buyPrice;
    }

    public LocalDateTime getBuyDate() {
        return buyDate;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public LocalDateTime getSellDate() {
        return sellDate;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public boolean isSuccess() {
        return success;
    }

    public Transaction setSellDate(LocalDateTime sellDate) {
        this.sellDate = sellDate;
        return this;
    }

    public Transaction setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
        success = sellPrice > buyPrice ? true : false;
        return this;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "buyDate=" + buyDate +
                ", buyPrice=" + buyPrice +
                ", sellDate=" + sellDate +
                ", sellPrice=" + sellPrice +
                ", success=" + success +
                '}';
    }
}
