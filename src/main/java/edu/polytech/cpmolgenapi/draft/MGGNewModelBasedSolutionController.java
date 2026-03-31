package edu.polytech.cpmolgenapi.draft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/optimization")
public class MGGNewModelBasedSolutionController {

    @Autowired
    private MGGAdjacencyMarixBasedSolutionService optimizationService;

    @GetMapping("/optimize2")
    public int[][] optimize() {
      //  int [][] matrix =  optimizationService.optimize();
      //  System.out.println("Optimized Matrix:");
      //  displayMatrix(matrix);
       // return matrix;
        return null;
    }

    public static void displayMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "  ");
            }
            System.out.println();
        }
    }
}

