package edu.polytech.cpmolgenapi.benchmarkingOrders;



import ilog.concert.*;
import ilog.cp.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Benchmark: Lex Symmetry-Breaking Orders on Regular Graph Enumeration (CP Optimizer)
 *
 * Orders tested:
 *   ROW_LEX      -- consecutive row pairs ordered lex
 *   COLUMN_LEX   -- consecutive column pairs ordered lex
 *   DOUBLE_LEX   -- rows AND columns ordered lex simultaneously
 *   REVERSE_LEX  -- rows compared in reversed order
 *   SNAKE_LEX    -- snake column order: even cols top->bot, odd cols bot->top (Flener et al. 2002)
 *   SNAKE_REVLEX -- snake column order reversed: even cols bot->top, odd cols top->bot
 *   ALL_ROW_LEX  -- every pair of rows (not just consecutive) ordered lex
 *   ALL_COL_LEX  -- every pair of columns (not just consecutive) ordered lex
 */
public class RegularGraphOrderBenchmark {

    // ==========================================================================
    // Order types
    // ==========================================================================

    enum OrderType {
        ROW_LEX,
        COLUMN_LEX,
        DOUBLE_LEX,
        REVERSE_LEX,
        SNAKE_LEX,
        SNAKE_REVLEX,
        ALL_ROW_LEX,
        ALL_COL_LEX
    }

    // ==========================================================================
    // Result record
    // ==========================================================================

    static class Result {
        final String    instanceName;
        final OrderType orderType;
        final int       n;
        final int       degree;
        final long      solutionCount;
        final long      elapsedTimeMs;
        final long      fails;
        final long      branches;
        final long      choicePoints;
        final long      constraints;
        final boolean   solved;

        Result(String instanceName, OrderType orderType, int n, int degree,
               long solutionCount, long elapsedTimeMs, long fails,
               long branches, long choicePoints, long constraints, boolean solved) {
            this.instanceName  = instanceName;
            this.orderType     = orderType;
            this.n             = n;
            this.degree        = degree;
            this.solutionCount = solutionCount;
            this.elapsedTimeMs = elapsedTimeMs;
            this.fails         = fails;
            this.branches      = branches;
            this.choicePoints  = choicePoints;
            this.constraints   = constraints;
            this.solved        = solved;
        }

        String toCsvRow() {
            return String.join(",",
                    instanceName, orderType.name(),
                    String.valueOf(n), String.valueOf(degree),
                    String.valueOf(solutionCount), String.valueOf(elapsedTimeMs),
                    String.valueOf(fails), String.valueOf(branches),
                    String.valueOf(choicePoints), String.valueOf(constraints),
                    String.valueOf(solved));
        }
    }

    // ==========================================================================
    // Instance descriptor
    // ==========================================================================

    static class RegularInstance {
        final String name;
        final int    n;
        final int    degree;

        RegularInstance(String name, int n, int degree) {
            this.name   = name;
            this.n      = n;
            this.degree = degree;
        }

        int[] degreeSequence() {
            int[] d = new int[n];
            Arrays.fill(d, degree);
            return d;
        }
    }

    // ==========================================================================
    // Entry point
    // ==========================================================================

    public static void main(String[] args) {
        List<RegularInstance> instances = buildInstances();
        List<Result>          results   = new ArrayList<>();

        printHeader();

        for (RegularInstance instance : instances) {
            if (!isFeasible(instance.n, instance.degree)) {
                System.out.printf("  [SKIP] %s -- infeasible degree sequence%n", instance.name);
                continue;
            }

            System.out.printf("%n-- Instance: %s  (n=%d, d=%d) --%n",
                    instance.name, instance.n, instance.degree);

            for (OrderType order : OrderType.values()) {
                try {
                    Result r = solveInstance(instance, order);
                    results.add(r);
                    printResult(r);
                } catch (Exception e) {
                    System.err.printf("  ERROR  %-16s : %s%n", order, e.getMessage());
                    results.add(new Result(instance.name, order, instance.n, instance.degree,
                            -1, -1, -1, -1, -1, -1, false));
                }
            }
        }

        String csvPath = "output/benchmark_results.csv";
        writeCsv(results, csvPath);
        System.out.printf("%nResults written to %s%n", csvPath);
    }

    // ==========================================================================
    // Instance catalogue
    // ==========================================================================

