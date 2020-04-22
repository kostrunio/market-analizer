package com.kostro.analizer.ui;

import com.kostro.analizer.ui.analizer.AnalizerView;
import com.kostro.analizer.ui.configuration.ConfigurationView;
import com.kostro.analizer.ui.verifier.VerifierView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

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
        RouterLink analizerLink = new RouterLink(AnalizerView.VIEW_NAME, AnalizerView.class);
        RouterLink verifierLink = new RouterLink(VerifierView.VIEW_NAME, VerifierView.class);
        RouterLink configurationLink = new RouterLink(ConfigurationView.VIEW_NAME, ConfigurationView.class);

        analizerLink.setHighlightCondition(HighlightConditions.sameLocation());
        configurationLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(analizerLink, verifierLink, configurationLink));
    }
}