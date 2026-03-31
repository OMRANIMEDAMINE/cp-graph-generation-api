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

public class LexVsRevLex {


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
                //for (int j = 0; j < N; j++) { OPTIMIZATION
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            // Constraint 3: Symmetry breaking of equal degree
            for (int i = 0; i < N - 1; i++) {
                if ((DEGREE[i] == DEGREE[i + 1])) {
                    cp.add(cp.lexicographic(MATRIX[i], MATRIX[i + 1]));
                }
            }

            Map<Integer, List<Integer>> degreeGroups = new HashMap<>();
            for (int i = 0; i < DEGREE.length; i++) {
                int d = DEGREE[i];
                // if key doesn't exist, create new list
                degreeGroups.computeIfAbsent(d, k -> new ArrayList<>()).add(i);
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

            return new Result(solutionCount, elapsedTime);
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }
    public static Result  testRevLex (int[] DEGREE) {
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


            // Constraint 4: Symmetry breaking RevLex

            for (int i = 0; i < N - 1; i++) {
                IloIntExpr[] reversedMatrixI = reverseArray(MATRIX[i]);
                IloIntExpr[] reversedMatrixJ = reverseArray(MATRIX[i + 1]);
                cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
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


            //   Constraint 4: Symmetry breaking RevLex

            /*
               for (int i = 0; i < N - 1; i++) {
                    if (DEGREE[i] == DEGREE[i + 1]) {
                        IloIntExpr[] reversedMatrixI = reverseArrayNewiplus1(MATRIX[i], i + 1);
                        IloIntExpr[] reversedMatrixJ = reverseArrayNewiplus1(MATRIX[i + 1], i + 1);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            */
            /* OK WORKS FINE*/
            for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
            }


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
    public static Result testOptimizedAntiRevLex(int[] DEGREE) {
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


            //   Constraint 4: Symmetry breaking RevLex

            /*
               for (int i = 0; i < N - 1; i++) {
                    if (DEGREE[i] == DEGREE[i + 1]) {
                        IloIntExpr[] reversedMatrixI = reverseArrayNewiplus1(MATRIX[i], i + 1);
                        IloIntExpr[] reversedMatrixJ = reverseArrayNewiplus1(MATRIX[i + 1], i + 1);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            */
            /* OK WORKS FINE*/
            for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixJ, reversedMatrixI));
                }
            }


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
            }

            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                //for (int j = 0; j < N; j++) { OPTIMIZATION
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            // Constraint 3: Symmetry breaking Opt Lex
           /* for (int i = 0; i < N - 1; i++) {
                //if ((DEGREE[i] == DEGREE[i + 1])) {
                cp.add(cp.lexicographic(MATRIX[i], MATRIX[i + 1]));
                //}
            }*/

            /* OK WORKS FINE*/
            for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = arrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = arrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
            }

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
    public static Result testOptimizedAntiLex(int[] DEGREE) {
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
                //for (int j = 0; j < N; j++) { OPTIMIZATION
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }


            // Constraint 3: Symmetry breaking Opt Lex
           /* for (int i = 0; i < N - 1; i++) {
                //if ((DEGREE[i] == DEGREE[i + 1])) {
                cp.add(cp.lexicographic(MATRIX[i], MATRIX[i + 1]));
                //}
            }*/

            /* OK WORKS FINE*/
            for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = arrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = arrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixJ, reversedMatrixI));
                }
            }

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

    public static Result optAntiLex(int[] DEGREE) {
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


            //   Constraint 4: Symmetry breaking RevLex
            /*for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
            }*/

            // Constraint 4: Degree-based Hybrid Symmetry Breaking
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    // Apply symmetry breaking only if degrees are equal
                    if (DEGREE[i] == DEGREE[j]) {
                        IloIntExpr[] reversedMatrixI = arrayNew(MATRIX[i], i, j);
                        IloIntExpr[] reversedMatrixJ = arrayNew(MATRIX[j], i, j);
                        cp.add(cp.lexicographic( reversedMatrixJ, reversedMatrixI));
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
           /* while (cp.next()) {
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
    public static Result optAntiRevLex(int[] DEGREE) {
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


            //   Constraint 4: Symmetry breaking RevLex
            /*for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
            }*/

            // Constraint 4: Degree-based Hybrid Symmetry Breaking
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    // Apply symmetry breaking only if degrees are equal
                    if (DEGREE[i] == DEGREE[j]) {
                        IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                        IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                        cp.add(cp.lexicographic( reversedMatrixJ, reversedMatrixI));
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
           /* while (cp.next()) {
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
    public static Result HybridAntiLexTopOptRevLexBotom(int[] DEGREE) {
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


            //   Constraint 4: Symmetry breaking RevLex
            /*for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
            }*/

            // Constraint 4: Degree-based Hybrid Symmetry Breaking
            int mid = N / 2; // Divide matrix into two halves
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    // Apply symmetry breaking only if degrees are equal
                    if (DEGREE[i] == DEGREE[j]) {
                        if (i < mid && j < mid) {
                        // --- Top half: Lexicographic order ---
                        IloIntExpr[] reversedMatrixI = arrayNew(MATRIX[i], i, j);
                        IloIntExpr[] reversedMatrixJ = arrayNew(MATRIX[j], i, j);
                        cp.add(cp.lexicographic( reversedMatrixJ, reversedMatrixI));

                        } else if (i >= mid && j >= mid) {
                        // --- Bottom half: Reverse-Lexicographic order ---

                            IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                            IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                            cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                        }
                        // Cross-half pairs are skipped
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
            String filename = "output_HybridAntiLexTopOptRevLexBotom.txt";
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
    public static Result HybridLexTopOptAntiRevLexBotom(int[] DEGREE) {
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


            //   Constraint 4: Symmetry breaking RevLex
            /*for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
            }*/

            // Constraint 4: Degree-based Hybrid Symmetry Breaking
            int mid = N / 2; // Divide matrix into two halves
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    // Apply symmetry breaking only if degrees are equal
                    if (DEGREE[i] == DEGREE[j]) {
                        if (i < mid && j < mid) {
                            // --- Top half: Lexicographic order ---
                            // cp.add(cp.lexicographic(MATRIX[j], MATRIX[i]));
                            IloIntExpr[] reversedMatrixI = arrayNew(MATRIX[i], i, j);
                            IloIntExpr[] reversedMatrixJ = arrayNew(MATRIX[j], i, j);
                            cp.add(cp.lexicographic( reversedMatrixI, reversedMatrixJ));


                        } else if (i >= mid && j >= mid) {
                            // --- Bottom half: Reverse-Lexicographic order ---

                            IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                            IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                            cp.add(cp.lexicographic(reversedMatrixJ, reversedMatrixI));
                        }
                        // Cross-half pairs are skipped
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
            String filename = "output_HybridLexTopOptAntiRevLexBotom.txt";
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



    public static Result test(int[] DEGREE) {
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


            //   Constraint 4: Symmetry breaking RevLex
            /*for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
            }*/

            // Constraint 4: Degree-based REvLex Symmetry Breaking
            for (int i = 0; i < N - 1; i++) {
                for (int j = i + 1; j < N; j++) {
                    // Apply symmetry breaking only if degrees are equal

                    if (DEGREE[i] == DEGREE[j]) {
                     //  if (i < mid && j < N) {
                           IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                           IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                           cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                     //  }

                    }
                }
            }


            // Constraint Of connectivity
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
            String filename = "output_Test.txt";
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
