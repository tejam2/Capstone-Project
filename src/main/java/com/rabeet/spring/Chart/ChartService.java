package com.rabeet.spring.Chart;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.*;
import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;
import com.rabeet.spring.Data.DataService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.JFreeChartWrapper;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;

import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import java.util.Date;
import java.text.SimpleDateFormat;

public class ChartService {

    private static final Logger logger = LoggerFactory.getLogger(ChartService.class);
    private DataService dataService;

    public ChartService(DataService dataService) {
        this.dataService = dataService;
    }

    public JFreeChartWrapper genChart(String omics, List<String> geneName, String fraction, List<String> age) {
        if (age.size() > 1) return new JFreeChartWrapper(genBoxAndWhisker(omics, geneName, fraction, age));
        else return new JFreeChartWrapper(genHistogram(omics, geneName, fraction, age));
    }

    public static void main(String[] args) {
        String omics = DataService.PROTEIN;
        List<String> geneName = new ArrayList<>();
        geneName.add("Thy1");
        String fraction = DataService.GCM_FRACTION;
        List<String> age = new ArrayList<>();
        age.add("E18"); age.add("P0");
        ChartService c = new ChartService(new DataService());

        JFreeChartWrapper cff = new JFreeChartWrapper();

        JFreeChart chart = new JFreeChart(new CategoryPlot());

//        PDFDocument pdfDoc = new PDFDocument();
//        pdfDoc.setTitle("Test");
//        pdfDoc.setAuthor("Tester");
//        Page page = pdfDoc.createPage(new Rectangle(612, 468));
//        PDFGraphics2D g2 = page.getGraphics2D();
//        chart.draw(g2, new Rectangle(0, 0, 612, 468));
//        pdfDoc.writeToFile(new File("PDFBarChartDemo1.pdf"));
//        pdfDoc.getPDFBytes();
    }

    private JFreeChart genBoxAndWhisker(String omics, List<String> geneName, String fraction, List<String> age) {

        // Get related data
        Map<String, Map<String, List<Double>>> data = dataService.getData(omics, geneName, fraction, age);

        logger.info("Generated data: {}", data);

        // Create dataset from related data
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        for (Map.Entry<String, Map<String, List<Double>>> entry : data.entrySet()) {
            String key = entry.getKey();
            Map<String, List<Double>> value = entry.getValue();
            for (Map.Entry<String, List<Double>> temp : value.entrySet()) {
                dataset.add(temp.getValue(), key, temp.getKey());
            }
        }

        // create the second dataset...
        DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
        for (Object row : dataset.getRowKeys()) {
            for (Object col : dataset.getColumnKeys()) {
                Number mean = dataset.getMeanValue((String) row, (String) col);
                dataset1.addValue(mean, row + " mean", (String)col);
            }
        }

        // create chart from dataset
        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                omics,
                "Developmental stage",
                "Mean relative abundance (log2)",
                dataset,
                true);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainGridlinesVisible(true);
        plot.setRangePannable(true);
        BoxAndWhiskerRenderer renderer1 = new BoxAndWhiskerRenderer();
        renderer1.setMeanVisible(false);

        LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
        plot.setDataset(1, dataset1);
        plot.setRenderer(renderer1);
        plot.setRenderer(1, renderer2);

