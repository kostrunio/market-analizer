package com.kostro.analizer.wallet;

public class Wallet {
    private double money;
    private double bitcoin;
    private double operationCost;

    public Wallet(double money, double operationCost) {
        this.money = money;
        this.bitcoin = 0;
        this.operationCost = operationCost;
    }

    public void buy(double price) {
        bitcoin = money / price * operationCost;
        money = 0;
    }

    public void sell(double price) {
        money = bitcoin * price * operationCost;
        bitcoin = 0;
    }
}
