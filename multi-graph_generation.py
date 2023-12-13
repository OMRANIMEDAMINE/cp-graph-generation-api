from ortools.sat.python import cp_model
from GraphPlotter import plot_multigraph


def solve_symmetric_multigraph_generation(degrees, distance_constraints=None):
    """
    Solves the symmetric multigraph generation problem with the CP-SAT solver.
    """
    # Create the model.
    model = cp_model.CpModel()

    num_nodes = len(degrees)
    line = list(range(num_nodes))

    # Create variables for the multiplicity of edges
    grid = {}
    for i in line:
        for j in line:
            min_degree = min(degrees[i], degrees[j])
            grid[(i, j)] = model.NewIntVar(0, min_degree, "grid %i %i" % (i, j))

    # Degree constraints
    for i in line:
        model.Add(sum(grid[(i, j)] for j in line) == degrees[i])

    # Additional constraints
    for i in line:
        model.Add(grid[(i, i)] == 0)  # No self-loops

    # Symmetry constraint
    for i in line:
        for j in range(i + 1, num_nodes):
            model.Add(grid[(i, j)] == grid[(j, i)])

    # Transitive closure constraint for connectivity
    for k in line:
        for i in line:
            for j in line:
                if i != j and i != k and j != k:
                    model.AddImplication(grid[(i, j)] > 0, grid[(i, k)] > 0).OnlyEnforceIf(grid[(k, j)] > 0)
                    model.AddImplication(grid[(i, j)] > 0, grid[(k, j)] > 0).OnlyEnforceIf(grid[(i, k)] > 0)

    # Solve the model
    solver = cp_model.CpSolver()
    status = solver.Solve(model)

    if (status == cp_model.OPTIMAL or status == cp_model.FEASIBLE):
        print("Generated Symmetric Multigraph:")
        solution = [[int(solver.Value(grid[(i, j)])) for j in line] for i in line]
        for i in line:
            print(solution[i])
        return solution
    else:
        print("No solution")
        return None


# Example usage:
degrees = [2, 2, 2, 4, 4, 4, 4, 4, 4]
distance_constraint = [(1, 2, 5)]
solve_symmetric_multigraph_generation(degrees, distance_constraints=distance_constraint)

# Call the plotting function if a solution exists
if solution:
    plot_multigraph(solution)
