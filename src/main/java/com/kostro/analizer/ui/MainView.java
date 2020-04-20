package com.kostro.analizer.ui;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.json.domain.candle.CandleResponse;
import com.kostro.analizer.json.service.JsonService;
import com.kostro.analizer.wallet.Candel;
import com.kostro.analizer.wallet.Configuration;
import com.kostro.analizer.wallet.Transaction;
import com.kostro.analizer.wallet.Wallet;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("")
public class MainView extends MainDesign {

    int hours = 1;

    double lastPrice = 0;

    private JsonService jsonService;
    private CandleService candleService;

    ComponentEventListener<ClickEvent<Button>> jsonButtonClicked = e -> {
        LocalDateTime fromDate = LocalDateTime.of(fromDatePicker.getValue(), LocalTime.of(0, 0, 0, 0));
        LocalDateTime toDate = LocalDateTime.of(toDatePicker.getValue(), LocalTime.of(23, 59, 59, 999));
        CandleResponse response = jsonService.getCandles("BTC-PLN", resolutionBox.getValue().getSecs(), fromDate, toDate);
        Map<String, Candel> candels = createCandels(response);
        for (Candel candel : candels.values()) {
            candleService.save(candleService.from(candel));
        }
    };

    ComponentEventListener<ClickEvent<Button>> analizeButtonClicked = e -> {
        LocalDateTime startDate = LocalDateTime.of(fromDatePicker.getValue(), LocalTime.of(0, 0, 0, 0));
        LocalDateTime endDate = LocalDateTime.of(toDatePicker.getValue(), LocalTime.of(23, 59, 59, 999));
        List<Candel> candelsList = candleService.find(startDate, endDate, resolutionBox.getValue().getSecs());
        Map<String, Candel> candels = createCandels(candelsList);

        for (int days = daysNumberFromField.getValue().intValue(); days <= daysNumberToField.getValue().intValue(); days++) {
            Wallet wallet = new Wallet(moneyField.getValue(), 0.9953);
            endDate = LocalDateTime.now().isAfter(startDate.plusDays(days).minusSeconds(1)) ? startDate.plusDays(days).minusSeconds(1) : LocalDateTime.now();
            do {
                Configuration configuration = countConfiguration(startDate, endDate, candels);
                System.out.println(configuration);
                startDate = startDate.plusDays(days);
                endDate = LocalDateTime.now().isAfter(startDate.plusDays(days).minusSeconds(1)) ? startDate.plusDays(days).minusSeconds(1) : LocalDateTime.now();
                if (configuration.getResult() < 1010) continue;
                count(wallet, startDate, endDate, candels, configuration.getBuy(), configuration.getSellFailure(), configuration.getSellSuccess(), false, wrongField.getValue().intValue());
            } while (startDate.plusDays(days).isBefore(LocalDateTime.now()));
            System.out.println("RESULT:" + (wallet.getMoney() + wallet.getBitcoin() * lastPrice) + " for " + days);
            moneyField.setValue(wallet.getMoney()+wallet.getBitcoin()*lastPrice);
            transactionsGrid.setItems(wallet.getTransactionHistory());
        }
    };

    public MainView(JsonService jsonService, CandleService candleService) {
        this.jsonService = jsonService;
        this.candleService = candleService;

        jsonButton.addClickListener(jsonButtonClicked);
        analizeButton.addClickListener(analizeButtonClicked);

    }

    private Configuration countConfiguration(LocalDateTime startDate, LocalDateTime endDate, Map<String, Candel> candels) {
//        System.out.println("buy;sellFailure;sellSucess;wallet;" + startDate.toString() + ';' + endDate.toString());
        List<Configuration> configurationList = new ArrayList<>();
        for (double buy = buyFromField.getValue(); buy >= buyToField.getValue(); buy -= stepField.getValue())
            for (double sellFailure = sellFailureFromField.getValue(); sellFailure >= sellFailureToField.getValue(); sellFailure -= stepField.getValue())
                for (double sellSucess = sellSucessToField.getValue(); sellSucess <= sellSucessToField.getValue(); sellSucess += stepField.getValue())
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

    private Map<String, Candel> createCandels(CandleResponse response) {
        Map<String, Candel> candels = new HashMap<>();
        for (List<Object> item : response.getItems()) {
            String timestamp = item.get(0).toString();
            double open = Double.parseDouble(((Map<String, String>)item.get(1)).get("o"));
            double close = Double.parseDouble(((Map<String, String>)item.get(1)).get("c"));
            double low = Double.parseDouble(((Map<String, String>)item.get(1)).get("l"));
            double high = Double.parseDouble(((Map<String, String>)item.get(1)).get("h"));
            double volume = Double.parseDouble(((Map<String, String>)item.get(1)).get("v"));
            candels.put(timestamp, new Candel(timestamp, resolutionBox.getValue().getSecs(), open, close, low, high, volume));
        }
        return candels;
    }

    private Map<String, Candel> createCandels(List<Candel> candelsList) {
        Map<String, Candel> candels = new HashMap<>();
        for (Candel candel : candelsList) {
            candels.put(candel.getTimestamp(), candel);
        }
        return candels;
    }

    private String dateToTimestamp(LocalDateTime date) {
        return date.toEpochSecond(ZoneOffset.of("+1"))+"000";
    }
}