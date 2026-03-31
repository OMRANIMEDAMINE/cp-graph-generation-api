package edu.polytech.cpmolgenapi;

import ilog.concert.*;
import ilog.cp.IloCP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {

    public static void testLex() {
        try {
            //int[] DEGREE = {2,2,2,2,2,2,2,2,2}; // Example degree constraints
            //int[] DEGREE = {2,2,2,2,2,2,2,2,2,2}; // Example degree constraints
            //int[] DEGREE = {3,3,3,3,3,3}; // Example degree constraints
            //int[] DEGREE = {4,4,4,4,4,4,4,4,4}; // Example degree constraints
            int[] DEGREE = {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5}; // Example degree constraints

            int N = DEGREE.length; // Example size of adjacency matrix
            boolean isSymmetryBreakingActive = true;
            boolean isConnectivityActive = true;
            boolean isConnectivityOptimizationActive = true;

            System.out.println("  ------------> LEX");
            System.out.println("N: " + N);
            System.out.println("DEGREE: " + Arrays.toString(DEGREE));
            System.out.println("isSymmetryBreakingActive : " + isSymmetryBreakingActive); // Log the received JSON
            System.out.println("isConnectivityActive : " + isConnectivityActive); // Log the received JSON
            System.out.println("isConnectivityOptimizationActive : " + isConnectivityOptimizationActive); // Log the received JSON


            // Generation of K_i Variables
            IloIntVar[] z = null; //isConnectivityActive Constraint
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
                //for (int j = 0; j < N; j++) { OPTIMIZATION
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            // Constraint 3: Symmetry breaking of equal degree
            if (isSymmetryBreakingActive) {
                for (int i = 0; i < N - 1; i++) {
                    //if ((DEGREE[i] == DEGREE[i + 1])) {
                    cp.add(cp.lexicographic(MATRIX[i], MATRIX[i + 1]));
                    //}
                }
            }


            // Constraint 6: Connectivity Constraint
            // With Variables Z
            if (isConnectivityActive) {
                System.out.println("YES isConnectivityActive IS TRUE");
                // Generation of K_i Variables
                // Define the distance variables
                z = new IloIntVar[N];
                for (int i = 0; i < N; i++) {
                    try {
                        z[i] = cp.intVar(0, N - 1);
                    } catch (IloException e) {
                        throw new RuntimeException(e);
                    }
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
                if (isConnectivityOptimizationActive) {
                    for (int i = 0; i < N; i++) {
                        for (int j = i + 1; j < N; j++) {
                            cp.add(cp.ifThen(
                                    cp.neq(MATRIX[i][j], 0),
                                    cp.le(cp.abs(cp.diff(z[i], z[j])), 1)
                            ));  //this means if  aij >0 alors abs( zi - zj) <= 1
                        }
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

            while (cp.next()) {
                solutionCount++; // Count solutions without storing them

            }

            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            System.out.println("Number of solutions found: " + solutionCount);
            System.out.println("Time taken: " + elapsedTime + " ms");

        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testRevLex() {
        try {
            //int[] DEGREE = {2,2,2,2,2,2,2,2,2}; // Example degree constraints
            //int[] DEGREE = {2,2,2,2,2,2,2,2,2,2}; // Example degree constraints
            //int[] DEGREE = {3,3,3,3,3,3,3,3,3,3,3,3}; // Example degree constraints
            int[] DEGREE = {4, 4, 4, 4, 4, 4,4}; // Example degree constraints
            //int[] DEGREE = {5,5,5,5,5,5,5,5,5,5,5,5}; // Example degree constraints

            int N = DEGREE.length; // Example size of adjacency matrix
            boolean isSymmetryBreakingActive = true;
            boolean isConnectivityActive = false;
            boolean isConnectivityOptimizationActive = false;

            System.out.println("  ------------> REVLEX");
            System.out.println("N: " + N);
            System.out.println("DEGREE: " + Arrays.toString(DEGREE));
            System.out.println("isSymmetryBreakingActive : " + isSymmetryBreakingActive); // Log the received JSON
            System.out.println("isConnectivityActive : " + isConnectivityActive); // Log the received JSON
            System.out.println("isConnectivityOptimizationActive : " + isConnectivityOptimizationActive); // Log the received JSON


            // Generation of K_i Variables
            IloIntVar[] z = null; //isConnectivityActive Constraint
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
            /*for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]+1);
            }*/


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
                //for (int j = 0; j < N; j++) { OPTIMIZATION
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            // Constraint 3: Symmetry breaking of equal degree
          /*  if (isSymmetryBreakingActive) {
                for (int i = 0; i < N - 1; i++) {
                    if ((DEGREE[i] == DEGREE[i + 1])) {
                        IloIntExpr[] reversedMatrixI = reverseArray(MATRIX[i]);
                        IloIntExpr[] reversedMatrixJ = reverseArray(MATRIX[i + 1]);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            }*/

/*
            if (isSymmetryBreakingActive) {
                for (int i = 0; i < N - 1; i++) {
                    if (DEGREE[i] == DEGREE[i + 1]) {
                        IloIntExpr[] reversedMatrixI = reverseArrayNewiplus1(MATRIX[i], i + 1);
                        IloIntExpr[] reversedMatrixJ = reverseArrayNewiplus1(MATRIX[i + 1], i + 1);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            }
*/
            /* OK WORKS FINE*/
             if (isSymmetryBreakingActive) {
                for (int i = 0; i < N - 1; i++) {
                   // if (DEGREE[i] == DEGREE[i + 1]) {
                        IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i , i + 1);
                        IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[i + 1], i , i + 1);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    //}
                }
            }

            /*if (isSymmetryBreakingActive) {
                for (int i = 0; i < N - 1; i++) {
                    if (DEGREE[i] == DEGREE[i + 1]) {
                        IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, i + 1);
                        IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[i + 1], i, i + 1);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            }*/



            // Constraint 3: Symmetry breaking of equal degree applied on columns instead of rows
            /*if (isSymmetryBreakingActive) {
                for (int j = 0; j < N - 1; j++) { // Iterate over columns instead of rows
                    if (DEGREE[j] == DEGREE[j + 1]) { // Compare degrees of columns instead of rows
                        IloIntExpr[] reversedColumnJ = reverseColumn(MATRIX, j);
                        IloIntExpr[] reversedColumnJPlus1 = reverseColumn(MATRIX, j + 1);
                        cp.add(cp.lexicographic(reversedColumnJ, reversedColumnJPlus1));
                    }
                }
            }*/


            // Constraint 6: Connectivity Constraint
            // With Variables Z
            if (isConnectivityActive) {
                System.out.println("YES isConnectivityActive IS TRUE");
                // Generation of K_i Variables
                // Define the distance variables
                z = new IloIntVar[N];
                for (int i = 0; i < N; i++) {
                    try {
                        z[i] = cp.intVar(0, N - 1);
                    } catch (IloException e) {
                        throw new RuntimeException(e);
                    }
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
                if (isConnectivityOptimizationActive) {
                    for (int i = 0; i < N; i++) {
                        for (int j = i + 1; j < N; j++) {
                            cp.add(cp.ifThen(
                                    cp.neq(MATRIX[i][j], 0),
                                    cp.le(cp.abs(cp.diff(z[i], z[j])), 1)
                            ));  //this means if  aij >0 alors abs( zi - zj) <= 1
                        }
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
            }
           /* while (cp.next()) {
                solutionCount++; // Count solutions without storing them

            }*/


            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            System.out.println("Number of solutions found: " + solutionCount);
            System.out.println("Time taken: " + elapsedTime + " ms" +  ((double)(elapsedTime/1000)) + " s");

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

    public static IloIntExpr[] reverseColumn(IloIntVar[][] matrix, int col) throws IloException {
        IloIntExpr[] column = new IloIntExpr[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            column[i] = matrix[i][col]; // Extract column elements
        }
        return reverseArray(column); // Reverse and return
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


    // Modified reverseArray to exclude column i+1
    public static IloIntExpr[] reverseArrayNewiplus1(IloIntExpr[] array, int excludeIndex) throws IloException {
        int length = array.length;
        List<IloIntExpr> filteredList = new ArrayList<>();

        for (int i = length - 1; i >= 0; i--) { // Reverse iteration
            if (i != excludeIndex) {  // Exclude column i+1
                filteredList.add(array[i]);
            }
        }

        return filteredList.toArray(new IloIntExpr[0]);
    }

}
