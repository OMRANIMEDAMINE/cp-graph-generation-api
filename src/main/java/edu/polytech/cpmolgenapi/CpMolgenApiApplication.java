package edu.polytech.cpmolgenapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main entry point for the graph generation experiments.
 *
 * Paper: "RevLex Ordering and Upper Off-Diagonal Connectivity Constraints:
 *         A Synergistic Approach for Connected Non-Isomorphic Graph Enumeration"
 *
 * Produces a formatted multi-metric comparison table with the 5 paper columns:
 *
 *   Instance | Lex | RevLex | Lex (P) | RevLex (P) | RevLex (D)
 *
 * Each column reports per instance:
 *   - Solutions found  + CPU time (seconds)
 *   - Fails            (backtracks)
 *   - Branches         (branches explored)
 *   - Choice points
 */
@SpringBootApplication
public class CpMolgenApiApplication {

    // =========================================================================
    //  TABLE LAYOUT CONSTANTS
    // =========================================================================

    /** Column headers — match Table 1 of the paper exactly. */
    private static final String[] COL_LABELS = {
            "Lex", "RevLex", "Lex (P)", "RevLex (P)", "RevLex (D)"
    };

    /** Character width of the Instance column. */
    private static final int W_INST = 13;

    /**
     * Character width of each data column.
     * Wide enough to fit e.g. "45,722 (11.75s)" comfortably.
     */
    private static final int W_COL = 20;

