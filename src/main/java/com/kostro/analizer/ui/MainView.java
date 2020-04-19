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
        double buyFirst = 0.99;
        double buyLast = 0.90;
        double sellFailureFirst = 0.99;
        double sellFailureLast = 0.90;
        double sellSucessFirst = 1.02;
        double sellSucessLast = 1.10;
        int hours = 1;

        LocalDateTime startDate = LocalDateTime.of(2019, 12, 16, 00, 00, 00);
        LocalDateTime endDate = LocalDateTime.now();
        CandleResponse response = jsonService.getCandles("BTC-PLN", 3600, startDate, endDate);

        startDate = LocalDateTime.of(2019, 12, 16, 00, 00, 00);
        endDate = LocalDateTime.of(2020, 01, 15, 23, 59, 59);
        System.out.println("buy;sellFailure;sellSucess;wallet;" + startDate.toString() + ';' + endDate.toString());
        for (double buy = buyFirst; buy >= buyLast; buy -= 0.01)
            for (double sellFailure = sellFailureFirst; sellFailure >= sellFailureLast; sellFailure -= 0.01)
                for (double sellSucess = sellSucessFirst; sellSucess <= sellSucessLast; sellSucess += 0.01) {
                    count(new Wallet(1000, 0.9953), startDate, endDate, response, buy, sellFailure, sellSucess);
        }

        startDate = LocalDateTime.of(2020, 01, 16, 00, 00, 00);
        endDate = LocalDateTime.of(2020, 02, 15, 23, 59, 59);
        System.out.println("buy;sellFailure;sellSucess;wallet;" + startDate.toString() + ';' + endDate.toString());
        for (double buy = buyFirst; buy >= buyLast; buy -= 0.01)
            for (double sellFailure = sellFailureFirst; sellFailure >= sellFailureLast; sellFailure -= 0.01)
                for (double sellSucess = sellSucessFirst; sellSucess <= sellSucessLast; sellSucess += 0.01) {
                    count(new Wallet(1000, 0.9953), startDate, endDate, response, buy, sellFailure, sellSucess);
                }

        startDate = LocalDateTime.of(2020, 02, 16, 00, 00, 00);
        endDate = LocalDateTime.of(2020, 03, 15, 23, 59, 59);
        System.out.println("buy;sellFailure;sellSucess;wallet;" + startDate.toString() + ';' + endDate.toString());
        for (double buy = buyFirst; buy >= buyLast; buy -= 0.01)
            for (double sellFailure = sellFailureFirst; sellFailure >= sellFailureLast; sellFailure -= 0.01)
                for (double sellSucess = sellSucessFirst; sellSucess <= sellSucessLast; sellSucess += 0.01) {
                    count(new Wallet(1000, 0.9953), startDate, endDate, response, buy, sellFailure, sellSucess);
                }

        startDate = LocalDateTime.of(2020, 03, 16, 00, 00, 00);
        endDate = LocalDateTime.of(2020, 04, 15, 23, 59, 59);
        System.out.println("buy;sellFailure;sellSucess;wallet;" + startDate.toString() + ';' + endDate.toString());
        for (double buy = buyFirst; buy >= buyLast; buy -= 0.01)
            for (double sellFailure = sellFailureFirst; sellFailure >= sellFailureLast; sellFailure -= 0.01)
                for (double sellSucess = sellSucessFirst; sellSucess <= sellSucessLast; sellSucess += 0.01) {
                    count(new Wallet(1000, 0.9953), startDate, endDate, response, buy, sellFailure, sellSucess);
                }

//        count(new Wallet(1000, 0.9953), startDate, endDate, response, 0.97, 0.94, 1.09);
    }

    private void count(Wallet wallet, LocalDateTime startDate, LocalDateTime endDate, CandleResponse response, double buy, double sellFailure, double sellSucess) {
        double lastPrice = 0;
        hour:for (LocalDateTime date = LocalDateTime.from(startDate); date.isBefore(endDate); date = date.plusHours(1)) {
            if (response.getLow(date) == null || response.getLow(date.plusHours(1)) == null) continue hour;
            lastPrice = response.getLow(date);
            if (wallet.hasMoney()) {
                if (response.getLow(date.plusHours(1)) < response.getHigh(date) * buy) {
                    wallet.buy(response.getHigh(date) * buy);
                }
            } else {
                if (response.getLow(date.plusHours(1)) < wallet.getPrice() * sellFailure) {
                    wallet.sell(wallet.getPrice() * sellFailure);
                } else if (response.getHigh(date.plusHours(1)) > wallet.getPrice() * sellSucess) {
                    wallet.sell(wallet.getPrice() * sellSucess);

                }
            }
        }
        if (!wallet.hasMoney()) wallet.sell(lastPrice);
        System.out.println(buy + ";" + sellFailure + ";" + sellSucess + ";" + wallet.getMoney());
    }
}