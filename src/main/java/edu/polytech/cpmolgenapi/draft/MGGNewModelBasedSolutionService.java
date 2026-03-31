package edu.polytech.cpmolgenapi.draft;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cp.IloCP;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;


@Service
public class MGGNewModelBasedSolutionService {
    private static final int[] DEGREE = {2, 2, 2, 4, 4, 4};  // Manually set degree values between 2 and 4 for each atom


    public static int[][] optimize() {
        try {
            IloCP cp = new IloCP();
            // Set parameters
            // Variables Declaration
            int N = 6;

            IloIntVar[][] NEIGHBORS = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                NEIGHBORS[i] = cp.intVarArray(DEGREE[i], 0, N - 1);
            }


            // Constraint 1:   Ensure uniqueness of neighbors
            for (int i = 0; i < N; i++) {
                cp.add(cp.allDiff(NEIGHBORS[i]));
            }

            // Constraint 2:   Ensure i not appear in i row
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < DEGREE[i]; j++) {
                    //  System.out.println("NEIGHBORS["+i+"]["+j+"], "+i+" )");
                    cp.add(cp.neq(NEIGHBORS[i][j], i));
                }
            }


        /*   // Constraint 3:  Ensure no self-loops
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.count(NEIGHBORS[i], i), 0);
            }*/

            // Constraint 4: Ensure graph stability
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < DEGREE[i]; j++) {
                    for (int neighborIndex = 0; neighborIndex < N; neighborIndex++) {
                        cp.add(
                                cp.or(
                                        cp.neq(NEIGHBORS[i][j], neighborIndex),
                                        cp.addGe(cp.count(NEIGHBORS[neighborIndex], i),1  )
                                )
                        );

                    }
                }
            }

            //(NEIGHBORS[i][j] = K) =>  (cp.count(NEIGHBORS[k], i))>0

            // Constraint 5: Ensure neighbors are ordered
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < DEGREE[i] - 1; j++) {
                    cp.add(cp.le(NEIGHBORS[i][j], NEIGHBORS[i][j + 1]));
                }
            }

            // Constraint 6: Ensure every index "i" appears DEGREE[i] times
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.count(cp.getAllIloIntVars(), i), DEGREE[i]);
            }


             // Constraint 7: Ensure NEIGHBORS array sums are ordered for vertices with the same degree
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                        cp.add(cp.le(cp.sum(NEIGHBORS[i]), cp.sum(NEIGHBORS[j])));
                    }
                }
            }
            /*atom 0:  (1*N)exp 0  + (2*N)exp1, (3*N) exp2      --> 6  ->
            atom 1:  0, 1, 5     --> 6 */



            // Constraint 2: Degree constraint
      /*      for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(NEIGHBORS[i]), DEGREE[i]); // Ensure the count of valid NEIGHBORS equals the desired degree
            }*/

      /*      // Constraint 3: Symmetry constraint
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < DEGREE[i]; j++) {
                    int neighborIndex = (int) cp.getValue(NEIGHBORS[i][j]) - 1; // Subtract 1 to get the 0-based index
                    cp.add(cp.member(i + 1, NEIGHBORS[neighborIndex])); // Ensure symmetry by checking mutual membership
                }
            }
*/

            Date Start = new Date();
            int[][] result = new int[N][N];
            if (cp.solve()) {
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < DEGREE[i]; j++) {
                        System.out.print(" " + (int) cp.getValue(NEIGHBORS[i][j]));
                        //result[i][j] = (int) cp.getValue(NEIGHBORS[i][j]);
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

    public static int[][] optimize_allsolution() {
        try {
            IloCP cp = new IloCP();
            // Set parameters
            // Variables Declaration
            int N = 6;

            IloIntVar[][] NEIGHBORS = new IloIntVar[N][];
            for (int i = 0; i < N; i++) {
                NEIGHBORS[i] = cp.intVarArray(DEGREE[i], 0, N - 1);
            }


            // Constraint 1:   Ensure uniqueness of neighbors
            for (int i = 0; i < N; i++) {
                cp.add(cp.allDiff(NEIGHBORS[i]));
            }
            // Constraint 1:   Ensure i not appear in i row
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < DEGREE[i]; j++) {
                    //  System.out.println("NEIGHBORS["+i+"]["+j+"], "+i+" )");
                    cp.add(cp.neq(NEIGHBORS[i][j], i));
                }
            }


            // Constraint 2:  Ensure no self-loops
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.count(NEIGHBORS[i], i), 0);
            }

            // Constraint 3: Ensure graph stability
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < DEGREE[i]; j++) {
                    for (int neighborIndex = 0; neighborIndex < N; neighborIndex++) {
                        cp.add(
                                cp.or(
                                        cp.neq(NEIGHBORS[i][j], neighborIndex),
                                        cp.addGe(cp.count(NEIGHBORS[neighborIndex], i),1  )
                                )
                        );

                    }
                }
            }
            // Constraint 4: Ensure neighbors are ordered
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < DEGREE[i] - 1; j++) {
                    cp.add(cp.le(NEIGHBORS[i][j], NEIGHBORS[i][j + 1]));
                }
            }

            // Constraint 5: Ensure every index "i" appears DEGREE[i] times
           /* for (int i = 0; i < N; i++) {
                cp.addEq(cp.count(cp.getAllIloIntVars(), i), DEGREE[i]);
            }*/

            // Constraint 6: Ensure NEIGHBORS array sums are ordered for vertices with the same degree
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (DEGREE[i] == DEGREE[j]) {
                        cp.add(cp.le(cp.sum(NEIGHBORS[i]), cp.sum(NEIGHBORS[j])));
                    }
                }
            }

            // Constraint 2: Degree constraint
      /*      for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(NEIGHBORS[i]), DEGREE[i]); // Ensure the count of valid NEIGHBORS equals the desired degree
            }*/

      /*      // Constraint 3: Symmetry constraint
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < DEGREE[i]; j++) {
                    int neighborIndex = (int) cp.getValue(NEIGHBORS[i][j]) - 1; // Subtract 1 to get the 0-based index
                    cp.add(cp.member(i + 1, NEIGHBORS[neighborIndex])); // Ensure symmetry by checking mutual membership
                }
            }
*/

            int[][] result = new int[N][N];

            Date Start = new Date();
            boolean ok = false;
            cp.startNewSearch();
            int compteur = 0;
            while (cp.next()) {
                compteur++;
                ok = true;

                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < DEGREE[i]; j++) {
                        System.out.print(" " + (int) cp.getValue(NEIGHBORS[i][j]));
                        result[i][j] = (int) cp.getValue(NEIGHBORS[i][j]);
                    }
                    System.out.print(" \n");

                }
                Date Stop = new Date();
                double diff = Stop.getTime() - Start.getTime();
                diff = diff / 1000 % 60;
                System.out.println("Molecular Structure is successfully generated CPU Time: " + diff + " Second(s)   Thanks for trying ! ");

                //all_solutions.add(result);

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

            return result;
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }

}