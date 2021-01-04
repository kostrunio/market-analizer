package com.kostro.analizer.ui.dashboard.btcusdt;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.ui.dashboard.DashboardView;
import com.kostro.analizer.utils.CandleOperation;
import com.vaadin.flow.router.Route;

@Route(value = "btcusdt-dashboard", layout = MainLayout.class)
public class BTCUSDTDashboardView extends DashboardView {
    public static final String MARKET = "BTCUSDT";

    public BTCUSDTDashboardView(String market, CandleService candleService, ConfigurationService configurationService, CandleOperation candleOperation) {
        super(MARKET, candleService, configurationService, candleOperation);
    }

    public static String getViewName() {
        return MARKET + " " + VIEW_NAME;
    }
}
