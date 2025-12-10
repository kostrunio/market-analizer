package com.kostro.analizer.ui.dashboard.btcusdc;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.ui.dashboard.DashboardView;
import com.kostro.analizer.utils.CandleOperation;
import com.vaadin.flow.router.Route;

@Route(value = "btcusdc-dashboard", layout = MainLayout.class)
public class BTCUSDCDashboardView extends DashboardView {
    public static final String MARKET = "BTCUSDC";

    public BTCUSDCDashboardView(CandleService candleService, ConfigurationService configurationService, CandleOperation candleOperation) {
        super(MARKET, candleService, configurationService, candleOperation);
    }

    public static String getViewName() {
        return MARKET + " " + VIEW_NAME;
    }
}
