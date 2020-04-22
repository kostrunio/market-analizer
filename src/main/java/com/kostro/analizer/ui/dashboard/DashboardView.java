package com.kostro.analizer.ui.dashboard;

import com.kostro.analizer.db.model.CandelEntity;
import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.wallet.Candel;
import com.kostro.analizer.wallet.Resolution;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.Title;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | Market Analizer")
public class DashboardView extends VerticalLayout {
    public static final String VIEW_NAME = "Dashboard";

    private CandleService candleService;
    private ConfigurationService configurationService;

    public DashboardView(CandleService candleService, ConfigurationService configurationService) {
        this.candleService = candleService;
        this.configurationService = configurationService;
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(getBigFishes());
    }

    private Chart getBigFishes() {
        List<CandelEntity> candels = candleService.findAll();

        Chart chart = new Chart(ChartType.AREASPLINERANGE);
        Configuration conf = chart.getConfiguration();
        conf.setTitle(new Title("BigFishes prices"));

        Tooltip tooltip = conf.getTooltip();
        tooltip.setValueSuffix("zł");


        DataSeries dataSeries = new DataSeries("BTC-PLN");
        for (Candel candel : candleService.find(LocalDateTime.of(2020, 1, 1, 0, 0, 0), LocalDateTime.now(), Resolution.ONE_MIN.getSecs())) {
            dataSeries.add(new DataSeriesItem(candel.getTime().toInstant(ZoneOffset.of("+2")), (Number)candel.getLow(), (Number)candel.getHigh()));
        }
        conf.setSeries(dataSeries);

        chart.setTimeline(true);
        add(chart);
        return chart;
    }
}