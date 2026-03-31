package edu.polytech.cpmolgenapi.draft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/optimization")
public class MGGAdjacencyMarixBasedSolutionController {

    @Autowired
    private MGGAdjacencyMarixBasedSolutionService optimizationService;



   /* @PostMapping("/optimize")
    public int[][] optimize(@RequestBody BodyRequest request) {
        int[] degreeSequence = request.getDegreeSequence();
        // Ensure degreeSequence is not null or empty, handle validation if needed

        int[][] matrix = optimizationService.optimize(degreeSequence);


        System.out.println("Optimized Matrix:");
        displayMatrix(matrix);
        return matrix;
    }
*/
    public static void displayMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "  ");
            }
            System.out.println();
        }
    }
}

