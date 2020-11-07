package com.kostro.analizer.ui.configuration;

import com.kostro.analizer.wallet.Resolution;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class ConfiguraionDesign extends VerticalLayout {
    protected TextField maxPeriodField = new TextField("maxPeriod");
    protected TextField marketField = new TextField("market");
    protected ComboBox<Resolution> resolutionField = new ComboBox("resolution");
    protected Checkbox sendVolume = new Checkbox("sendVolume");

    protected Button saveButton = new Button("Save");

    public ConfiguraionDesign() {
        FormLayout layout = new FormLayout();
        layout.add(maxPeriodField, marketField, resolutionField, sendVolume, saveButton);

        add(layout);
    }
}
