package edu.polytech.cpmolgenapi;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class ChartsLexVsRevLex {

    // -------------------- Data model --------------------
    static class Row {
        final String sample;           // "Reg{d}_{n}" e.g., "Reg2_10"
        final int lex, revLex, optLex, optRevLex;

        Row(String sample, int lex, int revLex, int optLex, int optRevLex) {
            this.sample = sample;
            this.lex = lex;
            this.revLex = revLex;
            this.optLex = optLex;
            this.optRevLex = optRevLex;
        }
    }

    // -------------------- Parsing --------------------
    static int degreeOf(String s) {
        // Expected format: Reg{d}_{n}
        int start = 3; // after "Reg"
        int underscore = s.indexOf('_', start);
        return Integer.parseInt(s.substring(start, underscore));
    }

    static int nOf(String s) {
        int underscore = s.indexOf('_');
        return Integer.parseInt(s.substring(underscore + 1));
    }

    // -------------------- Chart styling --------------------
    static void style(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        XYLineAndShapeRenderer r = new XYLineAndShapeRenderer(true, false);
        r.setSeriesPaint(0, new Color(31, 119, 180)); // blue
        r.setSeriesPaint(1, new Color(255, 127, 14)); // orange
        r.setSeriesStroke(0, new BasicStroke(2.2f));
        r.setSeriesStroke(1, new BasicStroke(2.2f));
        plot.setRenderer(r);

        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setAutoRangeIncludesZero(true);
        range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    }

    // -------------------- Chart creation --------------------
    static JFreeChart makeDegreeChart(int degree, List<Row> degreeRows, boolean useOpt) {
        // allowDuplicateXValues = true because you have Reg2_6 twice
        XYSeries s1 = new XYSeries(useOpt ? "OptLex" : "Lex", true, true);
        XYSeries s2 = new XYSeries(useOpt ? "OptRevLex" : "RevLex", true, true);

        degreeRows.sort(Comparator.comparingInt(r -> nOf(r.sample)));

        for (Row r : degreeRows) {
            int n = nOf(r.sample);
            if (!useOpt) {
                s1.add(n, r.lex);
                s2.add(n, r.revLex);
            } else {
                s1.add(n, r.optLex);
                s2.add(n, r.optRevLex);
            }
        }

        XYSeriesCollection ds = new XYSeriesCollection();
        ds.addSeries(s1);
        ds.addSeries(s2);

        // Per your request: chart title is the degree
        String title = "Degree: " + degree;

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "N",
                "Number of solutions found",
                ds
        );

        style(chart);
        return chart;
    }

    static void save(JFreeChart chart, String fileName) throws Exception {
        ChartUtils.saveChartAsPNG(new File(fileName), chart, 1200, 520);
        System.out.println("Saved: " + fileName);
    }

    public static void main(String[] args) throws Exception {

        // -------------------- Input data (Regular graphs only) --------------------
        // -------------------- Input data (Regular + selected non-regular) --------------------
        List<Row> rows = List.of(
                // ---- Regular graphs ----
                new Row("Reg2_4", 1, 1, 1, 1),
                new Row("Reg2_5", 1, 1, 1, 1),
                new Row("Reg2_6", 3, 2, 2, 2),
                new Row("Reg2_7", 4, 3, 3, 3),
                new Row("Reg2_8", 8, 4, 5, 4),
                new Row("Reg2_9", 16, 6, 9, 6),
                new Row("Reg2_10", 36, 9, 16, 9),
                new Row("Reg2_11", 73, 13, 29, 13),
                new Row("Reg2_12", 174, 19, 60, 19),
                new Row("Reg2_13", 417, 28, 116, 28),
                new Row("Reg2_14", 995, 41, 256, 41),

                new Row("Reg3_4", 1, 1, 1, 1),
                new Row("Reg3_6", 2, 3, 2, 2),
                new Row("Reg3_8", 23, 22, 13, 15),
                new Row("Reg3_10", 506, 229, 192, 133),
                new Row("Reg3_12", 15217, 2726, 4984, 1462),

                new Row("Reg4_5", 1, 1, 1, 1),
                new Row("Reg4_6", 1, 1, 1, 1),
                new Row("Reg4_7", 3, 4, 3, 3),
                new Row("Reg4_8", 22, 23, 15, 13),
                new Row("Reg4_9", 175, 175, 81, 81),
                new Row("Reg4_10", 1615, 1487, 750, 614),
                new Row("Reg4_11", 20836, 13752, 7982, 5111),
                new Row("Reg4_12", 284148, 135705, 98223, 45729),

                new Row("Reg5_6", 1, 1, 1, 1),
                new Row("Reg5_8", 4, 8, 4, 5),
                new Row("Reg5_10", 1487, 1615, 614, 750),
                new Row("Reg5_12", 1032403, 776051, 391921, 257117),

                new Row("Reg6_7", 1, 1, 1, 1),
                new Row("Reg6_8", 1, 1, 1, 1),
                new Row("Reg6_9", 6, 16, 6, 9),
                new Row("Reg6_10", 229, 506, 133, 192),
                new Row("Reg6_11", 13752, 20836, 5111, 7982),
                new Row("Reg6_12", 776051, 1032403, 257117, 391921),

                new Row("Reg7_8", 1, 1, 1, 1),
                new Row("Reg7_10", 9, 36, 9, 16),
                new Row("Reg7_12", 135705, 284148, 45729, 98223)

        );
        // -------------------- Group by degree --------------------
        Map<Integer, List<Row>> byDegree = new HashMap<>();
        for (Row r : rows) {
            int d = degreeOf(r.sample);
            byDegree.computeIfAbsent(d, k -> new ArrayList<>()).add(r);
        }

        // Degrees to output (Regular graphs)
        int[] degrees = {2, 3, 4, 5, 6, 7};

        // -------------------- 5 charts: Lex vs RevLex --------------------
        for (int d : degrees) {
            JFreeChart c = makeDegreeChart(d, new ArrayList<>(byDegree.getOrDefault(d, List.of())), false);
            save(c, "Degree_" + d + "_Lex_vs_RevLex.png");
        }

        // -------------------- 5 charts: OptLex vs OptRevLex --------------------
        for (int d : degrees) {
            JFreeChart c = makeDegreeChart(d, new ArrayList<>(byDegree.getOrDefault(d, List.of())), true);
            save(c, "Degree_" + d + "_OptLex_vs_OptRevLex.png");
        }


    }
}
