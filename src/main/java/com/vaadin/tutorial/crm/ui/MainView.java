package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.domain.Ticker;
import com.vaadin.tutorial.crm.service.JsonService;

@Route("")
public class MainView extends VerticalLayout {

    private JsonService jsonService;
    private Grid<Ticker> grid = new Grid<>(Ticker.class);

    public MainView(JsonService jsonService) {
        this.jsonService = jsonService;

        setSizeFull();
        configureGrid();

        add(grid);
        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("market.code");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(jsonService.getMarkets("").getItems().values());
    }
}