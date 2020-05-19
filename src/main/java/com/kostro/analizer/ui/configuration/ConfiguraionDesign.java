package com.kostro.analizer.ui.configuration;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class ConfiguraionDesign extends VerticalLayout {
    protected TextField maxPeriodField = new TextField("maxPeriod");
    protected TextField marketField = new TextField("market");
    protected TextField resolutionField = new TextField("resolution");

    protected Button saveButton = new Button("Save");

    public ConfiguraionDesign() {
        FormLayout layout = new FormLayout();
        layout.add(maxPeriodField, marketField, resolutionField, saveButton);

        add(layout);
    }
}
