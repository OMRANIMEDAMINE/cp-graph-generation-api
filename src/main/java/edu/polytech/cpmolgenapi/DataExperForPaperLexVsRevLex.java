package edu.polytech.cpmolgenapi;

public class DataExperForPaperLexVsRevLex {
    // One unified table (no repeated degree sequences), grouped by family.
    public static TestCase[] GraphSamples = {
            /*new TestCase("BD2_6",  new int[]{2,2,2,2,2,2},   "Bounded-degree ≤2 graph on 8 vertices"),
            new TestCase("BD2_8",  new int[]{2,2,2,2,2,2,2,2},   "Bounded-degree ≤2 graph on 8 vertices"),
            new TestCase("BD2_10", new int[]{2,2,2,2,2,2,2,2,2,2}, "Bounded-degree ≤2 graph on 10 vertices"),
            new TestCase("BD2_12", new int[]{2,2,2,2,2,2,2,2,2,2,2,2}, "Bounded-degree ≤2 graph on 12 vertices"),
            new TestCase("BD2_14", new int[]{2,2,2,2,2,2,2,2,2,2,2,2,2,2}, "Bounded-degree ≤2 graph on 14 vertices"),

            */
          //  new TestCase("BD3_6",  new int[]{3,3,3,3,3,3}, "Bounded-degree ≤3 graph on 8 vertices"),
            new TestCase("BD3_6",  new int[]{3,3,3,3,3,3,3,3,3,3,3,3}, "Bounded-degree ≤3 graph on 8 vertices"),
            //****new TestCase("BD3_14",  new int[]{3,3,3,3,3,3,3,3,3,3,3,3}, "Bounded-degree ≤3 graph on 8 vertices"),
            //new TestCase("BD3_10", new int[]{3,3,3,3,3,3,3,3,3,3}, "Bounded-degree ≤3 graph on 10 vertices"),
            //*new TestCase("BD3_12", new int[]{3,3,3,3,3,3,3,3,3,3,3,3}, "Bounded-degree ≤3 graph on 12 vertices"),
            //*** new TestCase("BD3_14", new int[]{3,3,3,3,3,3,3,3,3,3,3,3,3,3}, "Bounded-degree ≤3 graph on 14 vertices"),

            /*new TestCase("BD4_6", new int[]{4,4,4,4,4,4}, "Bounded-degree ≤4 graph on 10 vertices"),
            new TestCase("BD4_8", new int[]{4,4,4,4,4,4,4,4}, "Bounded-degree ≤4 graph on 10 vertices"),*/
            //****new TestCase("BD4_10", new int[]{4,4,4,4,4,4,4,4,4,4}, "Bounded-degree ≤4 graph on 10 vertices"),
           // new TestCase("BD4_12", new int[]{4,4,4,4,4,4,4,4,4,4,4,4}, "Bounded-degree ≤4 graph on 12 vertices"),
            //new TestCase("BD4_14", new int[]{4,4,4,4,4,4,4,4,4,4,4,4,4,4}, "Bounded-degree ≤4 graph on 14 vertices"),

          //  new TestCase("BD5_8", new int[]{5,5,5,5,5,5,5,5}, "Bounded-degree ≤5 graph on 10 vertices"),
            //**new TestCase("BD5_10", new int[]{5,5,5,5,5,5,5,5,5,5}, "Bounded-degree ≤5 graph on 10 vertices"),





            //"Reg4_14", new int[]{4,4,4,4,4,4,4,4,4,4,4,4,4,4},     "4-regular graph on 14 vertices")

            // new TestCase("Reg5_12", new int[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5}, "5-regular graph on 12 vertices"),

           // new TestCase("Reg5_14", new int[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5}, "5-regular graph on 14 vertices"),
            // TestCase("Reg6_11", new int[]{6,6,6,6,6,6,6,6,6,6,6},     "6-regular graph on 12 vertices"),
            //new TestCase("Reg7_14", new int[]{7,7,7,7,7,7,7,7,7,7,7,7,7,7},  "7-regular graph on 14 vertices"),

            //new TestCase("Reg6_13", new int[]{6,6,6,6,6,6,6,6,6,6,6,6,6},    "6-regular graph on 13 vertices"),
            //new TestCase("Reg6_14", new int[]{6,6,6,6,6,6,6,6,6,6,6,6,6,6},  "6-regular graph on 14 vertices"),
           // new TestCase("Reg7_14", new int[]{7,7,7,7,7,7,7,7,7,7,7,7,7,7},  "7-regular graph on 14 vertices"),


/*
            // ============================================================
            // REGULAR GRAPHS — DEGREE 2 (baseline regular)
            // ============================================================
            new TestCase("Reg2_4", new int[]{2, 2, 2, 2}, "2-regular graph on 4 vertices"),
            new TestCase("Reg2_5", new int[]{2, 2, 2, 2, 2}, "2-regular graph on 5 vertices"),
            new TestCase("Reg2_6", new int[]{2, 2, 2, 2, 2, 2}, "2-regular graph on 6 vertices"),
            new TestCase("Reg2_7", new int[]{2, 2, 2, 2, 2, 2, 2}, "2-regular graph on 7 vertices"),
            new TestCase("Reg2_8", new int[]{2, 2, 2, 2, 2, 2, 2, 2}, "2-regular graph on 8 vertices"),
            new TestCase("Reg2_9", new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2}, "2-regular graph on 9 vertices"),
            new TestCase("Reg2_10", new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, "2-regular graph on 10 vertices"),
            new TestCase("Reg2_11", new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, "2-regular graph on 11 vertices"),
            new TestCase("Reg2_12", new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, "2-regular graph on 12 vertices"),
            new TestCase("Reg2_13", new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, "2-regular graph on 13 vertices"),
            new TestCase("Reg2_14", new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, "2-regular graph on 14 vertices"),
            new TestCase("Reg2_15", new int[]{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},   "2-regular graph on 15 vertices"),
            new TestCase("Reg2_16", new int[]{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},   "2-regular graph on 16 vertices"),
            //new TestCase("Reg2_17", new int[]{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},   "2-regular graph on 17 vertices"),
            //new TestCase("Reg2_18", new int[]{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},   "2-regular graph on 18 vertices"),
            //new TestCase("Reg2_19", new int[]{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},   "2-regular graph on 19 vertices"),
            //new TestCase("Reg2_20", new int[]{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},   "2-regular graph on 20 vertices"),


            // ============================================================
            // REGULAR GRAPHS — DEGREE 3 (cubic)
            // ============================================================
            new TestCase("Reg3_4", new int[]{3, 3, 3, 3}, "3-regular graph on 4 vertices"),
            // Note: 3-regular on 5 vertices impossible (n*d = 15 odd)
            new TestCase("Reg3_6", new int[]{3, 3, 3, 3, 3, 3}, "3-regular graph on 6 vertices"),
            new TestCase("Reg3_8", new int[]{3, 3, 3, 3, 3, 3, 3, 3}, "3-regular graph on 8 vertices"),
            // Note: 3-regular on 9 vertices impossible (n*d = 27 odd)
            new TestCase("Reg3_10", new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3}, "3-regular graph on 10 vertices"),
            new TestCase("Reg3_12", new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3}, "3-regular graph on 12 vertices"),
            //new TestCase("Reg3_14", new int[]{3,3,3,3,3,3,3,3,3,3,3,3,3,3}, "3-regular graph on 14 vertices"),
            // Note: 3-regular on 15 vertices impossible (n*d = 45 odd)
            // new TestCase("Reg3_16", new int[]{3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},    "3-regular graph on 16 vertices"),


            // ============================================================
            // REGULAR GRAPHS — DEGREE 4
            // ============================================================
            new TestCase("Reg4_5", new int[]{4, 4, 4, 4, 4}, "4-regular graph on 5 vertices"),
            new TestCase("Reg4_6", new int[]{4, 4, 4, 4, 4, 4}, "4-regular graph on 6 vertices"),
            new TestCase("Reg4_7", new int[]{4, 4, 4, 4, 4, 4, 4}, "4-regular graph on 7 vertices"),
            new TestCase("Reg4_8", new int[]{4, 4, 4, 4, 4, 4, 4, 4}, "4-regular graph on 8 vertices"),
            new TestCase("Reg4_9", new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4}, "4-regular graph on 9 vertices"),
            new TestCase("Reg4_10", new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4}, "4-regular graph on 10 vertices"),
            new TestCase("Reg4_11", new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4}, "4-regular graph on 11 vertices"),
            new TestCase("Reg4_12", new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4}, "4-regular graph on 12 vertices"),
            //new TestCase("Reg4_13", new int[]{4,4,4,4,4,4,4,4,4,4,4,4,4},     "4-regular graph on 13 vertices"),
            //new TestCase("Reg4_14", new int[]{4,4,4,4,4,4,4,4,4,4,4,4,4,4},     "4-regular graph on 14 vertices"),


            // ============================================================
            // REGULAR GRAPHS — DEGREE 5
            // ============================================================
            new TestCase("Reg5_6", new int[]{5, 5, 5, 5, 5, 5}, "5-regular graph on 6 vertices"),
            // Note: 5-regular on 7 vertices impossible (n*d = 35 odd)
            new TestCase("Reg5_8", new int[]{5, 5, 5, 5, 5, 5, 5, 5}, "5-regular graph on 8 vertices"),
            // Note: 5-regular on 9 vertices impossible (n*d = 45 odd)
            new TestCase("Reg5_10", new int[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5}, "5-regular graph on 10 vertices"),
            // Note: 5-regular on 11 vertices impossible (n*d = 55 odd)
            new TestCase("Reg5_12", new int[]{5,5,5,5,5,5,5,5,5,5,5,5},     "5-regular graph on 12 vertices"),
            // Note: 5-regular on 13 vertices impossible (n*d = 65 odd)
            //new TestCase("Reg5_14", new int[]{5,5,5,5,5,5,5,5,5,5,5,5,5,5},      "5-regular graph on 14 vertices"),


            // ============================================================
            // REGULAR GRAPHS — DEGREE 6
            // ============================================================
            new TestCase("Reg6_7", new int[]{6, 6, 6, 6, 6, 6, 6}, "6-regular graph on 7 vertices"),
            new TestCase("Reg6_8", new int[]{6, 6, 6, 6, 6, 6, 6, 6}, "6-regular graph on 8 vertices"),
            new TestCase("Reg6_9", new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6}, "6-regular graph on 9 vertices"),
            new TestCase("Reg6_10", new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6, 6}, "6-regular graph on 10 vertices"),
          //  new TestCase("Reg6_11", new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6}, "6-regular graph on 11 vertices"),
            // new TestCase("Reg6_12", new int[]{6,6,6,6,6,6,6,6,6,6,6,6},     "6-regular graph on 12 vertices"),
            //new TestCase("Reg6_13", new int[]{6,6,6,6,6,6,6,6,6,6,6,6,6},                    "6-regular graph on 13 vertices"),
            //  new TestCase("Reg6_14", new int[]{6,6,6,6,6,6,6,6,6,6,6,6,6,6},                    "6-regular graph on 14 vertices"),
            //new TestCase("Reg6_15", new int[]{6,6,6,6,6,6,6,6,6,6,6,6,6,6,6},                    "6-regular graph on 15 vertices"),
            //new TestCase("Reg6_16", new int[]{6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6},                    "6-regular graph on 16 vertices"),

            // ============================================================
            // REGULAR GRAPHS — DEGREE 7
            // ============================================================
            new TestCase("Reg7_8", new int[]{7, 7, 7, 7, 7, 7, 7, 7}, "7-regular graph on 8 vertices"),
            // Note: 7-regular on 9 vertices impossible (n*d = 63 odd)
            new TestCase("Reg7_10", new int[]{7, 7, 7, 7, 7, 7, 7, 7, 7, 7}, "7-regular graph on 10 vertices"),
            // Note: 7-regular on 11 vertices impossible (n*d = 77 odd)
            new TestCase("Reg7_12", new int[]{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7}, "7-regular graph on 12 vertices"),
            // Note: 7-regular on 13 vertices impossible (n*d = 91 odd)
            //  new TestCase("Reg7_14", new int[]{7,7,7,7,7,7,7,7,7,7,7,7,7,7},                    "7-regular graph on 14 vertices"),
            // Note: 7-regular on 15 vertices impossible (n*d = 105 odd)
            //new TestCase("Reg7_16", new int[]{7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7},                    "7-regular graph on 16 vertices"),

            // ============================================================
            // REGULAR GRAPHS — DEGREE 8
            // ============================================================
            /*  new TestCase("Reg8_9", new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8}, "8-regular graph on 9 vertices"),
              new TestCase("Reg8_10", new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 8}, "8-regular graph on 10 vertices"),
              new TestCase("Reg8_11", new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8}, "8-regular graph on 11 vertices"),
              new TestCase("Reg8_12", new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8}, "8-regular graph on 12 vertices"),
              // new TestCase("Reg8_13", new int[]{8,8,8,8,8,8,8,8,8,8,8,8,8},                    "8-regular graph on 13 vertices"),
              //  new TestCase("Reg8_14", new int[]{8,8,8,8,8,8,8,8,8,8,8,8,8,8},                    "8-regular graph on 14 vertices"),
              //new TestCase("Reg8_15", new int[]{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},                    "8-regular graph on 15 vertices"),
              //new TestCase("Reg8_16", new int[]{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},                    "8-regular graph on 16 vertices"),
                 */
    };

