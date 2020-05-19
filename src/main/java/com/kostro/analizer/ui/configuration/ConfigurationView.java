package com.kostro.analizer.ui.configuration;

import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
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
        configurationService.setMaxPeriod(Long.parseLong(maxPeriodField.getValue()));
        configurationService.setMarket(marketField.getValue());
        configurationService.setResolution(resolutionField.getValue());
    };

    public ConfigurationView(ConfigurationService configurationService) {
        this.configurationService = configurationService;

        maxPeriodField.setValue(configurationService.getMaxPeriod()+"");
        marketField.setValue(configurationService.getMarket());
        resolutionField.setValue(configurationService.getResolution().name());

        saveButton.addClickListener(saveClicked);
    }
}
