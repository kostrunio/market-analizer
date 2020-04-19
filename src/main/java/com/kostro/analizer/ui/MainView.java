package com.kostro.analizer.ui;

import com.kostro.analizer.json.domain.candle.CandleResponse;
import com.kostro.analizer.json.service.JsonService;
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
        double result = 1;
        double buy = 0.95;
        double sellFailure = 0.9;
        double sellSucess = 1.1;

        boolean bought = false;
        Double price = null;
        String line = null;

        LocalDate date = LocalDate.of(2020, 03, 18);
        day: for (; date.isBefore(LocalDate.now().plusDays(1)); date = date.plusDays(1)) {
            CandleResponse response = jsonService.getCandles("BTC-PLN", 3600,
                    LocalDateTime.of(2020, date.getMonthValue(), date.getDayOfMonth(), 00, 00, 00),
                    LocalDateTime.of(2020, date.getMonthValue(), date.getDayOfMonth(), 23, 59, 59));
            for (int hour = 0; hour < 23; hour++) {
                if (response.getLow(hour + 1) == null) continue day;
                if (!bought) {
                    if (response.getLow(hour + 1) < response.getHigh(hour) * buy) {
                        price = response.getClose(hour) * buy;
                        line = LocalDateTime.of(2020, date.getMonthValue(), date.getDayOfMonth(), hour + 1, 00, 00).toString() + ": " + price;
                        System.out.println(line);
                        bought = true;
                    }
                } else {
                    if (response.getLow(hour + 1) < price * sellFailure) {
                        String newLine = line + " -> " + LocalDateTime.of(2020, date.getMonthValue(), date.getDayOfMonth(), hour + 1, 00, 00).toString() + ": " + price * sellFailure + " STRATA";
                        System.out.println(newLine);
                        add(new Label(newLine));
                        result *= sellFailure;
                        bought = false;
                    } else if (response.getHigh(hour + 1) > price * sellSucess) {
                        String newLine = line + " -> " + LocalDateTime.of(2020, date.getMonthValue(), date.getDayOfMonth(), hour + 1, 00, 00).toString() + ": " + price * sellSucess + " ZYSK";
                        System.out.println(newLine);
                        add(new Label(newLine));
                        result *= sellSucess;
                        bought = false;
                    }
                }
            }
        }
        System.out.println("result: " + result);
    }
}