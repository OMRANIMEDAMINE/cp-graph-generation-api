package edu.polytech.cpmolgenapi;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
public class plotClass {

    public static void main(String[] args) throws Exception {

        // Categories (experience names replaced by numbers)
        // Categories (experience names replaced by numbers) — updated to match the 20 rows

// Iso% extracted from the table (same order as rows shown)

// NOTE: your pasted snippet above still shows the old 18-length arrays;
// the table below has 20 rows, so these arrays are length 20.

        String[] x = {"1","2","3","4","5","6","7","8","9","10","11","12"};

        double[] lex = {
                66.7, 75.0, 15.9, 5.8, 1.8, 75.0, 35.3, 15.9, 5.8, 12.9, 6.8, 1.6
        };

        double[] revLex = {
                100.0, 100.0, 84.6, 71.4, 58.3, 100.0, 100.0, 84.6, 71.4, 13.8, 31.7, 19.2
        };

        double[] optLex = {
                100.0, 100.0, 40.7, 18.9, 7.4, 100.0, 75.0, 40.7, 18.9, 21.2, 18.3, 5.1
        };

        double[] optRevLex = {
                100.0, 100.0, 84.6, 71.4, 58.3, 100.0, 100.0, 84.6, 71.4, 24.7, 40.0, 27.6
        };
        // Chart 1: Lex vs RevLex
        JFreeChart chart1 = createIsoChart(
                "Lex vs RevLex (Iso%)",
                "Lex",
                lex,
                "RevLex",
                revLex,
                x
        );
        ChartUtils.saveChartAsPNG(new File("lex_vs_revlex.png"), chart1, 1200, 520);

        // Chart 2: OptLex vs OptRevLex
        JFreeChart chart2 = createIsoChart(
                "OptLex vs OptRevLex (Iso%)",
                "OptLex",
                optLex,
                "OptRevLex",
                optRevLex,
                x
        );
        ChartUtils.saveChartAsPNG(new File("optlex_vs_optrevlex.png"), chart2, 1200, 520);

        System.out.println("Saved:");
        System.out.println(" - lex_vs_revlex.png");
        System.out.println(" - optlex_vs_optrevlex.png");
    }

    private static JFreeChart createIsoChart(
            String title,
            String seriesAName,
            double[] seriesA,
            String seriesBName,
            double[] seriesB,
            String[] categories
    ) {
        if (seriesA.length != categories.length || seriesB.length != categories.length) {
            throw new IllegalArgumentException("Series lengths must match categories length.");
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < categories.length; i++) {
            // JFreeChart skips null values; use null for NaN to avoid plotting weird bars/labels
            dataset.addValue(asNumberOrNull(seriesA[i]), seriesAName, categories[i]);
            dataset.addValue(asNumberOrNull(seriesB[i]), seriesBName, categories[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title,      // title
                "",         // x-axis label (blank to match your style)
                "Iso(%)",   // y-axis label
                dataset,
                PlotOrientation.VERTICAL,
                true,       // legend
                false,
                false
        );

        // --- Style (close to the screenshot) ---
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 26));

        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 16));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Blue + orange (match the example)
        renderer.setSeriesPaint(0, new Color(31, 119, 180));
        renderer.setSeriesPaint(1, new Color(255, 127, 14));

        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.08);

        // Value labels (1 decimal) on top of bars
        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.0"))
        );
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.BOLD, 12));
        renderer.setDefaultItemLabelPaint(Color.DARK_GRAY);

        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 16));

        // Optional: set a fixed range if you always want 0..100
        // plot.getRangeAxis().setRange(0.0, 110.0);

        return chart;
    }

    private static Number asNumberOrNull(double v) {
        return Double.isFinite(v) ? v : null; // turns NaN/Inf into gaps (no bar)
    }
}