    // =========================================================================
    //  ENTRY POINT
    // =========================================================================

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CpMolgenApiApplication.class, args);
        runPaperExperiments();
    }

    // =========================================================================
    //  PAPER EXPERIMENTS
    // =========================================================================

    /**
     * Runs all 5 paper configurations on every benchmark instance and
     * prints a formatted multi-metric table to stdout.
     *
     * Configurations:
     *   Lex          → testOptimizedLex           (all graphs)
     *   RevLex       → testOptimizedRevLex        (all graphs)
     *   Lex (P)      → testOptimizedLexCon        (connected, path-based)
     *   RevLex (P)   → testOptimizedRevLexCon     (connected, path-based)
     *   RevLex (D)   → testOptimizedRevLexConDiag (connected, off-diagonal)
     */
    public static void runPaperExperiments() throws IOException {

        // ---- banner ----
        printBanner();

        // ---- build instance lookup ----
        Map<String, TestCase> lookup = new LinkedHashMap<>();
        for (TestCase tc : PaperBenchmarkData.GRAPH_SAMPLES) {
            lookup.put(tc.name, tc);
        }

        // ---- degree-family groups (mirrors paper table row groupings) ----
        String[][] groups = {
                { "2-Regular Graphs  K_n(2)",
                        "K5_2", "K6_2", "K8_2", "K10_2", "K12_2", "K14_2", "K16_2" },
                { "3-Regular Graphs  K_n(3)  [cubic]",
                        "K6_3", "K8_3", "K10_3", "K12_3", "K14_3" },
                { "4-Regular Graphs  K_n(4)",
                        "K8_4", "K9_4", "K10_4", "K11_4", "K12_4" },
                { "5-Regular Graphs  K_n(5)",
                        "K12_5" }
        };
       /* String[][] groups = {

                { "3-Regular Graphs  K_n(3)  [cubic]",
                          "K14_3" },
                { "5-Regular Graphs  K_n(5)",
                        "K12_5" }
        };*/

        for (String[] group : groups) {
            System.out.println();
            printGroupHeader(group[0]);
            printColumnHeader();
            printHRule('=');

            for (int i = 1; i < group.length; i++) {
                TestCase tc = lookup.get(group[i]);
                if (tc == null) continue;

                // ---- run the 5 configurations ----
                Result rLex     = OpLexVsOpRevLexVsHybrid.testOptimizedLex(tc.degrees);
                Result rRevLex  = OpLexVsOpRevLexVsHybrid.testOptimizedRevLex(tc.degrees);
                Result rLexP    = OpLexVsOpRevLexVsHybrid.testOptimizedLexCon(tc.degrees);
                Result rRevLexP = OpLexVsOpRevLexVsHybrid.testOptimizedRevLexCon(tc.degrees);
                Result rRevLexD = OpLexVsOpRevLexVsHybrid.testOptimizedRevLexConDiag(tc.degrees);

                Result[] cols = { rLex, rRevLex, rLexP, rRevLexP, rRevLexD };

                // ---- print the 4 metric sub-rows for this instance ----
                printInstanceRows(tc.name, cols);
                printHRule('-');
            }
        }

        printFooter();
    }

    // =========================================================================
    //  TABLE PRINTING — STRUCTURE
    // =========================================================================

    private static void printBanner() {
        int w = tableWidth();
        System.out.println();
        System.out.println("=".repeat(w));
        System.out.println(center("d-Regular Graph Generation — Paper Benchmark", w));
        System.out.println(center(
                "RevLex + Upper Off-Diagonal Connectivity Constraints", w));
        System.out.println("=".repeat(w));
        System.out.println();
        System.out.println("Metrics per cell:");
        System.out.printf("  %-14s %s%n", "Sol (Time)",
                "Solutions found  +  CPU time in seconds");
        System.out.printf("  %-14s %s%n", "Fails",
                "Number of failures / backtracks");
        System.out.printf("  %-14s %s%n", "Branches",
                "Number of branches explored in the search tree");
        System.out.printf("  %-14s %s%n", "Choices",
                "Number of choice points");
        System.out.printf("  %-14s %s%n", "(>300)",
                "Instance exceeded the 300-second time limit");
        System.out.printf("  %-14s %s%n", "--",
                "Metric not collected by the solver");
        System.out.println();
    }

    private static void printGroupHeader(String title) {
        System.out.println("  [ " + title + " ]");
    }

    /**
     * Prints the two-line column header:
     *
     *   | Instance     | Lex                | RevLex             | ...
     *   | Metric       | Sol(s) Fail Br Ch  | Sol(s) Fail Br Ch  | ...
     */
    private static void printColumnHeader() {
        // Row 1 — column labels
        StringBuilder r1 = new StringBuilder("|");
        r1.append(pad("Instance", W_INST)).append("|");
        for (String lbl : COL_LABELS) {
            r1.append(center(lbl, W_COL)).append("|");
        }
        System.out.println(r1);

        // Row 2 — metric sub-label row
        StringBuilder r2 = new StringBuilder("|");
        r2.append(pad("", W_INST)).append("|");
        for (int i = 0; i < COL_LABELS.length; i++) {
            r2.append(center("Sol (Time) / Metrics", W_COL)).append("|");
        }
        System.out.println(r2);
    }

    /**
     * Prints one horizontal rule using the given character.
     *
     * @param ch '-' for a thin rule, '=' for a thick rule
     */
    private static void printHRule(char ch) {
        String seg = String.valueOf(ch).repeat(W_COL);
        StringBuilder sb = new StringBuilder("+");
        sb.append(String.valueOf(ch).repeat(W_INST)).append("+");
        for (int i = 0; i < COL_LABELS.length; i++) {
            sb.append(seg).append("+");
        }
        System.out.println(sb);
    }

    /**
     * Prints an instance block as four sub-rows:
     *
     *   | K10_2        |  9 (0.01s)         |  16 (0.01s)        | ...
     *   |  Fails       |  0                 |  2                 | ...
     *   |  Branches    |  24                |  31                | ...
     *   |  Choices     |  12                |  18                | ...
     */
    private static void printInstanceRows(String name, Result[] cols) {

        // Sub-row 0 — Solutions + Time  (instance name in first column)
        printSubRow(name, cols, SubMetric.SOL_TIME);

        // Sub-row 1 — Fails
        printSubRow("  Fails",    cols, SubMetric.FAILS);

        // Sub-row 2 — Branches
        printSubRow("  Branches", cols, SubMetric.BRANCHES);

        // Sub-row 3 — Choice Points
        printSubRow("  Choices",  cols, SubMetric.CHOICES);
    }

    /** The four metrics displayed per cell. */
    private enum SubMetric { SOL_TIME, FAILS, BRANCHES, CHOICES }

    private static void printSubRow(String rowLabel, Result[] cols, SubMetric m) {
        StringBuilder sb = new StringBuilder("|");
        sb.append(pad(rowLabel, W_INST)).append("|");
        for (Result r : cols) {
            String cell = (r == null) ? "--" : cellValue(r, m);
            sb.append(center(cell, W_COL)).append("|");
        }
        System.out.println(sb);
    }

    /** Extracts the formatted string for a given metric from a result. */
    private static String cellValue(Result r, SubMetric m) {
        switch (m) {
            case SOL_TIME: return solTime(r);
            case FAILS:    return metric(r.fails);
            case BRANCHES: return metric(r.branches);
            case CHOICES:  return metric(r.choicePoints);
            default:       return "--";
        }
    }

    private static void printFooter() {
        int w = tableWidth();
        System.out.println();
        System.out.println("=".repeat(w));
        System.out.println(center("End of Experiments", w));
        System.out.println("=".repeat(w));
        System.out.println();
    }

    // =========================================================================
    //  VALUE FORMATTERS
    // =========================================================================

    /**
     * Formats the primary cell content: solution count + CPU time.
     * Shows "(>300)" when the time limit was exceeded.
     */
    private static String solTime(Result r) {
        if (r == null) return "--";
        String t = (r.cpu >= 300_000)
                ? "(>300)"
                : String.format("%.2fs", r.cpu / 1000.0);
        return String.format("%,d (%s)", r.count, t);
    }

    /**
     * Formats a long solver metric with compact notation for large values:
     *   -1        → "--"   (metric not collected)
     *   ≥ 1 000 000 → "X.XXM"
     *   ≥ 1 000     → "X,XXX"
     *   otherwise   → plain integer
     */
    private static String metric(long v) {
        if (v < 0)            return "--";
        if (v >= 1_000_000L)  return String.format("%.2fM", v / 1_000_000.0);
        if (v >= 1_000L)      return String.format("%,d", v);
        return Long.toString(v);
    }

    // =========================================================================
    //  STRING / LAYOUT HELPERS
    // =========================================================================

    /** Left-pads {@code s} to exactly {@code width} characters (1 space margin). */
    private static String pad(String s, int width) {
        String cell = " " + s;
        if (cell.length() >= width) return cell.substring(0, width);
        return cell + " ".repeat(width - cell.length());
    }

    /** Centers {@code s} in a field of {@code width} characters. */
    private static String center(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s.substring(0, width);
        int pad   = width - s.length();
        int left  = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }

    /** Total character width of the printed table. */
    private static int tableWidth() {
        // | + W_INST + | + N * (W_COL + |)
        return 1 + W_INST + 1 + COL_LABELS.length * (W_COL + 1);
    }
}