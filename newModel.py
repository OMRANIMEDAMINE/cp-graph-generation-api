from ortools.sat.python import cp_model
import collections


def solve_symmetric_graph_generation(degrees):
    # Create the model.
    model = cp_model.CpModel()
    num_nodes = len(degrees)
    line = list(range(num_nodes))
    # Create variables for the adjacency lists
    neighbors = {}
    for i in line:
        neighbors[i] = [model.NewIntVar(1, num_nodes, f"neighbors_{i}_{j}") for j in range(degrees[i])]

    # Ensure all neighbors are different for each vertex (simple graph)
    for i in line:
        model.AddAllDifferent(neighbors[i])

    # # Define a dictionary to store appearance counts for each vertex
    # vertex_appearances = {}
    # for i in line:
    #     vertex_appearances[i] = model.NewIntVar(0, degrees[i] + 1, f"vertex_appearances_{i}")
    #
    # # Loop through each vertex and count its appearances in all neighbor lists
    # for i in line:
    #     for neighbor_list in neighbors.values():
    #         vertex_appearances[i] += sum(1 for neighbor in neighbor_list if neighbor == i)
    #
    # # Add constraints to enforce degree-matching appearances
    # for i in line:
    #     model.Add(vertex_appearances[i] == degrees[i])

    # Add constraints to update the occurrences variables
    # # Create boolean variables to represent occurrences
    occurrences = {}
    for i in line:
        occurrences[i] = model.NewBoolVar(f"occurrences_{i}")
        for neighbor_list in neighbors.values():
            model.Add(
                occurrences[i] ==  sum(1 for neighbor in neighbor_list if neighbor == i)
            )

        # model.Add(
        #     occurrences[i] == (
        #                 ([neighbors[v][k] == i for v in line for k in range(degrees[v])]) == degrees[i]))

    #
    # for i in line:
    #     model.Add(occurrences[i] == True)
    #
    # # Add constraints to update the occurrences variables
    # for i in line:
    #     model.Add(occurrences[i] == model.Sum(neighbors[v][k] == i for v in line for k in range(degrees[v])))

    # # Loop through each vertex and add a constraint for its appearance count
    # for i in line:
    #     # Define a counter variable for vertex i's appearances
    #     vertex_i_count = model.NewIntVar(0, len(line), f"vertex_{i}_count")
    #
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
