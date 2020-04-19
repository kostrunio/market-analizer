package com.kostro.analizer.ui;

import com.kostro.analizer.json.domain.candle.CandleResponse;
import com.kostro.analizer.json.service.JsonService;
import com.kostro.analizer.wallet.Candel;
import com.kostro.analizer.wallet.Configuration;
import com.kostro.analizer.wallet.Transaction;
import com.kostro.analizer.wallet.Wallet;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("")
public class MainView extends VerticalLayout {

    double buyFirst = 0.99;
    double buyLast = 0.90;
    double sellFailureFirst = 0.99;
    double sellFailureLast = 0.90;
    double sellSucessFirst = 1.02;
    double sellSucessLast = 1.10;
    double step = 0.01;
    int wrongLimit = -1;
    int daysFirst = 5;
    int daysLast = 20;
    int hours = 1;

    double lastPrice = 0;

    private JsonService jsonService;

    public MainView(JsonService jsonService) {
        this.jsonService = jsonService;

        setSizeFull();

        updateList();
    }

    private void updateList() {

        LocalDateTime startDate = LocalDateTime.of(2019, 12, 16, 00, 00, 00);
        LocalDateTime endDate = LocalDateTime.now();
        CandleResponse response = jsonService.getCandles("BTC-PLN", 3600, startDate, endDate);
        Map<String, Candel> candels = createCandels(response, startDate, endDate);

        for (int days = daysFirst; days <= daysLast; days++) {
            for (int day = 16; day < 30; day++) {
                startDate = LocalDateTime.of(2019, 12, day, 00, 00, 00);
                Wallet wallet = new Wallet(1000, 0.9953);
                endDate = LocalDateTime.now().isAfter(startDate.plusDays(days).minusSeconds(1)) ? startDate.plusDays(days).minusSeconds(1) : LocalDateTime.now();
                do {
                    Configuration configuration = countConfiguration(startDate, endDate, candels);
//                System.out.println(configuration);
                    startDate = startDate.plusDays(days);
                    endDate = LocalDateTime.now().isAfter(startDate.plusDays(days).minusSeconds(1)) ? startDate.plusDays(days).minusSeconds(1) : LocalDateTime.now();
                    if (configuration.getResult() < 1010) continue;
                    count(wallet, startDate, endDate, candels, configuration.getBuy(), configuration.getSellFailure(), configuration.getSellSuccess(), false, wrongLimit);
                } while (startDate.plusDays(days).isBefore(LocalDateTime.now()));
                System.out.println("RESULT:" + (wallet.getMoney() + wallet.getBitcoin() * lastPrice) + " for " + days + " from " + day);
            }
        }
    }

    private Configuration countConfiguration(LocalDateTime startDate, LocalDateTime endDate, Map<String, Candel> candels) {
//        System.out.println("buy;sellFailure;sellSucess;wallet;" + startDate.toString() + ';' + endDate.toString());
        List<Configuration> configurationList = new ArrayList<>();
        for (double buy = buyFirst; buy >= buyLast; buy -= step)
            for (double sellFailure = sellFailureFirst; sellFailure >= sellFailureLast; sellFailure -= step)
                for (double sellSucess = sellSucessFirst; sellSucess <= sellSucessLast; sellSucess += step)
                    configurationList.add(count(new Wallet(1000, 0.9953), startDate, endDate, candels, buy, sellFailure, sellSucess, false, -1));
        return findConfiguration(configurationList);
    }

    private Configuration findConfiguration(List<Configuration> configurationList) {
        Configuration toReturn = new Configuration(0, 0, 0, 0);
        for (Configuration configuration : configurationList)
            if (configuration.getResult() > toReturn.getResult())
                toReturn = configuration;
            return toReturn;
    }

    private Configuration count(Wallet wallet, LocalDateTime startDate, LocalDateTime endDate, Map<String, Candel> candels, double buy, double sellFailure, double sellSucess, boolean withDetails, int wrongLimit) {
        lastPrice = 0;
        int wrong = 0;
        hour:for (LocalDateTime date = LocalDateTime.from(startDate); date.isBefore(endDate); date = date.plusHours(1)) {
            if (candels.get(dateToTimestamp(date)) == null || candels.get(dateToTimestamp(date.plusHours(1))) == null) continue hour;
            lastPrice = candels.get(dateToTimestamp(date.plusHours(1))).getLow();
            if (wallet.hasMoney()) {
                if (wrongLimit > 0 && wrong > wrongLimit) break;
                if (candels.get(dateToTimestamp(date.plusHours(1))).getLow() < candels.get(dateToTimestamp(date)).getHigh() * buy) {
                    wallet.buy(date.plusHours(1), candels.get(dateToTimestamp(date)).getHigh() * buy);
                }
            } else {
                if (candels.get(dateToTimestamp(date.plusHours(1))).getLow() < wallet.getPrice() * sellFailure) {
                    wallet.sell(date.plusHours(1), wallet.getPrice() * sellFailure);
                    wrong++;
                } else if (candels.get(dateToTimestamp(date.plusHours(1))).getHigh() > wallet.getPrice() * sellSucess) {
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

    private Map<String, Candel> createCandels(CandleResponse response, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Candel> candels = new HashMap<>();
        for (List<Object> item : response.getItems()) {
            String timestamp = item.get(0).toString();
            double open = Double.parseDouble(((Map<String, String>)item.get(1)).get("o"));
            double close = Double.parseDouble(((Map<String, String>)item.get(1)).get("c"));
            double low = Double.parseDouble(((Map<String, String>)item.get(1)).get("l"));
            double high = Double.parseDouble(((Map<String, String>)item.get(1)).get("h"));
            candels.put(timestamp, new Candel(timestamp, open, close, low, high));
        }
        return candels;
    }

    private String dateToTimestamp(LocalDateTime date) {
        return date.toEpochSecond(ZoneOffset.of("+1"))+"000";
    }
}