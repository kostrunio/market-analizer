package com.kostro.analizer.ui;

import com.kostro.analizer.json.domain.candle.CandleResponse;
import com.kostro.analizer.json.service.JsonService;
import com.kostro.analizer.wallet.Configuration;
import com.kostro.analizer.wallet.Transaction;
import com.kostro.analizer.wallet.Wallet;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Route("")
public class MainView extends VerticalLayout {

    double buyFirst = 0.99;
    double buyLast = 0.90;
    double sellFailureFirst = 0.99;
    double sellFailureLast = 0.90;
    double sellSucessFirst = 1.02;
    double sellSucessLast = 1.10;
    double step = 0.01;
    int wrongLimit = 2;
    int days = 30;
    int hours = 1;

    private JsonService jsonService;

    public MainView(JsonService jsonService) {
        this.jsonService = jsonService;

        setSizeFull();

        updateList();
    }

    private void updateList() {

        Wallet wallet = new Wallet(1000, 0.9953);

        LocalDateTime startDate = LocalDateTime.of(2019, 12, 16, 00, 00, 00);
        LocalDateTime endDate = LocalDateTime.now();
        CandleResponse response = jsonService.getCandles("BTC-PLN", 3600, startDate, endDate);
        //startDate
        endDate = LocalDateTime.now().isAfter(startDate.plusDays(days).minusSeconds(1)) ? startDate.plusDays(days).minusSeconds(1) : LocalDateTime.now();
        do {
            Configuration configuration = countConfiguration(startDate, endDate, response);
            System.out.println(configuration);
            startDate = startDate.plusDays(days);
            endDate = LocalDateTime.now().isAfter(startDate.plusDays(days).minusSeconds(1)) ? startDate.plusDays(days).minusSeconds(1) : LocalDateTime.now();
            count(wallet, startDate, endDate, response, configuration.getBuy(), configuration.getSellFailure(), configuration.getSellSuccess(), true, wrongLimit);
        } while(startDate.plusDays(days).isBefore(LocalDateTime.now()));
    }

    private Configuration countConfiguration(LocalDateTime startDate, LocalDateTime endDate, CandleResponse response) {
//        System.out.println("buy;sellFailure;sellSucess;wallet;" + startDate.toString() + ';' + endDate.toString());
        List<Configuration> configurationList = new ArrayList<>();
        for (double buy = buyFirst; buy >= buyLast; buy -= step)
            for (double sellFailure = sellFailureFirst; sellFailure >= sellFailureLast; sellFailure -= step)
                for (double sellSucess = sellSucessFirst; sellSucess <= sellSucessLast; sellSucess += step)
                    configurationList.add(count(new Wallet(1000, 0.9953), startDate, endDate, response, buy, sellFailure, sellSucess, false, -1));
        return findConfiguration(configurationList);
    }

    private Configuration findConfiguration(List<Configuration> configurationList) {
        Configuration toReturn = new Configuration(0, 0, 0, 0);
        for (Configuration configuration : configurationList)
            if (configuration.getResult() > toReturn.getResult())
                toReturn = configuration;
            return toReturn;
    }

    private Configuration count(Wallet wallet, LocalDateTime startDate, LocalDateTime endDate, CandleResponse response, double buy, double sellFailure, double sellSucess, boolean withDetails, int wrongLimit) {
        double lastPrice = 0;
        int wrong = 0;
        hour:for (LocalDateTime date = LocalDateTime.from(startDate); date.isBefore(endDate); date = date.plusHours(1)) {
            if (response.getLow(date) == null || response.getLow(date.plusHours(1)) == null) continue hour;
            lastPrice = response.getLow(date.plusHours(1));
            if (wallet.hasMoney()) {
                if (wrongLimit > 0 && wrong > wrongLimit) break;
                if (response.getLow(date.plusHours(1)) < response.getHigh(date) * buy) {
                    wallet.buy(date.plusHours(1), response.getHigh(date) * buy);
                }
            } else {
                if (response.getLow(date.plusHours(1)) < wallet.getPrice() * sellFailure) {
                    wallet.sell(date.plusHours(1), wallet.getPrice() * sellFailure);
                    wrong++;
                } else if (response.getHigh(date.plusHours(1)) > wallet.getPrice() * sellSucess) {
                    wallet.sell(date.plusHours(1), wallet.getPrice() * sellSucess);

                }
            }
        }
        if (withDetails) {
        System.out.println(buy + ";" + sellFailure + ";" + sellSucess + ";" + (wallet.getMoney() + wallet.getBitcoin()*lastPrice));
            for (Transaction transaction : wallet.getTransactionHistory())
                System.out.println(transaction);
        }
        return new Configuration(buy, sellFailure, sellSucess, wallet.getMoney() + wallet.getBitcoin()*lastPrice);
    }
}