        // change the rendering order so the primary dataset appears "behind" the other datasets...
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        return chart;
    }

    private JFreeChart genHistogram(String omics, List<String> geneName, String fraction, List<String> age) {

        // Get related data
        Map<String, Map<String, List<Double>>> data = dataService.getData(omics, geneName, fraction, age);

        logger.info("Generated data: {}", data);

        // min, max for range
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

        // Create dataset from related data
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Map<String, List<Double>>> entry : data.entrySet()) {
            String key = entry.getKey();
            int i = 1;
            for (Double d : entry.getValue().values().iterator().next()) {
                if (d < min) min = d;
                if (d > max) max = d;
                dataset.addValue(d, key, "" + (i++));
            }
        }

        // create chart from dataset
        JFreeChart chart = ChartFactory.createBarChart(
                omics, // chart title
                "Biological Replicates", // domain axis label
                "Relative abundance (log2)", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);

        return chart;
    }

    public byte[] getPDFBytes(String omics, String fraction, List<String> age, List<String> names) throws IOException, InterruptedException {

        JFreeChart chart;

        if (age.size() > 1) chart = genBoxAndWhisker(omics, names, fraction, age);
        else chart = genHistogram(omics, names, fraction, age);

        PDFDocument pdfDoc = new PDFDocument();
        pdfDoc.setTitle(omics);
        pdfDoc.setAuthor("GCInsights");
        Rectangle bounds = new Rectangle(590, 468);
        Page page = pdfDoc.createPage(bounds);
        PDFGraphics2D g2 = page.getGraphics2D();
        chart.draw(g2, bounds);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument targetPDF = new PdfDocument(writer);
        PaginateHandler footerHandler = addHeaderAndFooterHandlers(targetPDF);
        Document document = new Document(targetPDF);
        document.add(new Paragraph("This report is generated by GC-Insights: https://gcinsights.herokuapp.com"));
        document.add(new Paragraph("Inputs:"));

        com.itextpdf.layout.element.List itextList = new com.itextpdf.layout.element.List();
        ListItem item = new ListItem("Omics Layer: " + omics); itextList.add(item);
        item = new ListItem("Gene Names: " + String.join(",", names)); itextList.add(item);
        item = new ListItem("Fraction: " + fraction); itextList.add(item);
        item = new ListItem("Developmental Stages: " + String.join(",", age)); itextList.add(item);
        document.add(itextList);

        document.add(new Paragraph("\nOutput:"));

        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfDoc.getPDFBytes()));
        PdfDocument chartDoc = new PdfDocument(reader);
        PdfFormXObject chart1 = chartDoc.getFirstPage().copyAsFormXObject(targetPDF);
        Image chartImage = new Image(chart1);
        document.add(chartImage);

        document.add(new Paragraph("\nData:"));
        addTable(document, dataService.getTransposedData(omics, names, fraction, age));

        document.add(new Paragraph("\nCredits:"));
        document.add(new Paragraph("This tool was created in support of the Neuronal Growth Cone Multi-Omics study and in collaboration with Florida Polytechnic" +
                " University and University of Miami by Rabeet Fatmi, Florida Polytechnic alumnus, and Dr. Mohammad Samarah, assistant professor of computer science at Florida Polytechnic University. Contributors include Zain Chauhan, Rabeet Fatmi, Dr. Sanjoy Bhattacharya, and Dr. " +
                "Mohammad Samarah. Dr. Samarah led requirements engineering, design and development of the tool. Dr. Bhattacharya" +
                " provided helpful insights and direction. Zain was instrumental in requirements and user interface design."));

        footerHandler.writeTotal(targetPDF);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    public static void addTable(Document doc, String[][] data) {

        int numrows = data.length;
        int numcols = data[0].length;

        Table table = new Table(numcols);

        com.itextpdf.kernel.color.Color color = com.itextpdf.kernel.color.Color.LIGHT_GRAY;
        for (int i = 0; i < numrows; i++) {
            for (int j = 0; j < numcols; j++) {
                String text = data[i][j];
                Cell cell = new Cell().add(new Paragraph(text));
                cell.setBackgroundColor(color);
                table.addCell(cell);
            }
            color = (color == com.itextpdf.kernel.color.Color.LIGHT_GRAY) ? com.itextpdf.kernel.color.Color.WHITE : com.itextpdf.kernel.color.Color.LIGHT_GRAY;
        }

        doc.add(table);
    }

    public PaginateHandler addHeaderAndFooterHandlers(PdfDocument pdfDocument) throws IOException, InterruptedException
    {
        Date myDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("E yyyy-MM-dd hh:mm:ss a");
        String myDateString = sdf.format(myDate);
        String header = "Neuronal Growth Cone Multi-Omics Insight (GC-Insights)\t" + myDateString;
        Header headerHandler = new Header(header);
        PaginateHandler footerHandler = new PaginateHandler(pdfDocument);

        pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE,headerHandler);
        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE,footerHandler);
        return(footerHandler);
    }


    protected class Header implements IEventHandler
    {
        String m_strHeader;

        public Header(String header)
        {
            this.m_strHeader = header;
        }

        @Override
        public void handleEvent(Event event)
        {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            com.itextpdf.kernel.geom.Rectangle pageSize = page.getPageSize();

            PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
            Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);
            canvas.setFontSize(10f);
            canvas.showTextAligned(m_strHeader,pageSize.getWidth() / 2,pageSize.getTop() - 20, TextAlignment.CENTER);
        }
    }

    protected class PaginateHandler implements IEventHandler
    {
        protected PdfFormXObject m_pageNumsPlaceholder;
        protected float m_side = 800;
        protected float m_x = 20;
        protected float m_y = 25;
        protected float m_space = 4.5f;
        protected float m_descent = 3;

        public PaginateHandler(PdfDocument pdf)
        {
            m_pageNumsPlaceholder = new PdfFormXObject(new com.itextpdf.kernel.geom.Rectangle(0, 0, m_side, m_side));
        }

        @Override
        public void handleEvent(Event event)
        {
            String strCopyrightMessage = "Copyright © 2019 M. Samarah, Florida Polytechnic University/S. Bhattacharya, University of Miami. All rights reserved.";
            String strSpaces = "                                                                                                                ";
            String strPage = "Page";
            String strOf = "of";

            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNumber = pdf.getPageNumber(page);
            com.itextpdf.kernel.geom.Rectangle pageSize = page.getPageSize();

            PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
            Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);
            Paragraph p = new Paragraph().add(strCopyrightMessage + strSpaces + strPage + " ").add(String.valueOf(pageNumber)).add(" " + strOf);
            canvas.setFontSize(6f);
            canvas.showTextAligned(p, m_x, m_y, TextAlignment.LEFT);
            pdfCanvas.addXObject(m_pageNumsPlaceholder, m_x + m_space, m_y - m_descent);
            pdfCanvas.release();
        }

        public void writeTotal(PdfDocument pdf)
        {
            Canvas canvas = new Canvas(m_pageNumsPlaceholder, pdf);
            canvas.setFontSize(6f);
            canvas.showTextAligned(String.valueOf(pdf.getNumberOfPages()),528, m_descent, TextAlignment.RIGHT);
        }
    }
}