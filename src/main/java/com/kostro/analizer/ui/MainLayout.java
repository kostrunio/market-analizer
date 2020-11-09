package com.kostro.analizer.ui;

import com.kostro.analizer.ui.configuration.ConfigurationView;
import com.kostro.analizer.ui.dashboard.DashboardView;
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

    public MainLayout() {
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
        RouterLink dashboardLink = new RouterLink(DashboardView.VIEW_NAME, DashboardView.class);
        RouterLink configurationLink = new RouterLink(ConfigurationView.VIEW_NAME, ConfigurationView.class);

        configurationLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(dashboardLink, configurationLink));
    }
}