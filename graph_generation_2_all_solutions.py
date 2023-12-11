
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

class SolutionPrinter(cp_model.CpSolverSolutionCallback):
    def __init__(self, grid):
        cp_model.CpSolverSolutionCallback.__init__(self)
        self.grid = grid
        self.solutions = []

    def on_solution_callback(self):
        solution = [[self.Value(self.grid[(i, j)]) for j in self.line] for i in self.line]
        self.solutions.append(solution)

def solve_symmetric_graph_generation_all_solutions(degrees):
    """
    Solves the symmetric graph generation problem and returns all solutions.
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

    # Create a solution printer to collect all solutions
    solution_printer = SolutionPrinter(grid)
    solution_printer.line = line

    # Solve and collect all solutions
    solver = cp_model.CpSolver()
    solver.parameters.enumerate_all_solutions = True
    status = solver.Solve(model, solution_printer)

    # Print all solutions
    num_solutions = len(solution_printer.solutions)
    if num_solutions > 0:
        print(f"Found {num_solutions} solutions:")
        for sol_num, solution in enumerate(solution_printer.solutions):
            print(f"Solution {sol_num + 1}:")
            for row in solution:
                print(row)
            print()
    else:
        print("No solution")

# Example usage:
degrees = [2, 2, 3, 3, 4, 4, 4, 4, 4]
solve_symmetric_graph_generation_all_solutions(degrees)
