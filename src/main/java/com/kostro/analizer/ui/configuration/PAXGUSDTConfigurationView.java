package com.kostro.analizer.ui.configuration;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.vaadin.flow.router.Route;

@Route(value="paxgusdc-configuration", layout = MainLayout.class)
public class PAXGUSDTConfigurationView extends ConfigurationView {
    public static final String MARKET = "PAXGUSDT";

    public PAXGUSDTConfigurationView(ConfigurationService configurationService, CandleService candleService) {
        super(MARKET, configurationService, candleService);
    }

    public static String getViewName() {
        return MARKET + "\n" + VIEW_NAME;
    }

}
