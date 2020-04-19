package com.kostro.analizer.wallet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private double money;
    private double bitcoin;
    private double operationCost;
    private double price;
    private Transaction currentTransaction;
    private List<Transaction> transactionHistory = new ArrayList<>();

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

    public void buy(LocalDateTime date, double price) {
        currentTransaction = new Transaction(date, price);
        this.price = price;
        bitcoin = money / this.price * operationCost;
        money = 0;
    }

    public void sell(LocalDateTime date, double price) {
        currentTransaction.setSellDate(date).setSellPrice(price);
        transactionHistory.add(currentTransaction);
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

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
}
