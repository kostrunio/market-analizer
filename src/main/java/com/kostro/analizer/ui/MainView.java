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

    private int offerLong;
    private int startDay;
    private int periodLong;

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
        List<Configuration> configurationList = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.of(fromDatePicker.getValue(), LocalTime.of(0, 0, 0, 0));
        LocalDateTime endDate = LocalDateTime.of(toDatePicker.getValue(), LocalTime.of(23, 59, 59, 999));
        List<Candel> candelsList = candleService.find(startDate, endDate, resolutionBox.getValue().getSecs());
        Map<LocalDateTime, Candel> candels = createCandels(candelsList);

        for (offerLong = hoursFromField.getValue().intValue(); offerLong <= hoursToField.getValue().intValue(); offerLong++) {
            for (startDay = startDayNumberFromField.getValue().intValue(); startDay <= startDayNumberToField.getValue().intValue(); startDay++) {
                for (periodLong = daysNumberFromField.getValue().intValue(); periodLong <= daysNumberToField.getValue().intValue(); periodLong++) {
                    startDate = LocalDateTime.of(fromDatePicker.getValue(), LocalTime.of(0, 0, 0, 0)).plusDays(startDay);
                    Wallet wallet = new Wallet(moneyField.getValue(), 0.9953);
                    endDate = LocalDateTime.of(toDatePicker.getValue(), LocalTime.of(23, 59, 59, 999)).isAfter(startDate.plusDays(periodLong).minusSeconds(1)) ? startDate.plusDays(periodLong).minusSeconds(1) : LocalDateTime.of(toDatePicker.getValue(), LocalTime.of(23, 59, 59, 999));
                    do {
                        Configuration configuration = countConfiguration(startDate, endDate, candels);
                        startDate = startDate.plusDays(periodLong);
                        endDate = LocalDateTime.of(toDatePicker.getValue(), LocalTime.of(23, 59, 59, 999)).isAfter(startDate.plusDays(periodLong).minusSeconds(1)) ? startDate.plusDays(periodLong).minusSeconds(1) : LocalDateTime.of(toDatePicker.getValue(), LocalTime.of(23, 59, 59, 999));
                        if (configuration.getResult() < 1010) continue;
                        System.out.println(configuration);
                        configurationList.add(configuration);
                        count(wallet, startDate, endDate, candels, configuration.getBuy(), configuration.getSellFailure(), configuration.getSellSuccess(), false, wrongField.getValue().intValue());
                    } while (startDate.plusDays(periodLong).isBefore(LocalDateTime.of(toDatePicker.getValue(), LocalTime.of(23, 59, 59, 999))));
                    System.out.println("RESULT:" + (wallet.getMoney() + wallet.getBitcoin() * lastPrice) + " for " + periodLong);
//                    moneyField.setValue(wallet.getMoney() + wallet.getBitcoin() * lastPrice);
                    transactionsGrid.setItems(wallet.getTransactionHistory());
                }
            }
        }
        configurationsGrid.setItems(configurationList);
    };

    public MainView(JsonService jsonService, CandleService candleService) {
        this.jsonService = jsonService;
        this.candleService = candleService;

        jsonButton.addClickListener(jsonButtonClicked);
        analizeButton.addClickListener(analizeButtonClicked);

    }

    private Configuration countConfiguration(LocalDateTime startDate, LocalDateTime endDate, Map<LocalDateTime, Candel> candels) {
        List<Configuration> configurationList = new ArrayList<>();
        for (double buy = buyFromField.getValue(); buy >= buyToField.getValue(); buy -= stepField.getValue())
            for (double sellFailure = sellFailureFromField.getValue(); sellFailure >= sellFailureToField.getValue(); sellFailure -= stepField.getValue())
                for (double sellSucess = sellSuccessFromField.getValue(); sellSucess <= sellSucessToField.getValue(); sellSucess += stepField.getValue())
                    configurationList.add(count(new Wallet(1000, 0.9953), startDate, endDate, candels, buy, sellFailure, sellSucess, false, -1));
        return findConfiguration(configurationList);
    }

    private Configuration findConfiguration(List<Configuration> configurationList) {
        Configuration toReturn = new Configuration(offerLong, startDay, periodLong, 0, 0, 0, 0, LocalDateTime.now(), LocalDateTime.now());
        for (Configuration configuration : configurationList)
            if (configuration.getResult() > toReturn.getResult())
                toReturn = configuration;
            return toReturn;
    }

    private Configuration count(Wallet wallet, LocalDateTime startDate, LocalDateTime endDate, Map<LocalDateTime, Candel> candels, double buy, double sellFailure, double sellSucess, boolean withDetails, int wrongLimit) {
        lastPrice = 0;
        int wrong = 0;
        hour:for (LocalDateTime date = LocalDateTime.from(startDate); date.isBefore(endDate); date = date.plusHours(1)) {
            if (candels.get(date) == null || candels.get(date.plusHours(offerLong)) == null) continue hour;
            lastPrice = candels.get(date.plusHours(offerLong)).getLow();
            if (wallet.hasMoney()) {
                if (wrongLimit > 0 && wrong > wrongLimit) break;
                if (candels.get(date.plusHours(offerLong)).getLow() < candels.get(date).getHigh() * buy) {
                    wallet.buy(date.plusHours(offerLong), candels.get(date).getHigh() * buy);
                }
            } else {
                if (candels.get(date.plusHours(offerLong)).getLow() < wallet.getPrice() * sellFailure) {
                    wallet.sell(date.plusHours(offerLong), wallet.getPrice() * sellFailure);
                    wrong++;
                } else if (candels.get(date.plusHours(offerLong)).getHigh() > wallet.getPrice() * sellSucess) {
                    wallet.sell(date.plusHours(offerLong), wallet.getPrice() * sellSucess);

                }
            }
        }
        if (withDetails) {
            System.out.println(buy + ";" + sellFailure + ";" + sellSucess + ";" + (wallet.getMoney() + wallet.getBitcoin()*lastPrice));
            for (Transaction transaction : wallet.getTransactionHistory())
                System.out.println(transaction);
        }
        return new Configuration(offerLong, startDay, periodLong, buy, sellFailure, sellSucess, wallet.getMoney() + wallet.getBitcoin()*lastPrice, startDate, endDate);
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
            candels.put(timestamp, new Candel(LocalDateTime.ofEpochSecond(Long.parseLong(timestamp.substring(0, 10)), Integer.parseInt(timestamp.substring(11)), ZoneOffset.of("+2")), resolutionBox.getValue().getSecs(), open, close, low, high, volume));
        }
        return candels;
    }

    private Map<LocalDateTime, Candel> createCandels(List<Candel> candelsList) {
        Map<LocalDateTime, Candel> candels = new HashMap<>();
        for (Candel candel : candelsList) {
            candels.put(candel.getTime(), candel);
        }
        return candels;
    }
}