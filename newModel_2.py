from ortools.sat.python import cp_model
import collections


def count_constraint(model, variables, target_value, count_var):
    # Create binary variables indicating the presence of the target value
    presence_vars = [model.NewBoolVar(f"presence_{i}") for i in range(len(variables))]

    # Create constraints to ensure the count is equal to the target value
    model.Add(count_var == sum(presence_vars))
    for i in range(len(variables)):
        model.Add(variables[i] == target_value).OnlyEnforceIf(presence_vars[i])
        model.Add(variables[i] != target_value).OnlyEnforceIf(presence_vars[i].Not())


def solve_symmetric_graph_generation(degrees):
    # Create the model.
    model = cp_model.CpModel()
    num_nodes = len(degrees)
    line = list(range(num_nodes))
    # Create variables for the adjacency lists
    neighbors = {}
    for i in line:
        neighbors[i] = [model.NewIntVar(1, num_nodes , f"neighbors_{i}_{j}") for j in range(degrees[i])]

    #let's focusing only on simmple graph ->  Ensure all neighbors are different for each vertex
    for i in line:
        model.AddAllDifferent(neighbors[i])


     # Count occurrences of each vertex index in all neighbor lists
    vertex_counts = collections.defaultdict(int)
    for i in line:
        for neighbor in neighbors[i]:
            vertex_counts[solver.Value(neighbor)] += 1

    # Ensure each vertex appears as indicated in its degree
    for i, degree in enumerate(degrees):
        model.Add(vertex_counts[i] == degree)




    # Ensure each vertex appears as indicated in its degree
    # for v in line:
    #     model.Add(sum(neighbors[i][j] == v + 1 for i in line for j in range(len(neighbors[i]))) == degrees[v])
    # Create count constraints for each vertex
    # for v in line:
    #     count_var = model.NewIntVar(0, num_nodes, f"count_{v}")
    #     count_constraint(model, [neighbors[i][j] for i in line for j in range(degrees[i])], v + 1, count_var)

    # Solve and print out the solution.
    solver = cp_model.CpSolver()
    status = solver.Solve(model)
    if status in (cp_model.OPTIMAL, cp_model.FEASIBLE):
        print("Generated Symmetric Graph:")
        for i in line:
            print(f"Neighbors of vertex {i}: {[solver.Value(var) for var in neighbors[i]]}")
    else:
        print("No solution")


# Example usage:
degrees = [2, 2, 2, 4, 4, 4, 4, 4, 4]
solve_symmetric_graph_generation(degrees)

# neighbors[i][j] == k => model.AddAtLeastOne(neighbors[k], i)
# model.AddImplication(
#     neighbors[i][j] == k,
#     model.AddAtLeastOne([neighbors[k][l] == i for l in range(len(neighbors[k]))])
# )
