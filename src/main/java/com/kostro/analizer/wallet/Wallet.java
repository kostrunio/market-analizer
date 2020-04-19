package com.kostro.analizer.wallet;

public class Wallet {
    private double money;
    private double bitcoin;
    private double operationCost;
    private double price;

    public Wallet(double money, double operationCost) {
        this.money = money;
        this.bitcoin = 0;
        this.operationCost = operationCost;
    }

    public double getMoney() {
        return money;
    }

    public double getBitcoin() {
        return bitcoin;
    }

    public void buy(double price) {
        this.price = price;
        bitcoin = money / this.price * operationCost;
        money = 0;
    }

    public void sell(double price) {
        this.price = price;
        money = bitcoin * this.price * operationCost;
        bitcoin = 0;
    }

    public boolean hasMoney() {
        return money != 0;
    }

    public double getPrice() {
        return price;
    }
}
