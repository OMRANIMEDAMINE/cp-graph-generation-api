package edu.polytech.cpmolgenapi;
import ilog.concert.*;
import ilog.cplex.*;

public class prog_K_hub_finale {
        public static void main(String[] args) throws IloException {
            // Origins coordinates (longitude, latitude)
            double[][] origins = {
                    {10.34600002820839215, 36.87154648072773},
                    {10.33702643587130865, 36.87212219868559515},
                    // ... (add the rest of the origin coordinates here)
                    {9.74905459132647323, 36.7953595530382529}
            };

            // Vehicles per origin
            int[] vehicles = {
                    743, 769,
                    // ... (add the rest of the vehicle numbers here)
                    264
            };

            // Hubs coordinates (longitude, latitude)
            double[][] hubs = {
                    {10.105754537522937, 36.81503271950606},
                    {10.248318570118016, 36.75460817750301},
                    {10.280248644261466, 36.78161693016669},
                    {10.112684951059542, 36.80241943196568},
                    {10.082484060169218, 36.818861796152014},
                    {10.197335224347816, 36.86298947026899},
                    {10.2094665, 36.73497629999999},
                    {10.117809721649337, 36.8347818291268},
                    {10.229055671860465, 36.87704460186521},
                    {10.109172786404352, 36.78190081237857}
            };

            // Destination
            double dest_lon = 10.18;
            double dest_lat = 36.8;

            // Cost rates
            double alpha = 1.0;
            double beta = 1.0;

            // Number of hubs to select
            int H = 3;

            int num_origins = origins.length;
            int num_hubs = hubs.length;

            // Precompute distances: dist_ih[i][h] = distance from origin i to hub h
            double[][] dist_ih = new double[num_origins][num_hubs];
            for (int i = 0; i < num_origins; i++) {
                for (int h = 0; h < num_hubs; h++) {
                    dist_ih[i][h] = haversine(origins[i][0], origins[i][1], hubs[h][0], hubs[h][1]);
                }
            }

            // Precompute dist_hd[h] = distance from hub h to destination
            double[] dist_hd = new double[num_hubs];
            for (int h = 0; h < num_hubs; h++) {
                dist_hd[h] = haversine(hubs[h][0], hubs[h][1], dest_lon, dest_lat);
            }

            // Set up CPLEX model
            IloCplex cplex = new IloCplex();

            // Binary variables: x[h] = 1 if hub h is selected
            IloIntVar[] x = new IloIntVar[num_hubs];
            for (int h = 0; h < num_hubs; h++) {
                x[h] = cplex.boolVar("select_hub_" + h);
            }

            // Binary variables: y[i][h] = 1 if origin i is assigned to hub h
            IloIntVar[][] y = new IloIntVar[num_origins][];
            for (int i = 0; i < num_origins; i++) {
                y[i] = new IloIntVar[num_hubs];
                for (int h = 0; h < num_hubs; h++) {
                    y[i][h] = cplex.boolVar("assign_" + i + "_" + h);
                }
            }

            // Constraint: exactly H hubs selected
            IloLinearIntExpr sumX = cplex.linearIntExpr();
            for (int h = 0; h < num_hubs; h++) {
                sumX.addTerm(1, x[h]);
            }
            cplex.addEq(sumX, H);

            // Constraints: each origin assigned to exactly one hub
            for (int i = 0; i < num_origins; i++) {
                IloLinearIntExpr sumY = cplex.linearIntExpr();
                for (int h = 0; h < num_hubs; h++) {
                    sumY.addTerm(1, y[i][h]);
                }
                cplex.addEq(sumY, 1);
            }

            // Constraints: assignment only to selected hubs (y[i][h] <= x[h])
            for (int i = 0; i < num_origins; i++) {
                for (int h = 0; h < num_hubs; h++) {
                    cplex.addLe(y[i][h], x[h]);
                }
            }

            // Objective: minimize total cost
            IloLinearNumExpr obj = cplex.linearNumExpr();
            for (int i = 0; i < num_origins; i++) {
                for (int h = 0; h < num_hubs; h++) {
                    obj.addTerm(vehicles[i] * (alpha * dist_ih[i][h] + beta * dist_hd[h]), y[i][h]);
                }
            }
            cplex.addMinimize(obj);

            // Solve the problem
            if (cplex.solve()) {
                System.out.println("Status: " + cplex.getStatus());
                System.out.println("Total Minimal Cost: " + cplex.getObjValue());

                // Output selected hubs
                System.out.print("Selected Hub IDs: ");
                for (int h = 0; h < num_hubs; h++) {
                    if (cplex.getValue(x[h]) == 1.0) {
                        System.out.print(h + " ");
                    }
                }
                System.out.println();
            } else {
                System.out.println("No solution found.");
            }

            cplex.end();
        }

        private static double haversine(double lon1, double lat1, double lon2, double lat2) {
            double R = 6371; // Earth's radius in km
            double dlon = Math.toRadians(lon2 - lon1);
            double dlat = Math.toRadians(lat2 - lat1);
            double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dlon / 2) * Math.sin(dlon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return R * c;
        }

}