            // ============================================================
            // STRUCTURED / NON-REGULAR SAMPLES
            // ============================================================
           // new TestCase(       "Grid_3x3",         new int[]{4,3,4,3,4,3,4,3,4},      "3×3 grid graph (planar, bipartite)"      ),
           // new TestCase(  "Ladder_6",               new int[]{2,3,3,3,3,3,3,3,3,2},              "Ladder graph P2 × P6"       ),      new TestCase(   "Ladder_8",                    new int[]{2,3,3,3,3,3,3,3,3,3,3,2},                 "Ladder graph P2 × P8")




    public static TestCase[] GraphSamples_prev = {
            //new TestCase("K_16(3)",  new int[]{3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},     "3-regular graph on 16 vertices (cubic family sample)"),
            new TestCase("4-Reg12_A", new int[]{4,4,4,4,4,4,4,4,4,4,4,4}, "4-regular graph on 12 vertices"),
            new TestCase("K_10(5)", new int[]{5,5,5,5,5,5,5,5,5,5,5,5},  "5-regular graph on 10 vertices (regular family sample)"),
            //new TestCase("Barbell_6_6", new int[]{6,6, 5,5,5,5,5,5,5,5,5,5}, "Barbell B_6: two K6 joined by an edge (non-regular; degrees: two 6's, ten 5's)"),


            // ============ TREES WITH MAXIMUM DIAMETER ============
            new TestCase("MaxDiameter5", new int[]{1,2,2,2,1},   "Path P5 (diameter 4)"),
            new TestCase("MaxDiameter6", new int[]{1,2,2,2,2,1}, "Path P6 (diameter 5)"),

            // ============ DEGENERATE/EDGE CASES ============
            new TestCase("Chain10", new int[]{1,2,2,2,2,2,2,2,2,1},
                    "Long chain (maximum internal vertices)"),
            new TestCase("Chain12", new int[]{1,2,2,2,2,2,2,2,2,2,2,1},
                    "Long chain (maximum internal vertices)"),
            new TestCase("Chain14", new int[]{1,2,2,2,2,2,2,2,2,2,2,2,2,1},
                    "Long chain (maximum internal vertices)"),

            // ============================================================
            // PATHS (trees)
            // ============================================================
            new TestCase("Path6",  new int[]{1,2,2,2,2,1},               "Path on 6 vertices"),
            new TestCase("Path8",  new int[]{1,2,2,2,2,2,2,1},           "Path on 8 vertices"),
            new TestCase("Path10", new int[]{1,2,2,2,2,2,2,2,2,1},       "Path on 10 vertices"),
            new TestCase("Path12", new int[]{1,2,2,2,2,2,2,2,2,2,2,1},       "Path on 10 vertices"),

            // ============================================================
            // STRUCTURED / BIPARTITE-LIKE
            // ============================================================
            new TestCase("Grid_3x3", new int[]{4,3,4,3,4,3,4,3,4},     "3x3 grid 9 (bipartite planar)"),
            new TestCase("Ladder_6", new int[]{2,3,3,3,3,3,3,3,3,2},   "Ladder P2×P6 (bipartite, structured)"),
            new TestCase("Ladder_8", new int[]{2,3,3,3,3,3,3,3,3,3,3,2},   "Ladder P2×P8 (bipartite, structured)"),
            // ============================================================
            // REGULAR GRAPHS (merged from RegularSamples, duplicates removed by NAME/INTENT)
            // ============================================================
             // 2-regular (cycles / unions of cycles)
            new TestCase("C8",       new int[]{2,2,2,2,2,2,2,2},        "2-regular cycle C8"),
            new TestCase("C9",       new int[]{2,2,2,2,2,2,2,2,2},      "Cycle graph C9 (2-regular on 9 vertices)"),
            new TestCase("C10",      new int[]{2,2,2,2,2,2,2,2,2,2},    "2-regular cycle C10"),
            new TestCase("K_12(2)",  new int[]{2,2,2,2,2,2,2,2,2,2,2,2},  "C12 (2-regular on 12 vertices)"),

            // 3-regular (cubic)
            new TestCase("Petersen", new int[]{3,3,3,3,3,3,3,3,3,3},    "Petersen graph (3-regular on 10 vertices, d=3 < 5)"),
            new TestCase("Cubic12_A", new int[]{3,3,3,3,3,3,3,3,3,3,3,3},   "Hexagonal prism graph (3-regular on 12 vertices)"),
            //new TestCase("K_14(3)",  new int[]{3,3,3,3,3,3,3,3,3,3,3,3,3,3},     "3-regular graph on 14 vertices (e.g., Heawood-variant)"),
            //new TestCase("K_16(3)",  new int[]{3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},     "3-regular graph on 16 vertices (cubic family sample)"),

            // 4-regular
            new TestCase("4-Reg10",   new int[]{4,4,4,4,4,4,4,4,4,4},   "4-regular graph on 10 vertices"),
            new TestCase("4-Reg12_A", new int[]{4,4,4,4,4,4,4,4,4,4,4,4}, "4-regular graph on 12 vertices"),
           // new TestCase("4-Reg14_A", new int[]{4,4,4,4,4,4,4,4,4,4,4,4,4,4}, "4-regular graph on 14 vertices"),

            // 5-regular
            //new TestCase("K_12(5)", new int[]{5,5,5,5,5,5,5,5,5,5,5,5},  "5-regular graph on 12 vertices (regular family sample)"),
            //new TestCase("Barbell_6_6", new int[]{6,6, 5,5,5,5,5, 5,5,5,5,5}, "Barbell B_6: two K6 joined by an edge (non-regular; degrees: two 6's, ten 5's)"),
    };


