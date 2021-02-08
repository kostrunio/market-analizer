package com.kostro.analizer.ui.configuration;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.wallet.Resolution;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Configuration | Market Analizer")
public class ConfigurationView extends ConfiguraionDesign {

    private ConfigurationService configurationService;
    private CandleService candleService;

    ComponentEventListener<ClickEvent<Button>> saveClicked = e -> {
        configurationService.setMaxPeriod(market, maxPeriodField.getValue().intValue());
        configurationService.setResolution(market, resolutionField.getValue().toString());
        configurationService.setSendVolume(market, sendVolume.getValue());
        configurationService.setRunScheduler(market, runSheduler.getValue());
        configurationService.setStopBuying(market, stopBuying.getValue());
        configurationService.setLimit60(market, limit60.getValue().intValue());
        configurationService.setLastLevel(market, lastLevel.getValue().doubleValue());
        configurationService.setLevelStep(market, levelStep.getValue().doubleValue());
        configurationService.setSendLevel(market, sendLevel.getValue());
        configurationService.setMaxLevel(market, maxLevel.getValue());
    };

    public ConfigurationView(String market, ConfigurationService configurationService, CandleService candleService) {
        super(market);
        this.configurationService = configurationService;
        this.candleService = candleService;

        lastCandleField.setValue(candleService.getLastCandle(market));
        maxPeriodField.setValue(configurationService.getMaxPeriod(market).doubleValue());
        resolutionField.setItems(Resolution.getResolutions());
        resolutionField.setValue(configurationService.getResolution(market));
        sendVolume.setValue(configurationService.isSendVolume(market));
        runSheduler.setValue(configurationService.isRunScheduler(market));
        stopBuying.setValue(configurationService.isStopBuying(market));
        limit60.setValue(configurationService.getLimit60(market).doubleValue());
        lastLevel.setValue(configurationService.getLastLevel(market).doubleValue());
        levelStep.setValue(configurationService.getLevelStep(market).doubleValue());
        sendLevel.setValue(configurationService.isSendLevel(market));
        maxLevel.setValue(configurationService.getMaxLevel(market).doubleValue());

        saveButton.addClickListener(saveClicked);
    }

}
