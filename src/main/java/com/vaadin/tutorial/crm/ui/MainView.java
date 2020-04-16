package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.domain.CandleResponse;
import com.vaadin.tutorial.crm.service.JsonService;

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
        CandleResponse response = jsonService.getCandles("BTC-PLN", 3600, LocalDateTime.of(2020, 01,01,00,00, 00), LocalDateTime.of(2020, 01,01,23,59, 59));
        for (int i = 0; i < 23; i++)
            if (response.getLow(i+1) <  response.getClose(i)*0.95)
                add(new Label(LocalDateTime.of(2020, 01, 01, i+1, 00, 00).toString()));
    }
}