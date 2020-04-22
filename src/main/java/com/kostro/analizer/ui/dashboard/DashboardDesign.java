package com.kostro.analizer.ui.dashboard;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Title;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


public class DashboardDesign extends VerticalLayout {

    protected Chart chart = new Chart(ChartType.COLUMNRANGE);

    public DashboardDesign() {
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(getChart());
    }

    private Chart getChart() {
        chart.setSizeFull();
        Configuration conf = chart.getConfiguration();
        conf.setTitle(new Title("BigFishes prices"));

        Tooltip tooltip = conf.getTooltip();
        tooltip.setValueSuffix("zł");

        chart.setTimeline(true);
        return chart;
    }
}