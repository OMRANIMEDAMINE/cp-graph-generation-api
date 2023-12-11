from ortools.sat.python import cp_model


def solve_symmetric_graph_generation(degrees):
    # Create the model.
    model = cp_model.CpModel()
    num_nodes = len(degrees)
    line = list(range(num_nodes))

    # Create variables for the adjacency lists
    neighbors = {}
    for i in line:
        neighbors[i] = [model.NewIntVar(0, num_nodes - 1, f"neighbors_{i}_{j}") for j in range(degrees[i])]

    # Additional constraints
    for i in line:
        # No self-loops
        for j in range(degrees[i]):
            model.Add(neighbors[i][j] != i)

    # Symmetry constraints
    # for i in line:
    #     for j in range(degrees[i]):
    #         for k in line:
    #             # neighbors[i][j] == k => model.AddAtLeastOne(neighbors[k], i)
    #             model.AddImplication(neighbors[i][j] == k,
    #                              model.AddAtLeastOne([neighbors[k][l] == i for l in range(len(neighbors[k]))]))

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
