package com.kostro.analizer.ui.configuration;

import com.kostro.analizer.wallet.Resolution;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;

public class ConfiguraionDesign extends VerticalLayout {
    public static final String VIEW_NAME = "Configuration";
    protected String market;
    protected NumberField maxPeriodField = new NumberField("maxPeriod");
    protected ComboBox<Resolution> resolutionField = new ComboBox("resolution");
    protected Checkbox sendVolume = new Checkbox("sendVolume");
    protected Checkbox runSheduler = new Checkbox("runSheduler");
    protected Checkbox stopBuying = new Checkbox("stopBuying");
    protected NumberField limit60 = new NumberField("limit60");
    protected NumberField lastLevel = new NumberField("lastLevel");
    protected NumberField levelStep = new NumberField("levelStep");
    protected Checkbox sendLevel = new Checkbox("sendLevel");
    protected NumberField maxLevel = new NumberField("maxLevel");

    protected Button saveButton = new Button("Save");

    public ConfiguraionDesign(String market) {
        this.market = market;
        maxPeriodField.setStep(1);
        limit60.setStep(1);
        lastLevel.setStep(1);
        maxLevel.setStep(0.01);

        FormLayout layout = new FormLayout();
        layout.add(
                maxPeriodField, resolutionField, sendVolume,
                runSheduler, stopBuying,
                limit60, lastLevel,
                levelStep, sendLevel,
                maxLevel,
                saveButton);

        add(layout);
    }
}
