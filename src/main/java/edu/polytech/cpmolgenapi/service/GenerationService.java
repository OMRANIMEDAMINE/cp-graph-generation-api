package edu.polytech.cpmolgenapi.service;

import edu.polytech.cpmolgenapi.model.*;
import edu.polytech.cpmolgenapi.shared.GeneralFunction;
import ilog.concert.*;
import ilog.cp.IloCP;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenerationService {
    public int[][] generateOneMatrix(Molecule molecule, String isSymmetryBreakingActive, String isConnectivityActive) {
        try {
            System.out.println("Received JSON: " + molecule); // Log the received JSON
            System.out.println("isSymmetryBreakingActive JSON: " + isSymmetryBreakingActive); // Log the received JSON
            System.out.println("isConnectivityActive JSON: " + isConnectivityActive); // Log the received JSON

            int[] DEGREE = molecule.getDegreeSequence();//{2, 2, 2, 4, 4, 4, 4}; // Manually set degree values between 2 and 4 for each atom
            final int DEGREE_LOWER_BOUND = 0;
            int DEGREE_UPPER_BOUND = Arrays.stream(DEGREE).max().orElseThrow();
            int N = DEGREE.length;
            int[] hybridizationState = molecule.getHybridizationState();
            HashMap<String, Boolean> forbiddenFragments = molecule.getForbiddenFragments();
            HashMap<String, Boolean> imposedFragments = molecule.getImposedFragments();
            ArrayList<Correlation> correlations = molecule.getCorrelations();

            ArrayList<int[]> ExplicitCycle3 = new ArrayList<>();
            ArrayList<int[]> ExplicitCycle4 = new ArrayList<>();
            ArrayList<int[]> ExplicitCycle5 = new ArrayList<>();
            ArrayList<int[]> ExplicitCycle6 = new ArrayList<>();

            // Imposed Fragments
            IloIntVar[] ISC3 = null;
            IloIntVar[] ISC4 = null;
            IloIntVar[] ISC5 = null;
            IloIntVar[] ISC6 = null;

            // Generation of K_i Variables
            IloIntVar[] z = null; //isConnectivityActive Constraint


            if (forbiddenFragments.get("threecycle") == true) {
                int[] values = CyclesGeneration.generateArray(N);
                ExplicitCycle3 = CyclesGeneration.Cycles(values, 3);
                //CyclesGeneration.displayArrayList(ExplicitCycle3);
            }
            if (forbiddenFragments.get("fourcycle") == true) {
                int[] values = CyclesGeneration.generateArray(N);
                ExplicitCycle4 = CyclesGeneration.Cycles(values, 4);
                //CyclesGeneration.displayArrayList(ExplicitCycle4);
            }
            if (forbiddenFragments.get("fivecycle") == true) {
                int[] values = CyclesGeneration.generateArray(N);
                ExplicitCycle5 = CyclesGeneration.Cycles(values, 5);
                //CyclesGeneration.displayArrayList(ExplicitCycle5);
            }
            if (forbiddenFragments.get("sixcycle") == true) {
                int[] values = CyclesGeneration.generateArray(N);
                ExplicitCycle6 = CyclesGeneration.Cycles(values, 6);
                //CyclesGeneration.displayArrayList(ExplicitCycle6);
            }

            IloCP cp = new IloCP();
            // Set parameters

            /*
                cp.setParameter(IloCP.DoubleParam.TimeLimit, 1000000);
                cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
                cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
                cp.setParameter(IloCP.IntParam.Workers, 1);
            */


            // Define Vars of the Adjacency matrix
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, DEGREE_LOWER_BOUND, DEGREE_UPPER_BOUND);
            }

  /*          IloIntVar[][] MATRIX = new IloIntVar[N][N + 1];
            for (int i = 0; i < N; i++) {
                // Populate the first N columns with adjacency matrix values
                for (int j = 0; j < N; j++) {
                    MATRIX[i][j] = cp.intVar(DEGREE_LOWER_BOUND, DEGREE_UPPER_BOUND);
                }
                // Place index i in the last column (Nth column)

                MATRIX[i][N] = cp.intVar(i,i); // Assuming index i should be the vertex index
            }*/

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                //cp.add(cp.eq(MATRIX[i][i], 1));
                cp.add(cp.eq(MATRIX[i][i], 0));
            }
            // Constraint 3: Define Degree Constraint
            for (int i = 0; i < N; i++) {
                //cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]+1);
                cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]);
            }
            // Constraint 2: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }
            // Constraint 3: Symmetry breaking of equal degree
            if (isSymmetryBreakingActive.toString().toLowerCase().equals("true")) {
                System.out.println("isSymmetryBreakingActive.toString().toLowerCase().equals(\"true\")");
                for (int i = 0; i < N; i++) {
                    for (int j = i + 1; j < N; j++) {
                        if ((DEGREE[i] == DEGREE[j]) && (hybridizationState[i] == hybridizationState[j])) {
                            cp.add(cp.lexicographic(MATRIX[i], MATRIX[j]));

                           /*IloIntExpr[] negatedMatrixI = negateArray(cp, MATRIX[i]);
                            IloIntExpr[] negatedMatrixJ = negateArray(cp, MATRIX[j]);
                            cp.add(cp.lexicographic(negatedMatrixI, negatedMatrixJ));*/

                        }
                    }
                }
            }

            // Constraint 4: Forbidden Cycles
            if ((forbiddenFragments.get("threecycle") == true) ||
                    (forbiddenFragments.get("fourcycle") == true) ||
                    (forbiddenFragments.get("fivecycle") == true) ||
                    (forbiddenFragments.get("sixcycle") == true)) {
                System.out.println("YES FORBID");
                // Constraint 4.1: Forbidden 3-Cycles
                if (forbiddenFragments.get("threecycle") == true) {
                    // Model Mr Wady
               /* for (int i = 0; i < N - 2; i++) {
                    for (int j = i + 1; j < N - 1; j++) {
                        for (int k = j + 1; k < N; k++) {
                            System.out.println("ENU = {" + (i + 1) + "," + (j + 1) + "," + (k + 1) + "}");
                            IloConstraint[] forbiddenthreecyclesConstraints = new IloConstraint[3];
                            forbiddenthreecyclesConstraints[0] = cp.eq(MATRIX[i][j], 0);
                            forbiddenthreecyclesConstraints[1] = cp.eq(MATRIX[j][k], 0);
                            forbiddenthreecyclesConstraints[2] = cp.eq(MATRIX[i][k], 0);
                            try {
                                cp.add(cp.or(forbiddenthreecyclesConstraints));
                            } catch (IloException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }*/

                    // CP Model
               /* for (int[] Aux : ExplicitCycle3) {
                    try {

                        IloConstraint Cycle3Constraint[] = new IloConstraint[3];
                        Cycle3Constraint[0] = cp.eq(MATRIX[Aux[0]][Aux[1]], 0);
                        Cycle3Constraint[1] = cp.eq(MATRIX[Aux[1]][Aux[2]], 0);
                        Cycle3Constraint[2] = cp.eq(MATRIX[Aux[2]][Aux[0]], 0);
                        cp.add(cp.or(Cycle3Constraint));
                    } catch (IloException ex) {
                        throw new RuntimeException(ex);
                    }
                }*/
                    // optimized code model
                    // Generation of the cycles enumeration
                    int[] values = CyclesGeneration.generateArray(N);
                    ExplicitCycle3 = CyclesGeneration.Cycles(values, 3);
                    forbidCycles(cp, MATRIX, ExplicitCycle3, 3);

                }
                // Constraint 4.2: Forbidden 4-Cycles
                if (forbiddenFragments.get("fourcycle") == true) {
                    int[] values = CyclesGeneration.generateArray(N);
                    ExplicitCycle4 = CyclesGeneration.Cycles(values, 4);
                    forbidCycles(cp, MATRIX, ExplicitCycle4, 4);
                }
                // Constraint 4.3: Forbidden 5-Cycles
                if (forbiddenFragments.get("fivecycle") == true) {
                    int[] values = CyclesGeneration.generateArray(N);
                    ExplicitCycle5 = CyclesGeneration.Cycles(values, 5);
                    forbidCycles(cp, MATRIX, ExplicitCycle5, 5);
                }
                // Constraint 4.4: Forbidden 6-Cycles
                if (forbiddenFragments.get("sixcycle") == true) {
                    int[] values = CyclesGeneration.generateArray(N);
                    ExplicitCycle6 = CyclesGeneration.Cycles(values, 6);
                    forbidCycles(cp, MATRIX, ExplicitCycle6, 6);
                }
            }

            // Constraint 5:  Imposed Subgraphs
            if ((imposedFragments.get("threecycle") == true) ||
                    (imposedFragments.get("fourcycle") == true) ||
                    (imposedFragments.get("fivecycle") == true) ||
                    (imposedFragments.get("sixcycle") == true)) {
                System.out.println("YES IMPOSE");

                // IloLinearNumExpr expr = cp.linearNumExpr();

                // Constraint 5.1: Imposed Subgraphs 3-cycle
                if (imposedFragments.get("threecycle") == true) {
                    try {

                        ISC3 = cp.intVarArray(3, 0, N - 1);// Imposed Subgraph 3-Cycle
                        cp.add(cp.lt(ISC3[0], ISC3[1]));
                        cp.add(cp.lt(ISC3[0], ISC3[2]));
                        cp.add(cp.lt(ISC3[1], ISC3[2]));

                        // 1 et 2
                        for (int i1 = 0; i1 < N; i1++) {
                            for (int i2 = 0; i2 < N; i2++) {
                                cp.add(cp.imply(cp.and(cp.eq(ISC3[0], i1), cp.eq(ISC3[1], i2)), cp.neq(MATRIX[i1][i2], 0)));
                            }
                        }
                        // 2 et 3
                        for (int i1 = 0; i1 < N; i1++) {
                            for (int i2 = 0; i2 < N; i2++) {
                                cp.add(cp.imply(cp.and(cp.eq(ISC3[1], i1), cp.eq(ISC3[2], i2)), cp.neq(MATRIX[i1][i2], 0)));
                            }
                        }
                        // 3 et 1
                        for (int i1 = 0; i1 < N; i1++) {
                            for (int i2 = 0; i2 < N; i2++) {
                                cp.add(cp.imply(cp.and(cp.eq(ISC3[2], i1), cp.eq(ISC3[0], i2)), cp.neq(MATRIX[i1][i2], 0)));
                            }
                        }

                     /*   for (int i = 0; i < 3; i++) {
                            expr.addTerm(1, ISC3[i]);
                        }*/


                    } catch (IloException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                //#-------------Cycle 4--------------------#
                if (imposedFragments.get("fourcycle") == true) {
                    try {
                        ISC4 = cp.intVarArray(4, 0, N - 1);
                        cp.add(cp.allDiff(ISC4));
                        cp.add(cp.lt(ISC4[0], ISC4[1]));
                        cp.add(cp.lt(ISC4[0], ISC4[2]));
                        cp.add(cp.lt(ISC4[0], ISC4[3]));
                        cp.add(cp.lt(ISC4[1], ISC4[3]));

                        // 1 et 2
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC4[0], elem), cp.eq(ISC4[1], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                               /* if ((DEGREE[elem] == DEGREE[elem2]) && hybridizationState[elem] == hybridizationState[elem2]) {
                                    cp.add(cp.ifThen(cp.and(cp.eq(ISC4[0], elem), cp.eq(ISC4[1], elem2)), cp.lt(ISC4[0], ISC4[1])));
                                }*/
                            }
                        }
                        // 2 et 3
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC4[1], elem), cp.eq(ISC4[2], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                               /* if ((DEGREE[elem] == DEGREE[elem2]) && hybridizationState[elem] == hybridizationState[elem2]) {
                                   cp.add(cp.ifThen(cp.and(cp.eq(ISC4[1], elem), cp.eq(ISC4[2], elem2)), cp.lt(ISC4[1], ISC4[2])));
                                }*/

                            }
                        }

                        // 3 et 4
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC4[2], elem), cp.eq(ISC4[3], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                             /*   if ((DEGREE[elem] == DEGREE[elem2]) && hybridizationState[elem] == hybridizationState[elem2]) {
                                    cp.add(cp.ifThen(cp.and(cp.eq(ISC4[2], elem), cp.eq(ISC4[3], elem2)), cp.lt(ISC4[2], ISC4[3])));
                                }*/
                            }
                        }
                        // 4 et 1
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC4[3], elem), cp.eq(ISC4[0], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                       /* for (int i = 0; i < 4; i++) {
                           expr.addTerm(1, ISC4[i]);
                       }*/

                    } catch (IloException ex) {
                        throw new RuntimeException(ex);
                    }

                }
                //#-------------Cycle 5--------------------#
                if (imposedFragments.get("fivecycle") == true) {
                    try {
                        ISC5 = cp.intVarArray(5, 0, N - 1);
                        /*for (int elem = 0; elem < (5-1); elem++) {
                            cp.add(cp.lt(ISC5[elem], ISC5[elem+1]));
                        }*/
                        cp.add(cp.allDiff(ISC5));
                        cp.add(cp.lt(ISC5[0], ISC5[1]));
                        cp.add(cp.lt(ISC5[0], ISC5[2]));
                        cp.add(cp.lt(ISC5[0], ISC5[3]));
                        cp.add(cp.lt(ISC5[0], ISC5[4]));
                        cp.add(cp.lt(ISC5[1], ISC5[4]));

                        // 1 et 2
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC5[0], elem), cp.eq(ISC5[1], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                        // 2 et 3
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC5[1], elem), cp.eq(ISC5[2], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                        // 3 et 4
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC5[2], elem), cp.eq(ISC5[3], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }


                        // 4 et 5
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC5[3], elem), cp.eq(ISC5[4], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                        // 5 et 1
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC5[4], elem), cp.eq(ISC5[0], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                        /*for (int i = 0; i < 5; i++) {
                            expr.addTerm(1, ISC5[i]);
                        }*/
                    } catch (IloException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                // #-------------Cycle 6--------------------#
                if (imposedFragments.get("sixcycle") == true) {
                    try {
                        ISC6 = cp.intVarArray(6, 0, N - 1);
                        cp.add(cp.allDiff(ISC6));
                        cp.add(cp.lt(ISC6[0], ISC6[1]));
                        cp.add(cp.lt(ISC6[0], ISC6[2]));
                        cp.add(cp.lt(ISC6[0], ISC6[3]));
                        cp.add(cp.lt(ISC6[0], ISC6[4]));
                        cp.add(cp.lt(ISC6[0], ISC6[5]));
                        cp.add(cp.lt(ISC6[1], ISC6[5]));

                        // 1 et 2
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[0], elem), cp.eq(ISC6[1], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }
                        // 2 et 3
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[1], elem), cp.eq(ISC6[2], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }
                        // 3 et 4
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[2], elem), cp.eq(ISC6[3], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }
                        // 4 et 5
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[3], elem), cp.eq(ISC6[4], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }
                        // 5 et 6
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[4], elem), cp.eq(ISC6[5], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }
                        // 6 et 1
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[5], elem), cp.eq(ISC6[0], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                     /*   for (int i = 0; i < 5; i++) {
                            expr.addTerm(1, ISC6[i]);
                        }
*/
                    } catch (IloException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                //cp.addMinimize(expr);


            }

            // Constraint 6: Connectivity Constraints
            // Solution Ancienne à base de saturation des atoms
            /*if (isConnectivityActive.toString().toLowerCase().equals("true")) {
                System.out.println("YES isConnectivityActive IS TRUE");
                // Generation of IndexGroups
                Map<Integer, List<Integer>> indexGroups = CyclesGeneration.getIndexGroups(DEGREE);
                // Print the result
                for (Map.Entry<Integer, List<Integer>> entry : indexGroups.entrySet()) {
                    int value = entry.getKey();
                    List<Integer> indexes = entry.getValue();
                    //System.out.printf("groups%d = %s%n", value, indexes);
                    // Constraint of interdiction of direct relationship
                    // Loop through the elements of each index and add constraint that interdict the direct relationship

                    for (int i = 0; i < indexes.size(); i++) {
                        for (int j = i + 1; j < indexes.size(); j++) {
                            // Displays all pairs to forbid direct relationship based on degrees ,
                            // means vertex have degree 2 can't be related to another with double edge
                            //System.out.printf("groups%d = %s,%s",value,  indexes.get(i), indexes.get(j));
                            cp.add(cp.neq(MATRIX[indexes.get(i)][indexes.get(j)], value));
                        }
                    }

                }
                // Print only the result for groups4
                List<Integer> groups1 = indexGroups.get(1);
                List<Integer> groups2 = indexGroups.get(2);
                List<Integer> groups3 = indexGroups.get(3);
                List<Integer> groups4 = indexGroups.get(4);

                if (groups1 != null && !groups1.isEmpty())
                    System.out.printf("groups%d = %s%n", 1, groups1);
                if (groups2 != null && !groups2.isEmpty())
                    System.out.printf("groups%d = %s%n", 2, groups2);
                if (groups3 != null && !groups3.isEmpty())
                    System.out.printf("groups%d = %s%n", 3, groups3);
                if (groups4 != null && !groups4.isEmpty())
                    System.out.printf("groups%d = %s%n", 4, groups4);


                // Adding constraint for degree 2
                // Requirements: groups2 not null and not empty and there is other vertices in the graph except groups2
                if (groups2 != null && !groups2.isEmpty() && N > groups2.size()) {
                    // Forbidden Cycles from 3 to N
                    for (int cycledegree = 3; cycledegree <= groups2.size(); cycledegree++) {
                        // Forbidden cycledegree-Cycle
                        if (groups2.size() >= cycledegree) {
                            System.out.println("\n FORBIDDEN groups2 - cycledegree: " + cycledegree);
                            // convert  List<Integer>  to int[]
                            int[] groups2Array = groups2.stream().mapToInt(Integer::intValue).toArray();
                            ArrayList<int[]> ExplicitCycle_i = CyclesGeneration.Cycles(groups2Array, cycledegree);
                            System.out.println("ExplicitCycle_i: ");
                            for (int[] array : ExplicitCycle_i) {
                                System.out.print(Arrays.toString(array));
                            }
                            // Add constraint to forbid all cycles
                            forbidCycles(cp, MATRIX, ExplicitCycle_i, cycledegree);
                        }
                    }
                }
                //Forbidden Chaine based on "two" extremities and from length 1 to  chainedegree and N> size of (groups1 and groups2)
                if ((groups1 != null && !groups1.isEmpty()) && (groups2 != null && !groups2.isEmpty())) {
                    // Forbidden Cycles from 3 to N
                    if ((groups1.size() >= 2) && (groups2.size() >= 1) && ((groups1.size() + groups2.size() < N))) {
                        for (int chainedegree = 2; chainedegree <= groups2.size(); chainedegree++) {
                            // Forbidden cycledegree-Cycle
                            System.out.println("\n ***Forbidden- cycledegree:" + chainedegree + " groups2.size(): " + groups2.size() + "groups1.size(): " + groups1.size());
                            // convert  List<Integer>  to int[]
                            int[] groups2Array = groups2.stream().mapToInt(Integer::intValue).toArray();
                            int[] groups1Array = groups1.stream().mapToInt(Integer::intValue).toArray();
                            ArrayList<int[]> ExplicitChaine_i = CyclesGeneration.Chaines(groups2Array, groups1Array, chainedegree);
                            System.out.println("ExplicitChaine_i: groups2Array= " + groups2 + " and groups1Array= " + groups1);
                            for (int[] array : ExplicitChaine_i) {
                                System.out.print("| " + Arrays.toString(array));
                            }
                            // Add constraint to forbid all chaines
                            forbidChaines(cp, MATRIX, ExplicitChaine_i, chainedegree);
                        }
                    }
                }
            }*/
            if (isConnectivityActive.toString().toLowerCase().equals("true")) {
                System.out.println("YES isConnectivityActive IS TRUE");
                // Generation of K_i Variables
                // Define the distance variables
                z = new IloIntVar[N];
                for (int i = 0; i < N; i++) {
                    z[i] = cp.intVar(0, N - 1);
                }

                // Root node distance is 0
                cp.addEq(z[0], 0);

                // All distances must be different
                //cp.add(cp.allDiff(z));

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


                for (int i = 0; i < N; i++) {
                    for (int j = i + 1; j < N; j++) {
                        cp.add(cp.ifThen(
                                cp.neq(MATRIX[i][j], 0),
                                cp.le(cp.abs(cp.diff(z[i], z[j])), 1)
                        ));
                        //si aij >0 alors abs( zi - zj) <= 1
                    }
                }


            }

            //************** Constraint 7: Hybridization state *************
            for (Integer i = 0; i < N; i++) {
                //************** Constraint 4.1: Hybridization state SP3 ************* A revoir de le debut directement filtrer from domain value
                if (hybridizationState[i] == 3) {
                    for (int j = 0; j < N; j++) {
                        cp.add(cp.le(MATRIX[i][j], 1));
                    }
                }
                //************** Constraint 4.2: Hybridization state SP2 ************* avec filtrage sur la valeur 3
                if (hybridizationState[i] == 2) {
                    for (int j = 0; j < N; j++) {
                        cp.add(cp.ge(cp.count(MATRIX[i], 2), 1));
                        cp.add(cp.ge(cp.count(MATRIX[i], 3), 0));
                    }
                }
                //************** Constraint 4.3: Hybridization state SP1 *************
                if (hybridizationState[i] == 1) {
                    for (int j = 0; j < N; j++) {
                        cp.add(cp.or(cp.ge(cp.count(MATRIX[i], 3), 1), cp.ge(cp.count(MATRIX[i], 2), 2)));
                        //   cp.add(cp.eq(b[j], cp.gt(cp.count(v2, values[j]),0)));
                        //          .le(cp.sum(MATRIX[i]),4));
                        //  cp.add(cp.le(MATRIX[i][j], 1));
                    }
                }
            }

            //************** Constraint 8: Certain Proximity RelationShips Constraint state (HMBC and BOND) *************
            if (!molecule.getCorrelations().isEmpty()) {
                int NbrCorrelations = correlations.size();
                //System.out.println("NbrCorrelations " + NbrCorrelations);
                if (NbrCorrelations > 0) {
                    //************** Constraint 5.1:   BOND  *************
                    List<Correlation> BONDCorrelations = GeneralFunction.getBONDCorrelations(correlations);
                    int NbrBONDCorrelations = BONDCorrelations.size();
                    //System.out.println("NbrBONDCorrelations " + NbrBONDCorrelations);
                    for (Correlation correlation : BONDCorrelations) {
                        for (int i = 0; i < correlation.getAtomSet1().size(); i++) {
                            int atom1 = correlation.getAtomSet1().get(i) - 1;
                            int atom2 = correlation.getAtomSet2().get(i) - 1;
                            cp.add(cp.neq(MATRIX[atom1][atom2], 0));
                        }
                    }
                    //************** Constraint 8.2:   HMBC  *************
                    List<Correlation> HMBCCorrelations = GeneralFunction.getHMBCCorrelations(correlations);
                    int NbrHMBCCorrelations = HMBCCorrelations.size();
                    System.out.println("NbrHMBCCorrelations " + NbrHMBCCorrelations);
                    for (Correlation correlation : HMBCCorrelations) {
                        IloIntVar[] correlationsVars = new IloIntVar[NbrCorrelations];
                        correlationsVars = cp.intVarArray(NbrCorrelations, 0, N - 1);
                        for (int i = 0; i < correlation.getAtomSet1().size(); i++) {
                            int atom1 = correlation.getAtomSet1().get(i) - 1;
                            int atom2 = correlation.getAtomSet2().get(i) - 1;
                            for (int inter = 0; inter < N; inter++) {
                                cp.add(cp.ifThen(cp.eq(correlationsVars[i], inter), cp.and(cp.neq(MATRIX[atom1][inter], 0), cp.neq(MATRIX[atom1][inter], 0))));
                            }
                            //cp.add(cp.ifThenElse(cp.eq(MATRIX[atom1][atom2], 0), cp.eq(correlationsVars[i], 0), cp.neq(correlationsVars[i], 0)));
                            cp.add(cp.ifThen(cp.neq(MATRIX[atom1][atom2], 0), cp.eq(correlationsVars[i], 0)));
                            // il y'a des duplication dans les solutions à cause de l'intermediaire possible, il est possible d'avoir plusieurs intermediare , exemple de carré comme fragment
                        }
                    }

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
                if (imposedFragments.get("threecycle") == true) {
                    System.out.println("\n ISC3: ");
                    for (int i = 0; i < 3; i++) {
                        System.out.print((int) cp.getValue(ISC3[i]) + " , ");

                    }
                }
                if (imposedFragments.get("fourcycle") == true) {
                    System.out.println("\n ISC4: ");
                    for (int i = 0; i < 4; i++) {
                        System.out.print((int) cp.getValue(ISC4[i]) + " , ");
                    }
                }
                if (imposedFragments.get("fivecycle") == true) {
                    System.out.println("\n ISC5: ");
                    for (int i = 0; i < 5; i++) {
                        System.out.print((int) cp.getValue(ISC5[i]) + " , ");
                    }
                }
                if (imposedFragments.get("sixcycle") == true) {
                    System.out.println("\n ISC6: ");
                    for (int i = 0; i < 6; i++) {
                        System.out.print((int) cp.getValue(ISC6[i]) + " , ");
                    }
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
                return null;
            }

            return result;
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }

    public List<int[][]> generateAllMatrix(Molecule molecule, String isSymmetryBreakingActive, String isConnectivityActive) {
        try {
            System.out.println("Received JSON: " + molecule); // Log the received JSON
            System.out.println("isSymmetryBreakingActive JSON: " + isSymmetryBreakingActive); // Log the received JSON
            System.out.println("isConnectivityActive JSON: " + isConnectivityActive); // Log the received JSON

            int[] DEGREE = molecule.getDegreeSequence();//{2, 2, 2, 4, 4, 4, 4}; // Manually set degree values between 2 and 4 for each atom
            final int DEGREE_LOWER_BOUND = 0;
            int DEGREE_UPPER_BOUND = Arrays.stream(DEGREE).max().orElseThrow();
            int N = DEGREE.length;
            int[] hybridizationState = molecule.getHybridizationState();
            HashMap<String, Boolean> forbiddenFragments = molecule.getForbiddenFragments();
            HashMap<String, Boolean> imposedFragments = molecule.getImposedFragments();
            ArrayList<Correlation> correlations = molecule.getCorrelations();

            ArrayList<int[]> ExplicitCycle3 = new ArrayList<>();
            ArrayList<int[]> ExplicitCycle4 = new ArrayList<>();
            ArrayList<int[]> ExplicitCycle5 = new ArrayList<>();
            ArrayList<int[]> ExplicitCycle6 = new ArrayList<>();

            // Imposed Fragments
            IloIntVar[] ISC3 = null;
            IloIntVar[] ISC4 = null;
            IloIntVar[] ISC5 = null;
            IloIntVar[] ISC6 = null;

            // Generation of K_i Variables
            IloIntVar[] z = null; //isConnectivityActive Constraint

            if (forbiddenFragments.get("threecycle") == true) {
                int[] values = CyclesGeneration.generateArray(N);
                ExplicitCycle3 = CyclesGeneration.Cycles(values, 3);
                //CyclesGeneration.displayArrayList(ExplicitCycle3);
            }
            if (forbiddenFragments.get("fourcycle") == true) {
                int[] values = CyclesGeneration.generateArray(N);
                ExplicitCycle4 = CyclesGeneration.Cycles(values, 4);
                //CyclesGeneration.displayArrayList(ExplicitCycle4);
            }
            if (forbiddenFragments.get("fivecycle") == true) {
                int[] values = CyclesGeneration.generateArray(N);
                ExplicitCycle5 = CyclesGeneration.Cycles(values, 5);
                //CyclesGeneration.displayArrayList(ExplicitCycle5);
            }
            if (forbiddenFragments.get("sixcycle") == true) {
                int[] values = CyclesGeneration.generateArray(N);
                ExplicitCycle6 = CyclesGeneration.Cycles(values, 6);
                //CyclesGeneration.displayArrayList(ExplicitCycle6);
            }

            IloCP cp = new IloCP();
            // Set parameters

            /*
                cp.setParameter(IloCP.DoubleParam.TimeLimit, 1000000);
                cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
                cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
                cp.setParameter(IloCP.IntParam.Workers, 1);
            */


            // Define Vars of the Adjacency matrix
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, DEGREE_LOWER_BOUND, DEGREE_UPPER_BOUND);
            }

  /*          IloIntVar[][] MATRIX = new IloIntVar[N][N + 1];
            for (int i = 0; i < N; i++) {
                // Populate the first N columns with adjacency matrix values
                for (int j = 0; j < N; j++) {
                    MATRIX[i][j] = cp.intVar(DEGREE_LOWER_BOUND, DEGREE_UPPER_BOUND);
                }
                // Place index i in the last column (Nth column)

                MATRIX[i][N] = cp.intVar(i,i); // Assuming index i should be the vertex index
            }*/

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                //cp.add(cp.eq(MATRIX[i][i], 1));
                cp.add(cp.eq(MATRIX[i][i], 0));
            }
            // Constraint 2: Define Degree Constraint
            for (int i = 0; i < N; i++) {
                //cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]+1);
                cp.addEq(cp.sum(MATRIX[i]), DEGREE[i]);
            }
            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }
            // Constraint 3: Symmetry breaking of equal degree
            if (isSymmetryBreakingActive.toString().toLowerCase().equals("true")) {
                System.out.println("isSymmetryBreakingActive.toString().toLowerCase().equals(\"true\")");
                for (int i = 0; i < N; i++) {
                    for (int j = i + 1; j < N; j++) {
                        if ((DEGREE[i] == DEGREE[j]) && (hybridizationState[i] == hybridizationState[j])) {
                            cp.add(cp.lexicographic(MATRIX[i], MATRIX[j]));

                          /*  IloIntExpr[] reversedMatrixI = reverseArray(MATRIX[i]);
                            IloIntExpr[] reversedMatrixJ = reverseArray(MATRIX[j]);
                            cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));*/
                        }
                    }
                }
            }

            // Constraint 4: Forbidden Cycles
            if ((forbiddenFragments.get("threecycle") == true) ||
                    (forbiddenFragments.get("fourcycle") == true) ||
                    (forbiddenFragments.get("fivecycle") == true) ||
                    (forbiddenFragments.get("sixcycle") == true)) {
                System.out.println("YES FORBID");
                // Constraint 4.1: Forbidden 3-Cycles
                if (forbiddenFragments.get("threecycle") == true) {
                    // Model Mr Wady
               /* for (int i = 0; i < N - 2; i++) {
                    for (int j = i + 1; j < N - 1; j++) {
                        for (int k = j + 1; k < N; k++) {
                            System.out.println("ENU = {" + (i + 1) + "," + (j + 1) + "," + (k + 1) + "}");
                            IloConstraint[] forbiddenthreecyclesConstraints = new IloConstraint[3];
                            forbiddenthreecyclesConstraints[0] = cp.eq(MATRIX[i][j], 0);
                            forbiddenthreecyclesConstraints[1] = cp.eq(MATRIX[j][k], 0);
                            forbiddenthreecyclesConstraints[2] = cp.eq(MATRIX[i][k], 0);
                            try {
                                cp.add(cp.or(forbiddenthreecyclesConstraints));
                            } catch (IloException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }*/

                    // CP Model
               /* for (int[] Aux : ExplicitCycle3) {
                    try {

                        IloConstraint Cycle3Constraint[] = new IloConstraint[3];
                        Cycle3Constraint[0] = cp.eq(MATRIX[Aux[0]][Aux[1]], 0);
                        Cycle3Constraint[1] = cp.eq(MATRIX[Aux[1]][Aux[2]], 0);
                        Cycle3Constraint[2] = cp.eq(MATRIX[Aux[2]][Aux[0]], 0);
                        cp.add(cp.or(Cycle3Constraint));
                    } catch (IloException ex) {
                        throw new RuntimeException(ex);
                    }
                }*/
                    // optimized code model
                    // Generation of the cycles enumeration
                    int[] values = CyclesGeneration.generateArray(N);
                    ExplicitCycle3 = CyclesGeneration.Cycles(values, 3);
                    forbidCycles(cp, MATRIX, ExplicitCycle3, 3);

                }
                // Constraint 4.2: Forbidden 4-Cycles
                if (forbiddenFragments.get("fourcycle") == true) {
                    int[] values = CyclesGeneration.generateArray(N);
                    ExplicitCycle4 = CyclesGeneration.Cycles(values, 4);
                    forbidCycles(cp, MATRIX, ExplicitCycle4, 4);
                }
                // Constraint 4.3: Forbidden 5-Cycles
                if (forbiddenFragments.get("fivecycle") == true) {
                    int[] values = CyclesGeneration.generateArray(N);
                    ExplicitCycle5 = CyclesGeneration.Cycles(values, 5);
                    forbidCycles(cp, MATRIX, ExplicitCycle5, 5);
                }
                // Constraint 4.4: Forbidden 6-Cycles
                if (forbiddenFragments.get("sixcycle") == true) {
                    int[] values = CyclesGeneration.generateArray(N);
                    ExplicitCycle6 = CyclesGeneration.Cycles(values, 6);
                    forbidCycles(cp, MATRIX, ExplicitCycle6, 6);
                }
            }

            // Constraint 5:  Imposed Subgraphs
            if ((imposedFragments.get("threecycle") == true) ||
                    (imposedFragments.get("fourcycle") == true) ||
                    (imposedFragments.get("fivecycle") == true) ||
                    (imposedFragments.get("sixcycle") == true)) {
                System.out.println("YES IMPOSE");

                // IloLinearNumExpr expr = cp.linearNumExpr();

                // Constraint 5.1: Imposed Subgraphs 3-cycle
                if (imposedFragments.get("threecycle") == true) {
                    try {

                        ISC3 = cp.intVarArray(3, 0, N - 1);// Imposed Subgraph 3-Cycle
                        cp.add(cp.lt(ISC3[0], ISC3[1]));
                        cp.add(cp.lt(ISC3[0], ISC3[2]));
                        cp.add(cp.lt(ISC3[1], ISC3[2]));

                        // 1 et 2
                        for (int i1 = 0; i1 < N; i1++) {
                            for (int i2 = 0; i2 < N; i2++) {
                                cp.add(cp.imply(cp.and(cp.eq(ISC3[0], i1), cp.eq(ISC3[1], i2)), cp.neq(MATRIX[i1][i2], 0)));
                            }
                        }
                        // 2 et 3
                        for (int i1 = 0; i1 < N; i1++) {
                            for (int i2 = 0; i2 < N; i2++) {
                                cp.add(cp.imply(cp.and(cp.eq(ISC3[1], i1), cp.eq(ISC3[2], i2)), cp.neq(MATRIX[i1][i2], 0)));
                            }
                        }
                        // 3 et 1
                        for (int i1 = 0; i1 < N; i1++) {
                            for (int i2 = 0; i2 < N; i2++) {
                                cp.add(cp.imply(cp.and(cp.eq(ISC3[2], i1), cp.eq(ISC3[0], i2)), cp.neq(MATRIX[i1][i2], 0)));
                            }
                        }

                     /*   for (int i = 0; i < 3; i++) {
                            expr.addTerm(1, ISC3[i]);
                        }*/


                    } catch (IloException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                //#-------------Cycle 4--------------------#
                if (imposedFragments.get("fourcycle") == true) {
                    try {
                        ISC4 = cp.intVarArray(4, 0, N - 1);
                        cp.add(cp.allDiff(ISC4));
                        cp.add(cp.lt(ISC4[0], ISC4[1]));
                        cp.add(cp.lt(ISC4[0], ISC4[2]));
                        cp.add(cp.lt(ISC4[0], ISC4[3]));
                        cp.add(cp.lt(ISC4[1], ISC4[3]));

                        // 1 et 2
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC4[0], elem), cp.eq(ISC4[1], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                               /* if ((DEGREE[elem] == DEGREE[elem2]) && hybridizationState[elem] == hybridizationState[elem2]) {
                                    cp.add(cp.ifThen(cp.and(cp.eq(ISC4[0], elem), cp.eq(ISC4[1], elem2)), cp.lt(ISC4[0], ISC4[1])));
                                }*/
                            }
                        }
                        // 2 et 3
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC4[1], elem), cp.eq(ISC4[2], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                               /* if ((DEGREE[elem] == DEGREE[elem2]) && hybridizationState[elem] == hybridizationState[elem2]) {
                                   cp.add(cp.ifThen(cp.and(cp.eq(ISC4[1], elem), cp.eq(ISC4[2], elem2)), cp.lt(ISC4[1], ISC4[2])));
                                }*/

                            }
                        }

                        // 3 et 4
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC4[2], elem), cp.eq(ISC4[3], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                             /*   if ((DEGREE[elem] == DEGREE[elem2]) && hybridizationState[elem] == hybridizationState[elem2]) {
                                    cp.add(cp.ifThen(cp.and(cp.eq(ISC4[2], elem), cp.eq(ISC4[3], elem2)), cp.lt(ISC4[2], ISC4[3])));
                                }*/
                            }
                        }
                        // 4 et 1
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC4[3], elem), cp.eq(ISC4[0], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                       /* for (int i = 0; i < 4; i++) {
                           expr.addTerm(1, ISC4[i]);
                       }*/

                    } catch (IloException ex) {
                        throw new RuntimeException(ex);
                    }

                }
                //#-------------Cycle 5--------------------#
                if (imposedFragments.get("fivecycle") == true) {
                    try {
                        ISC5 = cp.intVarArray(5, 0, N - 1);
                        /*for (int elem = 0; elem < (5-1); elem++) {
                            cp.add(cp.lt(ISC5[elem], ISC5[elem+1]));
                        }*/
                        cp.add(cp.allDiff(ISC5));
                        cp.add(cp.lt(ISC5[0], ISC5[1]));
                        cp.add(cp.lt(ISC5[0], ISC5[2]));
                        cp.add(cp.lt(ISC5[0], ISC5[3]));
                        cp.add(cp.lt(ISC5[0], ISC5[4]));
                        cp.add(cp.lt(ISC5[1], ISC5[4]));

                        // 1 et 2
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC5[0], elem), cp.eq(ISC5[1], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                        // 2 et 3
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC5[1], elem), cp.eq(ISC5[2], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                        // 3 et 4
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC5[2], elem), cp.eq(ISC5[3], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }


                        // 4 et 5
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC5[3], elem), cp.eq(ISC5[4], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                        // 5 et 1
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC5[4], elem), cp.eq(ISC5[0], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                        /*for (int i = 0; i < 5; i++) {
                            expr.addTerm(1, ISC5[i]);
                        }*/
                    } catch (IloException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                // #-------------Cycle 6--------------------#
                if (imposedFragments.get("sixcycle") == true) {
                    try {
                        ISC6 = cp.intVarArray(6, 0, N - 1);
                        cp.add(cp.allDiff(ISC6));
                        cp.add(cp.lt(ISC6[0], ISC6[1]));
                        cp.add(cp.lt(ISC6[0], ISC6[2]));
                        cp.add(cp.lt(ISC6[0], ISC6[3]));
                        cp.add(cp.lt(ISC6[0], ISC6[4]));
                        cp.add(cp.lt(ISC6[0], ISC6[5]));
                        cp.add(cp.lt(ISC6[1], ISC6[5]));

                        // 1 et 2
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[0], elem), cp.eq(ISC6[1], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }
                        // 2 et 3
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[1], elem), cp.eq(ISC6[2], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }
                        // 3 et 4
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[2], elem), cp.eq(ISC6[3], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }
                        // 4 et 5
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[3], elem), cp.eq(ISC6[4], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }
                        // 5 et 6
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[4], elem), cp.eq(ISC6[5], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }
                        // 6 et 1
                        for (int elem = 0; elem < N; elem++) {
                            for (int elem2 = 0; elem2 < N; elem2++) {
                                cp.add(cp.ifThen(cp.and(cp.eq(ISC6[5], elem), cp.eq(ISC6[0], elem2)), cp.neq(MATRIX[elem][elem2], 0)));
                            }
                        }

                     /*   for (int i = 0; i < 5; i++) {
                            expr.addTerm(1, ISC6[i]);
                        }
*/
                    } catch (IloException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                //cp.addMinimize(expr);


            }

            // Constraint 6: Connectivity Constraints

            if (isConnectivityActive.toString().toLowerCase().equals("true")) {
                System.out.println("YES isConnectivityActive IS TRUE");
                // Generation of K_i Variables
                // Define the distance variables
                z = new IloIntVar[N];
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

                // ajouter pour optimiser les solutions redondantes due à la variable Z.
                for (int i = 0; i < N; i++) {
                    for (int j = i + 1; j < N; j++) {
                        cp.add(cp.ifThen(
                                cp.neq(MATRIX[i][j], 0),
                                cp.le(cp.abs(cp.diff(z[i], z[j])), 1)
                        ));
                        //si aij >0 alors abs( zi - zj) <= 1
                    }
                }

            }




          /* if (isConnectivityActive.toString().toLowerCase().equals("true")) {
                for (int i = 0; i < N; i++) {
                    // Ensure that the sum of the subarray from i+1 to N is greater than 0
                    if((i+1) < N) // pour eviter la derniere ligne
                    {
                        IloIntVar[] subArray = Arrays.copyOfRange(MATRIX[i], i + 1, N);
                        cp.add(cp.gt(cp.sum(subArray), 0));
                    }
                }
            }*/


            // NEW NEW

            // Constraint 4: Left-Stacked Constraint : not compatible with rev Lex (Contre exemple graphe complet bipartite [2 3 4] [ 1 5 6]
            /*for (int i = 0; i < 1; i++) {
                for (int j = i+1; j < N-1; j++) {
                    IloIntExpr sum = cp.intExpr();
                    for (int k = 0; k <= i; k++) {
                        sum = cp.sum(sum, MATRIX[k][j]);
                    }
                    cp.add(cp.le(MATRIX[i][j+1], sum));
                }
            }*/


            //************** Constraint 7: Hybridization state *************
            for (Integer i = 0; i < N; i++) {
                //************** Constraint 4.1: Hybridization state SP3 ************* A revoir de le debut directement filtrer from domain value
                if (hybridizationState[i] == 3) {
                    for (int j = 0; j < N; j++) {
                        cp.add(cp.le(MATRIX[i][j], 1));
                    }
                }
                //************** Constraint 4.2: Hybridization state SP2 ************* avec filtrage sur la valeur 3
                if (hybridizationState[i] == 2) {
                    for (int j = 0; j < N; j++) {
                        cp.add(cp.ge(cp.count(MATRIX[i], 2), 1));
                        cp.add(cp.ge(cp.count(MATRIX[i], 3), 0));
                    }
                }
                //************** Constraint 4.3: Hybridization state SP1 *************
                if (hybridizationState[i] == 1) {
                    for (int j = 0; j < N; j++) {
                        cp.add(cp.or(cp.ge(cp.count(MATRIX[i], 3), 1), cp.ge(cp.count(MATRIX[i], 2), 2)));
                        //   cp.add(cp.eq(b[j], cp.gt(cp.count(v2, values[j]),0)));
                        //          .le(cp.sum(MATRIX[i]),4));
                        //  cp.add(cp.le(MATRIX[i][j], 1));
                    }
                }
            }

            //************** Constraint 8: Certain Proximity RelationShips Constraint state (HMBC and BOND) *************
            if (!molecule.getCorrelations().isEmpty()) {
                int NbrCorrelations = correlations.size();
                //System.out.println("NbrCorrelations " + NbrCorrelations);
                if (NbrCorrelations > 0) {
                    //************** Constraint 5.1:   BOND  *************
                    List<Correlation> BONDCorrelations = GeneralFunction.getBONDCorrelations(correlations);
                    int NbrBONDCorrelations = BONDCorrelations.size();
                    //System.out.println("NbrBONDCorrelations " + NbrBONDCorrelations);
                    for (Correlation correlation : BONDCorrelations) {
                        for (int i = 0; i < correlation.getAtomSet1().size(); i++) {
                            int atom1 = correlation.getAtomSet1().get(i) - 1;
                            int atom2 = correlation.getAtomSet2().get(i) - 1;
                            cp.add(cp.neq(MATRIX[atom1][atom2], 0));
                        }
                    }
                    //************** Constraint 8.2:   HMBC  *************
                    List<Correlation> HMBCCorrelations = GeneralFunction.getHMBCCorrelations(correlations);
                    int NbrHMBCCorrelations = HMBCCorrelations.size();
                    System.out.println("NbrHMBCCorrelations " + NbrHMBCCorrelations);
                    for (Correlation correlation : HMBCCorrelations) {
                        IloIntVar[] correlationsVars = new IloIntVar[NbrCorrelations];
                        correlationsVars = cp.intVarArray(NbrCorrelations, 0, N - 1);
                        for (int i = 0; i < correlation.getAtomSet1().size(); i++) {
                            int atom1 = correlation.getAtomSet1().get(i) - 1;
                            int atom2 = correlation.getAtomSet2().get(i) - 1;
                            for (int inter = 0; inter < N; inter++) {
                                cp.add(cp.ifThen(cp.eq(correlationsVars[i], inter), cp.and(cp.neq(MATRIX[atom1][inter], 0), cp.neq(MATRIX[atom1][inter], 0))));
                            }
                            //cp.add(cp.ifThenElse(cp.eq(MATRIX[atom1][atom2], 0), cp.eq(correlationsVars[i], 0), cp.neq(correlationsVars[i], 0)));
                            cp.add(cp.ifThen(cp.neq(MATRIX[atom1][atom2], 0), cp.eq(correlationsVars[i], 0)));
                            // il y'a des duplication dans les solutions à cause de l'intermediaire possible, il est possible d'avoir plusieurs intermediare , exemple de carré comme fragment
                        }
                    }

                }
            }
            int[][] result = new int[N][N];
            Set<Matrix> allSolutions = new HashSet<>();

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
                /*
                if (isConnectivityActive.toString().toLowerCase().equals("true")) {
                    System.out.println(" * * * * Connectivity var Z values  * * * * ");
                    for (int i = 0; i < N; i++) {
                        System.out.print(" " + (int) cp.getValue(z[i]));
                    }
                }*/
                /*
                if (imposedFragments.get("threecycle") == true) {
                    System.out.println("\n ISC3: ");
                    for (int i = 0; i < 3; i++) {
                        System.out.print((int) cp.getValue(ISC3[i]) + " , ");

                    }
                }
                if (imposedFragments.get("fourcycle") == true) {
                    System.out.println("\n ISC4: ");
                    for (int i = 0; i < 4; i++) {
                        System.out.print((int) cp.getValue(ISC4[i]) + " , ");
                    }
                }
                if (imposedFragments.get("fivecycle") == true) {
                    System.out.println("\n ISC5: ");
                    for (int i = 0; i < 5; i++) {
                        System.out.print((int) cp.getValue(ISC5[i]) + " , ");
                    }
                }
                if (imposedFragments.get("sixcycle") == true) {
                    System.out.println("\n ISC6: ");
                    for (int i = 0; i < 6; i++) {
                        System.out.print((int) cp.getValue(ISC6[i]) + " , ");
                    }
                }
                */
                Date Stop = new Date();
                double diff = Stop.getTime() - Start.getTime();
                diff = diff / 1000 % 60;
                System.out.println("Molecular Structure is successfully generated CPU Time: " + diff + " Second(s)   Thanks for trying ! ");
                allSolutions.add(new Matrix(result));
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
            System.out.println("ArrayList size:" + allSolutions.size());
            List<int[][]> allSolutionsArray = allSolutions.stream()
                    .map(Matrix::getData)
                    .collect(Collectors.toList());

            return (allSolutionsArray);

        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }


    public List<int[][]> generateAllMatrixLex(ArrayList<Integer> degreeSequence, String isSymmetryBreakingActive, String isConnectivityActive, String isConnectivityOptimizationActive) {
        try {
            System.out.println("Received JSON in generateAllMatrixRevLexPerm degreeSequence: " + degreeSequence); // Log the received JSON
            System.out.println("isSymmetryBreakingActive JSON: " + isSymmetryBreakingActive); // Log the received JSON
            System.out.println("isConnectivityActive JSON: " + isConnectivityActive); // Log the received JSON
            System.out.println("isConnectivityOptimizationActive JSON: " + isConnectivityOptimizationActive); // Log the received JSON

            int[] DEGREE = degreeSequence.stream().mapToInt(Integer::intValue).toArray();
            final int DEGREE_LOWER_BOUND = 0;
            int DEGREE_UPPER_BOUND = 1;//Arrays.stream(DEGREE).max().orElseThrow();
            int N = DEGREE.length;

            // Generation of K_i Variables
            IloIntVar[] z = null; //isConnectivityActive Constraint
            IloCP cp = new IloCP();

            // Define Vars of the Adjacency matrix
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, DEGREE_LOWER_BOUND, DEGREE_UPPER_BOUND);
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
                for (int j = 0; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }
            // Constraint 3: Symmetry breaking of equal degree
            if (isSymmetryBreakingActive.toString().toLowerCase().equals("true")) {
                System.out.println("isSymmetryBreakingActive.toString().toLowerCase().equals(\"true\")");
                for (int i = 0; i < N-1; i++) {
                    if ((DEGREE[i] == DEGREE[i+1])) {
                        cp.add(cp.lexicographic(MATRIX[i], MATRIX[i+1]));
                    }
                }

            }

            // Constraint 6: Connectivity Constraint
            // With Variables Z
            if (isConnectivityActive.toString().toLowerCase().equals("true")) {
                System.out.println("YES isConnectivityActive IS TRUE");
                // Generation of K_i Variables
                // Define the distance variables
                z = new IloIntVar[N];
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
                if (isConnectivityOptimizationActive.toString().toLowerCase().equals("true")) {
                    for (int i = 0; i < N; i++) {
                        for (int j = i + 1; j < N; j++) {
                            cp.add(cp.ifThen(
                                    cp.neq(MATRIX[i][j], 0),
                                    cp.le(cp.abs(cp.diff(z[i], z[j])), 1)
                            ));
                            //si aij >0 alors abs( zi - zj) <= 1
                        }
                    }
                }

            }

            /*int[][] result = new int[N][N];
            Set<Matrix> allSolutions = new HashSet<>();
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

                if (isConnectivityActive.toString().toLowerCase().equals("true")) {
                    System.out.println(" * * * * Connectivity var Z values  * * * * ");
                    for (int i = 0; i < N; i++) {
                        System.out.print(" " + (int) cp.getValue(z[i]));
                    }
                }

                Date Stop = new Date();
                double diff = Stop.getTime() - Start.getTime();
                diff = diff / 1000 % 60;
                System.out.println("\n Structure is successfully generated CPU Time: " + diff + " Second(s)   Thanks for trying ! ");
                allSolutions.add(new Matrix(result));
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
            System.out.println("ArrayList size:" + allSolutions.size());
            List<int[][]> allSolutionsArray = allSolutions.stream()
                    .map(Matrix::getData)
                    .collect(Collectors.toList());

            return (allSolutionsArray);*/

            // Configure solver for memory optimization
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet); // Suppress logs
            cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst); // Depth-first search
            cp.setParameter(IloCP.IntParam.DefaultInferenceLevel, IloCP.ParameterValues.Low); // Low inference level

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


            return null;

        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }


    public List<int[][]> generateAllMatrixRevLex(ArrayList<Integer> degreeSequence, String isSymmetryBreakingActive, String isConnectivityActive, String isConnectivityOptimizationActive, String isPermMatrixActive) {
        try {
            System.out.println("Received JSON in generateAllMatrixRevLexPerm degreeSequence: " + degreeSequence); // Log the received JSON
            System.out.println("isSymmetryBreakingActive JSON: " + isSymmetryBreakingActive); // Log the received JSON
            System.out.println("isConnectivityActive JSON: " + isConnectivityActive); // Log the received JSON
            System.out.println("isConnectivityOptimizationActive JSON: " + isConnectivityOptimizationActive); // Log the received JSON
            System.out.println("isPermMatrixActive JSON: " + isPermMatrixActive); // Log the received JSON

            int[] DEGREE = degreeSequence.stream().mapToInt(Integer::intValue).toArray();
            final int DEGREE_LOWER_BOUND = 0;
            int DEGREE_UPPER_BOUND = 1;//Arrays.stream(DEGREE).max().orElseThrow();
            int N = DEGREE.length;

            // Generation of K_i Variables
            IloIntVar[] z = null; //isConnectivityActive Constraint
            IloCP cp = new IloCP();

            // Define Vars of the Adjacency matrix
            IloIntVar[][] MATRIX = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                MATRIX[i] = cp.intVarArray(N, DEGREE_LOWER_BOUND, DEGREE_UPPER_BOUND);
            }

            // Constraint 1: Null Diagonal of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                cp.add(cp.eq(MATRIX[i][i], 1));
            }
            // Constraint 2: Define Degree Constraint
            if (isPermMatrixActive.toString().toLowerCase().equals("false")) {
                for (int i = 0; i < N; i++) {
                    cp.addEq(cp.sum(MATRIX[i]), DEGREE[i] + 1);
                }
            }
            // Constraint 3: Symmetry of the Adjacency matrix
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    cp.add(cp.eq(MATRIX[i][j], MATRIX[j][i]));
                }
            }
            // Constraint 3: Symmetry breaking of equal degree
            if (isSymmetryBreakingActive.toString().toLowerCase().equals("true")) {
                System.out.println("isSymmetryBreakingActive.toString().toLowerCase().equals(\"true\")");
                for (int i = 0; i < N-1; i++) {
                    if ((DEGREE[i] == DEGREE[i+1])) {
                        IloIntExpr[] reversedMatrixI = reverseArray(MATRIX[i]);
                        IloIntExpr[] reversedMatrixJ = reverseArray(MATRIX[i + 1]);
                        cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                    }
                }
            }



            // Constraint 6: Connectivity Constraint
            // With Variables Z
            if (isConnectivityActive.toString().toLowerCase().equals("true")) {
                System.out.println("YES isConnectivityActive IS TRUE");
                // Generation of K_i Variables
                // Define the distance variables
                z = new IloIntVar[N];
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
                if (isConnectivityOptimizationActive.toString().toLowerCase().equals("true")) {

                    for (int i = 0; i < N; i++) {
                        for (int j = i + 1; j < N; j++) {
                            cp.add(cp.ifThen(
                                    cp.neq(MATRIX[i][j], 0),
                                    cp.le(cp.abs(cp.diff(z[i], z[j])), 1)
                            ));
                            //si aij >0 alors abs( zi - zj) <= 1
                        }
                    }
                }

            }
            /*
            if (isConnectivityActive.toString().toLowerCase().equals("true")) {
                for (int i = 0; i < N; i++) {
                    // Ensure that the sum of the subarray from i+1 to N is greater than 0
                    if ((i + 1) < N) // pour eviter la derniere ligne
                    {
                        IloIntVar[] subArray = Arrays.copyOfRange(MATRIX[i], i + 1, N);
                        cp.add(cp.gt(cp.sum(subArray), 0));
                    }
                }
            }
            */

            IloIntVar[][] PERMATRIX = new IloIntVar[0][];
            if (isPermMatrixActive.toString().toLowerCase().equals("true")) {
                // Define Vars of the Perm Adjacency matrix
                PERMATRIX = new IloIntVar[N][];
                for (int i = 0; i < N; i++) {
                    PERMATRIX[i] = cp.intVarArray(N, 0, 1);
                }

                // Constraint 1: Sum of each row = 1
                for (int i = 0; i < N; i++) {
                    cp.addEq(cp.sum(PERMATRIX[i]), 1);
                }

                // Constraint 2: Sum of each column = 1
                for (int j = 0; j < N; j++) {
                    IloIntVar[] column = new IloIntVar[N];
                    for (int i = 0; i < N; i++) {
                        column[i] = PERMATRIX[i][j];
                    }
                    cp.addEq(cp.sum(column), 1);
                }

                // Constraint 3: if i and j have same degree , they can't switch
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        if ((i != j) && (DEGREE[i] == DEGREE[j])) {
                            cp.addEq(PERMATRIX[i][j], 0);
                            cp.addEq(PERMATRIX[j][i], 0);
                        }
                    }
                }

                // Constraint: Scalar product of DEGREE and each row of PERMATRIX equals the sum of MATRIX[i] + 1
                for (int i = 0; i < N; i++) {
                    // Add the constraint: scalProd(DEGREE, PERMATRIX[i]) == sum(MATRIX[i]) + 1
                    cp.addEq(
                            cp.scalProd(DEGREE, PERMATRIX[i]), // Scalar product of DEGREE and PERMATRIX[i]
                            cp.sum(cp.sum(MATRIX[i]), -1)       // Sum of MATRIX[i] + 1
                    );
                }


                //Rev Lex On the Permit MAtrix // ESSAI pour empecher la matrice de permutation de faire des symmetry -->  donne 0 solution à ignorer / supprimer
                for (int i = 0; i < N; i++) {
                    for (int j = i + 1; j < N; j++) {
                        if ((DEGREE[i] == DEGREE[j])) {
                            // cp.add(cp.lexicographic(MATRIX[i], MATRIX[j]));

                            IloIntExpr[] reversedMatrixI = reverseArray(PERMATRIX[i]);
                            IloIntExpr[] reversedMatrixJ = reverseArray(PERMATRIX[j]);
                            cp.add(cp.lexicographic(reversedMatrixI, reversedMatrixJ));
                        }
                    }
                }


            }

            // NEW NEW
            // Constraint 4: Left-Stacked Constraint : not compatible with rev Lex (Contre exemple graphe complet bipartite [2 3 4] [ 1 5 6]
            /*for (int i = 0; i < 1; i++) {
                for (int j = i+1; j < N-1; j++) {
                    IloIntExpr sum = cp.intExpr();
                    for (int k = 0; k <= i; k++) {
                        sum = cp.sum(sum, MATRIX[k][j]);
                    }
                    cp.add(cp.le(MATRIX[i][j+1], sum));
                }
            }*/


            int[][] result = new int[N][N];
            Set<Matrix> allSolutions = new HashSet<>();

            Date Start = new Date();
            /*
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

                if (isPermMatrixActive.toString().toLowerCase().equals("true")) {
                    // Display the MAtrix of Permitation
                    System.out.print("* * * *  Permutation Matrix * * * * \n");
                    for (int i = 0; i < N; i++) {
                        for (int j = 0; j < N; j++) {
                            System.out.print(" " + (int) cp.getValue(PERMATRIX[i][j]));
                        }
                        System.out.print(" \n");
                    }
                }

                if (isConnectivityActive.toString().toLowerCase().equals("true")) {
                    System.out.println(" * * * * Connectivity var Z values  * * * * ");
                    for (int i = 0; i < N; i++) {
                        System.out.print(" " + (int) cp.getValue(z[i]));
                    }
                }

                Date Stop = new Date();
                double diff = Stop.getTime() - Start.getTime();
                diff = diff / 1000 % 60;
                System.out.println("\n Structure is successfully generated CPU Time: " + diff + " Second(s)   Thanks for trying ! ");
                //allSolutions.add(new Matrix(result));
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
            System.out.println("ArrayList size:" + allSolutions.size());
            List<int[][]> allSolutionsArray = allSolutions.stream()
                    .map(Matrix::getData)
                    .collect(Collectors.toList());
            */

            // Counter to track the number of solutions
            cp.startNewSearch(); // Start the search
            int solutionCount = 0;

            while (cp.next()) {
                solutionCount++;
                // Optionally, retrieve and print variable values for each solution
            }

            cp.endSearch(); // End the search
            System.out.println("Number of solutions found: " + solutionCount);

            return null;

        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }


    private void forbidCycles(IloCP cp, IloIntVar[][] MATRIX, ArrayList<int[]> explicitCycles, int cycleLength) throws
            IloException {
        for (int[] aux : explicitCycles) {
            IloConstraint[] cycleConstraint = new IloConstraint[cycleLength];
            for (int i = 0; i < cycleLength; i++) {
                int from = aux[i];
                int to = aux[(i + 1) % cycleLength]; // Wrap around for the last vertex
                cycleConstraint[i] = cp.eq(MATRIX[from][to], 0);
            }
            cp.add(cp.or(cycleConstraint));
        }
    }

    private void forbidChaines(IloCP cp, IloIntVar[][] MATRIX, ArrayList<int[]> ExplicitChaine, int chainedegree) throws
            IloException {
        for (int[] aux : ExplicitChaine) {
            IloConstraint[] cycleConstraint = new IloConstraint[chainedegree];
            for (int i = 0; i < chainedegree; i++) {
                System.out.println("chainedegree: " + chainedegree + " For: " + i);
                int from = aux[i];
                int to = aux[(i + 1) % chainedegree]; // Wrap around for the last vertex
                cycleConstraint[i] = cp.eq(MATRIX[from][to], 0);
            }
            cp.add(cp.or(cycleConstraint));
        }
    }


    public static IloIntExpr[] negateArray(IloCP cp, IloIntVar[] array) throws IloException {
        IloIntExpr[] negatedArray = new IloIntExpr[array.length];
        for (int i = 0; i < array.length; i++) {
            negatedArray[i] = cp.prod(-1, array[i]);
        }
        return negatedArray;
    }

    public static IloIntExpr[] reverseArray(IloIntExpr[] array) throws IloException {
        int length = array.length;
        IloIntExpr[] reversedArray = new IloIntExpr[length];

        for (int i = 0; i < length; i++) {
            reversedArray[i] = array[length - 1 - i];
        }

        return reversedArray;
    }


}
