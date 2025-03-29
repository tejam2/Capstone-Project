package com.rabeet.spring.Vaadin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.vaadin.addon.JFreeChartWrapper;

import java.awt.*;

@Route("jfreechart")
public class JFreeChartView extends VerticalLayout {

    public JFreeChartView() {

        Element image = new Element("object");
        image.setAttribute("type", "image/svg+xml");
        image.getStyle().set("display", "block");

        NativeButton button = new NativeButton("Generate Image");
        button.addClickListener(event -> {
            String url = "export?name=123";
            image.setAttribute("data", url);
        });

        UI.getCurrent().getElement().appendChild(image, button.getElement());

        JFreeChart chart = createChart(createDataset());
        JFreeChartWrapper wrapper = new JFreeChartWrapper(chart);
        add(wrapper);
        add(getLevelChart());
        add(regressionChart());
        add(new JFreeChartWrapper(cc()));
    }

    public JFreeChartWrapper getChart() {
        JFreeChart chart = createChart(createDataset());
        JFreeChartWrapper wrapper = new JFreeChartWrapper(chart);
        return wrapper;
    }

    public JFreeChartWrapper getChart1() {
        JFreeChart chart = createChart(createDataset());
        chart.setTitle("TEST");
        JFreeChartWrapper wrapper = new JFreeChartWrapper(chart);
        return wrapper;
    }

    public JFreeChart cc() {
        // create the first dataset...
        DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
        dataset1.addValue(1.0, "S1", "Category 1");
        dataset1.addValue(4.0, "S1", "Category 2");
        dataset1.addValue(3.0, "S1", "Category 3");
        dataset1.addValue(5.0, "S1", "Category 4");
        dataset1.addValue(5.0, "S1", "Category 5");
        dataset1.addValue(7.0, "S1", "Category 6");
        dataset1.addValue(7.0, "S1", "Category 7");
        dataset1.addValue(8.0, "S1", "Category 8");

        dataset1.addValue(5.0, "S2", "Category 1");
        dataset1.addValue(7.0, "S2", "Category 2");
        dataset1.addValue(6.0, "S2", "Category 3");
        dataset1.addValue(8.0, "S2", "Category 4");
        dataset1.addValue(4.0, "S2", "Category 5");
        dataset1.addValue(4.0, "S2", "Category 6");
        dataset1.addValue(2.0, "S2", "Category 7");
        dataset1.addValue(1.0, "S2", "Category 8");

        // create the first renderer...
        CategoryItemLabelGenerator generator
                = new StandardCategoryItemLabelGenerator();
        CategoryItemRenderer renderer = new BarRenderer();
        renderer.setDefaultItemLabelGenerator(generator);
        renderer.setDefaultItemLabelsVisible(true);

        CategoryPlot plot = new CategoryPlot();
        plot.setDataset(dataset1);
        plot.setRenderer(renderer);

        plot.setDomainAxis(new CategoryAxis("Category"));
        plot.setRangeAxis(new NumberAxis("Value"));

        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);

        // now create the second dataset and renderer...
        DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();
        dataset2.addValue(9.0, "T1", "Category 1");
        dataset2.addValue(7.0, "T1", "Category 2");
        dataset2.addValue(2.0, "T1", "Category 3");
        dataset2.addValue(6.0, "T1", "Category 4");
        dataset2.addValue(6.0, "T1", "Category 5");
        dataset2.addValue(9.0, "T1", "Category 6");
        dataset2.addValue(5.0, "T1", "Category 7");
        dataset2.addValue(4.0, "T1", "Category 8");

        CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
        plot.setDataset(1, dataset2);
        plot.setRenderer(1, renderer2);

        // create the third dataset and renderer...
        ValueAxis rangeAxis2 = new NumberAxis("Axis 2");
        plot.setRangeAxis(1, rangeAxis2);

        DefaultCategoryDataset dataset3 = new DefaultCategoryDataset();
        dataset3.addValue(94.0, "R1", "Category 1");
        dataset3.addValue(75.0, "R1", "Category 2");
        dataset3.addValue(22.0, "R1", "Category 3");
        dataset3.addValue(74.0, "R1", "Category 4");
        dataset3.addValue(83.0, "R1", "Category 5");
        dataset3.addValue(9.0, "R1", "Category 6");
        dataset3.addValue(23.0, "R1", "Category 7");
        dataset3.addValue(98.0, "R1", "Category 8");

        plot.setDataset(2, dataset3);
        CategoryItemRenderer renderer3 = new LineAndShapeRenderer();
        plot.setRenderer(2, renderer3);
        plot.mapDatasetToRangeAxis(2, 1);

        // change the rendering order so the primary dataset appears "behind"
        // the other datasets...
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        plot.getDomainAxis().setCategoryLabelPositions(
                CategoryLabelPositions.UP_45);
        JFreeChart chart = new JFreeChart(plot);
        chart.setTitle("Overlaid Bar Chart");
        return chart;
    }

    private static JFreeChart createChart(CategoryDataset dataset) {

        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
                "Bar Chart Demo 1", // chart title
                "Category", // domain axis label
                "Value", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
        );