    //Complete Bipartite Graphs
    public static TestCase[] BipartiteSamples = {
            // ============ CLASSIC BIPARTITE GRAPHS FROM GRAPH THEORY ============

            // 1. COMPLETE BIPARTITE GRAPHS K_{m,n}
            new TestCase("K3_3", new int[]{3,3,3,3,3,3},
                    "Utility graph, N=6, max degree=3 ≤ 3 (Petersen graph minor)"),
            new TestCase("K3_4", new int[]{4,4,4,3,3,3,3},
                    "K3,4, N=7, max degree=4 ≤ 3.5"),
            new TestCase("K3_5", new int[]{5,5,5,3,3,3,3,3},
                    "K3,5, N=8, max degree=5 ≤ 4"),

            new TestCase("K4_4", new int[]{4,4,4,4,4,4,4,4},
                    "Balanced bipartite, N=8, max degree=4 ≤ 4"),


           // 5. STRONGLY REGULAR BIPARTITE GRAPHS

            new TestCase("SRG_Bip_K4_4", new int[]{4,4,4,4,4,4,4,4},
                    "K4,4 - strongly regular with λ=0, μ=4"),

            // ============ IMPORTANT BIPARTITE GRAPHS WITH DEGREES ≤ N/2 ============

            // 7. GRID GRAPHS (planar bipartite)
            new TestCase("Grid_3x3", new int[]{4,3,4,3,4,3,4,3,4},
                    "3x3 grid (bipartite), N=9, max degree=4 ≤ 4.5"),

            // 8. LADDER GRAPHS (Cartesian product P₂ × Pₙ)
            new TestCase("Ladder_4", new int[]{2,3,3,3,3,2},
                    "Ladder P₂×P₄, N=6, max degree=3 ≤ 3"),
            new TestCase("Ladder_5", new int[]{2,3,3,3,3,3,3,2},
                    "Ladder P₂×P₅, N=8, max degree=3 ≤ 4"),
            new TestCase("Ladder_6", new int[]{2,3,3,3,3,3,3,3,3,2},
                    "Ladder P₂×P₆, N=10, max degree=3 ≤ 5"),

            // 9. CIRCULANT BIPARTITE GRAPHS
            new TestCase("CircBip_8_1_3", new int[]{4,4,4,4,4,4,4,4},
                    "Complete bipartite K4,4 as circulant, N=8, degree=4 ≤ 4"),

            // 10. BIPARTITE DOUBLE COVERS
            new TestCase("BipartiteDoubleCover_C5", new int[]{3,3,3,3,3,3,3,3,3,3},
                    "Double cover of C5 (Petersen-like), N=10, degree=3 ≤ 5"),



            // ============ SPARSE BIPARTITE GRAPHS (edge ≤ N-1) ============

            // 13. BIPARTITE TREES
            new TestCase("BipTree_Path6", new int[]{1,2,2,2,2,1},
                    "Path P6, N=6, max degree=2 ≤ 3"),
            new TestCase("BipTree_Star5", new int[]{4,1,1,1,1},
                    "Star K1,4, N=5, max degree=4 ≤ 2.5"),

            // 14. BIPARTITE FORESTS
            new TestCase("BipForest_2K2_2", new int[]{2,2,2,2},
                    "Two disjoint edges, N=4, degree=1 ≤ 2"),
            // ============ BIPARTITE WITH STRUCTURAL CONSTRAINTS ============

            // 15. BIPARTITE WITH FIXED PARTITION SIZES
            new TestCase("BipParts_3_5", new int[]{5,5,5,3,3,3,3,3},
                    "Partitions 3 vs 5, N=8, max degree=5 ≤ 4"),


            // ============ SMALL BIPARTITE GRAPHS FOR EXHAUSTIVE TESTING ============

            // 17. ALL BIPARTITE GRAPHS ON N ≤ 6 VERTICES (representative samples)
          //  new TestCase("Bip_N2", new int[]{1,1}, "Only bipartite on 2 vertices"),
           // new TestCase("Bip_N3_A", new int[]{2,1,1}, "Path P3"),
            new TestCase("Bip_N4_A", new int[]{2,2,2,2}, "C4"),
            new TestCase("Bip_N4_B", new int[]{3,1,1,1}, "Star K1,3"),
            new TestCase("Bip_N4_C", new int[]{1,1,1,1}, "2K2"),
            new TestCase("Bip_N5_B", new int[]{4,1,1,1,1}, "Star K1,4"),
            new TestCase("Bip_N5_C", new int[]{2,2,2,1,1}, "C4 plus isolated vertex"),
            new TestCase("Bip_N6_D", new int[]{2,2,2,2,2,2}, "C6"),

            // ============ BIPARTITE WITH BOUNDED DEGREE ============

            // 18. DEGREE-BOUNDED BIPARTITE GRAPHS
            new TestCase("BipDegBound_1", new int[]{1,1,1,1,1,1,1,1},
                    "Perfect matching on 8 vertices, N=8, degree=1 ≤ 4"),
            new TestCase("BipDegBound_2", new int[]{2,2,2,2,2,2,2,2,2,2},
                    "2-regular bipartite (union of even cycles), N=10, degree=2 ≤ 5"),
            new TestCase("BipDegBound_3", new int[]{3,3,3,3,3,3,3,3},
                    "Cubic bipartite, N=8, degree=3 ≤ 4"),

            // ============ BIPARTITE COMPLEMENTS OF KNOWN GRAPHS ============

            // 19. COMPLEMENTS OF BIPARTITE GRAPHS (still bipartite if degrees ≤ N/2)
            new TestCase("BipComp_K2_2", new int[]{1,1,1,1},
                    "Complement of K2,2 = 2K2, N=4, degree=1 ≤ 2"),
            new TestCase("BipComp_K3_3", new int[]{2,2,2,2,2,2},
                    "Complement of K3,3 = 2C3, N=6, degree=2 ≤ 3"),

            // ============ BIPARTITE LINE GRAPHS ============

            // 20. LINE GRAPHS OF BIPARTITE GRAPHS
            new TestCase("LineGraph_K1_3", new int[]{2,2,2},
                    "Line graph of K1,3 = triangle, but wait... Actually K3"),
            new TestCase("LineGraph_K2_2", new int[]{2,2,2,2},
                    "Line graph of K2,2 = C4, N=4"),


            // ============ EXTREME CASES WITH DEGREES = N/2 ============

            // 21. MAXIMUM DEGREE = N/2 CASES
            new TestCase("BipMaxDeg_N4", new int[]{2,2,2,2},
                    "K2,2: degree=2 = 4/2, N=4"),
            new TestCase("BipMaxDeg_N8", new int[]{4,4,4,4,4,4,4,4},
                    "K4,4: degree=4 = 8/2, N=8"),
            //new TestCase("BipMaxDeg_N10", new int[]{5,5,5,5,5,5,5,5,5,5},
            //        "K5,5: degree=5 = 10/2, N=10"),

            // ============ DEGENERATE/EDGE CASES ============



            // 23. BIPARTITE WITH ISOLATED VERTICES
            new TestCase("BipWithIsolated", new int[]{3,1,1,1,0,0},
                    "Star K1,3 plus 2 isolates, N=6, max degree=3 ≤ 3"),


    };
    //Graphs with Known Isomorphism Issues
    public static TestCase[] IsomorphismSamples = {
            // Pair of non-isomorphic graphs with same degree sequence
            new TestCase("Pair1_A", new int[]{3,3,3,3,2,2,2,2}, "First graph of pair (two 4-cycles joined)"),
            new TestCase("Pair1_B", new int[]{3,3,3,3,2,2,2,2}, "Second graph of pair (different structure)"),

            // Another classic pair
            new TestCase("Pair2_A", new int[]{3,3,2,2,2,2}, "Graph A: degree sequence with multiple realizations"),
            new TestCase("Pair2_B", new int[]{3,3,2,2,2,2}, "Graph B: isomorphic to A? Should be detected"),

            // Trees with same degree sequence
            new TestCase("TreePair_A", new int[]{3,2,2,1,1,1,1}, "Tree structure 1"),
            new TestCase("TreePair_B", new int[]{3,2,2,1,1,1,1}, "Tree structure 2"),
    };

