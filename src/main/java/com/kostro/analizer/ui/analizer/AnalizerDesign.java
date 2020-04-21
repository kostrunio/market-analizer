package com.kostro.analizer.ui.analizer;

import com.kostro.analizer.wallet.Configuration;
import com.kostro.analizer.wallet.Resolution;
import com.kostro.analizer.wallet.Transaction;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;

import java.time.LocalDate;

public class AnalizerDesign extends VerticalLayout {

    protected DatePicker fromDatePicker = new DatePicker("from date");
    protected DatePicker toDatePicker = new DatePicker("to date");
    protected ComboBox<Resolution> resolutionBox = new ComboBox<>("resolution");
    protected Button jsonButton = new Button("get data from Market");

    protected NumberField buyFromField = new NumberField("buy from");
    protected NumberField buyToField = new NumberField("buy to");
    protected NumberField sellFailureFromField = new NumberField("sell with failure from");
    protected NumberField sellFailureToField = new NumberField("sell with failure to");
    protected NumberField sellSuccessFromField = new NumberField("sell with sucess from");
    protected NumberField sellSucessToField = new NumberField("sell with sucess to");
    protected NumberField stepField = new NumberField("minimal change");
    protected NumberField wrongField = new NumberField("wrong decisions");
    protected NumberField startDayNumberFromField = new NumberField("day start from");
    protected NumberField startDayNumberToField = new NumberField("day start to");
    protected NumberField daysNumberFromField = new NumberField("days number from");
    protected NumberField daysNumberToField = new NumberField("days number to");
    protected NumberField hoursFromField = new NumberField("hours to analize from");
    protected NumberField hoursToField = new NumberField("hours to analize to");
    protected Button analizeButton = new Button("analize");

    protected NumberField moneyField = new NumberField("money");

    protected Grid<Configuration> configurationsGrid = new Grid<>(Configuration.class);
    protected Grid<Transaction> transactionsGrid = new Grid<>(Transaction.class);

    public AnalizerDesign() {
        setSizeFull();
        add(createJsonLayout());
        add(createAnalizeLayout());
        add(createWalletLayout());
        add(createGridLayout());
    }

    private Component createJsonLayout() {
        FormLayout layout = new FormLayout();
        LocalDate startDate = LocalDate.of(2020, 01, 01);
        fromDatePicker.setValue(startDate);
        toDatePicker.setValue(startDate.plusMonths(1).minusDays(1));
        resolutionBox.setItems(Resolution.ONE_MIN, Resolution.THREE_MINS, Resolution.FIVE_MINS, Resolution.FIFTEEN_MINS, Resolution.THIRTY_MINS,
                Resolution.ONE_HOUR, Resolution.TWO_HOURS, Resolution.FOUR_HOURS, Resolution.SIX_HOURS, Resolution.TWELWE_HOURS,
                Resolution.ONE_DAY, Resolution.THREE_DAYS, Resolution.ONE_WEEK);
        resolutionBox.setValue(Resolution.ONE_HOUR);
        layout.add(fromDatePicker, toDatePicker, resolutionBox, jsonButton);
        return layout;
    }

    private Component createAnalizeLayout() {
        FormLayout layout = new FormLayout();
        buyFromField.setValue(0.99);
        buyToField.setValue(0.90);
        layout.add(buyFromField, buyToField);
        sellFailureFromField.setValue(0.99);
        sellFailureToField.setValue(0.90);
        layout.add(sellFailureFromField, sellFailureToField);
        sellSuccessFromField.setValue(1.02);
        sellSucessToField.setValue(1.10);
        layout.add(sellSuccessFromField, sellSucessToField);
        stepField.setValue(0.01);
        wrongField.setValue(-1d);
        layout.add(stepField, wrongField);
        startDayNumberFromField.setValue(0d);
        startDayNumberToField.setValue(0d);
        layout.add(startDayNumberFromField, startDayNumberToField);
        daysNumberFromField.setValue(2d);
        daysNumberToField.setValue(2d);
        layout.add(daysNumberFromField, daysNumberToField);
        hoursFromField.setValue(1d);
        hoursToField.setValue(1d);
        layout.add(hoursFromField, hoursToField);
        layout.add(analizeButton);
        return layout;
    }

    private Component createWalletLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        moneyField.setValue(1000d);
        layout.add(moneyField);
        return layout;
    }

    private Component createGridLayout() {
        VerticalLayout layout = new VerticalLayout();
        configurationsGrid.setColumns("offerLong", "startDay", "periodLong", "buy", "sellFailure", "sellSuccess", "result", "startDate", "endDate");
        transactionsGrid.setColumns("buyDate", "buyPrice", "sellDate", "sellPrice", "success");
        layout.add(configurationsGrid, transactionsGrid);
        return layout;
    }
}
