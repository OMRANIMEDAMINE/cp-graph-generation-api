package edu.polytech.cpmolgenapi;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntVar;
import ilog.cp.IloCP;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import java.io.IOException;

public class OpLexVsOpRevLexVsHybrid {


    public static Result testLex(int[] DEGREE) {
        try {
            int N = DEGREE.length; // Example size of adjacency matrix
            IloCP cp = new IloCP();

            // Define Vars of the Adjacency matrix
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, 0, 1);
            }

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(MATRIX[i][i], 0));
            }

            // Constraint 2: Define Degree Constraint
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]);
            }

            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            // Constraint 3: Symmetry breaking Opt Lex
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                        cp.add(cp.lexicographic(MATRIX[i], MATRIX[j]));           // Rows
                    }
                }
            }

            /*for (int i = 0; i < N - 1; i++) { // double implication for Lex // VALID IN TEST BUT OptLex is better
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {

                        IloConstraint prefix = null;

                        for (int k = 0; k < N; k++) {

                            IloConstraint le = cp.le(MATRIX[i][k], MATRIX[j][k]);

                            if (k == 0) {
                                cp.add(le);
                            } else {
                                IloConstraint eq = cp.eq(MATRIX[i][k-1], MATRIX[j][k-1]);
                                cp.add(cp.imply(eq, le));
                            }
                        }
                    }
                }
            }*/

           /*
            // Lex on columns (by transposing logic) // USED FOR COLS and DOUBLE LEX
            for (int i = 0; i < N-1; i++) {
                for (int j = i+1; j < N; j++) {
                    IloIntVar[] colI = new IloIntVar[N];
                    IloIntVar[] colJ = new IloIntVar[N];
                    for (int k = 0; k < N; k++) {
                        colI[k] = MATRIX[k][i];
                        colJ[k] = MATRIX[k][j];
                    }
                    cp.add(cp.lexicographic(colI, colJ));
                }
            }*/
            // Configure solver for memory optimization
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet); // Suppress logs
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst); // Depth-first search
            cp.setParameter(IloCP.IntParam.DefaultInferenceLevel, IloCP.ParameterValues.Low); // Low inference level
            cp.setParameter(IloCP.IntParam.MemoryDisplay, 0); // 0 disable , 1 Enable memory usage display


            // Measure execution time
            long startTime = System.currentTimeMillis();

            String filename = "output_testLex.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            while (cp.next()) {
                solutionCount++;
                ok = true;
                //System.out.print(" \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        //System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        writer.print(" " + (int) cp.getValue(MATRIX[i][j]));
                    }
                    //System.out.print(" \n");
                    writer.println();
                }
                writer.println();
                writer.println();
            }
               /*while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }*/
            writer.close();
            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            // Collect solver diagnostics
            long fails = cp.getInfo(IloCP.IntInfo.NumberOfFails);
            long branches = cp.getInfo(IloCP.IntInfo.NumberOfBranches);
            long choicePoints = cp.getInfo(IloCP.IntInfo.NumberOfChoicePoints);
            long constraints = cp.getInfo(IloCP.IntInfo.NumberOfConstraints);

            return new Result(solutionCount, elapsedTime, fails, branches, choicePoints, constraints);
            //return new Result(solutionCount, elapsedTime);
        } catch (IloException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Result testOptimizedLex(int[] DEGREE) {
        try {
            int N = DEGREE.length; // Example size of adjacency matrix
            IloCP cp = new IloCP();

            // Define Vars of the Adjacency matrix
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, 0, 1);
            }

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(MATRIX[i][i], 0));
            }

            // Constraint 2: Define Degree Constraint
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]);
                // cp.addLe(cp.sum(MATRIX[i]), DEGREE[i]); // Used for Bounded Graphs
            }

            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            // Constraint 3: Symmetry breaking Opt Lex
            /* OK WORKS FINE*/

            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if ((DEGREE[i] == DEGREE[j])) {
                        IloIntExpr[] reversedMatrixI = arrayNew(MATRIX[i], i, j);
                        IloIntExpr[] reversedMatrixJ = arrayNew(MATRIX[j], i, j);
                        //cp.add(cp.lexicographic(reversedMatrixJ, reversedMatrixI)); // Anti_lex
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            }


            // Configure solver for memory optimization
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet); // Suppress logs
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst); // Depth-first search
            cp.setParameter(IloCP.IntParam.DefaultInferenceLevel, IloCP.ParameterValues.Low); // Low inference level
            cp.setParameter(IloCP.IntParam.MemoryDisplay, 0); // 0 disable , 1 Enable memory usage display

            // Measure execution time
            long startTime = System.currentTimeMillis();


            // Create timestamp for filename
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
            String timestamp = sdf.format(new Date());
            String filename = "output_testOptimizedLex.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            while (cp.next()) {
                solutionCount++;
                ok = true;
                //System.out.print(" \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        //System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        writer.print(" " + (int) cp.getValue(MATRIX[i][j]));
                    }
                    //System.out.print(" \n");
                    writer.println();
                }
                writer.println();
                writer.println();
            }
            /*while (cp.next()) {
                // 🔥 Your "callback"
                int[][] currentMatrix = new int[N][N];

                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        currentMatrix[i][j] = (int) cp.getValue(MATRIX[i][j]);
                    }
                }

                // 🔥 Canonical filtering
                if (!CanonicalChecker.verifyCanonical(currentMatrix)) {
                    continue;
                }
                solutionCount++; // Count solutions without storing them

            }*/
           /* while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }*/
            writer.close();
            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            // Collect solver diagnostics
            long fails = cp.getInfo(IloCP.IntInfo.NumberOfFails);
            long branches = cp.getInfo(IloCP.IntInfo.NumberOfBranches);
            long choicePoints = cp.getInfo(IloCP.IntInfo.NumberOfChoicePoints);
            long constraints = cp.getInfo(IloCP.IntInfo.NumberOfConstraints);

            return new Result(solutionCount, elapsedTime, fails, branches, choicePoints, constraints);
            //return new Result(solutionCount, elapsedTime);
        } catch (IloException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

     public static Result testOptimizedLexCon(int[] DEGREE) {
        try {
            int N = DEGREE.length; // Example size of adjacency matrix
            IloCP cp = new IloCP();

            // Define Vars of the Adjacency matrix
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, 0, 1);
            }

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(MATRIX[i][i], 0));
            }

            // Constraint 2: Define Degree Constraint
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]);
            }

            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            // Constraint 3: Symmetry breaking Opt Lex
            /* OK WORKS FINE*/
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if ((DEGREE[i] == DEGREE[j])) {
                        IloIntExpr[] reversedMatrixI = arrayNew(MATRIX[i], i, j);
                        IloIntExpr[] reversedMatrixJ = arrayNew(MATRIX[j], i, j);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            }

            // Constraint Of connectivity using Upper Off-Diagonal Technique

            // Generation of K_i Variables
            // Define the distance variables
            IloIntVar[] z = new IloIntVar[N];
            for (int i = 0; i < N; i++) {
                z[i] = cp.intVar(0, N - 1);
            }

            // Root node distance is 0
            cp.addEq(z[0], 0);
            // For all other nodes, distances must be greater than 0
            for (int i = 1; i < N; i++) {
                cp.addGe(z[i], 1);
            }

            for (int i = 1; i < N; i++) {
                for (int k = 1; k < N; k++) {
                    IloConstraint[] neighborConstraints = new IloConstraint[N - 1];
                    int index = 0;
                    for (int j = 0; j < N; j++) {
                        if (j != i) {
                            // Combine constraints into an array
                            IloConstraint[] combinedConstraints = new IloConstraint[2];
                            combinedConstraints[0] = cp.neq(MATRIX[i][j], 0);
                            combinedConstraints[1] = cp.eq(z[j], k - 1);

                            // Use cp.and with an array of constraints
                            neighborConstraints[index++] = cp.and(combinedConstraints);
                        }
                    }
                    cp.add(cp.ifThen(
                            cp.eq(z[i], k),
                            cp.or(neighborConstraints)
                    ));


                }
            }

            // ajouter pour optimiser les solution redondants due à la variable Z.
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.ifThen(
                            cp.neq(MATRIX[i][j], 0),
                            cp.le(cp.abs(cp.diff(z[i], z[j])), 1)
                    ));
                    //si aij >0 alors abs( zi - zj) <= 1
                }
            }


            // 5. Adapted Upper Off-Diagonal Connectivity for Lex
