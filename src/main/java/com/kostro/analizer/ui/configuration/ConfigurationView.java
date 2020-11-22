package com.kostro.analizer.ui.configuration;

import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.wallet.Resolution;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value="configuration", layout = MainLayout.class)
@PageTitle("Configuration | Market Analizer")
public class ConfigurationView extends ConfiguraionDesign {
    public static String VIEW_NAME = "Configuration";

    private ConfigurationService configurationService;

    ComponentEventListener<ClickEvent<Button>> saveClicked = e -> {
        configurationService.setMaxPeriod(maxPeriodField.getValue().intValue());
        configurationService.setMarket(marketField.getValue());
        configurationService.setResolution(resolutionField.getValue().toString());
        configurationService.setSendVolume(sendVolume.getValue());
        configurationService.setRunScheduler(runSheduler.getValue());
        configurationService.setStopBuying(stopBuying.getValue());
        configurationService.setLimit60(limit60.getValue().intValue());
        configurationService.setLastLevel(lastLevel.getValue().intValue());
        configurationService.setLevelStep(levelStep.getValue().intValue());
        configurationService.setSendLevel(sendLevel.getValue());
    };

    public ConfigurationView(ConfigurationService configurationService) {
        this.configurationService = configurationService;

        maxPeriodField.setValue(configurationService.getMaxPeriod().doubleValue());
        marketField.setValue(configurationService.getMarket());
        resolutionField.setItems(Resolution.getResolutions());
        resolutionField.setValue(configurationService.getResolution());
        sendVolume.setValue(configurationService.isSendVolume());
        runSheduler.setValue(configurationService.isRunScheduler());
        stopBuying.setValue(configurationService.isStopBuying());
        limit60.setValue(configurationService.getLimit60().doubleValue());
        lastLevel.setValue(configurationService.getLastLevel().doubleValue());
        levelStep.setValue(configurationService.getLevelStep().doubleValue());
        sendLevel.setValue(configurationService.isSendLevel());

        saveButton.addClickListener(saveClicked);
    }
}
