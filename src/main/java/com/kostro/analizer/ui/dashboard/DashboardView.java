package com.kostro.analizer.ui.dashboard;

import com.kostro.analizer.db.model.CandelEntity;
import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.wallet.Candel;
import com.kostro.analizer.wallet.Resolution;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | Market Analizer")
public class DashboardView extends DashboardDesign {
    public static final String VIEW_NAME = "Dashboard";

    private CandleService candleService;
    private ConfigurationService configurationService;

    public DashboardView(CandleService candleService, ConfigurationService configurationService) {
        this.candleService = candleService;
        this.configurationService = configurationService;

        loadData();
    }

    private void loadData() {
        DataSeries btcSeries = new DataSeries("BTC-PLN");
        DataSeries bigFishSeries = new DataSeries("BigFish");
        for (Candel candel : candleService.find(LocalDateTime.of(2020, 4, 1, 0, 0, 0), LocalDateTime.now(), Resolution.ONE_MIN.getSecs())) {
            btcSeries.add(new DataSeriesItem(candel.getTime().toInstant(ZoneOffset.of("+2")), (Number)candel.getLow(), (Number)candel.getHigh()));
            if (candel.getVolume() > configurationService.getLimitFor(Resolution.ONE_MIN.getSecs()))
                bigFishSeries.add(new DataSeriesItem(candel.getTime().toInstant(ZoneOffset.of("+2")), (Number)candel.getLow(), (Number)candel.getHigh()));
        }
        Configuration conf = chart.getConfiguration();
        conf.setSeries(btcSeries, bigFishSeries);
    }
}