/*
            // Best practical connectivity attempt for Optimized Lex - OFF DIAG IDEA NOT WORKS WITH LEX
            // (Lower off-diagonal focused on later rows)
            for (int i = 2; i < N; i++) {           // Start from row 2 — best compromise
                IloIntVar[] lowerPart = new IloIntVar[i];
                for (int j = 0; j < i; j++) {
                    lowerPart[j] = MATRIX[i][j];     // Must connect to at least one previous vertex
                }
                cp.add(cp.gt(cp.sum(lowerPart), 0));
            }*/
            // Configure solver for memory optimization
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet); // Suppress logs
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst); // Depth-first search
            cp.setParameter(IloCP.IntParam.DefaultInferenceLevel, IloCP.ParameterValues.Low); // Low inference level
            cp.setParameter(IloCP.IntParam.MemoryDisplay, 0); // 0 disable , 1 Enable memory usage display

            // Measure execution time
            long startTime = System.currentTimeMillis();


            // Create timestamp for filename
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
            String timestamp = sdf.format(new Date());
            String filename = "output_testOptimizedLexCon.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            while (cp.next()) {
                solutionCount++;


                ok = true;
                //System.out.print(" \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        //System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        writer.print(" " + (int) cp.getValue(MATRIX[i][j]));
                    }
                    //System.out.print(" \n");
                    writer.println();
                }
                writer.println();
                writer.println();
            }
               /*while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }*/
            writer.close();
            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            // Collect solver diagnostics
            long fails = cp.getInfo(IloCP.IntInfo.NumberOfFails);
            long branches = cp.getInfo(IloCP.IntInfo.NumberOfBranches);
            long choicePoints = cp.getInfo(IloCP.IntInfo.NumberOfChoicePoints);
            long constraints = cp.getInfo(IloCP.IntInfo.NumberOfConstraints);

            return new Result(solutionCount, elapsedTime, fails, branches, choicePoints, constraints);
            //return new Result(solutionCount, elapsedTime);
        } catch (IloException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

     public static Result testRevLex(int[] DEGREE) {
        try {
            int N = DEGREE.length; // Example size of adjacency matrix

            // Define Vars of the Adjacency matrix
            IloCP cp = new IloCP();

            // Define Vars of the Adjacency matrix
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, 0, 1);
            }

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(MATRIX[i][i], 1));
            }

            /*// Constraint: Define Degree Constraint sum of the row, excluding the diagonal element
            for (int i = 0; i < N; i++) {
                // Sum of the entire row
                IloIntExpr rowSum = cp.sum(MATRIX[i]);
                // Subtract the diagonal element (MATRIX[i][i])
                IloIntExpr sumExceptDiagonal = cp.diff(rowSum, MATRIX[i][i]);
                // Add the constraint
                cp.addEq(sumExceptDiagonal, DEGREE[i]);
            }*/
            // Adjust degree constraint: subtract 1 for the diagonal
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(MATRIX[i]), DEGREE[i] + 1); // +1 accounts for diagonal
            }


            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            //   Constraint 3: Symmetry breaking RevLex
            /* OK WORKS FINE*/
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                        IloIntExpr[] reversedMatrixI = reverseArray(MATRIX[i]);
                        IloIntExpr[] reversedMatrixJ = reverseArray(MATRIX[j]);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            }


            // Constraint: Symmetry breaking CoLex (RevLex) Same code

