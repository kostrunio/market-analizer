package com.kostro.analizer.ui.configuration;

import com.kostro.analizer.ui.MainLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value="configuration", layout = MainLayout.class)
@PageTitle("Configuration | Market Analizer")
public class ConfigurationView extends ConfiguraionDesign {
    public static String VIEW_NAME = "Configuration";
}
