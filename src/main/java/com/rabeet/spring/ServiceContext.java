package com.rabeet.spring;

import com.cloudinary.Cloudinary;
import com.rabeet.spring.Data.DataService;
import com.rabeet.spring.Servlets.CSVExportServlet;
import com.rabeet.spring.Servlets.PDFExportServlet;
import com.rabeet.spring.Chart.ChartService;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ServiceContext {

    @Bean
    public DataService dataService() {
        return new DataService();
    }

    @Bean
    public ChartService chartService(DataService dataService) {
        return new ChartService(dataService);
    }

    @Bean
    public ServletRegistrationBean<CSVExportServlet> csvServlet(DataService service) {
        return new ServletRegistrationBean<>(new CSVExportServlet(service), "/exportCSV");
    }

    @Bean
    public ServletRegistrationBean<PDFExportServlet> pdfServlet(ChartService service) {
        return new ServletRegistrationBean<>(new PDFExportServlet(service), "/exportPDF");
    }

    @Bean
    public Cloudinary cloudinaryEndpoint() {
        Map config = new HashMap();
        config.put("cloud_name", System.getenv("cloud_name"));
        config.put("api_key", System.getenv("api_key"));
        config.put("api_secret", System.getenv("api_secret"));
        Cloudinary cloudinary = new Cloudinary(config);
        return cloudinary;
    }

}
