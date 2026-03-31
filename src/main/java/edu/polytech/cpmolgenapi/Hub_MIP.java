package edu.polytech.cpmolgenapi;

import ilog.concert.*;
import ilog.cp.IloCP;


public class Hub_MIP {
    //version 1: Multi Hub Location Direct and Undirect Routing
    public static void version1() {

        try {
            /*int N = 10; // Number of nodes

            // Example cost matrix (symmetric)
            double[][] cost = {
                    {0, 2, 5, 6, 7, 4, 3, 8, 9, 6},
                    {2, 0, 3, 8, 5, 6, 7, 3, 2, 4},
                    {5, 3, 0, 4, 7, 8, 2, 1, 3, 5},
                    {6, 8, 4, 0, 6, 5, 4, 2, 1, 7},
                    {7, 5, 7, 6, 0, 2, 3, 4, 6, 8},
                    {4, 6, 8, 5, 2, 0, 1, 3, 5, 7},
                    {3, 7, 2, 4, 3, 1, 0, 2, 4, 6},
                    {8, 3, 1, 2, 4, 3, 2, 0, 5, 6},
                    {9, 2, 3, 1, 6, 5, 4, 5, 0, 7},
                    {6, 4, 5, 7, 8, 7, 6, 6, 7, 0}
            };

            // Example demand matrix
            double[][] demand = {
                    {0, 10, 20, 15, 25, 30, 10, 15, 20, 25},
                    {10, 0, 15, 20, 10, 15, 25, 20, 10, 30},
                    {20, 15, 0, 10, 15, 25, 30, 15, 20, 10},
                    {15, 20, 10, 0, 25, 10, 20, 30, 25, 15},
                    {25, 10, 15, 25, 0, 20, 15, 10, 30, 10},
                    {30, 15, 25, 10, 20, 0, 10, 25, 20, 15},
                    {10, 25, 30, 20, 15, 10, 0, 15, 25, 30},
                    {15, 20, 15, 30, 10, 25, 15, 0, 10, 20},
                    {20, 10, 20, 25, 30, 20, 25, 10, 0, 15},
                    {25, 30, 10, 15, 10, 15, 30, 20, 15, 0}
            };*/

            int N = 6; // Number of nodes

            // Example cost matrix (symmetric)
            double[][] cost = {
                    {0, 2, 5, 6, 7, 4},
                    {2, 0, 3, 8, 5, 6},
                    {5, 3, 0, 4, 7, 8},
                    {6, 8, 4, 0, 6, 5},
                    {7, 5, 7, 6, 0, 2},
                    {4, 6, 8, 5, 2, 0}
            };

            // Example demand matrix
            double[][] demand = {
                    {0, 10, 20, 15, 25, 30},
                    {10, 0, 15, 20, 10, 15},
                    {20, 15, 0, 10, 15, 25},
                    {15, 20, 10, 0, 25, 10},
                    {25, 10, 15, 25, 0, 20},
                    {30, 15, 25, 10, 20, 0}
            };
            IloCP cp = new IloCP();

            // Decision variables
            IloIntVar[] x = cp.intVarArray(N, 0, 1, "x"); // x[h] = 1 if node h is a hub
            IloIntVar[][] z = new IloIntVar[N][N]; // z[i][j] = 1 if demand from i to j is direct
            IloIntVar[][][] y = new IloIntVar[N][N][N]; // y[i][j][h] = 1 if routed via hub h

            // Initialize z and y variables
            for (int i = 0; i < N; i++) {
                z[i] = cp.intVarArray(N, 0, 1);
                for (int j = 0; j < N; j++) {
                    y[i][j] = cp.intVarArray(N, 0, 1);
                }
            }

            // Objective function
            IloLinearNumExpr totalCost = cp.linearNumExpr();
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i != j) {
                        // Cost of direct connection
                        totalCost.addTerm(cost[i][j] * demand[i][j], z[i][j]);

                        // Cost of hub-based routing
                        for (int h = 0; h < N; h++) {
                            totalCost.addTerm((cost[i][h] + cost[h][j]) * demand[i][j], y[i][j][h]);
                        }
                    }
                }
            }
            cp.addMinimize(totalCost);

            // Constraints
            // 1. Each demand must be routed either directly or via a hub
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i != j) {
                        IloLinearNumExpr routing = cp.linearNumExpr();
                        routing.addTerm(1, z[i][j]); // Direct routing
                        for (int h = 0; h < N; h++) {
                            routing.addTerm(1, y[i][j][h]); // Hub routing
                        }
                        cp.addEq(routing, 1); // Exactly one routing choice
                    }
                }
            }

            // 2. If routed via hub h, h must be a hub
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    for (int h = 0; h < N; h++) {
                        cp.addLe(y[i][j][h], x[h]);
                    }
                }
            }

            // Solve the model
            if (cp.solve()) {
                System.out.println("Minimum Cost: " + cp.getObjValue());
                System.out.println("Hub Nodes:");
                for (int i = 0; i < N; i++) {
                    if (cp.getValue(x[i]) > 0.5) {
                        System.out.println("Node " + (i + 1));
                    }
                }
                System.out.println("Direct Connections:");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        if (i != j && cp.getValue(z[i][j]) > 0.5) {
                            System.out.println("Direct: Node " + (i + 1) + " -> Node " + (j + 1));
                        }
                    }
                }
            } else {
                System.out.println("No feasible solution found.");
            }

        } catch (IloException e) {
            e.printStackTrace();
        }
    }


    // version2 :  Multi Hub LocationD irect and Undirect Routing With and Multi-Modal Costs
    public static void version2() {
        try {
            // Number of nodes
            int N = 5;

            // Cost matrices
            double[][] cost = {
                    {0, 2, 5, 6, 7},
                    {2, 0, 3, 8, 5},
                    {5, 3, 0, 4, 7},
                    {6, 8, 4, 0, 6},
                    {7, 5, 7, 6, 0}
            };

            // Demand matrix
            double[][] demand = {
                    {0, 10, 20, 15, 25},
                    {10, 0, 15, 20, 10},
                    {20, 15, 0, 10, 15},
                    {15, 20, 10, 0, 25},
                    {25, 10, 15, 25, 0}
            };

            // Cost parameters
            double collectionCost = 1.0; // Cost for routing from origin to hub
            double transferCost = 2.0;  // Cost for routing between hubs
            double distributionCost = 1.5; // Cost for routing from hub to destination
            double directRoutingCost = 0.5; // Cost for direct routing between nodes

            // Create a new CP solver
            IloCP cp = new IloCP();

            // Declare decision variables
            IloIntVar[] x = cp.boolVarArray(N); // Hub nodes
            IloIntVar[][] z = new IloIntVar[N][N]; // Direct routing between nodes
            IloIntVar[][][] y = new IloIntVar[N][N][N]; // Routing via hubs
            IloIntVar[][] t = new IloIntVar[N][N]; // Transfer between hubs

            // Create decision variables in the CP model
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    z[i][j] = cp.boolVar("z_" + i + "_" + j); // Direct routing variable
                    for (int k = 0; k < N; k++) {
                        y[i][j][k] = cp.boolVar("y_" + i + "_" + j + "_" + k); // Routing via hubs
                    }
                    t[i][j] = cp.boolVar("t_" + i + "_" + j); // Transfer between hubs
                }
            }

            // Define the objective function
            IloLinearNumExpr totalCost = cp.linearNumExpr();

            // Collection cost: routing from origin to hubs
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i != j) {
                        totalCost.addTerm(collectionCost * demand[i][j] * cost[i][j], z[i][j]);
                    }
                }
            }

            // Transfer cost: routing between hubs
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i != j) {
                        totalCost.addTerm(transferCost * demand[i][j] * cost[i][j], t[i][j]);
                    }
                }
            }

            // Distribution cost: routing from hubs to destinations
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i != j) {
                        totalCost.addTerm(distributionCost * demand[i][j] * cost[i][j], z[i][j]);
                    }
                }
            }

            // Direct routing cost between nodes
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i != j) {
                        totalCost.addTerm(directRoutingCost * cost[i][j], z[i][j]);
                    }
                }
            }

            // Minimize total cost
            cp.addMinimize(totalCost);

            // Constraints
            // 1. Ensure each node is either assigned to a hub or has a direct route
            for (int i = 0; i < N; i++) {
                IloLinearNumExpr constraint1 = cp.linearNumExpr();
                for (int j = 0; j < N; j++) {
                    if (i != j) {
                        constraint1.addTerm(1.0, z[i][j]);
                    }
                }
                cp.addLe(constraint1, 1);
            }

            // 2. Ensure hubs can serve the nodes assigned to them
            for (int i = 0; i < N; i++) {
                IloLinearNumExpr constraint2 = cp.linearNumExpr();
                for (int j = 0; j < N; j++) {
                    if (i != j) {
                        constraint2.addTerm(1.0, z[i][j]);
                    }
                }
                cp.addGe(constraint2, 1);
            }

            // 3. If two hubs are connected, the demand from nodes assigned to them must be routed through them
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i != j) {
                        IloLinearNumExpr constraint3 = cp.linearNumExpr();
                        constraint3.addTerm(1.0, t[i][j]);
                        constraint3.addTerm(-1.0, z[i][j]);
                        cp.addLe(constraint3, 0);
                    }
                }
            }

            // Solve the MIP
            if (cp.solve()) {
                System.out.println("Optimal Solution Found: ");
                System.out.println("Objective Value: " + cp.getObjValue());
            } else {
                System.out.println("No solution found.");
            }

            // Clean up
            cp.end();
        } catch (IloException e) {
            e.printStackTrace();
        }


    }

    public static void version3() {
        try {
            // Number of nodes
            int N = 5;

            // Distance matrix
            double[][] distance = {
                    {0, 2, 5, 6, 7},
                    {2, 0, 3, 8, 5},
                    {5, 3, 0, 4, 7},
                    {6, 8, 4, 0, 6},
                    {7, 5, 7, 6, 0}
            };

            // Demand matrix
            double[][] demand = {
                    {0, 10, 20, 15, 25},
                    {10, 0, 15, 20, 10},
                    {20, 15, 0, 10, 15},
                    {15, 20, 10, 0, 25},
                    {25, 10, 15, 25, 0}
            };

            // Cost parameters
            double collectionCost = 1.0; // Cost for routing from origin to hub
            double transferCost = 2.0;  // Cost for routing between hubs
            double distributionCost = 1.5; // Cost for routing from hub to destination
            double directRoutingCost = 0.5; // Cost for direct routing between nodes
            int p = 1; // Number of hubs to be selected

            // Create a new CP solver
            IloCP cp = new IloCP();

            // Decision Variables
            IloIntVar[] x = new IloIntVar[N];  // Whether a hub is located at each node
            IloIntVar[][] a = new IloIntVar[N][N];  // Allocation variables, a_ik if note i is allocated to hub k
            IloIntVar[][][] Z = new IloIntVar[N][N][N]; // Flow from origin to hub
            IloIntVar[][][] Y = new IloIntVar[N][N][N]; // Flow between hubs
            IloIntVar[][][] X = new IloIntVar[N][N][N]; // Flow from hub to destination
            IloIntVar[][] O = new IloIntVar[N][N]; // Direct flow between nodes

            for (int i = 0; i < N; i++) {
                x[i] = cp.boolVar(); // Binary decision variable for hub location
                for (int j = 0; j < N; j++) {
                    a[i][j] = cp.boolVar(); // Binary variable for node allocation to hub and i is a hub
                    for (int k = 0; k < N; k++) {
                        Z[i][j][k] = cp.intVar(0, Integer.MAX_VALUE); // Flow from origin i to hub k
                        Y[i][j][k] = cp.intVar(0, Integer.MAX_VALUE); // Flow between hubs
                        X[i][j][k] = cp.intVar(0, Integer.MAX_VALUE); // Flow from hub k to destination j
                    }
                    O[i][j] = cp.intVar(0, Integer.MAX_VALUE); // Direct flow from origin i to destination j
                }
            }

            // Objective Function: Minimize transportation cost
            IloLinearNumExpr cost = cp.linearNumExpr();

            // Add terms to the objective function

            // Collection cost: sum of Z[i][j][k] for all i, j, k
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < N; k++) {
                        cost.addTerm(collectionCost, Z[i][j][k]);
                    }
                }
            }

            // Transfer cost: sum of Y[i][j][k] for all i, j, k
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < N; k++) {
                        cost.addTerm(transferCost, Y[i][j][k]);
                    }
                }
            }

            // Distribution cost: sum of X[i][j][k] for all i, j, k
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < N; k++) {
                        cost.addTerm(distributionCost, X[i][j][k]);
                    }
                }
            }

            // Direct routing cost: sum of O[i][j] for all i, j
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    cost.addTerm(directRoutingCost, O[i][j]);
                }
            }

            // Fixed cost for hub location: sum of x[i] for all i
            /*for (int i = 0; i < N; i++) {
                cost.addTerm(1.0, x[i]); // Fixed cost for hub location
            }*/

            // Set the objective function to minimize
            cp.addMinimize(cost);

            // Constraints

            // 1. Ensure that exactly p hubs are opened
            cp.addEq(cp.sum(x), p);

            // 2. Ensure each demand node is assigned to exactly one hub
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(a[i]), 1);  // Assign to one hub
            }

            // 3. A node can only be assigned to an open hub
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    cp.addLe(a[i][j], x[j]);  // If a node is assigned, it must be assigned to an open hub
                }
            }

       /*     // 4. Flow conservation: inbound flow at a hub equals outbound flow
            for (int i = 0; i < N; i++) {
                for (int k = 0; k < N; k++) {
                    IloLinearNumExpr expr = cp.linearNumExpr();

                    // Add flow from origin i to hub k (Z[i][k])
                    expr.addTerm(1.0, Z[i][k][k]);  // Flow from origin i to hub k (corrected indexing)

                    // Add flow from hub k to other hubs (Y[i][k][l]) for l in the hub set
                    for (int l = 0; l < N; l++) {
                        if (l != k) { // Ensure we don't include flow from k to itself
                            expr.addTerm(1.0, Y[i][k][l]);  // Flow from hub k to hub l
                        }
                    }

                    // Add flow from hub k to destination j (X[i][k][j])
                    for (int j = 0; j < N; j++) {
                        expr.addTerm(1.0, X[i][k][j]);  // Flow from hub k to destination j
                    }

                    // Add the flow conservation demand term (demand at node k)
                    expr.addTerm(-1.0, d_k[i][k]);  // Demand at hub k

                    // Now add the constraint: the sum of all flows equals zero
                    cp.addEq(expr, 0);  // Inflow should match outflow at the hub
                }
            }*/

            // 5. Direct routing cannot exceed demand
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    cp.addLe(O[i][j], demand[i][j]);  // Direct routing cannot exceed demand
                }
            }

