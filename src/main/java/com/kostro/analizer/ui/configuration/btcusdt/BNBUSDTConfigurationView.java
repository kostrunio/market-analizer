package com.kostro.analizer.ui.configuration.btcusdt;

import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.ui.configuration.ConfigurationView;
import com.vaadin.flow.router.Route;

@Route(value="bnbusdt-configuration", layout = MainLayout.class)
public class BNBUSDTConfigurationView extends ConfigurationView {
    public static final String MARKET = "BNBUSDT";

    public BNBUSDTConfigurationView(ConfigurationService configurationService) {
        super(MARKET, configurationService);
    }

    public static String getViewName() {
        return MARKET + " " + VIEW_NAME;
    }

}
