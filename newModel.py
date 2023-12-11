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
        #status_vertex = [model.NewBoolVar(f"status_vertex_{i}") for i in range(degrees[i])]

    # Additional constraints
    for i in line:
        # No self-loops
        for j in range(degrees[i]):
            model.Add(neighbors[i][j] != i)

    for i in line:
        for j in range(degrees[i]):
            for k in line:
                if i != k and j < len(neighbors[k]):
                    contains_i = model.NewBoolVar(f"contains_{i}_{k}")
                    contains_k = model.NewBoolVar(f"contains_{k}_{i}")

                    model.Add(neighbors[i][j] == k).OnlyEnforceIf(contains_i.Not())
                    model.Add(neighbors[k][j] == i).OnlyEnforceIf(contains_k.Not())

                    model.AddBoolOr([contains_i, contains_k])

        # Constraints to ensure the sum of appeared indices for each vertex is equal to its degree
     #--> to do

    # Symmetry constraints VALID JUST NOT SAME RANGE or LEN ERROR
    # for i in line:
    #     for j in range(degrees[i]):
    #         for k in line:
    #             if i != k:
    #                 contains_i = model.NewBoolVar(f"contains_{i}_{k}")
    #                 contains_k = model.NewBoolVar(f"contains_{k}_{i}")
    #
    #                 model.Add(neighbors[i][j] == k).OnlyEnforceIf(contains_i.Not())
    #                 model.Add(neighbors[k][j] == i).OnlyEnforceIf(contains_k.Not())
    #
    #                 model.AddBoolOr([contains_i, contains_k])

    # Symmetry constraints
    # for i in line:
    #     for j in range(degrees[i]):
    #         for k in line:
    #             if i != k:
    #                 constraint = model.NewBoolVar(f"symmetry_{i}_{j}_{k}")
    #                 model.AddBoolOr(
    #                     neighbors[i][j] != k,
    #                     model.AddBoolOr([constraint, model.AddBoolOr([neighbors[k][l] == i for l in range(len(neighbors[k]))])])
    #                 )
    #
    # # Symmetry constraints
    # for i in line:
    #     for j in range(degrees[i]):
    #         for k in line:
    #             model.AddBoolOr(
    #                 neighbors[i][j] != k,
    #                 model.AddAtLeastOne((neighbors[k][l] == i for l in range(len(neighbors[k]))))
    #             )
    #

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