    //Special Graph Families : Wheel graphs, Friendship graphs, Complete multipartite, Prism graphs, Ladder graphs
    public static TestCase[] SpecialSamples = {
            // Wheel graphs
            new TestCase("Wheel4", new int[]{3,3,3,3}, "W4 (K4)"),
            new TestCase("Wheel5", new int[]{4,3,3,3,3}, "W5"),
            new TestCase("Wheel6", new int[]{5,3,3,3,3,3}, "W6"),

            // Friendship graphs
            new TestCase("Friendship3", new int[]{4,2,2,2,2,2,2}, "3 triangles sharing common vertex"),

            // Complete multipartite
            new TestCase("K2_2_2", new int[]{4,4,4,4,4,4}, "Complete tripartite K2,2,2"),

            // Prism graphs
            new TestCase("TriangularPrism", new int[]{3,3,3,3,3,3}, "Prism over triangle"),

            // Ladder graphs
            new TestCase("Ladder4", new int[]{2,3,3,2,2,3,3,2}, "Ladder graph P2×P4"),
    };



    //Mixed/Real-World Like
    public static TestCase[] MixedSamples = {
            // Power-law like (scale-free networks)
            new TestCase("ScaleFree1", new int[]{4,3,3,2,2,2,1,1}, "Small scale-free like"),
            new TestCase("ScaleFree2", new int[]{5,4,3,3,2,2,2,1,1,1}, "Larger scale-free like"),

            // Sparse graphs
            new TestCase("Sparse1", new int[]{2,2,1,1}, "Two edges sharing vertex"),
            new TestCase("Sparse2", new int[]{3,2,2,1,1,1}, "Small sparse connected"),

            // Dense non-complete
            new TestCase("Dense1", new int[]{4,4,4,3,3}, "K5 minus one edge"),
            new TestCase("Dense2", new int[]{5,5,4,4,4,4}, "K6 minus matching"),
    };