    static List<RegularInstance> buildInstances() {
        List<RegularInstance> instances = new ArrayList<>();

        // Small regular graphs -- good for solution-count verification
        instances.add(new RegularInstance("RG_04_2",  4,  2));
        instances.add(new RegularInstance("RG_04_3",  4,  3));
        instances.add(new RegularInstance("RG_06_2",  6,  2));
        instances.add(new RegularInstance("RG_06_3",  6,  3));
        instances.add(new RegularInstance("RG_06_4",  6,  4));
        instances.add(new RegularInstance("RG_06_5",  6,  5));
        instances.add(new RegularInstance("RG_08_2",  8,  2));
        instances.add(new RegularInstance("RG_08_3",  8,  3));
        instances.add(new RegularInstance("RG_08_4",  8,  4));
        instances.add(new RegularInstance("RG_10_2", 10,  2));
        instances.add(new RegularInstance("RG_10_3", 10,  3));
        instances.add(new RegularInstance("RG_10_4", 10,  4));
        instances.add(new RegularInstance("RG_12_2", 12,  2));
        instances.add(new RegularInstance("RG_12_3", 12,  3));
        instances.add(new RegularInstance("RG_14_3", 14,  3));

        return instances;
    }

    // ==========================================================================
    // Feasibility (Erdos-Gallai for regular graphs)
    // ==========================================================================

    static boolean isFeasible(int n, int d) {
        return d >= 1 && d < n && (n * d) % 2 == 0;
    }

    // ==========================================================================
    // Core solver -- enumerate all solutions
    // ==========================================================================

    static Result solveInstance(RegularInstance instance, OrderType orderType)
            throws IloException {

        int   N      = instance.n;
        int[] DEGREE = instance.degreeSequence();

        IloCP         cp     = new IloCP();
        IloIntVar[][] MATRIX = new IloIntVar[N][];
        for (int i = 0; i < N; i++) {
            MATRIX[i] = cp.intVarArray(N, 0, 1);
        }

        addCoreConstraints(cp, MATRIX, DEGREE, orderType);
        addSymmetryBreaking(cp, MATRIX, orderType);

        cp.setParameter(IloCP.IntParam.LogVerbosity,          IloCP.ParameterValues.Quiet);
        cp.setParameter(IloCP.IntParam.SearchType,            IloCP.ParameterValues.DepthFirst);
        cp.setParameter(IloCP.IntParam.DefaultInferenceLevel, IloCP.ParameterValues.Low);
        cp.setParameter(IloCP.IntParam.MemoryDisplay,         0);

        long solutionCount = 0;
        long startTime     = System.currentTimeMillis();

        cp.startNewSearch();
        while (cp.next()) {
            solutionCount++;
        }
        cp.endSearch();

        long elapsed      = System.currentTimeMillis() - startTime;
        long fails        = cp.getInfo(IloCP.IntInfo.NumberOfFails);
        long branches     = cp.getInfo(IloCP.IntInfo.NumberOfBranches);
        long choicePoints = cp.getInfo(IloCP.IntInfo.NumberOfChoicePoints);
        long constraints  = cp.getInfo(IloCP.IntInfo.NumberOfConstraints);

        cp.end();

        return new Result(instance.name, orderType, instance.n, instance.degree,
                solutionCount, elapsed, fails, branches, choicePoints, constraints, true);
    }

    // ==========================================================================
    // Graph constraints
    // ==========================================================================

