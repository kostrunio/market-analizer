package com.kostro.analizer.ui.dashboard;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.utils.CandleOperation;
import com.kostro.analizer.wallet.Candle;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | Market Analizer")
public class DashboardView extends DashboardDesign {
    public static final String VIEW_NAME = "Dashboard";

    private CandleService candleService;
    private CandleOperation candleOperation;

    ComponentEventListener<ClickEvent<Button>> showDataClicked = e -> loadData();

    public DashboardView(CandleService candleService, ConfigurationService configurationService) {
        this.candleService = candleService;
        candleOperation = new CandleOperation(candleService, configurationService, false);
        showData.addClickListener(showDataClicked);
    }

    private void loadData() {
        DataSeries btcSeries = new DataSeries("BTC-PLN");
        DataSeries bigFishSeries = new DataSeries("HugeVolume");
        for (Candle candle : candleService.find(LocalDateTime.of(fromDatePicker.getValue(), fromTimePicker.getValue()), LocalDateTime.of(toDatePicker.getValue(), toTimePicker.getValue()), resolutionBox.getValue().getSecs())) {
            boolean inserter = false;
            DataSeriesItem data = new DataSeriesItem(candle.getTime().toInstant(ZoneOffset.UTC), candle.getLow(), candle.getHigh());
            if (dataSeriesList.getSelectedItems().contains("HugeVolume"))
                if (candleOperation.checkHugeVolume(candle, false)) {
                    bigFishSeries.add(data);
                    inserter = true;
                }
            if (dataSeriesList.getSelectedItems().contains("BTC"))
                if (!inserter) btcSeries.add(data);
        }
        Configuration conf = chart.getConfiguration();
        conf.setSeries(btcSeries, bigFishSeries);
        conf.getTooltip().setShared(true);
        chart.drawChart();

    }
}
