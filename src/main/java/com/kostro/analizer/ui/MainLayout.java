package com.kostro.analizer.ui;

import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.ui.configuration.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route(value="")
@PageTitle("Market Analizer")
public class MainLayout extends AppLayout {

    ConfigurationService configurationService;

    public MainLayout(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Market Analizer");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        header.setWidth("100%");
        header.addClassName("header");

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink btcusdc = new RouterLink(BTCUSDCConfigurationView.getViewName() , BTCUSDCConfigurationView.class);
        btcusdc.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink paxgusdt = new RouterLink(PAXGUSDTConfigurationView.getViewName() , PAXGUSDTConfigurationView.class);
        paxgusdt.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(btcusdc, paxgusdt));
    }
}