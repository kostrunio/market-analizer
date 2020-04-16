package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.domain.CandleResponse;
import com.vaadin.tutorial.crm.service.JsonService;

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

        LocalDate date = LocalDate.of(2020, 01, 01);
        day: for (; date.isBefore(LocalDate.now().plusDays(1)); date = date.plusDays(1)) {
            CandleResponse response = jsonService.getCandles("BTC-PLN", 3600,
                    LocalDateTime.of(2020, date.getMonthValue(), date.getDayOfMonth(), 00, 00, 00),
                    LocalDateTime.of(2020, date.getMonthValue(), date.getDayOfMonth(), 23, 59, 59));
            for (int hour = 0; hour < 23; hour++) {
                if (response.getLow(hour+1) == null) continue day;
                if (response.getLow(hour + 1) < response.getClose(hour) * buy) {
                    Double price = response.getClose(hour) * buy;
                    String line = LocalDateTime.of(2020, date.getMonthValue(), date.getDayOfMonth(), hour + 1, 00, 00).toString() + ": " + price;
                    System.out.println(line);
                    bought:
                    newDay: for (LocalDate newDate = LocalDate.from(date); newDate.isBefore(LocalDate.now().plusDays(1)); newDate = newDate.plusDays(1)) {
                        CandleResponse secondResponse = jsonService.getCandles("BTC-PLN", 3600,
                                LocalDateTime.of(2020, newDate.getMonthValue(), newDate.getDayOfMonth(), 00, 00, 00),
                                LocalDateTime.of(2020, newDate.getMonthValue(), newDate.getDayOfMonth(), 23, 59, 59));
                        for (int newHour = newDate.getDayOfMonth() == date.getDayOfMonth() ? hour + 2 : 0; newHour < 23; newHour++) {
                            if (secondResponse.getLow(newHour) == null) continue newDay;
                            if (secondResponse.getLow(newHour) < price * sellFailure) {
                                String newLine = line + " -> " + LocalDateTime.of(2020, newDate.getMonthValue(), newDate.getDayOfMonth(), newHour, 00, 00).toString() + ": " + price * sellFailure + " STRATA";
                                System.out.println(newLine);
                                add(new Label(newLine));
                                result*=sellFailure;
                                break bought;
                            } else if (secondResponse.getHigh(newHour) > price * sellSucess) {
                                String newLine = line + " -> " + LocalDateTime.of(2020, newDate.getMonthValue(), newDate.getDayOfMonth(), newHour, 00, 00).toString() + ": " + price * sellSucess + " ZYSK";
                                System.out.println(newLine);
                                add(new Label(newLine));
                                result*=sellSucess;
                                break bought;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("result: " + result);
    }
}