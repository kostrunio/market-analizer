package com.kostro.analizer.ui;

import com.kostro.analizer.json.domain.candle.CandleResponse;
import com.kostro.analizer.json.service.JsonService;
import com.kostro.analizer.wallet.Wallet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Route("")
public class MainView extends VerticalLayout {

    private JsonService jsonService;

    public MainView(JsonService jsonService) {
        this.jsonService = jsonService;

        setSizeFull();

        updateList();
    }

    private void updateList() {
        Wallet wallet = new Wallet(1000, 0.9953);
        double buy = 0.95;
        double sellFailure = 0.9;
        double sellSucess = 1.1;

        String line = null;

        LocalDateTime startDate = LocalDateTime.of(2020, 03, 18, 00, 00, 00);
        LocalDateTime now = LocalDateTime.now();
        CandleResponse response = jsonService.getCandles("BTC-PLN", 3600, startDate, now);
        hour: for (LocalDateTime date = LocalDateTime.from(startDate); date.isBefore(now); date = date.plusHours(1)) {
            if (response.getLow(date) == null || response.getLow(date.plusHours(1)) == null) continue hour;
            if (wallet.hasMoney()) {
                if (response.getLow(date.plusHours(1)) < response.getHigh(date) * buy) {
                    wallet.buy(response.getHigh(date) * buy);
                    line = date.plusHours(1).toString() + ": " + wallet.getPrice();
                    System.out.println(line);
                }
            } else {
                if (response.getLow(date.plusHours(1)) < wallet.getPrice() * sellFailure) {
                    wallet.sell(wallet.getPrice() * sellFailure);
                    String newLine = line + " -> " + date.plusHours(1).toString() + ": " + wallet.getPrice() + " STRATA";
                    System.out.println(newLine);
                    add(new Label(newLine));
                } else if (response.getHigh(date.plusHours(1)) > wallet.getPrice() * sellSucess) {
                    wallet.sell(wallet.getPrice() * sellSucess);
                    String newLine = line + " -> " + date.plusHours(1).toString() + ": " + wallet.getPrice() + " ZYSK";
                    System.out.println(newLine);
                    add(new Label(newLine));

                }
            }
        }
        System.out.println("result: " + wallet.getMoney() + " PLN + " + wallet.getBitcoin() + "BTC");
    }
}