//        String title, String xAxisLabel, boolean dateAxis, String yAxisLabel, IntervalXYDataset dataset


        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);

        // ******************************************************************
        // More than 150 demo applications are included with the JFreeChart
        // Developer Guide...for more information, see:
        //
        // > http://www.object-refinery.com/jfreechart/guide.html
        //
        // ******************************************************************
        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        // renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
//        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f,
//                0.0f, new Color(0, 0, 64));
//        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f,
//                0.0f, new Color(0, 64, 0));
//        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f,
//                0.0f, new Color(64, 0, 0));
//        renderer.setSeriesPaint(0, gp0);
//        renderer.setSeriesPaint(1, gp1);
//        renderer.setSeriesPaint(2, gp2);

//        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions
//                .createUpRotationLabelPositions(Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;

    }

    private static CategoryDataset createDataset() {

        // row keys...
        String series1 = "First";
        String series2 = "Second";
        String series3 = "Third";
        String series4 = "Fourth";

        // column keys...
        String category1 = "Category 1";
        String category2 = "Category 2";
        String category3 = "Category 3";
        String category4 = "Category 4";
        String category5 = "Category 5";

        DefaultStatisticalCategoryDataset ds1 = new DefaultStatisticalCategoryDataset();

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(1.0, series1, category1);
        dataset.addValue(4.0, series1, category2);
        dataset.addValue(3.0, series1, category3);
        dataset.addValue(5.0, series1, category4);
        dataset.addValue(5.0, series1, category5);

        dataset.addValue(5.0, series2, category1);
        dataset.addValue(7.0, series2, category2);
        dataset.addValue(6.0, series2, category3);
        dataset.addValue(8.0, series2, category4);
        dataset.addValue(4.0, series2, category5);

        dataset.addValue(4.0, series3, category1);
        dataset.addValue(3.0, series3, category2);
        dataset.addValue(2.0, series3, category3);
        dataset.addValue(3.0, series3, category4);
        dataset.addValue(6.0, series3, category5);


        dataset.addValue(4.0, series4, category1);
        dataset.addValue(3.0, series4, category2);
        dataset.addValue(2.0, series4, category3);
        dataset.addValue(3.0, series4, category4);
        dataset.addValue(6.0, series4, category5);

        return dataset;
    }

    private static JFreeChartWrapper getLevelChart() {

        DefaultTableXYDataset ds = new DefaultTableXYDataset();
        NumberAxis y = new NumberAxis("Y");

        XYSeries series;

        series = new XYSeries("BAR", false, false);
        series.add(0, 0);
        series.add(1, 56);
        series.add(2, 54);
        series.add(3, 40);
        series.add(4, 12);
        series.add(5, 5);
        series.add(6, 2);
        ds.addSeries(series);

        series = new XYSeries("FOO", false, false);
        series.add(0, 0);
        series.add(1, 66);
        series.add(2, 64);
        series.add(3, 50);
        series.add(4, 22);
        series.add(5, 15);
        series.add(6, 10);
        ds.addSeries(series);

        series = new XYSeries("SDF", false, false);
        series.add(0, 0);
        series.add(1, 76);
        series.add(2, 74);
        series.add(3, 60);
        series.add(4, 32);
        series.add(5, 25);
        series.add(6, 20);
        ds.addSeries(series);

        // Paint p = new Color(0, 0, 0, Color.OPAQUE);
        // r.setSeriesPaint(0, p);
        // BasicStroke s = new BasicStroke(2);
        // r.setSeriesStroke(0, s);
        DefaultTableXYDataset ds2 = new DefaultTableXYDataset();
        series = new XYSeries("DOO", false, false);
        series.add(1, 60);
        series.add(2, 64);
        series.add(3, 54);
        series.add(4, 30);
        series.add(5, 25);
        series.add(6, 15);
        ds2.addSeries(series);

        XYAreaRenderer r = new XYAreaRenderer(XYAreaRenderer.AREA_AND_SHAPES);

        XYPlot plot2 = new XYPlot(ds2, new NumberAxis("X"), y,
                new XYLineAndShapeRenderer());

        plot2.setDataset(1, ds);

        plot2.setRenderer(1, r);

        JFreeChart c = new JFreeChart(plot2);

        return new JFreeChartWrapper(c);
    }

    private static JFreeChartWrapper regressionChart() {

        DefaultTableXYDataset ds = new DefaultTableXYDataset();

        XYSeries series;

        series = new XYSeries("BAR", false, false);
        series.add(1, 1);
        series.add(2, 4);
        series.add(3, 6);
        series.add(4, 9);
        series.add(5, 9);
        series.add(6, 11);
        ds.addSeries(series);

        JFreeChart scatterPlot = ChartFactory.createScatterPlot("Regression",
                "X", "Y", ds, PlotOrientation.HORIZONTAL, true, false, false);

        XYPlot plot = (XYPlot) scatterPlot.getPlot();

        double[] regression = Regression.getOLSRegression(ds, 0);

        // regression line points
        double v1 = regression[0] + regression[1] * 1;
        double v2 = regression[0] + regression[1] * 6;

        DefaultXYDataset ds2 = new DefaultXYDataset();
        ds2.addSeries("regline", new double[][]{new double[]{1, 6},
                new double[]{v1, v2}});
        plot.setDataset(1, ds2);
        plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));

        JFreeChart c = new JFreeChart(plot);

        return new JFreeChartWrapper(c);
    }

}
