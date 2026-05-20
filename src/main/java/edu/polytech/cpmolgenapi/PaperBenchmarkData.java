package edu.polytech.cpmolgenapi;

/**
 * Benchmark instances used in the paper:
 *
 * <p>"RevLex Ordering and Upper Off-Diagonal Connectivity Constraints:
 * A Synergistic Approach for Connected Non-Isomorphic Graph Enumeration"
 *
 * <p>All instances are d-regular graphs K_n(d) (every vertex has degree d).
 * The benchmark is restricted to d &lt; n/2; graph complementation gives a
 * one-to-one correspondence between K_n(d) and K_n(n−1−d), so the regime
 * d ≥ n/2 requires no separate treatment.
 *
 * <p>Naming convention: K_n(d) → {@code "Kn_d"}, e.g. K_8(3) → {@code "K8_3"}.
 *
 * <p>The instances match exactly the rows of Table 1 in the paper, grouped
 * by degree family.
 */
public class PaperBenchmarkData {

    /** All benchmark instances, in paper-table order. */
    public static final TestCase[] GRAPH_SAMPLES = {

            // =====================================================================
            // d = 2  —  2-regular graphs (disjoint unions of cycles)
            // =====================================================================
            new TestCase("K5_2",
                    new int[]{2, 2, 2, 2, 2},
                    "K_5(2): 2-regular on 5 vertices"),

            new TestCase("K6_2",
                    new int[]{2, 2, 2, 2, 2, 2},
                    "K_6(2): 2-regular on 6 vertices"),

            new TestCase("K8_2",
                    new int[]{2, 2, 2, 2, 2, 2, 2, 2},
                    "K_8(2): 2-regular on 8 vertices"),

            new TestCase("K10_2",
                    new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    "K_10(2): 2-regular on 10 vertices"),

            new TestCase("K12_2",
                    new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    "K_12(2): 2-regular on 12 vertices"),

            new TestCase("K14_2",
                    new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    "K_14(2): 2-regular on 14 vertices"),

            new TestCase("K16_2",
                    new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    "K_16(2): 2-regular on 16 vertices"),

            // =====================================================================
            // d = 3  —  cubic (3-regular) graphs
            // Note: 3-regular requires n even (n·d must be even).
            // =====================================================================
            new TestCase("K6_3",
                    new int[]{3, 3, 3, 3, 3, 3},
                    "K_6(3): 3-regular on 6 vertices  [hybrid boundary: 2d==n, d odd → Lex]"),

            new TestCase("K8_3",
                    new int[]{3, 3, 3, 3, 3, 3, 3, 3},
                    "K_8(3): 3-regular on 8 vertices"),

            new TestCase("K10_3",
                    new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
                    "K_10(3): 3-regular on 10 vertices (Petersen family)"),

            new TestCase("K12_3",
                    new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
                    "K_12(3): 3-regular on 12 vertices"),

            new TestCase("K14_3",
                    new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
                    "K_14(3): 3-regular on 14 vertices (timeout expected for some configs)"),

            // =====================================================================
            // d = 4  —  4-regular graphs
            // =====================================================================
            new TestCase("K8_4",
                    new int[]{4, 4, 4, 4, 4, 4, 4, 4},
                    "K_8(4): 4-regular on 8 vertices  [hybrid boundary: 2d==n, d even → RevLex]"),

            new TestCase("K9_4",
                    new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4},
                    "K_9(4): 4-regular on 9 vertices"),

            new TestCase("K10_4",
                    new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
                    "K_10(4): 4-regular on 10 vertices"),

            new TestCase("K11_4",
                    new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
                    "K_11(4): 4-regular on 11 vertices"),

            new TestCase("K12_4",
                    new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
                    "K_12(4): 4-regular on 12 vertices"),

            // =====================================================================
            // d = 5  —  5-regular graphs
            // Note: 5-regular requires n even.
            // =====================================================================
            new TestCase("K12_5",
                    new int[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                    "K_12(5): 5-regular on 12 vertices"),
    };
}