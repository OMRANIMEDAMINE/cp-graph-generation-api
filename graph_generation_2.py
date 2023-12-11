#!/usr/bin/env python3
# Copyright 2010-2022 Google LLC
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""This model implements a sudoku solver."""
from ortools.sat.python import cp_model

def solve_symmetric_graph_generation(degrees):
    """
    Solves the symmetric graph generation problem with the CP-SAT solver.
    """
    # Create the model.
    model = cp_model.CpModel()

    num_nodes = len(degrees)
    line = list(range(num_nodes))

    # Create variables for the adjacency matrix
    grid = {}
    for i in line:
        for j in line:
            grid[(i, j)] = model.NewIntVar(0, 1, "grid %i %i" % (i, j))

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

    # Solve and print out the solution.
    solver = cp_model.CpSolver()
    status = solver.Solve(model)
    if (status == cp_model.OPTIMAL or status == cp_model.FEASIBLE):
        print("Generated Symmetric Graph:")
        for i in line:
            print([int(solver.Value(grid[(i, j)])) for j in line])
    else:
        print("No solution")


# Example usage:
degrees = [2, 2, 2, 3, 4, 4, 4, 4, 4]
solve_symmetric_graph_generation(degrees)




