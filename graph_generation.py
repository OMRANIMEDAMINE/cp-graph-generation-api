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

def solve_graph_generation(deg):
    """Solves the sudoku problem with the CP-SAT solver."""
    # Create the model.
    model = cp_model.CpModel()

    cell_size = 3
    line_size = cell_size**2
    line = list(range(0, line_size))
    cell = list(range(0, cell_size))

    initial_grid = [
        [0, 1, 0, 0, 0, 0, 0, 1, 0],
        [1, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 1],
        [0, 1, 0, 0, 0, 0, 0, 1, 0],
    ]



    grid = {}
    for i in line:
        for j in line:
            grid[(i, j)] = model.NewIntVar(1, line_size, "grid %i %i" % (i, j))

    # AllDifferent on rows.
    #for i in line:
    #    model.AddAllDifferent(grid[(i, j)] for j in line)

    # AllDifferent on columns.
    #for j in line:
    #    model.AddAllDifferent(grid[(i, j)] for i in line)

    # Sum of each row equal to deg(i). 
    for i in line:
        #print(deg[i])
        model.Add(sum(grid[(i, j)] for j in line) == deg[i])

  # Additional diagonal constraint: Set diagonals to 0.
    #for i in line:
        #model.Add(grid[(i, i)] == 0)

  


    # Lexicographically ascending order constraint for each row.
    #for i in line:
        #model. .Add(cp_model.LexicographicalLessOrEqual([grid[(i, j)] for j in line]))
        #model.Add(cp_model.lex_less_or_equal([grid[(i, j)] for j in line]))

    # Lexicographically ascending order constraint for each row.
    #for i in range(1, line_size):
       # for j in line:
           # model.Add(grid[(i - 1, j)] * line_size + grid[(i - 1, j + 1)] <= grid[(i, j)] * line_size + grid[(i, j + 1)])


 # Lexicographically ascending order constraint for each row.
    #for i in range(1, line_size):
      #  for j in range(line_size):
      #      for k in range(j + 1, line_size):
       #         model.Add(grid[(i - 1, j)] * line_size + grid[(i - 1, k)] <= grid[(i, j)] * line_size + grid[(i, k)])

    # Initial values.
    #for i in line:
        #for j in line:
            #if initial_grid[i][j]:
                #model.Add(grid[(i, j)] == initial_grid[i][j])

    # Solve and print out the solution.
    solver = cp_model.CpSolver()
    status = solver.Solve(model)
    if status == cp_model.OPTIMAL:
        for i in line:
            print([int(solver.Value(grid[(i, j)])) for j in line])

    else:
        print("No solution")
    
deg = [2, 2, 3, 3, 4, 4, 4, 4, 4]

solve_graph_generation(deg)