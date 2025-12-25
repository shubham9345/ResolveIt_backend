package com.org.ResolveIt.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class ConstantUtil {
    public static final long TOKEN_EXPIRATION_TIME = 1000 * 60 * 60;
    public static final String INVALID_CREDENTIAL = "please fill the correct username and password";

    public static byte[] createPieChart(String title, Map<?, Long> data) throws Exception {

        DefaultPieDataset dataset = new DefaultPieDataset();
        data.forEach((k, v) -> dataset.setValue(k.toString(), v));

        JFreeChart chart = ChartFactory.createPieChart(
                title, dataset, true, true, false
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 500, 300);
        return baos.toByteArray();
    }
    public static byte[] createBarChart(String title, Map<?, Long> data) throws Exception {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((k, v) -> dataset.addValue(v, "Count", k.toString()));

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                "Type",
                "Count",
                dataset
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 500, 300);
        return baos.toByteArray();
    }


}
