package com.kostro.analizer.ui.configuration.btcusdt;

import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.ui.configuration.ConfigurationView;
import com.vaadin.flow.router.Route;

@Route(value="xrpusdt-configuration", layout = MainLayout.class)
public class XRPUSDTConfigurationView extends ConfigurationView {
    public static final String MARKET = "XRPUSDT";

    public XRPUSDTConfigurationView(ConfigurationService configurationService) {
        super(MARKET, configurationService);
    }

    public static String getViewName() {
        return MARKET + " " + VIEW_NAME;
    }

}
