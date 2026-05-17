package edu.polytech.cpmolgenapi;

/**
 * Benchmark instances used in the paper:
 *
 *   "RevLex Ordering and Upper Off-Diagonal Connectivity Constraints:
 *    A Synergistic Approach for Connected Non-Isomorphic Graph Enumeration"
 *
 * All instances are d-regular graphs K_n(d), i.e., every vertex has
 * exactly degree d.  We restrict to d < n/2 since graph complementation
 * gives a one-to-one correspondence between K_n(d) and K_n(n-1-d).
 *
 * Instance naming convention:  K_n(d)  →  "Kn_d"
 * e.g. K_8(3) is named "K8_3".
 *
 * The instances below match exactly the rows reported in Table 1 of the
 * paper, grouped by degree family.
 */
public class PaperBenchmarkData {

    /**
     * All benchmark instances used in the paper experiments.
     * Each {@link TestCase} holds the instance name, the degree sequence
     * (all equal to d for a d-regular graph), and a human-readable description.
     */
    public static final TestCase[] GRAPH_SAMPLES = {

            // =====================================================================
            // DEGREE 2  —  2-regular graphs (unions of cycles)
            // =====================================================================
            new TestCase("K5_2",
                    new int[]{2, 2, 2, 2, 2},
                    "2-regular graph on 5 vertices"),

            new TestCase("K6_2",
                    new int[]{2, 2, 2, 2, 2, 2},
                    "2-regular graph on 6 vertices"),

            new TestCase("K8_2",
                    new int[]{2, 2, 2, 2, 2, 2, 2, 2},
                    "2-regular graph on 8 vertices"),

            new TestCase("K10_2",
                    new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    "2-regular graph on 10 vertices"),

            new TestCase("K12_2",
                    new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    "2-regular graph on 12 vertices"),

            new TestCase("K14_2",
                    new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    "2-regular graph on 14 vertices"),

            new TestCase("K16_2",
                    new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    "2-regular graph on 16 vertices"),

            // =====================================================================
            // DEGREE 3  —  cubic graphs
            // Note: 3-regular requires n even (n*d must be even).
            // =====================================================================
            new TestCase("K6_3",
                    new int[]{3, 3, 3, 3, 3, 3},
                    "3-regular graph on 6 vertices"),

            new TestCase("K8_3",
                    new int[]{3, 3, 3, 3, 3, 3, 3, 3},
                    "3-regular graph on 8 vertices"),

            new TestCase("K10_3",
                    new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
                    "3-regular graph on 10 vertices (Petersen family)"),

            new TestCase("K12_3",
                    new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
                    "3-regular graph on 12 vertices"),

            // K14_3 exceeds the 300-second time limit for all configurations;
            // it is included here so the solver records partial candidate counts.
            new TestCase("K14_3",
                    new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
                    "3-regular graph on 14 vertices (timeout expected)"),

            // =====================================================================
            // DEGREE 4  —  4-regular graphs
            // =====================================================================
            new TestCase("K8_4",
                    new int[]{4, 4, 4, 4, 4, 4, 4, 4},
                    "4-regular graph on 8 vertices"),

            new TestCase("K9_4",
                    new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4},
                    "4-regular graph on 9 vertices"),

            new TestCase("K10_4",
                    new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
                    "4-regular graph on 10 vertices"),

            new TestCase("K11_4",
                    new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
                    "4-regular graph on 11 vertices"),

            new TestCase("K12_4",
                    new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
                    "4-regular graph on 12 vertices"),

            // =====================================================================
            // DEGREE 5  —  5-regular graphs
            // Note: 5-regular requires n even.
            // =====================================================================
            new TestCase("K12_5",
                    new int[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
                    "5-regular graph on 12 vertices"),
    };
}