/*
            // 6. Demand satisfaction: Total flow from origin must meet demand
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    IloLinearNumExpr expr = cp.linearNumExpr();
                    expr.addTerm(1.0,  cp.sum(Z[i][j]));  // Flow from origin i to hub j
                    expr.addTerm(1.0, O[i][j]);  // Direct flow from origin i to destination j
                    cp.addEq(expr, demand[i][j]);  // Total flow must equal the demand
                }
            }*/

            // Solve the problem
            if (cp.solve()) {
                System.out.println("Optimal solution found:");
                for (int i = 0; i < N; i++) {
                    System.out.println("Hub at node " + i + ": " + cp.getValue(x[i]));
                }
            } else {
                System.out.println("No solution found.");
            }

        } catch (IloException e) {
            e.printStackTrace();
        }
    }


    public static void version4() {
        try {
            // Number of nodes
            int N = 5;

            // Distance matrix
            double[][] distance = {
                    {0, 2, 5, 6, 7},
                    {2, 0, 3, 8, 5},
                    {5, 3, 0, 4, 7},
                    {6, 8, 4, 0, 6},
                    {7, 5, 7, 6, 0}
            };

            // Demand matrix
            double[][] demand = {
                    {0, 10, 20, 15, 25},
                    {10, 0, 15, 20, 10},
                    {20, 15, 0, 10, 15},
                    {15, 20, 10, 0, 25},
                    {25, 10, 15, 25, 0}
            };

            // Cost parameters
            double collectionCost = 1.0;  // Cost from origin to hub
            double transferCost = 2.0;    // Cost between hubs
            double distributionCost = 1.5; // Cost from hub to destination
            double directRoutingCost = 0.5; // Cost for direct routing

            // Create a new CP solver
            IloCP cp = new IloCP();

            // Decision Variables
            IloIntVar[] x = new IloIntVar[N]; // Whether a hub is located at each node
            IloIntVar[][] a = new IloIntVar[N][N]; // Allocation variables
            IloIntVar[][] Z = new IloIntVar[N][N]; // Flow from origin to hub
            IloIntVar[][] Y = new IloIntVar[N][N]; // Flow between hubs
            IloIntVar[][] X = new IloIntVar[N][N]; // Flow from hub to destination
            IloIntVar[][] O = new IloIntVar[N][N]; // Direct flow between nodes

            int maxFlow = 50; // Upper bound on flow variables

            // Initialize variables
            for (int i = 0; i < N; i++) {
                x[i] = cp.intVar(0, 1); // Hub decision (0 or 1)
                for (int k = 0; k < N; k++) {
                    a[i][k] = cp.intVar(0, 1); // Node allocation
                    Z[i][k] = cp.intVar(0, maxFlow);
                    Y[i][k] = cp.intVar(0, maxFlow);
                    X[i][k] = cp.intVar(0, maxFlow);
                    O[i][k] = cp.intVar(0, maxFlow);
                }
            }

            // Constraint 1: Each node is assigned to exactly one hub
            for (int i = 0; i < N; i++) {
                cp.addEq(cp.sum(a[i]), 1);
            }

            // Constraint 2: If node `i` is assigned to hub `k`, `k` must be a hub
            for (int i = 0; i < N; i++) {
                for (int k = 0; k < N; k++) {
                    cp.addLe(a[i][k], x[k]);
                }
            }

            // Constraint 3: At least one hub must be established
            cp.addGe(cp.sum(x), 1);

            // Constraint 4: Flow conservation at each hub
            for (int k = 0; k < N; k++) {
                IloLinearNumExpr inboundFlow = cp.linearNumExpr();
                IloLinearNumExpr outboundFlow = cp.linearNumExpr();
                for (int i = 0; i < N; i++) {
                    inboundFlow.addTerm(1.0, Z[i][k]);
                    outboundFlow.addTerm(1.0, X[k][i]);
                }
                cp.addEq(inboundFlow, outboundFlow); // Inflow = Outflow
            }

            // Constraint 5: Direct routing must be within demand
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    cp.addLe(O[i][j], demand[i][j]);
                }
            }

            // Objective Function: Minimize total cost
            IloLinearNumExpr cost = cp.linearNumExpr();
            for (int i = 0; i < N; i++) {
                for (int k = 0; k < N; k++) {
                    cost.addTerm(collectionCost * distance[i][k], Z[i][k]);
                    cost.addTerm(transferCost * distance[i][k], Y[i][k]);
                    cost.addTerm(distributionCost * distance[k][i], X[k][i]);
                    cost.addTerm(directRoutingCost * distance[i][k], O[i][k]);
                }
            }
            cp.addMinimize(cost);

            // Solve the model

            /*if (cp.solve()) {
                System.out.println("Optimal Solution Found!");
                for (int i = 0; i < N; i++) {
                    if (cp.getValue(x[i]) > 0.5) {
                        System.out.println("Hub at node: " + i);
                    }
                }
            } else {
                System.out.println("No feasible solution found.");
            }*/

            if (cp.solve()) {
                System.out.println("Optimal solution found!");

                // 1️⃣ Print hub locations
                System.out.println("\nHub Locations:");
                for (int k = 0; k < N; k++) {
                    if (cp.getValue(x[k]) == 1) {
                        System.out.println("Node " + k + " is a hub.");
                    }
                }

                // 2️⃣ Print allocation of nodes to hubs
                System.out.println("\nNode Allocations to Hubs:");
                for (int i = 0; i < N; i++) {
                    for (int k = 0; k < N; k++) {
                        if (cp.getValue(a[i][k]) == 1) {
                            System.out.println("Node " + i + " is assigned to Hub " + k);
                        }
                    }
                }

                // 3️⃣ Print flow from origins to hubs (Z)
                System.out.println("\nFlow from Origin to Hub:");
                for (int i = 0; i < N; i++) {
                    for (int k = 0; k < N; k++) {
                        double flow = cp.getValue(Z[i][k]);
                        if (flow > 0) {
                            System.out.println("Flow from Node " + i + " to Hub " + k + " = " + flow);
                        }
                    }
                }

                // 4️⃣ Print flow between hubs (Y)
                // Flow between hubs (Y) - Ensure it is part of the model before querying
           /*     System.out.println("\nFlow between Hubs:");
                for (int k = 0; k < N; k++) {
                    for (int k2 = 0; k2 < N; k2++) {
                            double flow = cp.getValue(Y[k][k2]);
                            if (flow > 0) {
                                System.out.println("Flow between Hub " + k + " and Hub " + k2 + " = " + flow);
                            }

                    }
                }*/


                // 5️⃣ Print flow from hubs to destinations (X)
                System.out.println("\nFlow from Hub to Destination:");
                for (int k = 0; k < N; k++) {
                    for (int i = 0; i < N; i++) {
                        double flow = cp.getValue(X[k][i]);
                        if (flow > 0) {
                            System.out.println("Flow from Hub " + k + " to Destination " + i + " = " + flow);
                        }
                    }
                }

                // 6️⃣ Print direct flow between nodes (O)
                System.out.println("\nDirect Flow between Nodes:");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        double flow = cp.getValue(O[i][j]);
                        if (flow > 0) {
                            System.out.println("Direct flow from Node " + i + " to Node " + j + " = " + flow);
                        }
                    }
                }

                // 7️⃣ Print total cost
                System.out.println("\nTotal Cost: " + cp.getObjValue());

            } else {
                System.out.println("No feasible solution found.");
            }
            cp.end();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }


    public static void version5() {
        try {
            // Number of nodes
            int N = 5;

            // Distance matrix (for your reference)
            double[][] distance = {
                    {0, 2, 5, 6, 7},
                    {2, 0, 3, 8, 5},
                    {5, 3, 0, 4, 7},
                    {6, 8, 4, 0, 6},
                    {7, 5, 7, 6, 0}
            };

            // Demand matrix (for your reference)
            double[][] demand = {
                    {0, 10, 20, 15, 25},
                    {10, 0, 15, 20, 10},
                    {20, 15, 0, 10, 15},
                    {15, 20, 10, 0, 25},
                    {25, 10, 15, 25, 0}
            };

            // Cost parameters
            double collectionCost = 1.0; // Cost for routing from origin to hub
            double transferCost = 2.0;  // Cost for routing between hubs
            double distributionCost = 1.5; // Cost for routing from hub to destination
            double directRoutingCost = 0.5; // Cost for direct routing between nodes

            // Create a new CP solver
            IloCP cp = new IloCP();

            // Decision Variables
            IloIntVar[] x = new IloIntVar[N];  // Whether a hub is located at each node
            IloIntVar[][] a = new IloIntVar[N][N];  // Allocation variables
            IloIntVar[][][] Z = new IloIntVar[N][N][N]; // Flow from origin to hub
            IloIntVar[][][] Y = new IloIntVar[N][N][N]; // Flow between hubs
            IloIntVar[][][] X = new IloIntVar[N][N][N]; // Flow from hub to destination
            IloIntVar[][] O = new IloIntVar[N][N]; // Direct flow between nodes

            // Define decision variables
            for (int i = 0; i < N; i++) {
                x[i] = cp.boolVar();  // Hub location decision
                for (int j = 0; j < N; j++) {
                    a[i][j] = cp.boolVar();  // Allocation decision
                    O[i][j] = cp.intVar(0, 1); // Direct flow decision
                    for (int k = 0; k < N; k++) {
                        Z[i][j][k] = cp.intVar(0, Integer.MAX_VALUE); // Flow from origin to hub
                        Y[i][j][k] = cp.intVar(0, Integer.MAX_VALUE); // Flow between hubs
                        X[i][j][k] = cp.intVar(0, Integer.MAX_VALUE); // Flow from hub to destination
                    }
                }
            }

            // Objective function: minimize the total cost
            IloLinearNumExpr totalCost = cp.linearNumExpr();
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < N; k++) {
                        totalCost.addTerm(collectionCost, Z[i][j][k]);  // Cost for flow from origin to hub
                        totalCost.addTerm(transferCost, Y[i][j][k]);    // Cost for flow between hubs
                        totalCost.addTerm(distributionCost, X[i][j][k]); // Cost for flow from hub to destination
                    }
                    totalCost.addTerm(directRoutingCost, O[i][j]); // Cost for direct flow between nodes
                }
            }
            cp.addMinimize(totalCost);

            // Constraints

            // 1. Hub constraint: Each node must either be a hub or not
            for (int i = 0; i < N; i++) {
                cp.addEq(x[i], cp.sum(a[i]));
            }

            // 2. Flow conservation: Inbound flow equals outbound flow at each hub
            for (int i = 0; i < N; i++) {
                for (int k = 0; k < N; k++) {
                    IloLinearNumExpr flowIn = cp.linearNumExpr();
                    IloLinearNumExpr flowOut = cp.linearNumExpr();
                    for (int j = 0; j < N; j++) {
                        flowIn.addTerm(1.0, Z[j][i][k]);   // Flow from origin to hub k
                        flowOut.addTerm(1.0, X[i][j][k]);  // Flow from hub k to destination j
                    }
                    cp.addEq(flowIn, flowOut);
                }
            }

            // 3. Allocation constraint: A node can only be assigned to a hub if the hub is located at that node
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    cp.addLe(a[i][j], x[j]); // Allocate node i to hub j
                }
            }

            // 4. Flow constraints: Only route flow if allocation is true
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < N; k++) {
                        cp.addLe(Z[i][j][k], cp.prod(a[i][j], demand[i][j]));  // Flow from origin i to hub k
                        cp.addLe(Y[i][j][k], cp.prod(a[j][k], demand[j][k]));  // Flow between hubs j and k
                        cp.addLe(X[i][j][k], cp.prod(a[i][j], demand[i][j]));  // Flow from hub k to destination i
                    }
                }
            }

            // Solve the problem
            if (cp.solve()) {
                System.out.println("Optimal solution found!");

                // Print hub locations
                System.out.println("\nHub Locations:");
                for (int k = 0; k < N; k++) {
                    if (cp.getValue(x[k]) == 1) {
                        System.out.println("Node " + k + " is a hub.");
                    }
                }


                // Print total cost
                System.out.println("\nTotal Cost: " + cp.getObjValue());
            } else {
                System.out.println("No feasible solution found.");
            }

        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }
}
