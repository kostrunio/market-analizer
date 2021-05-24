package com.kostro.analizer.ui.dashboard;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.db.service.QueryParams;
import com.kostro.analizer.ui.configuration.BTCUSDTConfigurationView;
import com.kostro.analizer.utils.CandleOperation;
import com.kostro.analizer.wallet.Candle;
import com.kostro.analizer.wallet.Resolution;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.router.PageTitle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@PageTitle("Dashboard | Market Analizer")
public class DashboardView extends DashboardDesign {

    private CandleService candleService;
    private ConfigurationService configurationService;
    private CandleOperation candleOperation;

    ComponentEventListener<ClickEvent<Button>> showDataClicked = e -> loadData(BTCUSDTConfigurationView.MARKET);

    @Autowired
    public DashboardView(String market, CandleService candleService, ConfigurationService configurationService, CandleOperation candleOperation) {
        super(market);
        this.candleService = candleService;
        this.configurationService = configurationService;
        this.candleOperation = candleOperation;
        showData.addClickListener(showDataClicked);
    }

    private void loadData(String market) {
        configurationService.setSendLevel(market, false);
        configurationService.setSendVolume(market, false);

        DataSeries btcSeries = new DataSeries("BTC-PLN");
        DataSeries bigFishSeries = new DataSeries("HugeVolume");
        QueryParams params = new QueryParams.Builder(market, LocalDateTime.of(fromDatePicker.getValue(), fromTimePicker.getValue()), LocalDateTime.of(toDatePicker.getValue(), toTimePicker.getValue()), Resolution.of(resolutionBox.getValue().getSecs())).build();
        for (Candle candle : candleService.find(params)) {
            boolean inserter = false;
            DataSeriesItem data = new DataSeriesItem(candle.getTime().toInstant(ZoneOffset.UTC), candle.getLow(), candle.getHigh());
            if (dataSeriesList.getSelectedItems().contains("HugeVolume"))
                if (candleOperation.checkHugeVolume(market, candle, false)) {
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

        configurationService.setSendLevel(market, true);
        configurationService.setSendVolume(market, true);
    }
}
