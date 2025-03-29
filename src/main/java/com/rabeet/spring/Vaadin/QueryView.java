package com.rabeet.spring.Vaadin;

import com.rabeet.spring.Chart.ChartService;
import com.rabeet.spring.Data.DataService;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.addon.JFreeChartWrapper;

@Route(value = "query", layout = RouterLayout.class)
@PageTitle("GC Insights App")
public class QueryView extends VerticalLayout {

    private InputForm inputForm;
    private JFreeChartWrapper chart = new JFreeChartWrapper();
    private Dialog dialog = new Dialog(chart);

    public QueryView(DataService dataService, ChartService chartService) {

        inputForm = new InputForm(this, dataService, chartService);

        setPadding(true);

        dialog.setWidth("1100px");
        dialog.setHeight("700px");

        add(inputForm);
        setSizeFull();
    }


    public void updateGraph(JFreeChartWrapper chart) {
        this.dialog.remove(this.chart);
        this.chart = chart;

        this.dialog.add(this.chart);
        this.dialog.open();
    }
}
