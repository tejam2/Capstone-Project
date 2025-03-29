package com.rabeet.spring.Chart;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

// iText imports
// Orson PDF imports
// Box-and-whisker and line-and-shape plots imports


/**
 * Creates a PDF document with iText, then inserts a PDF chart generated with
 * JFreeChart and OrsonPDF.
 */
public class iTextOrsonPDFDemo 
{
    private static JFreeChart createChart() 
    {
        DefaultBoxAndWhiskerCategoryDataset boxData = new DefaultBoxAndWhiskerCategoryDataset();
         
        boxData.add(Arrays.asList(30, 36, 46, 55, 65, 76, 81, 80, 71, 59, 44, 34), "Planet", "Endor");
        boxData.add(Arrays.asList(22, 25, 34, 44, 54, 63, 69, 67, 59, 48, 38, 28), "Planet", "Hoth");
        BoxAndWhiskerRenderer boxRenderer = new BoxAndWhiskerRenderer();
 
        DefaultCategoryDataset catData = new DefaultCategoryDataset();
        catData.addValue(boxData.getMeanValue(0, 0), "Mean", boxData.getColumnKey(0));
        catData.addValue(boxData.getMeanValue(0, 1), "Mean", boxData.getColumnKey(1));
 
        LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
        CategoryAxis xAxis = new CategoryAxis("Type");
        NumberAxis yAxis = new NumberAxis("Value");
        yAxis.setAutoRangeIncludesZero(false);
 
        CategoryPlot plot = new CategoryPlot(boxData, xAxis, yAxis, boxRenderer);
 
        plot.setDataset(1, catData);
        plot.setRenderer(1, lineRenderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
 
        JFreeChart chart = new JFreeChart("Box-and-Whisker plot overlaid with Line Shape demo", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        return chart;
    }
 
    private static byte[] generateChartPDF() 
    {
        // here we use OrsonPDF to generate PDF in a byte array
      PDFDocument doc = new PDFDocument();
      Rectangle bounds = new Rectangle(500, 335);
        Page page = doc.createPage(bounds);
        PDFGraphics2D g2 = page.getGraphics2D();
        JFreeChart chart = createChart();
        chart.draw(g2, bounds);
        return doc.getPDFBytes();
    }
     
    public static void doDemo1(String[] args) throws FileNotFoundException, IOException 
    {
        PdfWriter writer = new PdfWriter("iTextOrsonPDFDemo1.pdf");
        PdfDocument targetPDF = new PdfDocument(writer);
        Document document = new Document(targetPDF);
        document.add(new Paragraph("Demo showing a JFreeChart being rendered into a PDF document created by iText 7."));
 
        PdfReader reader = new PdfReader(new ByteArrayInputStream(generateChartPDF()));
        PdfDocument chartDoc = new PdfDocument(reader);
        PdfFormXObject chart = chartDoc.getFirstPage().copyAsFormXObject(targetPDF);
        Image chartImage = new Image(chart);
        document.add(new Paragraph("Box and whisker plot").add(chartImage));
        document.add(new Paragraph("Analysis of multiple regions"));
        document.close();
    }
 
    public static void doDemo2(String[] args) throws FileNotFoundException, IOException 
    {
        PdfWriter writer = new PdfWriter("iTextOrsonPDFDemo2.pdf");        
        PdfDocument targetPDF = new PdfDocument(writer);              
        Document document = new Document(targetPDF);              
        document.add(new Paragraph("Demo of iText and Orson PDF with JPEG image"));
        ImageData data = ImageDataFactory.create("test-image.jpg");
        Image image = new Image(data);                        
        document.add(new Paragraph("Add picture from filesystem"));
        document.add(image);              
        document.add(new Paragraph("Bindie at Play"));
        document.close();              
    }
     
    public static void doDemo3(String[] args) throws FileNotFoundException, IOException 
    {
        PdfWriter writer = new PdfWriter("iTextOrsonPDFDemo3.pdf");        
        PdfDocument targetPDF = new PdfDocument(writer);              
        Document document = new Document(targetPDF);              
        document.add(new Paragraph("Demo of iText and Orson PDF with table"));
        ImageData data = ImageDataFactory.create("test-image.jpg");              
        Image image = new Image(data);                        
        document.add(new Paragraph("Add picture from filesystem"));
        document.add(image);              
        document.add(new Paragraph("Bindie at Play"));
        addTable(targetPDF, document); 
        document.close();              
    }
 
    private static void addTable(PdfDocument pdfDoc, Document doc) 
    {
        Table table = new Table(2);
         
        Cell hdr = new Cell().add(new Paragraph("Replicate"));
        hdr.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(hdr);
         
        Cell gene = new Cell().add(new Paragraph("Thy1"));
        gene.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(gene);
         
        Cell row1Age = new Cell().add(new Paragraph("E18"));
        row1Age.setBackgroundColor(Color.WHITE);
        table.addCell(row1Age);
         
        Cell row1Value = new Cell().add(new Paragraph("17.69655"));
        row1Value.setBackgroundColor(Color.WHITE);
        table.addCell(row1Value);
 
        Cell row2Age = new Cell().add(new Paragraph("E18"));
        row2Age.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(row2Age);
         
        Cell row2Value = new Cell().add(new Paragraph("17.5023"));
        row2Value.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(row2Value);
 
        Cell row3Age = new Cell().add(new Paragraph("E18"));
        row3Age.setBackgroundColor(Color.WHITE);
        table.addCell(row3Age);
         
        Cell row3Value = new Cell().add(new Paragraph("14.47377"));
        row3Value.setBackgroundColor(Color.WHITE);
        table.addCell(row3Value);
 
        doc.add(table);

    }

    /*
     Omics Layer: Proteins
     Gene Name: Thy1
     Fraction: Growth Cone Particulate (GCP)
     Developmental Stages: E18
     */
     
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
        PdfWriter writer = new PdfWriter("ITextPDFWithChart.pdf");
        PdfDocument targetPDF = new PdfDocument(writer);
        Document document = new Document(targetPDF);

        com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List();

        String text ="a b c";
        ListItem item = new ListItem(text);
        list.add(item);
        text = "a b c align ";
        for (int i = 0; i < 5; i++) {
            text = text + text;
        }
        item = new ListItem(text);
        list.add(item);
        text = "supercalifragilisticexpialidocious ";
        for (int i = 0; i < 3; i++) {
            text = text + text;
        }
        item = new ListItem(text);
        list.add(item);
        document.add(list);

        document.add(new Paragraph("This report is generated by GC-Insights: https://gcinsights.herokuapp.com</b>"));
        PdfReader reader = new PdfReader(new ByteArrayInputStream(generateChartPDF()));
        PdfDocument chartDoc = new PdfDocument(reader);
        PdfFormXObject chart = chartDoc.getFirstPage().copyAsFormXObject(targetPDF);
        Image chartImage = new Image(chart);
        document.add(new Paragraph("Here we add a chart... ").add(chartImage));
        document.close();
    }
 
 
}