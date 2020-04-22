package com.kostro.analizer.ui.verifier;

import com.kostro.analizer.wallet.Candel;
import com.kostro.analizer.wallet.Resolution;
import com.kostro.analizer.wallet.Transaction;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;

import java.time.LocalDate;
import java.time.LocalTime;

public class VerifierDesign extends VerticalLayout {

    protected DatePicker fromDatePicker = new DatePicker("from date");
    protected TimePicker fromTimePicker = new TimePicker("from time");
    protected DatePicker toDatePicker = new DatePicker("to date");
    protected TimePicker toTimePicker = new TimePicker("to time");
    protected ComboBox<Resolution> resolutionBox = new ComboBox<>("resolution");
    protected Button jsonButton = new Button("get data from Market");

    protected ComboBox<Resolution> candelResolutionBox = new ComboBox<>("candel resolution");
    protected Grid<Candel> candelGrid = new Grid<>(Candel.class);

    public VerifierDesign() {
        setSizeFull();
        add(createJsonLayout());
        add(createGridLayout());
    }

    private Component createJsonLayout() {
        FormLayout layout = new FormLayout();
        LocalDate startDate = LocalDate.of(2020, 01, 01);
        LocalTime startTime = LocalTime.of(0, 0, 0);
        fromDatePicker.setValue(startDate);
        fromTimePicker.setValue(startTime);
        toDatePicker.setValue(startDate);
        toTimePicker.setValue(startTime.plusHours(1));
        resolutionBox.setItems(Resolution.ONE_MIN, Resolution.THREE_MINS, Resolution.FIVE_MINS, Resolution.FIFTEEN_MINS, Resolution.THIRTY_MINS,
                Resolution.ONE_HOUR, Resolution.TWO_HOURS, Resolution.FOUR_HOURS, Resolution.SIX_HOURS, Resolution.TWELWE_HOURS,
                Resolution.ONE_DAY, Resolution.THREE_DAYS, Resolution.ONE_WEEK);
        resolutionBox.setValue(Resolution.ONE_MIN);
        layout.add(fromDatePicker, fromTimePicker, toDatePicker, toTimePicker, resolutionBox, jsonButton);
        return layout;
    }

    private Component createGridLayout() {
        VerticalLayout layout = new VerticalLayout();
        candelResolutionBox.setItems(Resolution.ONE_MIN, Resolution.THREE_MINS, Resolution.FIVE_MINS, Resolution.FIFTEEN_MINS, Resolution.THIRTY_MINS,
                Resolution.ONE_HOUR, Resolution.TWO_HOURS, Resolution.FOUR_HOURS, Resolution.SIX_HOURS, Resolution.TWELWE_HOURS,
                Resolution.ONE_DAY, Resolution.THREE_DAYS, Resolution.ONE_WEEK);
        candelResolutionBox.setValue(Resolution.ONE_MIN);
        candelGrid.setColumns("time", "resolution", "open", "close", "low", "high", "volume");
        candelGrid.addColumn(candel -> candel.getHigh()-candel.getLow()).setHeader("change");
        layout.add(candelResolutionBox, candelGrid);
        return layout;
    }
}
