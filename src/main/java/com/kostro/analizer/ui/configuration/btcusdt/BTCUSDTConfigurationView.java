package com.kostro.analizer.ui.configuration.btcusdt;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.ui.configuration.ConfigurationView;
import com.vaadin.flow.router.Route;

@Route(value="btcusdt-configuration", layout = MainLayout.class)
public class BTCUSDTConfigurationView extends ConfigurationView {
    public static final String MARKET = "BTCUSDT";

    public BTCUSDTConfigurationView(ConfigurationService configurationService, CandleService candleService) {
        super(MARKET, configurationService, candleService);
    }

    public static String getViewName() {
        return MARKET + "\n" + VIEW_NAME;
    }

}
