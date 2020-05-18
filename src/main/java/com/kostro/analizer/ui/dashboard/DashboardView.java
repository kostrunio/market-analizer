package com.kostro.analizer.ui.dashboard;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.utils.CandelOperation;
import com.kostro.analizer.utils.SendEmail;
import com.kostro.analizer.wallet.Candel;
import com.kostro.analizer.wallet.Resolution;
import com.kostro.analizer.wallet.Wallet;
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
import java.util.ArrayList;
import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | Market Analizer")
public class DashboardView extends DashboardDesign {
    public static final String VIEW_NAME = "Dashboard";
    private static final Logger log = LoggerFactory.getLogger(DashboardView.class);

    private CandleService candleService;
    private CandelOperation candelOperation;

    ComponentEventListener<ClickEvent<Button>> showDataClicked = e -> loadData();

    public DashboardView(CandleService candleService, ConfigurationService configurationService) {
        this.candleService = candleService;
        candelOperation = new CandelOperation(candleService, configurationService, false);
        showData.addClickListener(showDataClicked);
    }

    private void loadData() {
        candelOperation.resetWallet();
        DataSeries btcSeries = new DataSeries("BTC-PLN");
        DataSeries bigFishSeries = new DataSeries("BigFish");
        DataSeries buySeries = new DataSeries("buy");
        DataSeries sellSeries = new DataSeries("sell");
        for (Candel candel : candleService.find(LocalDateTime.of(fromDatePicker.getValue(), fromTimePicker.getValue()), LocalDateTime.of(toDatePicker.getValue(), toTimePicker.getValue()), resolutionBox.getValue().getSecs())) {
            boolean inserter = false;
            DataSeriesItem data = new DataSeriesItem(candel.getTime().toInstant(ZoneOffset.UTC), (Number) candel.getLow(), (Number) candel.getHigh());
            if (dataSeriesList.getSelectedItems().contains("BigFish"))
            if (candelOperation.checkHugeVolume(candel)) {
                bigFishSeries.add(data);
                inserter = true;
            }
            if (dataSeriesList.getSelectedItems().contains("buy")) {
                candelOperation.checkIfBuy(candel);
                if (candelOperation.checkIfBought(candel)) {
                    buySeries.add(data);
                    inserter = true;
                }
            }
            if (dataSeriesList.getSelectedItems().contains("sell")) {
                candelOperation.checkIfSell(candel);
                if (candelOperation.checkIfSold(candel)) {
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

        log.info("\nmoney: {}\nbtc: {}", candelOperation.getWallet().getMoney(), candelOperation.getWallet().getBitcoin());
        candelOperation.getWallet().getTransactionHistory().stream().forEach(t -> log.info(t.toString()));
    }
}
