package edu.polytech.cpmolgenapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main entry point for the graph-generation experiments.
 *
 * <p>Paper: "RevLex Ordering and Upper Off-Diagonal Connectivity Constraints:
 * A Synergistic Approach for Connected Non-Isomorphic Graph Enumeration"
 *
 * <p>Produces a single unified comparison table with 7 columns in two groups:
 *
 * <pre>
 * ── Group A: Symmetry breaking (all graphs) ──────────────────────────────────
 *   OptLex | OptRevLex | Hybrid
 *
 * ── Group B: Connectivity (connected graphs only) ────────────────────────────
 *   OptLex(P) | OptRevLex(P) | Hybrid(P) | OptRevLex(D)
 * </pre>
 *
 * <p>Each cell reports two sub-rows per instance:
 * <pre>
 *   Row 1: solutions found  +  CPU time (seconds)
 *   Row 2: failures / branches / choice-points
 * </pre>
 *
 * <p><b>Hybrid switching rule</b>
 * ({@link OpLexVsOpRevLexVsHybrid#useRevLex(int, int)}):
 * <pre>
 *   RevLex  when  2d &lt; n,  or  (2d == n AND d even)
 *   Lex     otherwise
 * </pre>
 * On the current benchmark this selects OptRevLex for every instance except
 * K₆(3) (2d = n = 6, odd d → OptLex).  The selected ordering is shown in the
 * Hybrid column header per group.
 *
 * <p><b>Why no Hybrid(D)?</b>
 * The upper off-diagonal encoding (Theorem 2) is only valid under OptRevLex.
 * When the hybrid selects OptLex the encoding is unsound (K₆(3) → 0 solutions
 * empirically).  Hybrid connectivity therefore uses the path-based encoding,
 * which is always valid.
 */
@SpringBootApplication
public class CpMolgenApiApplication {

    // =========================================================================
    //  TABLE LAYOUT
    // =========================================================================

    /** Group A column headers — symmetry breaking, all graphs. */
    private static final String[] GROUP_A_COLS = {
            "OptLex", "OptRevLex", "Hybrid"
    };

    /** Group B column headers — connectivity, connected graphs. */
    private static final String[] GROUP_B_COLS = {
            "OptLex(P)", "OptRevLex(P)", "Hybrid(P)", "OptRevLex(D)"
    };

    /** All 7 columns merged for the unified table. */
    private static final String[] ALL_COLS = {
            "OptLex", "OptRevLex", "Hybrid",
            "OptLex(P)", "OptRevLex(P)", "Hybrid(P)", "OptRevLex(D)"
    };

    private static final int W_INST = 13;
    private static final int W_COL  = 20;

    // =========================================================================
    //  ENTRY POINT
    // =========================================================================

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CpMolgenApiApplication.class, args);
        runPaperExperiments();
    }

    // =========================================================================
    //  EXPERIMENT RUNNER
    // =========================================================================

    public static void runPaperExperiments() throws IOException {

        printBanner();

        // Build instance lookup
        Map<String, TestCase> lookup = new LinkedHashMap<>();
        for (TestCase tc : PaperBenchmarkData.GRAPH_SAMPLES) {
            lookup.put(tc.name, tc);
        }

        // Degree-family groups — mirror paper table row groupings
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

        for (String[] group : groups) {
            System.out.println();
            printGroupHeader(group[0]);
            printColumnHeader();
            printHRule('=');

            for (int i = 1; i < group.length; i++) {
                TestCase tc = lookup.get(group[i]);
                if (tc == null) continue;

                // ---- Group A: symmetry breaking (all graphs) ----
                Result rLex      = OpLexVsOpRevLexVsHybrid.testOptimizedLex(tc.degrees);
                Result rRevLex   = OpLexVsOpRevLexVsHybrid.testOptimizedRevLex(tc.degrees);
                Result rHybrid   = OpLexVsOpRevLexVsHybrid.testHybridLexRevLex(tc.degrees);

                // ---- Group B: connectivity (connected graphs) ----
                Result rLexP     = OpLexVsOpRevLexVsHybrid.testOptimizedLexCon(tc.degrees);
                Result rRevLexP  = OpLexVsOpRevLexVsHybrid.testOptimizedRevLexCon(tc.degrees);
                Result rHybridP  = OpLexVsOpRevLexVsHybrid.testHybridLexRevLexCon(tc.degrees);
                Result rRevLexD  = OpLexVsOpRevLexVsHybrid.testOptimizedRevLexConDiag(tc.degrees);

                // Annotate instance name with hybrid ordering decision
                int d = tc.degrees[0];
                int n = tc.degrees.length;
                String tag = OpLexVsOpRevLexVsHybrid.useRevLex(d, n) ? "[RL]" : "[L] ";
                String label = tc.name + tag;

                printInstanceRows(label,
                        new Result[]{ rLex, rRevLex, rHybrid,
                                rLexP, rRevLexP, rHybridP, rRevLexD });
                printHRule('-');
            }
        }

        printFooter();
    }

    // =========================================================================
    //  TABLE PRINTING
    // =========================================================================

    private static void printBanner() {
        int w = tableWidth();
        System.out.println();
        System.out.println("=".repeat(w));
        System.out.println(center("d-Regular Graph Generation — Benchmark", w));
        System.out.println(center(
                "Lex / RevLex / Hybrid  ×  Symmetry-breaking & Connectivity", w));
        System.out.println("=".repeat(w));
        System.out.println();
        System.out.println("Instance tag:  [RL] = Hybrid selects OptRevLex,  [L] = Hybrid selects OptLex");
        System.out.println("Metrics:  Row 1 = Sol (CPU time s)   |   Row 2 = Failures/Branches/ChoicePoints");
        System.out.println("(>300s) = time-limit exceeded   |   -- = not collected");
        System.out.println();
        System.out.println("  Group A ── Symmetry breaking (all graphs) ──────────────────────────────");
        System.out.println("    OptLex      : OptLex ordering (Codish 2018)");
        System.out.println("    OptRevLex   : OptRevLex ordering (this paper)");
        System.out.println("    Hybrid      : per-instance OptRevLex or OptLex based on (d,n)");
        System.out.println();
        System.out.println("  Group B ── Connectivity (connected graphs only) ─────────────────────────");
        System.out.println("    OptLex(P)   : OptLex   + path-based connectivity");
        System.out.println("    OptRevLex(P): OptRevLex + path-based connectivity");
        System.out.println("    Hybrid(P)   : Hybrid   + path-based connectivity  [always valid]");
        System.out.println("    OptRevLex(D): OptRevLex + upper off-diagonal encoding (Theorem 2, O(n))");
        System.out.println("    [No Hybrid(D): off-diagonal is unsound when Hybrid→OptLex]");
        System.out.println();
    }

    private static void printGroupHeader(String title) {
        System.out.println("  ─── " + title + " ───");
    }

    /**
     * Three-line column header:
     * <pre>
     *  Line 1: group labels spanning their sub-columns
     *  Line 2: individual column names
     *  Line 3: metric sub-label
     * </pre>
     */
    private static void printColumnHeader() {
        int wA = GROUP_A_COLS.length * (W_COL + 1) - 1;   // width of group A span
        int wB = GROUP_B_COLS.length * (W_COL + 1) - 1;   // width of group B span

        // Line 1 — group span labels
        StringBuilder g = new StringBuilder("|");
        g.append(pad("Instance", W_INST)).append("|");
        g.append(center("── Group A: Symmetry breaking ──", wA)).append("|");
        g.append(center("────────── Group B: Connectivity ──────────", wB)).append("|");
        System.out.println(g);

        // Line 2 — column names
        StringBuilder r1 = new StringBuilder("|");
        r1.append(pad("", W_INST)).append("|");
        for (String lbl : ALL_COLS) {
            r1.append(center(lbl, W_COL)).append("|");
        }
        System.out.println(r1);

        // Line 3 — metric sub-label
        StringBuilder r2 = new StringBuilder("|");
        r2.append(pad("", W_INST)).append("|");
        for (String ignored : ALL_COLS) {
            r2.append(center("Sol(s) | f/br/ch", W_COL)).append("|");
        }
        System.out.println(r2);
    }

    private static void printHRule(char ch) {
        String seg = String.valueOf(ch).repeat(W_COL);
        StringBuilder sb = new StringBuilder("+");
        sb.append(String.valueOf(ch).repeat(W_INST)).append("+");
        for (String ignored : ALL_COLS) {
            sb.append(seg).append("+");
        }
        System.out.println(sb);
    }

    /**
     * Prints two sub-rows per instance across all 7 columns.
     * <pre>
     *   Row 1: instance name | sol (time) × 7
     *   Row 2: "  f/br/ch"  | f/br/ch    × 7
     * </pre>
     */
    private static void printInstanceRows(String name, Result[] cols) {
        // Row 1: Solutions + Time
        StringBuilder r1 = new StringBuilder("|");
        r1.append(pad(name, W_INST)).append("|");
        for (Result r : cols) {
            r1.append(center(r == null ? "--" : solTime(r), W_COL)).append("|");
        }
        System.out.println(r1);

        // Row 2: f / br / ch
        StringBuilder r2 = new StringBuilder("|");
        r2.append(pad("  f/br/ch", W_INST)).append("|");
        for (Result r : cols) {
            r2.append(center(r == null ? "--" : fbch(r), W_COL)).append("|");
        }
        System.out.println(r2);
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

    private static String solTime(Result r) {
        String t = (r.cpu >= 300_000)
                ? "(>300s)"
                : String.format("%.2fs", r.cpu / 1000.0);
        return String.format("%,d (%s)", r.count, t);
    }

    private static String fbch(Result r) {
        return metric(r.fails) + "/" + metric(r.branches) + "/" + metric(r.choicePoints);
    }

    private static String metric(long v) {
        if (v < 0)           return "--";
        if (v >= 1_000_000L) return String.format("%.1fM", v / 1_000_000.0);
        if (v >= 1_000L)     return String.format("%.1fK", v / 1_000.0);
        return Long.toString(v);
    }

    // =========================================================================
    //  LAYOUT HELPERS
    // =========================================================================

    private static String pad(String s, int width) {
        String cell = " " + s;
        if (cell.length() >= width) return cell.substring(0, width);
        return cell + " ".repeat(width - cell.length());
    }

    private static String center(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s.substring(0, width);
        int total = width - s.length();
        int left  = total / 2;
        int right = total - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }

    private static int tableWidth() {
        return 1 + W_INST + 1 + ALL_COLS.length * (W_COL + 1);
    }
}