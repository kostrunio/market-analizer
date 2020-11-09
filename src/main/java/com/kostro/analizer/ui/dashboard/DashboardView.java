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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | Market Analizer")
public class DashboardView extends DashboardDesign {
    public static final String VIEW_NAME = "Dashboard";
    private static final Logger log = LoggerFactory.getLogger(DashboardView.class);

    private CandleService candleService;
    private CandleOperation candleOperation;

    ComponentEventListener<ClickEvent<Button>> showDataClicked = e -> loadData();

    public DashboardView(CandleService candleService, ConfigurationService configurationService) {
        this.candleService = candleService;
        candleOperation = new CandleOperation(candleService, configurationService, false);
        showData.addClickListener(showDataClicked);
    }

    private void loadData() {
        candleOperation.resetWallet();
        DataSeries btcSeries = new DataSeries("BTC-PLN");
        DataSeries bigFishSeries = new DataSeries("BigFish");
        DataSeries buySeries = new DataSeries("buy");
        DataSeries sellSeries = new DataSeries("sell");
        for (Candle candle : candleService.find(LocalDateTime.of(fromDatePicker.getValue(), fromTimePicker.getValue()), LocalDateTime.of(toDatePicker.getValue(), toTimePicker.getValue()), resolutionBox.getValue().getSecs())) {
            boolean inserter = false;
            DataSeriesItem data = new DataSeriesItem(candle.getTime().toInstant(ZoneOffset.UTC), (Number) candle.getLow(), (Number) candle.getHigh());
            if (dataSeriesList.getSelectedItems().contains("BigFish"))
            if (candleOperation.checkHugeVolume(candle)) {
                bigFishSeries.add(data);
                inserter = true;
            }
            if (dataSeriesList.getSelectedItems().contains("buy")) {
                candleOperation.checkIfBuy(candle);
                if (candleOperation.checkIfBought(candle)) {
                    buySeries.add(data);
                    inserter = true;
                }
            }
            if (dataSeriesList.getSelectedItems().contains("sell")) {
                candleOperation.checkIfSell(candle);
                if (candleOperation.checkIfSold(candle)) {
                    sellSeries.add(data);
                    inserter = true;
                }
            }
            if (dataSeriesList.getSelectedItems().contains("BTC"))
            if (!inserter) btcSeries.add(data);
        }
        Configuration conf = chart.getConfiguration();
        conf.setSeries(btcSeries, bigFishSeries, buySeries, sellSeries);
        conf.getTooltip().setShared(true);
        chart.drawChart();

        log.info("\nmoney: {}\nbtc: {}", candleOperation.getWallet().getMoney(), candleOperation.getWallet().getBitcoin());
        candleOperation.getWallet().getTransactionHistory().stream().forEach(t -> log.info(t.toString()));
    }
}
