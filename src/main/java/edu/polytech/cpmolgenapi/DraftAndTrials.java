package edu.polytech.cpmolgenapi;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntVar;
import ilog.cp.IloCP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DraftAndTrials {
    public static Result Hybrid4(int[] DEGREE) {
        try {
            int N = DEGREE.length;
            if (N % 2 != 0) {
                throw new IllegalArgumentException("Matrix size N must be even for block decomposition.");
            }
            IloCP cp = new IloCP();

            // Define adjacency matrix variables
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, 0, 1);
            }

            // 1) Null diagonal
            for (int i = 0; i < N; i++) {
                cp.addEq(MATRIX[i][i], 0);
            }

            // 2) Degree constraints (sum of row excluding diagonal)
            for (int i = 0; i < N; i++) {
                IloIntExpr rowSum = cp.sum(MATRIX[i]);
                IloIntExpr sumExceptDiag = cp.diff(rowSum, MATRIX[i][i]);
                cp.addEq(sumExceptDiag, DEGREE[i]);
            }

            // 3) Full matrix symmetry (undirected graph)
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.addEq(MATRIX[i][j], MATRIX[j][i]);
                }
            }

            // 4) Block decomposition (N = 2*k)
            int k = N / 2;

            // Use IloIntExpr[][] for subblocks so we can reuse helper signatures
            IloIntExpr[][] D  = new IloIntExpr[k][k];
            IloIntExpr[][] C  = new IloIntExpr[k][k];
            IloIntExpr[][] Cp = new IloIntExpr[k][k]; // C'
            IloIntExpr[][] Dp = new IloIntExpr[k][k]; // D'

            for (int i = 0; i < k; i++) {
                for (int j = 0; j < k; j++) {
                    D[i][j]  = MATRIX[i][j];
                    C[i][j]  = MATRIX[i][j + k];
                    Cp[i][j] = MATRIX[i + k][j];
                    Dp[i][j] = MATRIX[i + k][j + k];
                }
            }

            // 5) Conditional symmetry-breaking:
            // (a) OptLex on C iff rows i and j of D are partially twins
            for (int i = 0; i < k - 1; i++) {
                for (int j = i + 1; j < k; j++) {
                    // D
                    IloIntExpr[] rowDi = reverseArrayNew(D[i], i, j);
                    IloIntExpr[] rowDj = reverseArrayNew(D[j], i, j);
                    if (rowDi.length >0 && rowDj.length >0) {
                        IloConstraint OptAntiLexD = cp.lexicographic(rowDi, rowDj);
                        cp.add(OptAntiLexD);
                    }

                    // C
                    //IloConstraint lexC = cp.lexicographic(C[i], C[j]);
                    IloIntExpr[] rowCi = reverseArray(C[i]);
                    IloIntExpr[] rowCj = reverseArray(C[j]);
                    IloConstraint lexC = cp.lexicographic( rowCi,rowCj);
                    IloConstraint twinsInD = twinsInBlockPartial(cp, D[i], D[j], i, j);
                    cp.add(cp.ifThen(twinsInD, lexC));
                }
            }
            // (b) OptRevLex on C' iff rows i and j of D' are partially twins
            for (int i = 0; i < k - 1; i++) {
                for (int j = i + 1; j < k; j++) {
                    // D'
                    IloIntExpr[] rowDi = reverseArrayNew(Dp[i], i, j);
                    IloIntExpr[] rowDj = reverseArrayNew(Dp[j], i, j);
                    if (rowDi.length > 0 && rowDj.length > 0) {
                        IloConstraint OptRevLexDp = cp.lexicographic(rowDi, rowDj);
                        cp.add(OptRevLexDp);
                    }
                    //C'
                    /*
                    IloIntExpr[] rowCpiRev = reverseArray(Cp[i]);

                    IloIntExpr[] rowCpjRev = reverseArray(Cp[j]);
                    IloConstraint revLexCp = cp.lexicographic(rowCpjRev,rowCpiRev);
                    IloConstraint twinsInDp = twinsInBlockPartial(cp, Dp[i], Dp[j], i, j);
                    cp.add(cp.ifThen(twinsInDp, revLexCp));
                    */



                    IloIntExpr[] rowCpiRev = reverseArray(Cp[i]);
                    IloIntExpr[] rowCpjRev = reverseArray(Cp[j]);
                    IloConstraint revLexCp = cp.lexicographic(rowCpiRev, rowCpjRev);
                    IloConstraint twinsInDp = twinsInBlockPartial(cp, Dp[i], Dp[j], i, j);
                    cp.add(cp.ifThen(twinsInDp, revLexCp));


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
            /*while (cp.next()) {
                solutionCount++; // Count solutions without storing them
            }*/
            cp.endSearch(); // End the search

            // Measure and print execution time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            return new Result(solutionCount, elapsedTime);
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Returns a constraint that enforces "partial twins":
     * two rows are identical except at columns exclude1 and exclude2.
     * Example: twinsInBlockPartial(cp, D[i], D[j], i, j)
     */
    private static IloConstraint twinsInBlockPartial(
            IloCP cp, IloIntExpr[] rowA, IloIntExpr[] rowB,
            int exclude1, int exclude2) throws IloException {

        int len = rowA.length;
        List<IloConstraint> eqs = new ArrayList<>();

        for (int t = 0; t < len; t++) {
            if (t != exclude1 && t != exclude2) {
                eqs.add(cp.eq(rowA[t], rowB[t]));
            }
        }

        // If no constraints (shouldn’t happen normally), return true constraint
        if (eqs.isEmpty()) {
            return cp.trueConstraint();
        }
        return cp.and(eqs.toArray(new IloConstraint[0]));
    }
    public static Result testRevLexTwinsMatrix(int[] DEGREE) {
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


            //   Constraint 3: Symmetry breaking RevLex

        /*
           for (int i = 0; i < N - 1; i++) {
                if (DEGREE[i] == DEGREE[i + 1]) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNewiplus1(MATRIX[i], i + 1);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNewiplus1(MATRIX[i + 1], i + 1);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
            }
        */
            /* OK WORKS FINE*/ // REVLEX OPT
            /*for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));

                }
            }*/
            // REVLEX + CONSTRAINT BASED ON : Matrix T
            // Define Vars of the T matrix
            IloIntVar[][] T = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                T[i] = cp.intVarArray(N, 0, 1);
            }


            // Constraint 1: 1 Diagonal of the T matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(T[i][i], 1));
            }

            // Constraint 2:   Symmetry of the T matrix
            for (int i = 0; i < N-1; i++) {
                //for (int j = 0; j < N; j++) { OPTIMIZATION
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(T[i][j], T[j][i]));
                }
            }

            // Constraint 3:  Link Matrix  isTwin
            for (int i = 0; i < N - 1 ; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.equiv(isTrueTwin(cp, MATRIX, i ,j ), (cp.eq(T[i][j], 1))));
                }
            }



            // Constraint 4:  Link Matrix  isTwin MAX OPT REVLEX
         /*   for (int i = 0; i < N - 1; i++) {
                IloIntExpr[] Ligne1 = new IloIntExpr[N];
                IloIntExpr[] Ligne2 = new IloIntExpr[N];
                for (int k = 0; k < N; k++) {
                    //Ligne1[k] = cp.min(1, cp.sum(MATRIX[a][k], T[a][k]));
                    //Ligne2[k] = cp.min(1, cp.sum(MATRIX[a+1][k], T[a+1][k]));
                    Ligne1[k] = cp.max(MATRIX[i][k], T[i][k]);
                    Ligne2[k] = cp.max(MATRIX[i+1][k], T[i+1][k]);
                }
                IloIntExpr[] reversedMatrixI = reverseArray(Ligne1);
                IloIntExpr[] reversedMatrixJ = reverseArray(Ligne2);
                cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));

            }*/

                // Opt RevLex applied on MATRIX and T with Max value
            /*for (int i = 0; i < N - 1; i++) {
                    IloIntExpr[] Lignei = new IloIntExpr[N];
                    for (int k = 0; k < N; k++) {
                        Lignei[k] = cp.max(MATRIX[i][k], T[i][k]);
                    }

                  // IloIntExpr[] reversedMatrixI = reverseArray(Ligne1);
                   //IloIntExpr[] reversedMatrixJ = reverseArray(Ligne2);
                    IloIntExpr[] Lignej = new IloIntExpr[N];
                    for (int j = i + 1; j < N; j++) {
                       for (int k = 0; k < N; k++) {
                           Lignej[k] = cp.max(MATRIX[j][k], T[j][k]);
                       }


                       IloIntExpr[] reversedMatrixI = reverseArrayNew(Lignei, i, j);
                       IloIntExpr[] reversedMatrixJ = reverseArrayNew(Lignej, i, j);
                       cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                   }
            }*/


            // Constraint 4: OPT REVLEW STANDARD
            for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
            }


            // Constraint 4:  Link Matrix  isTwin   - fi jorret b3adh.hom
          /*  for (int i = 0; i < N - 2; i++) {
                IloConstraint[] cons = new IloConstraint[N - (i + 2)];
                for (int j = i + 2; j < N; j++) {
                    cons[j - (i + 2)] = cp.eq(T[i][j], 0);
                }
                if (cons.length > 0) {
                    cp.add(
                            cp.ifThen(
                                    cp.eq(T[i][i + 1], 0),
                                    cp.and(cons)
                            )
                    );
                }
            }*/



          /* for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
           }*/

            // new proposal based on OptRevLex
          /*  for (int i = 0; i < N-1; i++) {

                IloIntExpr[] Ligne1 = new IloIntExpr[N];
                IloIntExpr[] Ligne2 = new IloIntExpr[N];
                for (int k = 0; k < N; k++) {
                    //Ligne1[k] = cp.min(1, cp.sum(MATRIX[a][k], T[a][k]));
                    //Ligne2[k] = cp.min(1, cp.sum(MATRIX[a+1][k], T[a+1][k]));
                    Ligne1[k] = cp.max(MATRIX[i][k], T[i][k]);
                    Ligne2[k] = cp.max(MATRIX[i+1][k], T[i+1][k]);
                }

                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
            }*/



          /*  // Twins
            for (int i = 0; i < N - 2; i++) {
                IloIntExpr[] a_I  = arrayNew(MATRIX[i], i, i + 1);
                IloIntExpr[] a_I1 = arrayNew(MATRIX[i + 1], i, i + 1);

                IloConstraint neq1 = neqArrays(cp, a_I, a_I1);  // pass cp

                for (int j = i + 2; j < N; j++) {
                    IloIntExpr[] a_IJ = arrayNew(MATRIX[i], i, j);
                    IloIntExpr[] a_J  = arrayNew(MATRIX[j], i, j);

                    IloConstraint neq2 = neqArrays(cp, a_IJ, a_J);  // pass cp

                    cp.add(cp.ifThen(neq1, neq2));
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
                System.out.print(" T is : \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + (int) cp.getValue(T[i][j]));
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


    public static Result testOptimizedRevLexTwinsTwins(int[] DEGREE) {
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


            //   Constraint 3: Symmetry breaking RevLex

            // REVLEX + CONSTRAINT BASED ON : Matrix T
            // Define Vars of the T matrix
            IloIntVar[][] T = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                T[i] = cp.intVarArray(N, 0, 1);
            }


            // Constraint 1: 1 Diagonal of the T matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(T[i][i], 1));
            }

            // Constraint 2:   Symmetry of the T matrix
            for (int i = 0; i < N-1; i++) {
                //for (int j = 0; j < N; j++) { OPTIMIZATION
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.eq(T[i][j], T[j][i]));
                }
            }

            // Constraint 3:  Link Matrix  isTwin
            for (int i = 0; i < N - 1 ; i++) {
                for (int j = i + 1; j < N; j++) {
                    cp.add(cp.equiv(isTrueTwin(cp, MATRIX, i ,j ), (cp.eq(T[i][j], 1))));
                }
            }


            // Constraint 4:  Link Matrix  isTwin MAX OPT REVLEX


            // Constraint 4: OPT REVLEX STANDARD Excluant TrueTwin de i et TrueTwin de J à la plase de Twin I , Twin J.
            // à la place d'exclure i and j , l'idée c'est d'enlever le vrai jumeau de i et vrai jumai de J .
            /*for (int i = 0; i < N-1; i++) {
                IloIntExpr[] Lignei = new IloIntExpr[N];
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] Lignej = new IloIntExpr[N];
                    for (int k = 0; k < N  ; k++) {
                        Lignei[k] = cp.min(MATRIX[i][k], cp.min(cp.diff(1, T[i][k]) , cp.diff(1, T[j][k]) ) );
                        Lignej[k] = cp.min(MATRIX[j][k], cp.min(cp.diff(1, T[j][k]) , cp.diff(1, T[i][k]) ) );
                    }

                    IloIntExpr[] reversedMatrixI = reverseArrayNew(Lignei,i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(Lignej,i , j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                }
            }*/

          /* for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
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
                System.out.print(" T is : \n");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + (int) cp.getValue(T[i][j]));
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

    public static Result testOptimizedRevLexTwins(int[] DEGREE) {
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


            //   Constraint 3: Symmetry breaking RevLex
          /*
                for (int i = 0; i < N - 1; i++) {
                    if ((DEGREE[i] == DEGREE[i + 1])) {
                        IloIntExpr[] reversedMatrixI = reverseArray(MATRIX[i]);
                        IloIntExpr[] reversedMatrixJ = reverseArray(MATRIX[i + 1]);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            */

            /*
               for (int i = 0; i < N - 1; i++) {
                    if (DEGREE[i] == DEGREE[i + 1]) {
                        IloIntExpr[] reversedMatrixI = reverseArrayNewiplus1(MATRIX[i], i + 1);
                        IloIntExpr[] reversedMatrixJ = reverseArrayNewiplus1(MATRIX[i + 1], i + 1);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            */
            /* OK WORKS FINE*/ // REVLEX OPT
            for (int i = 0; i < N-1; i++) {
                for (int j = i + 1; j < N; j++) {
                    IloIntExpr[] reversedMatrixI = reverseArrayNew(MATRIX[i], i, j);
                    IloIntExpr[] reversedMatrixJ = reverseArrayNew(MATRIX[j], i, j);
                    cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));

                }
            }

            // Twins

            for (int i = 0; i < N - 2; i++) {
                IloIntExpr[] a_I  = arrayNew(MATRIX[i], i, i + 1);
                IloIntExpr[] a_I1 = arrayNew(MATRIX[i + 1], i, i + 1);

                IloConstraint neq1 = neqArrays(cp, a_I, a_I1);  // pass cp

                for (int j = i + 2; j < N; j++) {
                    IloIntExpr[] a_IJ = arrayNew(MATRIX[i], i, j);
                    IloIntExpr[] a_J  = arrayNew(MATRIX[j], i, j);

                    IloConstraint neq2 = neqArrays(cp, a_IJ, a_J);  // pass cp

                    cp.add(cp.ifThen(neq1, neq2));
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


    // reverseArrayNew excluant trueTwin i et TrueTwing de J
    public static IloIntExpr[] reverseArrayNewTrueTwin(IloCP cp,  int[][] T,  IloIntExpr[] arrayM, int exclude1, int exclude2) {
        int length = arrayM.length;
        List<IloIntExpr> filteredList = new ArrayList<>();
        for (int k = length - 1; k >= 0; k--) {
            if (T[k][exclude1] == 0 && T[k][exclude2] == 0) { // directly from data
                filteredList.add(arrayM[k]);
            }
        }
        return filteredList.toArray(new IloIntExpr[0]);
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

    static IloConstraint neqArrays(IloCP cp, IloIntExpr[] arr1, IloIntExpr[] arr2) throws IloException {
        if (arr1.length != arr2.length)
            throw new IllegalArgumentException("Array lengths must match");

        IloConstraint diffExists = cp.falseConstraint(); // start with false
        for (int k = 0; k < arr1.length; k++) {
            diffExists = cp.or(diffExists, cp.neq(arr1[k], arr2[k]));
        }
        return diffExists;  // at least one element differs
    }
    /* is twin standard version
    static IloConstraint isTwin(IloCP cp, IloIntExpr[][] M, int i, int j) throws IloException {
        if (M[i].length != M[j].length)
            throw new IllegalArgumentException("Row lengths must match");

        IloConstraint twin = cp.trueConstraint(); // start with true
        for (int k = 0; k < M[i].length; k++) {
            if (k != i && k != j) {
                twin = cp.and(twin, cp.eq(M[i][k], M[j][k]));
            }
        }
        return twin;
    }*/


    static IloConstraint isTrueTwin(IloCP cp, IloIntExpr[][] M, int i, int j) throws IloException {
        if (M[i].length != M[j].length)
            throw new IllegalArgumentException("Row lengths must match");

        IloConstraint truetwin = cp.trueConstraint(); // start with true
        for (int k = 0; k < M[i].length; k++) {
            if (k != i && k != j) {
                truetwin = cp.and(truetwin, cp.eq(M[i][k], M[j][k]));
            }
        }
        truetwin = cp.and(truetwin, cp.eq(M[i][j], 1));  // Optimisation : we can avoid for loop if M[i][j] = 0

        return truetwin;
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
