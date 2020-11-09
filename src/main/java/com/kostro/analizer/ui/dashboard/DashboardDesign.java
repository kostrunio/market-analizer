package com.kostro.analizer.ui.dashboard;

import com.kostro.analizer.wallet.Resolution;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Title;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;

import java.time.LocalDate;
import java.time.LocalTime;

public class DashboardDesign extends VerticalLayout {

    protected DatePicker fromDatePicker = new DatePicker("from date");
    protected TimePicker fromTimePicker = new TimePicker("from time");
    protected DatePicker toDatePicker = new DatePicker("to date");
    protected TimePicker toTimePicker = new TimePicker("to time");
    protected MultiSelectListBox<String> dataSeriesList = new MultiSelectListBox<>();
    protected ComboBox<Resolution> resolutionBox = new ComboBox<>("resolution");
    protected Button showData = new Button("show data");

    protected Chart chart = new Chart(ChartType.COLUMNRANGE);

    public DashboardDesign() {
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(createFormLayout());
        add(getChart());
    }

    private Component createFormLayout() {
        FormLayout layout = new FormLayout();
        LocalDate startDate = LocalDate.of(2020, 3, 18);
        LocalTime startTime = LocalTime.of(0, 0, 0);
        fromDatePicker.setValue(startDate);
        fromTimePicker.setValue(startTime);
        toDatePicker.setValue(LocalDate.now());
        toTimePicker.setValue(LocalTime.now());
        dataSeriesList.setItems("BTC", "BigFish", "buy", "sell");
        dataSeriesList.select("BTC", "BigFish", "buy", "sell");
        resolutionBox.setItems(Resolution.ONE_MIN, Resolution.THREE_MINS, Resolution.FIVE_MINS, Resolution.FIFTEEN_MINS, Resolution.THIRTY_MINS,
                Resolution.ONE_HOUR, Resolution.TWO_HOURS, Resolution.FOUR_HOURS, Resolution.SIX_HOURS, Resolution.TWELWE_HOURS,
                Resolution.ONE_DAY, Resolution.THREE_DAYS, Resolution.ONE_WEEK);
        resolutionBox.setValue(Resolution.ONE_MIN);
        layout.add(fromDatePicker, fromTimePicker, toDatePicker, toTimePicker, dataSeriesList, resolutionBox, showData);
        return layout;
    }

    private Chart getChart() {
        chart.setSizeFull();
        Configuration conf = chart.getConfiguration();
        conf.setTitle(new Title("BigFishes prices"));

        Tooltip tooltip = conf.getTooltip();
        tooltip.setShared(true);
        tooltip.setValueSuffix("zł");

        chart.setTimeline(true);
        return chart;
    }
}