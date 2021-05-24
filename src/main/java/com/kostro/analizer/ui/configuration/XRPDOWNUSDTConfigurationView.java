package com.kostro.analizer.ui.configuration;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.vaadin.flow.router.Route;

@Route(value="xrpdownusdt-configuration", layout = MainLayout.class)
public class XRPDOWNUSDTConfigurationView extends ConfigurationView {
    public static final String MARKET = "XRPDOWNUSDT";

    public XRPDOWNUSDTConfigurationView(ConfigurationService configurationService, CandleService candleService) {
        super(MARKET, configurationService, candleService);
    }

    public static String getViewName() {
        return MARKET + " " + VIEW_NAME;
    }

}
