package edu.polytech.cpmolgenapi.draft;

import ilog.concert.*;
import ilog.cp.IloCP;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


@Service
public class MGGAdjacencyMarixBasedSolutionService {

    public int[][] generateO(int[] degreeSequence) {
        try {
            int[] DEGREE = degreeSequence;//{2, 2, 2, 4, 4, 4, 4}; // Manually set degree values between 2 and 4 for each atom
            final int DEGREE_LOWER_BOUND = 0;
            int DEGREE_UPPER_BOUND = Arrays.stream(DEGREE).max().orElseThrow();
            int N = DEGREE.length;

            IloCP cp = new IloCP();
            // Set parameters
         /*  cp.setParameter(IloCP.DoubleParam.TimeLimit, 1000000);
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            cp.setParameter(IloCP.IntParam.Workers, 1);*/


            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, DEGREE_LOWER_BOUND, DEGREE[i]);
            }

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(MATRIX[i][1], 0));
            }

            // Constraint 3: Define Degree Constraint
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]);
            }

            // Constraint 2: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }

            Date Start = new Date();
            int[][] result = new int[N][N];
            if (cp.solve()) {
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        result[i][j] = (int) cp.getValue(MATRIX[i][j]);
                    }
                    System.out.print(" \n");

                }
                Date Stop = new Date();
                double diff = Stop.getTime() - Start.getTime();
                diff = diff / 1000 % 60;
                //JOptionPane.showMessageDialog(null,"Molecular Structure is successfully generated \n CPU Time: "+diff+" Second(s) \n Thanks for trying ! ", "Error Messsage", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Molecular Structure is successfully generated CPU Time: " + diff + " Second(s)   Thanks for trying ! ");
                cp.end();
                // cp.abortSearch();
            } else {
                System.out.println("No solution found.");
            }

            return result;
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }

    public int[][] optimize(int[] degreeSequence) {
        try {
            int[] DEGREE = degreeSequence;//{2, 2, 2, 4, 4, 4, 4}; // Manually set degree values between 2 and 4 for each atom
            final int DEGREE_LOWER_BOUND = 0;
            int DEGREE_UPPER_BOUND = Arrays.stream(DEGREE).max().orElseThrow();
            int N = DEGREE.length;

            IloCP cp = new IloCP();
            // Set parameters
         /*  cp.setParameter(IloCP.DoubleParam.TimeLimit, 1000000);
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            cp.setParameter(IloCP.IntParam.Workers, 1);*/


            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, DEGREE_LOWER_BOUND, DEGREE[i]);
            }

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(MATRIX[i][1], 0));
            }

            // Constraint 3: Define Degree Constraint
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]);
            }

            // Constraint 2: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }

            Date Start = new Date();
            int[][] result = new int[N][N];
            if (cp.solve()) {
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        result[i][j] = (int) cp.getValue(MATRIX[i][j]);
                    }
                    System.out.print(" \n");

                }
                Date Stop = new Date();
                double diff = Stop.getTime() - Start.getTime();
                diff = diff / 1000 % 60;
                //JOptionPane.showMessageDialog(null,"Molecular Structure is successfully generated \n CPU Time: "+diff+" Second(s) \n Thanks for trying ! ", "Error Messsage", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Molecular Structure is successfully generated CPU Time: " + diff + " Second(s)   Thanks for trying ! ");
                cp.end();
                // cp.abortSearch();
            } else {
                System.out.println("No solution found.");
            }

            return result;
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }

/*
    public int[][] optimize22(int[] degreeSequence) {
        try {
            IloCP cp = new IloCP();
            // Set parameters
            // Variables Declaration
            int N = 6;

          cp.setParameter(IloCP.DoubleParam.TimeLimit, 1000000);
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            cp.setParameter(IloCP.IntParam.Workers, 1);


            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, DEGREE_LOWER_BOUND, DEGREE_UPPER_BOUND);
            }


            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(MATRIX[i][i], 0));
            }

            // Constraint 3: Define Degree Constraint
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]);
            }

            // Constraint 2: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }

            // Constraint 3: Symmetry breaking of equal degree
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                         cp.add(cp.lexicographic(MATRIX[i] , MATRIX[j]));
                    }
                }
            }


            int[][] result = new int[N][N];
            ArrayList all_solutions = new ArrayList();

            Date Start = new Date();
            boolean ok = false;
            cp.startNewSearch();
            int compteur = 0;
            while (cp.next()) {
                compteur++;
                ok = true;

                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + (int) cp.getValue(MATRIX[i][j]));
                        result[i][j] = (int) cp.getValue(MATRIX[i][j]);
                    }
                    System.out.print(" \n");

                }
                Date Stop = new Date();
                double diff = Stop.getTime() - Start.getTime();
                diff = diff / 1000 % 60;
                System.out.println("Molecular Structure is successfully generated CPU Time: " + diff + " Second(s)   Thanks for trying ! ");

                all_solutions.add(result);

            }
            cp.endSearch();
            if (!ok) {
                System.out.println("No solution found.");
                return null;
            } else {
                Date Stop = new Date();
                double diff = Stop.getTime() - Start.getTime();
                diff = diff / 1000 % 60;
                System.out.println("" + compteur + " Solution(s) found with this configuration \n CPU Time: " + diff + " Second(s) \n Thanks for trying...");

            }
            return all_solutions;
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }*/

}