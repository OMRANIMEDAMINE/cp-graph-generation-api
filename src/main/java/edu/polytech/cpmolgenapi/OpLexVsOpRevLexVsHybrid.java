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
            /* OK WORKS FINE*/
            for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if ((DEGREE[i] == DEGREE[j])) {
                        cp.add(cp.lexicographic(MATRIX[i], MATRIX[j]));
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
            String filename = "output_testLex.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
           /* while (cp.next()) {
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

            return new Result(solutionCount, elapsedTime);
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

            for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if ((DEGREE[i] == DEGREE[j])) {
                        IloIntExpr[] reversedMatrixI = arrayNew(MATRIX[i], i, j);
                        IloIntExpr[] reversedMatrixJ = arrayNew(MATRIX[j], i, j);
                        //cp.add(cp.lexicographic(reversedMatrixJ, reversedMatrixI)); // Anti_lex
                        cp.add(cp.lexicographic(reversedMatrixJ, reversedMatrixI));
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
            /*while (cp.next()) {
                solutionCount++;
                ok = true;

                System.out.print(" \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        //System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        writer.print(" " + (int) cp.getValue(MATRIX[i][j]));
                    }
                   // System.out.print(" \n");
                    writer.println();
                }
                writer.println();
                writer.println();
            }*/
            while (cp.next()) {

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

            }
            writer.close();
            cp.endSearch(); // End the search

// Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            return new Result(solutionCount, elapsedTime);
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
            for (int i = 0; i < N-1; i++) {
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
                solutionCount++; // Count solutions without storing them
            }*/
            writer.close();
            cp.endSearch(); // End the search

// Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            return new Result(solutionCount, elapsedTime);
        } catch (IloException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    public static Result testOptimizedLexConKKtree(int[] DEGREE) {
        try {
            int N = DEGREE.length; // Example size of adjacency matrix
            int K = DEGREE[0];
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
                //cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]);
                cp.addLe(cp.sum(MATRIX[i]), DEGREE[i]); // used for Bounded Graph

            }

            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            // Constraint 3: Symmetry breaking Opt Lex
            /* OK WORKS FINE*/
            for (int i = 0; i < N-1; i++) {
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


            // Constraint K-TREEE
            // K-Tree Constraint
            for (int i = 0; i < N-1-K; i++) {
                IloIntExpr rowSum = cp.constant(0);
                for (int j = i + 1; j < N ; j++) {
                    rowSum = cp.sum(rowSum, MATRIX[i][j]);
                }
                //cp.addLe(rowSum, K);
                cp.add(cp.le(rowSum, K) );
            }


            /*// K-Tree Constraint
            for (int i = 0; i < N; i++) {
                IloIntExpr rowSum = cp.constant(0);
                for (int j = 0; j < i ; j++) {
                    rowSum = cp.sum(rowSum, MATRIX[i][j]);
                }
                //cp.addLe(rowSum, K);
                cp.add(cp.le(rowSum, K) );
            }*/

            // Ad constraint exact sum of edge =
           /* // Sum all variables in the adjacency matrix -- NOt NECESSARY
            IloIntExpr allSum = cp.constant(0);
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N ; j++) {
                    allSum = cp.sum(allSum, MATRIX[i][j]);
                }
            }
            System.out.println("SUM EDGE :" + (2* K * N - K * (K+1)));
            cp.add(cp.le(allSum, 2* K * N - K * (K+1) ) );*/



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
            String filename = "output_testOptimizedLex.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            /*while (cp.next()) {
                solutionCount++;
                ok = true;
                System.out.print(" \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        writer.print(" " + (int) cp.getValue(MATRIX[i][j]));
                    }
                    System.out.print(" \n");
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

            return new Result(solutionCount, elapsedTime);
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
            for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                        IloIntExpr[] reversedMatrixI = reverseArray(MATRIX[i]);
                        IloIntExpr[] reversedMatrixJ = reverseArray(MATRIX[j]);
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

            return new Result(solutionCount, elapsedTime);

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
            for (int i = 0; i < N-1; i++) {
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
            String timestamp = sdf.format(new Date());
            String filename = "output_testOptimizedRevLex.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            /*while (cp.next()) {
                solutionCount++;
                ok = true;
                System.out.print(" \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        writer.print(" " + (int) cp.getValue(MATRIX[i][j]));
                    }
                    System.out.print(" \n");
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

            return new Result(solutionCount, elapsedTime);

        } catch (IloException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Result testOptimizedRevLexConKKtree(int[] DEGREE) {
        try {
            int N = DEGREE.length; // Example size of adjacency matrix
            int K = DEGREE[0];
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
                //cp.addEq(sumExceptDiagonal, DEGREE[i]);
                cp.addLe(sumExceptDiagonal, DEGREE[i]);  // used for Bounded Graphs
            }


            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            //   Constraint 3: Symmetry breaking RevLex
            /* OK WORKS FINE*/
            for (int i = 0; i < N-1; i++) {
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


            // Constraint K-TREEE
            // K-Tree Constraint
            for (int i = 0; i < N-1-K; i++) {
                IloIntExpr rowSum = cp.constant(0);
                for (int j = i + 1; j < N ; j++) {
                    rowSum = cp.sum(rowSum, MATRIX[i][j]);
                }
                //cp.addLe(rowSum, K);
                cp.add(cp.le(rowSum, K) );
            }


            // Ad constraint exact sum of edge =
           /* // Sum all variables in the adjacency matrix -- NOt NECESSARY
            IloIntExpr allSum = cp.constant(0);
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N ; j++) {
                    allSum = cp.sum(allSum, MATRIX[i][j]);
                }
            }
            System.out.println("SUM EDGE :" + (2* K * N - K * (K+1)));
            cp.add(cp.le(allSum, 2* K * N - K * (K+1) ) );*/



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
            String filename = "output_testOptimizedRevLex.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            /*while (cp.next()) {
                solutionCount++;
                ok = true;
                System.out.print(" \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        writer.print(" " + (int) cp.getValue(MATRIX[i][j]));
                    }
                    System.out.print(" \n");
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

            return new Result(solutionCount, elapsedTime);

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
            for (int i = 0; i < N-1; i++) {
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
               /*while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }*/
            writer.close();
            cp.endSearch(); // End the search

// Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            return new Result(solutionCount, elapsedTime);

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
                //cp.addEq(sumExceptDiagonal, DEGREE[i]);
                cp.addLe(sumExceptDiagonal, DEGREE[i]);  // used for Bounded Graphs
            }


            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            //   Constraint 3: Symmetry breaking RevLex
            /* OK WORKS FINE*/
            for (int i = 0; i < N-1; i++) {
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


            // Constraint Of connectivity using Upper Off-Diagonal Technique
            for (int i = 0; i < N; i++) {
                // Ensure that the sum of the subarray from i+1 to N is greater than 0
                if ((i + 1) < N) // pour eviter la derniere ligne
                {
                    IloIntVar[] subArray = Arrays.copyOfRange(MATRIX[i], i + 1, N);
                    cp.add(cp.gt(cp.sum(subArray), 0));
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
            String filename = "output_testOptimizedRevLex.txt";
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

            return new Result(solutionCount, elapsedTime);

        } catch (IloException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Result testHybridLexRevLex(int[] DEGREE) {
        try {

            /*for (int i = 0; i < DEGREE.length; i++) {
                System.out.print(DEGREE[i]);
            }
            System.out.print("\n");*/

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
                cp.add(cp.eq(MATRIX[i][i], 0));
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
            for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                        int d = DEGREE[i];
                        int n= N;
                        IloIntExpr[] arrI;
                        IloIntExpr[] arrJ;
                        if (d % 2 == 0) {        // even degree
                            if (d <= n / 2){
                                // reverse order
                                arrI =  reverseArrayNew(MATRIX[i], i, j);
                                arrJ =  reverseArrayNew(MATRIX[j], i, j);
                            } else {
                                // normal order
                                arrI =  arrayNew(MATRIX[i], i, j);
                                arrJ =  arrayNew(MATRIX[j], i, j);
                            }
                        }else{
                            if (d < n / 2){
                                // reverse order
                                arrI =  reverseArrayNew(MATRIX[i], i, j);
                                arrJ =  reverseArrayNew(MATRIX[j], i, j);
                            } else {
                                // normal order
                                arrI =  arrayNew(MATRIX[i], i, j);
                                arrJ =  arrayNew(MATRIX[j], i, j);
                            }
                        }
                        cp.add(cp.lexicographic(arrI, arrJ));

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

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            /*
            while (cp.next()) {
                solutionCount++;
                ok = true;
                System.out.print(" \n");

                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        //result[i][j] = (int) cp.getValue(MATRIX[i][j]);
                    }
                    System.out.print(" \n");
                }
            }*/
            while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }
            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            return new Result(solutionCount, elapsedTime);
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }
    public static Result testHybridLexRevLexAntiLexAntiRevLex(int[] DEGREE) {
        try {

            /*for (int i = 0; i < DEGREE.length; i++) {
                System.out.print(DEGREE[i]);
            }
            System.out.print("\n");*/

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
                cp.add(cp.eq(MATRIX[i][i], 0));
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
            for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                        int d = DEGREE[i];
                        int n= N;
                        IloIntExpr[] arrI;
                        IloIntExpr[] arrJ;
                        if (d % 2 == 0) {        // even degree
                            if (d <= n / 2){
                                // reverse order
                                arrI =  reverseArrayNew(MATRIX[i], i, j);
                                arrJ =  reverseArrayNew(MATRIX[j], i, j);
                            } else {
                                // normal order
                                arrI =  arrayNew(MATRIX[i], i, j);
                                arrJ =  arrayNew(MATRIX[j], i, j);
                            }
                        }else{
                            if (d < n / 2){
                                // reverse order
                                arrI =  reverseArrayNew(MATRIX[i], i, j);
                                arrJ =  reverseArrayNew(MATRIX[j], i, j);
                            } else {
                                // normal order
                                arrI =  arrayNew(MATRIX[i], i, j);
                                arrJ =  arrayNew(MATRIX[j], i, j);
                            }
                        }
                        cp.add(cp.lexicographic(arrI, arrJ));

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

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;
            /*
            while (cp.next()) {
                solutionCount++;
                ok = true;
                System.out.print(" \n");

                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        //result[i][j] = (int) cp.getValue(MATRIX[i][j]);
                    }
                    System.out.print(" \n");
                }
            }*/
            while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }
            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            return new Result(solutionCount, elapsedTime);
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }
    public static Result testHybridLexRevLexByGroup(int[] DEGREE) {
        try {

            /*for (int i = 0; i < DEGREE.length; i++) {
                System.out.print(DEGREE[i]);
            }
            System.out.print("\n");*/

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
                cp.add(cp.eq(MATRIX[i][i], 0));
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



            // --- Constraint 4: Symmetry breaking (reverse lex order within same degree group) ---
            Map<Integer, List<Integer>> degreeGroups = new HashMap<>();
            for (int i = 0; i < DEGREE.length; i++) {
                int d = DEGREE[i];
                // if key doesn't exist, create new list
                degreeGroups.computeIfAbsent(d, k -> new ArrayList<>()).add(i);
            }

            // Apply symmetry breaking only inside groups of same degree
            for (Map.Entry<Integer, List<Integer>> entry : degreeGroups.entrySet()) {
                int d = entry.getKey();          // degree of this group
                List<Integer> group = entry.getValue();
                int n = group.size();            // number of vertices in this group

                if (n < 2) continue;            // skip singleton groups

                for (int i = 0; i < n - 1; i++) {
                    for (int j = i + 1; j < n; j++) {
                        int vi = group.get(i);  // graph node index
                        int vj = group.get(j);  // graph node index
                        IloIntExpr[] arrI, arrJ;
                        if (d % 2 == 0) {        // even degree
                            if (d <= n / 2){
                                // reverse order
                                arrI = reverseArrayNewGroup(MATRIX[vi], group, vi, vj);
                                arrJ = reverseArrayNewGroup(MATRIX[vj], group, vi, vj);
                            } else {
                                // normal order
                                arrI = arrayNewGroup(MATRIX[vi], group, vi, vj);
                                arrJ = arrayNewGroup(MATRIX[vj], group, vi, vj);
                            }
                        }else{
                            if (d < n / 2){
                                // reverse order
                                arrI = reverseArrayNewGroup(MATRIX[vi], group, vi, vj);
                                arrJ = reverseArrayNewGroup(MATRIX[vj], group, vi, vj);
                            } else {
                                // normal order
                                arrI = arrayNewGroup(MATRIX[vi], group, vi, vj);
                                arrJ = arrayNewGroup(MATRIX[vj], group, vi, vj);
                            }
                        }
                        cp.add(cp.lexicographic(arrI, arrJ));
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

            // Start the search
            cp.startNewSearch();
            int solutionCount = 0;
            boolean ok = false;

            /*while (cp.next()) {
                solutionCount++;
                ok = true;
                System.out.print(" \n");

                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        //result[i][j] = (int) cp.getValue(MATRIX[i][j]);
                    }
                    System.out.print(" \n");
                }
            }*/
            while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }
            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            return new Result(solutionCount, elapsedTime);
        } catch (IloException e) {
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



    public static IloIntExpr[] arrayNewGroup(IloIntVar[] row, List<Integer> groupIndices, int exclude1, int exclude2) throws IloException {
        List<IloIntExpr> filteredList = new ArrayList<>();
        for (int idx : groupIndices) {
            if (idx != exclude1 && idx != exclude2) {
                filteredList.add(row[idx]);
            }
        }
        return filteredList.toArray(new IloIntExpr[0]);
    }

    public static IloIntExpr[] reverseArrayNewGroup(IloIntVar[] row, List<Integer> groupIndices, int exclude1, int exclude2) throws IloException {
        List<IloIntExpr> filteredList = new ArrayList<>();
        for (int k = groupIndices.size() - 1; k >= 0; k--) {
            int idx = groupIndices.get(k);
            if (idx != exclude1 && idx != exclude2) {
                filteredList.add(row[idx]);
            }
        }
        return filteredList.toArray(new IloIntExpr[0]);
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



    public static IloIntExpr[] arraySlice(IloIntExpr[] array, int start) {
        int length = array.length - start;
        IloIntExpr[] result = new IloIntExpr[length];
        for (int k = 0; k < length; k++) {
            result[k] = array[start + k];
        }
        return result;
    }
}