// =============================================
            // 4. Symmetry Breaking: DOUBLE COLEX / DOUBLE RevLex
            // =============================================
            // --- Colex on Rows ---
           /* for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                        IloIntVar[] rowI_rev = new IloIntVar[N];
                        IloIntVar[] rowJ_rev = new IloIntVar[N];
                        for (int k = 0; k < N; k++) {
                            rowI_rev[k] = MATRIX[i][N - 1 - k];
                            rowJ_rev[k] = MATRIX[j][N - 1 - k];
                        }
                        cp.add(cp.lexicographic(rowI_rev, rowJ_rev));   // Colex on rows
                    }
                }
            }*/


            // --- Colex on Columns ---
            /*
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {

                    IloIntVar[] colI_rev = new IloIntVar[N];
                    IloIntVar[] colJ_rev = new IloIntVar[N];

                    for (int k = 0; k < N; k++) {
                        colI_rev[k] = MATRIX[N - 1 - k][i];   // reverse column i
                        colJ_rev[k] = MATRIX[N - 1 - k][j];   // reverse column j
                    }

                    cp.add(cp.lexicographic(colI_rev, colJ_rev));   // Colex on columns
                }
            }*/


            // Configure solver for memory optimization
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet); // Suppress logs
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst); // Depth-first search
            cp.setParameter(IloCP.IntParam.DefaultInferenceLevel, IloCP.ParameterValues.Low); // Low inference level
            cp.setParameter(IloCP.IntParam.MemoryDisplay, 0); // 0 disable , 1 Enable memory usage display



            // Measure execution time
            long startTime = System.currentTimeMillis();

            // Create timestamp for filename
            String filename = "output_testRevLex.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            while (cp.next()) {
                solutionCount++;
                ok = true;
                //System.out.print(" \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        //System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        writer.print(" " + (int) cp.getValue(MATRIX[i][j]));
                    }
                    //System.out.print(" \n");
                    writer.println();
                }
                writer.println();
                writer.println();
            }
            /*while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }*/
            writer.close();
            cp.endSearch(); // End the search

// Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

// Collect solver diagnostics
            long fails = cp.getInfo(IloCP.IntInfo.NumberOfFails);
            long branches = cp.getInfo(IloCP.IntInfo.NumberOfBranches);
            long choicePoints = cp.getInfo(IloCP.IntInfo.NumberOfChoicePoints);
            long constraints = cp.getInfo(IloCP.IntInfo.NumberOfConstraints);

            return new Result(solutionCount, elapsedTime, fails, branches, choicePoints, constraints);
            // return new Result(solutionCount, elapsedTime);

        } catch (IloException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

     public static Result testOptimizedRevLex(int[] DEGREE) {
        try {
            int N = DEGREE.length; // Example size of adjacency matrix

            // Define Vars of the Adjacency matrix
            IloCP cp = new IloCP();

            // Define Vars of the Adjacency matrix
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, 0, 1);
            }

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(MATRIX[i][i], 1));
            }

            // Constraint: Define Degree Constraint sum of the row, excluding the diagonal element
            for (int i = 0; i < N; i++) {
                // Sum of the entire row
                IloIntExpr rowSum = cp.sum(MATRIX[i]);
                // Subtract the diagonal element (MATRIX[i][i])
                IloIntExpr sumExceptDiagonal = cp.diff(rowSum, MATRIX[i][i]);
                // Add the constraint
                cp.addEq(sumExceptDiagonal, DEGREE[i]);
                //cp.addLe(sumExceptDiagonal, DEGREE[i]);  // used for Bounded Graphs
            }


            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            //   Constraint 3: Symmetry breaking RevLex
            /* OK WORKS FINE*/
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                        IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                        IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            }

            // Configure solver for memory optimization
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet); // Suppress logs
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst); // Depth-first search
            cp.setParameter(IloCP.IntParam.DefaultInferenceLevel, IloCP.ParameterValues.Low); // Low inference level
            cp.setParameter(IloCP.IntParam.MemoryDisplay, 0); // 0 disable , 1 Enable memory usage display

            // Measure execution time
            long startTime = System.currentTimeMillis();

            // Create timestamp for filename
            String filename = "output_testOptimizedRevLex.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            while (cp.next()) {
                solutionCount++;
                ok = true;
                //System.out.print(" \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        //System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        writer.print(" " + (int) cp.getValue(MATRIX[i][j]));
                    }
                    //System.out.print(" \n");
                    writer.println();
                }
                writer.println();
                writer.println();
            }
           /* while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }*/
            writer.close();
            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            // Collect solver diagnostics
            long fails = cp.getInfo(IloCP.IntInfo.NumberOfFails);
            long branches = cp.getInfo(IloCP.IntInfo.NumberOfBranches);
            long choicePoints = cp.getInfo(IloCP.IntInfo.NumberOfChoicePoints);
            long constraints = cp.getInfo(IloCP.IntInfo.NumberOfConstraints);

            return new Result(solutionCount, elapsedTime, fails, branches, choicePoints, constraints);
            //return new Result(solutionCount, elapsedTime);


        } catch (IloException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Result testOptimizedRevLexCon(int[] DEGREE) {
        try {
            int N = DEGREE.length; // Example size of adjacency matrix

            // Define Vars of the Adjacency matrix
            IloCP cp = new IloCP();

            // Define Vars of the Adjacency matrix
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, 0, 1);
            }

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(MATRIX[i][i], 1));
            }

            // Constraint: Define Degree Constraint sum of the row, excluding the diagonal element
            for (int i = 0; i < N; i++) {
                // Sum of the entire row
                IloIntExpr rowSum = cp.sum(MATRIX[i]);
                // Subtract the diagonal element (MATRIX[i][i])
                IloIntExpr sumExceptDiagonal = cp.diff(rowSum, MATRIX[i][i]);
                // Add the constraint
                cp.addEq(sumExceptDiagonal, DEGREE[i]);
            }


            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            //   Constraint 3: Symmetry breaking RevLex
            /* OK WORKS FINE*/
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                        IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                        IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            }


            // Constraint Of connectivity using Upper Off-Diagonal Technique

            // Generation of K_i Variables
            // Define the distance variables
            IloIntVar[] z = new IloIntVar[N];
            for (int i = 0; i < N; i++) {
                z[i] = cp.intVar(0, N - 1);
            }

            // Root node distance is 0
            cp.addEq(z[0], 0);
            // For all other nodes, distances must be greater than 0
            for (int i = 1; i < N; i++) {
                cp.addGe(z[i], 1);
            }

            for (int i = 1; i < N; i++) {
                for (int k = 1; k < N; k++) {
                    IloConstraint[] neighborConstraints = new IloConstraint[N - 1];
                    int index = 0;
                    for (int j = 0; j < N; j++) {
                        if (j != i) {
                            // Combine constraints into an array
                            IloConstraint[] combinedConstraints = new IloConstraint[2];
                            combinedConstraints[0] = cp.neq(MATRIX[i][j], 0);
                            combinedConstraints[1] = cp.eq(z[j], k - 1);

                            // Use cp.and with an array of constraints
                            neighborConstraints[index++] = cp.and(combinedConstraints);
                        }
                    }
                    cp.add(cp.ifThen(
                            cp.eq(z[i], k),
                            cp.or(neighborConstraints)
                    ));


                }
            }

            // ajouter pour optimiser les solution redondants due à la variable Z.
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.ifThen(
                            cp.neq(MATRIX[i][j], 0),
                            cp.le(cp.abs(cp.diff(z[i], z[j])), 1)
                    ));
                    //si aij >0 alors abs( zi - zj) <= 1
                }
            }


            // Constraint Of connectivity using Upper Off-Diagonal Technique
            /*for (int i = 0; i < N; i++) {
                // Ensure that the sum of the subarray from i+1 to N is greater than 0
                if ((i + 1) < N) // pour eviter la derniere ligne
                {
                    IloIntVar[] subArray = Arrays.copyOfRange(MATRIX[i], i + 1, N);
                    cp.add(cp.gt(cp.sum(subArray), 0));
                }
            }*/

            // Configure solver for memory optimization
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet); // Suppress logs
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst); // Depth-first search
            cp.setParameter(IloCP.IntParam.DefaultInferenceLevel, IloCP.ParameterValues.Low); // Low inference level
            cp.setParameter(IloCP.IntParam.MemoryDisplay, 0); // 0 disable , 1 Enable memory usage display

            // Measure execution time
            long startTime = System.currentTimeMillis();

            // Create timestamp for filename
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
            String timestamp = sdf.format(new Date());
            String filename = "output_testOptimizedRevLexCon.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            while (cp.next()) {
                solutionCount++;
                ok = true;
                //System.out.print(" \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        //System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        writer.print(" " + (int) cp.getValue(MATRIX[i][j]));
                    }
                    //System.out.print(" \n");
                    writer.println();
                }
                writer.println();
                writer.println();
            }
               /*while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }*/
            writer.close();
            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            // Collect solver diagnostics
            long fails = cp.getInfo(IloCP.IntInfo.NumberOfFails);
            long branches = cp.getInfo(IloCP.IntInfo.NumberOfBranches);
            long choicePoints = cp.getInfo(IloCP.IntInfo.NumberOfChoicePoints);
            long constraints = cp.getInfo(IloCP.IntInfo.NumberOfConstraints);

            return new Result(solutionCount, elapsedTime, fails, branches, choicePoints, constraints);
            //return new Result(solutionCount, elapsedTime);

        } catch (IloException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Result testOptimizedRevLexConDiag(int[] DEGREE) {
        try {
            int N = DEGREE.length; // Example size of adjacency matrix

            // Define Vars of the Adjacency matrix
            IloCP cp = new IloCP();

            // Define Vars of the Adjacency matrix
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, 0, 1);
            }

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(MATRIX[i][i], 1));
            }

            // Constraint: Define Degree Constraint sum of the row, excluding the diagonal element
            for (int i = 0; i < N; i++) {
                // Sum of the entire row
                IloIntExpr rowSum = cp.sum(MATRIX[i]);
                // Subtract the diagonal element (MATRIX[i][i])
                IloIntExpr sumExceptDiagonal = cp.diff(rowSum, MATRIX[i][i]);
                // Add the constraint
                cp.addEq(sumExceptDiagonal, DEGREE[i]);
                // cp.addLe(sumExceptDiagonal, DEGREE[i]);  // used for Bounded Graphs
            }


            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            //   Constraint 3: Symmetry breaking RevLex
            /* OK WORKS FINE*/
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                        IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                        IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            }


            // Constraint Of connectivity using Upper Off-Diagonal Technique