    //Challenge Cases for Lexicographic Constraint
    public static TestCase[] ChallengeSamples = {
            // Highly symmetric (many automorphisms)
            new TestCase("HighSym1", new int[]{1,1,1,1,1,1}, "3 disjoint edges"),
            new TestCase("HighSym2", new int[]{2,2,2,2,2,2,2,2}, "Two disjoint 4-cycles"),

            // Regular with many isomorphic representations
            new TestCase("Cubic8", new int[]{3,3,3,3,3,3,3,3}, "Cubic graph on 8 vertices (many)"),
            new TestCase("4Reg8", new int[]{4,4,4,4,4,4,4,4}, "4-regular on 8 vertices"),

            // Near-regular
            new TestCase("NearReg1", new int[]{4,4,4,4,4,3}, "Almost regular"),
            new TestCase("NearReg2", new int[]{5,5,5,4,4,4,4}, "Bimodal degrees"),
    };

    //Edge Cases
    public static TestCase[] EdgeCaseSamples = {
            new TestCase("Empty", new int[]{}, "Empty graph"),
            new TestCase("Single", new int[]{0}, "Single vertex"),
            new TestCase("TwoVertices", new int[]{1,1}, "Single edge"),
            new TestCase("Disconnected", new int[]{2,2,1,1}, "Cycle + edge"),
            new TestCase("IsolatedVertex", new int[]{3,2,2,1,0}, "With isolated vertex"),
    };


}