    /**
     * Core graph constraints.
     * Diagonal value:
     *   REVERSE_LEX, SNAKE_LEX, SNAKE_REVLEX -- set to 1
     *     These orders break row-permutation symmetry, so rows must be free to move.
     *     A fixed 0 on the diagonal of row i pins it to position i (the zero column
     *     must stay on the diagonal), preventing the solver from reaching the
     *     canonical row ordering the symmetry-breaking constraint requires.
     *     Setting diag=1 removes that pin; the degree constraint is satisfied
     *     by having (degree - 1) ones among the off-diagonal entries.
     *   all others -- set to 0 (standard no-self-loop adjacency matrix)
     */
    static void addCoreConstraints(IloCP cp, IloIntVar[][] MATRIX, int[] DEGREE,
                                   OrderType orderType) throws IloException {
        int N = MATRIX.length;

        // Orders that require diagonal = 1
        boolean diagOne = (orderType == OrderType.REVERSE_LEX)
                || (orderType == OrderType.SNAKE_LEX)
                || (orderType == OrderType.SNAKE_REVLEX);
        int diagVal = diagOne ? 1 : 0;
        for (int i = 0; i < N; i++) {
            cp.add(cp.eq(MATRIX[i][i], diagVal));
        }

        // Degree constraint per vertex
        for (int i = 0; i < N; i++) {
            cp.add(cp.eq(cp.sum(MATRIX[i]), DEGREE[i]));
        }

        // Symmetry of adjacency matrix: MATRIX[i][j] == MATRIX[j][i]
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
            }
        }
    }

    // ==========================================================================
    // Symmetry-breaking dispatcher
    // ==========================================================================

    static void addSymmetryBreaking(IloCP cp, IloIntVar[][] MATRIX, OrderType orderType)
            throws IloException {
        switch (orderType) {
            case ROW_LEX:     addConsecutiveRowLex(cp, MATRIX); break;
            case COLUMN_LEX:  addConsecutiveColLex(cp, MATRIX); break;
            case DOUBLE_LEX:  addDoubleLex(cp, MATRIX);         break;
            case REVERSE_LEX: addReverseLex(cp, MATRIX);        break;
            case SNAKE_LEX:     addSnakeLex(cp, MATRIX, false);    break;
            case SNAKE_REVLEX:  addSnakeLex(cp, MATRIX, true);     break;
            case ALL_ROW_LEX: addAllPairRowLex(cp, MATRIX);     break;
            case ALL_COL_LEX: addAllPairColLex(cp, MATRIX);     break;
            default: throw new IllegalArgumentException("Unknown order: " + orderType);
        }
    }

    // ==========================================================================
    // Lex order implementations
    // ==========================================================================

    /**
     * ROW_LEX: consecutive row pairs only.
     *   row[i] <=lex row[i+1]  for i in [0, N-2]
     */
    static void addConsecutiveRowLex(IloCP cp, IloIntVar[][] M) throws IloException {
        for (int i = 0; i < M.length - 1; i++) {
            cp.add(cp.lexicographic(M[i], M[i + 1]));
        }
    }

    /**
     * COLUMN_LEX: consecutive column pairs only.
     *   col[j] <=lex col[j+1]  for j in [0, N-2]
     */
    static void addConsecutiveColLex(IloCP cp, IloIntVar[][] M) throws IloException {
        for (int j = 0; j < M.length - 1; j++) {
            cp.add(cp.lexicographic(getColumn(M, j), getColumn(M, j + 1)));
        }
    }

    /**
     * DOUBLE_LEX: consecutive row pairs AND consecutive column pairs.
     */
    static void addDoubleLex(IloCP cp, IloIntVar[][] M) throws IloException {
        addConsecutiveRowLex(cp, M);
        addConsecutiveColLex(cp, M);
    }

    /**
     * REVERSE_LEX: ALL row pairs (i,j) with i < j, each row reversed before comparing.
     *   reverseArray(row[i]) <=lex reverseArray(row[j])  for all i < j
     *
     * Each IloIntVar[] row is widened to IloIntExpr[] so it can be passed
     * to the general-purpose reverseArray helper.
     */
    static void addReverseLex(IloCP cp, IloIntVar[][] M) throws IloException {
        int N = M.length;
        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                IloIntExpr[] reversedI = reverseArray(M[i]);
                IloIntExpr[] reversedJ = reverseArray(M[j]);
                cp.add(cp.lexicographic(reversedI, reversedJ));
            }
        }
    }

    /**
     * SNAKE_LEX / SNAKE_REVLEX (Flener et al., CP 2002):
     *
     * Defined on COLUMNS, not rows. A "snake column" is read in alternating
     * directions so that the scan path through the matrix is continuous
     * (boustrophedon / snake traversal):
     *
     * SNAKE_LEX    (revlex=false):
     *   col j even : read top -> bottom   [M[0][j], M[1][j], ..., M[N-1][j]]
     *   col j odd  : read bottom -> top   [M[N-1][j], ..., M[1][j], M[0][j]]
     *   Constraint : snakeCol(j) <=lex snakeCol(j+1)  for j in [0, N-2]
     *
     * SNAKE_REVLEX (revlex=true) -- scan directions flipped:
     *   col j even : read bottom -> top
     *   col j odd  : read top -> bottom
     *   Constraint : snakeCol(j) <=lex snakeCol(j+1)  for j in [0, N-2]
     *
     * Verified on K4 (RG_04_3): both variants correctly return 1 solution.
     */
    static void addSnakeLex(IloCP cp, IloIntVar[][] M, boolean revlex) throws IloException {
        int N = M.length;
        for (int j = 0; j < N - 1; j++) {
            IloIntExpr[] colJ    = snakeColumn(M, j,     revlex);
            IloIntExpr[] colJp1  = snakeColumn(M, j + 1, revlex);
            cp.add(cp.lexicographic(colJ, colJp1));
        }
    }


    /**
     * ALL_ROW_LEX: ALL row pairs (i,j) with i < j -- stronger than consecutive.
     *   row[i] <=lex row[j]  for all i < j
     */
    static void addAllPairRowLex(IloCP cp, IloIntVar[][] M) throws IloException {
        int N = M.length;
        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                cp.add(cp.lexicographic(M[i], M[j]));
            }
        }
    }

    /**
     * ALL_COL_LEX: ALL column pairs (i,j) with i < j.
     *   col[i] <=lex col[j]  for all i < j
     */
    static void addAllPairColLex(IloCP cp, IloIntVar[][] M) throws IloException {
        int N = M.length;
        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                cp.add(cp.lexicographic(getColumn(M, i), getColumn(M, j)));
            }
        }
    }

    // ==========================================================================
    // Array helpers
    // ==========================================================================

    static IloIntExpr[] getColumn(IloIntVar[][] M, int col) {
        IloIntExpr[] column = new IloIntExpr[M.length];
        for (int i = 0; i < M.length; i++) {
            column[i] = M[i][col];
        }
        return column;
    }

    /**
     * Reverses an IloIntExpr[] array (right-to-left).
     * Accepts IloIntVar[] implicitly via Java covariance when called with M[i].
     */
    static IloIntExpr[] reverseArray(IloIntExpr[] array) {
        int          len     = array.length;
        IloIntExpr[] reversed = new IloIntExpr[len];
        for (int i = 0; i < len; i++) {
            reversed[i] = array[len - 1 - i];
        }
        return reversed;
    }



    /**
     * Returns the snake-read of column j:
     *   revlex=false (SNAKE_LEX)    : even j -> top-to-bottom, odd j -> bottom-to-top
     *   revlex=true  (SNAKE_REVLEX) : even j -> bottom-to-top, odd j -> top-to-bottom
     */
    static IloIntExpr[] snakeColumn(IloIntVar[][] M, int j, boolean revlex) {
        int          N      = M.length;
        IloIntExpr[] col    = new IloIntExpr[N];
        boolean      flipJ  = (j % 2 != 0);          // odd column -> flip direction
        boolean      reverse = revlex ? !flipJ : flipJ; // revlex inverts the parity rule
        for (int i = 0; i < N; i++) {
            col[i] = reverse ? M[N - 1 - i][j] : M[i][j];
        }
        return col;
    }

    // ==========================================================================
    // Console output
    // ==========================================================================

    static void printHeader() {
        System.out.println("=".repeat(105));
        System.out.println("  Regular Graph Enumeration -- Lex Symmetry-Breaking Benchmark (CP Optimizer)");
        System.out.println("=".repeat(105));
        System.out.printf("  %-18s  %-14s  %9s  %9s  %9s  %9s  %9s  %8s%n",
                "Instance", "Order", "Solutions", "Time(ms)", "Fails", "Branches", "ChoicePts", "Constrs");
        System.out.println("-".repeat(105));
    }

    static void printResult(Result r) {
        System.out.printf("  %-18s  %-14s  %9d  %9d  %9d  %9d  %9d  %8d%n",
                r.instanceName, r.orderType,
                r.solutionCount, r.elapsedTimeMs,
                r.fails, r.branches, r.choicePoints, r.constraints);
    }

    // ==========================================================================
    // CSV output
    // ==========================================================================

    static void writeCsv(List<Result> results, String path) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("instance,order,n,degree,solutionCount,elapsedTimeMs," +
                    "fails,branches,choicePoints,constraints,solved");
            for (Result r : results) {
                pw.println(r.toCsvRow());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV: " + e.getMessage(), e);
        }
    }
}