/*
            // Generation of K_i Variables
            // Define the distance variables
            IloIntVar[] z = new IloIntVar[N];
            for (int i = 0; i < N; i++) {
                z[i] = cp.intVar(0, N - 1);
            }

            // Root node distance is 0
            cp.addEq(z[0], 0);
            // For all other nodes, distances must be greater than 0
            for (int i = 1; i < N; i++) {
                cp.addGe(z[i], 1);
            }

            for (int i = 1; i < N; i++) {
                for (int k = 1; k < N; k++) {
                    IloConstraint[] neighborConstraints = new IloConstraint[N - 1];
                    int index = 0;
                    for (int j = 0; j < N; j++) {
                        if (j != i) {
                            // Combine constraints into an array
                            IloConstraint[] combinedConstraints = new IloConstraint[2];
                            combinedConstraints[0] = cp.neq(MATRIX[i][j], 0);
                            combinedConstraints[1] = cp.eq(z[j], k - 1);

                            // Use cp.and with an array of constraints
                            neighborConstraints[index++] = cp.and(combinedConstraints);
                        }
                    }
                    cp.add(cp.ifThen(
                            cp.eq(z[i], k),
                            cp.or(neighborConstraints)
                    ));


                }
            }

            // ajouter pour optimiser les solution redondants due à la variable Z.
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.ifThen(
                            cp.neq(MATRIX[i][j], 0),
                            cp.le(cp.abs(cp.diff(z[i], z[j])), 1)
                    ));
                    //si aij >0 alors abs( zi - zj) <= 1
                }
            }

*/


            // Constraint Of connectivity using Upper Off-Diagonal Technique (First proposal
            for (int i = 0; i < N; i++) {
                // Ensure that the sum of the subarray from i+1 to N is greater than 0
                if ((i + 1) < N) // pour eviter la derniere ligne
                {
                    IloIntVar[] subArray = Arrays.copyOfRange(MATRIX[i], i + 1, N);
                    cp.add(cp.gt(cp.sum(subArray), 0));
                }
            }

            // Connectivity Constraint: Every vertex i must connect to at least one higher-indexed vertex
          /*  for (int i = 0; i < N - 1; i++) {                    // No need for i == N-1
                IloIntVar[] upperPart = new IloIntVar[N - i - 1];
                for (int j = 0; j < upperPart.length; j++) {
                    upperPart[j] = MATRIX[i][i + 1 + j];
                }
                cp.add(cp.gt(cp.sum(upperPart), 0));
            }*/
            /*
            for (int i = 0; i < N - 1; i++) {
                IloIntVar[] upper = Arrays.copyOfRange(MATRIX[i], i + 1, N);
                cp.add(cp.gt(cp.sum(upper), 0));
            }*/

            // Configure solver for memory optimization
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet); // Suppress logs
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst); // Depth-first search
            cp.setParameter(IloCP.IntParam.DefaultInferenceLevel, IloCP.ParameterValues.Low); // Low inference level
            cp.setParameter(IloCP.IntParam.MemoryDisplay, 0); // 0 disable , 1 Enable memory usage display

            // Measure execution time
            long startTime = System.currentTimeMillis();

            // Create timestamp for filename
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
            String timestamp = sdf.format(new Date());
            String filename = "output_testOptimizedRevLexConDiag.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            /*while (cp.next()) {
                solutionCount++;
                ok = true;
                //System.out.print(" \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        //System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        writer.print(" " + (int) cp.getValue(MATRIX[i][j]));
                    }
                    //System.out.print(" \n");
                    writer.println();
                }
                writer.println();
                writer.println();
            }*/
            while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }
            writer.close();
            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            // Collect solver diagnostics
            long fails = cp.getInfo(IloCP.IntInfo.NumberOfFails);
            long branches = cp.getInfo(IloCP.IntInfo.NumberOfBranches);
            long choicePoints = cp.getInfo(IloCP.IntInfo.NumberOfChoicePoints);
            long constraints = cp.getInfo(IloCP.IntInfo.NumberOfConstraints);

            return new Result(solutionCount, elapsedTime, fails, branches, choicePoints, constraints);
            //return new Result(solutionCount, elapsedTime);

        } catch (IloException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public static IloIntExpr[] reverseArray(IloIntExpr[] array) throws IloException {
        int length = array.length;
        IloIntExpr[] reversedArray = new IloIntExpr[length];

        for (int i = 0; i < length; i++) {
            reversedArray[i] = array[length - 1 - i];
        }

        return reversedArray;
    }

     // Modified reverseArray to exclude columns i and i+1
    public static IloIntExpr[] reverseArrayNew(IloIntExpr[] array, int exclude1, int exclude2) throws IloException {
        int length = array.length;
        List<IloIntExpr> filteredList = new ArrayList<>();

        for (int i = length - 1; i >= 0; i--) { // Reverse iteration
            if (i != exclude1 && i != exclude2) {
                filteredList.add(array[i]);
            }
        }

        return filteredList.toArray(new IloIntExpr[0]);
    }

    public static IloIntExpr[] arrayNew(IloIntExpr[] array, int exclude1, int exclude2) throws IloException {
        if (array == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
        int length = array.length;
        if (exclude1 < 0 || exclude1 >= length || exclude2 < 0 || exclude2 >= length) {
            throw new IllegalArgumentException("Exclude indices must be within [0, length)");
        }

        // Calculate size of result array (exclude1 and exclude2 may be the same)
        int resultSize = (exclude1 == exclude2) ? length - 1 : length - 2;
        IloIntExpr[] result = new IloIntExpr[resultSize];
        int index = 0;

        for (int i = 0; i < length; i++) {
            if (i != exclude1 && i != exclude2) {
                result[index++] = array[i];
            }
        }

        return result;
    }


}
