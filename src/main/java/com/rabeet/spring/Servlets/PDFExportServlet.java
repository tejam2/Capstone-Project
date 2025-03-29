package com.rabeet.spring.Servlets;

import com.rabeet.spring.Chart.ChartService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class PDFExportServlet extends HttpServlet {

    private ChartService chartService;

    private static final Logger logger = LoggerFactory.getLogger(PDFExportServlet.class);

    public PDFExportServlet(ChartService service) {
        this.chartService = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            String query = req.getQueryString();
            if (StringUtils.isEmpty(query)) return;
            query = query.replace("%20", " ");
            Map<String, String> map = new HashMap<>();
            String[] split = query.split("&");

            for (String s1 : split) {
                String[] temp = s1.split("=");
                map.put(temp[0], temp[1]);
            }

            String omics = map.get("omics");
            String fraction = map.get("fraction");
            List<String> age = Arrays.asList(map.get("age").split(","));
            List<String> names = Arrays.asList(map.get("names").split(","));

            logger.info("Query params: {}, {}, {}, {}", omics, fraction, age, names);

            resp.setContentType("application/pdf");
            resp.addHeader("Content-Disposition", "attachment;filename=" + omics + "-" + new Date().getTime() + ".pdf");

            ServletOutputStream outputStream = resp.getOutputStream();

            byte[] pdfBytes = chartService.getPDFBytes(omics, fraction, age, names);
            outputStream.write(pdfBytes);

        } catch (Exception e) {
            logger.error("Unable to generate PDF", e);
            resp.setStatus(400);
        }
    }

}