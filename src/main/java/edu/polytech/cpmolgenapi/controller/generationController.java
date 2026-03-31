package edu.polytech.cpmolgenapi.controller;

import edu.polytech.cpmolgenapi.exception.BusinessException;
import edu.polytech.cpmolgenapi.model.Atom;
import edu.polytech.cpmolgenapi.model.Correlation;
import edu.polytech.cpmolgenapi.model.GenerationResponse;
import edu.polytech.cpmolgenapi.model.Molecule;
import edu.polytech.cpmolgenapi.service.GenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/api/v1/matrices")
public class generationController {

    @Autowired
    private GenerationService generationService;

    @PostMapping("/generate")
    public ResponseEntity<GenerationResponse> generate_post(@RequestBody Molecule molecule,
                                                            @RequestParam(defaultValue = "all") String generationType,
                                                            @RequestParam(defaultValue = "true") String isSymmetryBreakingActive,
                                                            @RequestParam(defaultValue = "true") String isConnectivityActive) {
        List<int[][]> solutionsFound = new ArrayList<>();
        Date Start = new Date();
        double diff = 0;
        try {
            molecule.validateMoleculeConfig(); // check for validity still not valid by OMA need more improvements
            //System.out.println("Received JSON: " + molecule); // Log the received JSON
            if (generationType.equalsIgnoreCase("all")) {
                solutionsFound = generationService.generateAllMatrix(molecule, isSymmetryBreakingActive, isConnectivityActive);
                diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
                GenerationResponse response = new GenerationResponse(true, "Trial finished successfully", solutionsFound, diff);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else { // generation of One solution
                int[][] matrix = generationService.generateOneMatrix(molecule, isSymmetryBreakingActive, isConnectivityActive);
                diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
                if (matrix != null) {
                    solutionsFound.add(matrix);
                }
                GenerationResponse response = new GenerationResponse(true, "Trial finished successfully", solutionsFound, diff);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        } catch (BusinessException e) {
            diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
            GenerationResponse response = new GenerationResponse(false, "An error occurred while processing the request : " + e.getBody().getDetail(), solutionsFound, diff);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
            GenerationResponse response = new GenerationResponse(false, "An RuntimeException occurred while processing the request : " + e.getMessage(), solutionsFound, diff);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
            GenerationResponse response = new GenerationResponse(false, "An error occurred while processing the request : " + e.getMessage(), solutionsFound, diff);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/generate_lex")
    public ResponseEntity<GenerationResponse> generate_lex(@RequestBody ArrayList<Integer> degreeSequence,
                                                           @RequestParam(defaultValue = "all") String generationType,
                                                           @RequestParam(defaultValue = "false") String isSymmetryBreakingActive,
                                                           @RequestParam(defaultValue = "false") String isConnectivityActive,
                                                           @RequestParam(defaultValue = "false") String isConnectivityOptimizationActive
    ) {
        List<int[][]> solutionsFound = new ArrayList<>();
        Date Start = new Date();
        double diff = 0;
        try {
            System.out.println("Received JSON degreeSequence : " + degreeSequence); // Log the received JSON
            if (generationType.equalsIgnoreCase("all")) {
                solutionsFound = generationService.generateAllMatrixLex(degreeSequence, isSymmetryBreakingActive, isConnectivityActive, isConnectivityOptimizationActive);
                diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
                GenerationResponse response = new GenerationResponse(true, "Trial finished successfully", solutionsFound, diff);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else { // generation of One solution

                GenerationResponse response = new GenerationResponse(true, "Not Yet implemented", solutionsFound, 0);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        } catch (BusinessException e) {
            diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
            GenerationResponse response = new GenerationResponse(false, "An error occurred while processing the request : " + e.getBody().getDetail(), solutionsFound, diff);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
            GenerationResponse response = new GenerationResponse(false, "An RuntimeException occurred while processing the request : " + e.getMessage(), solutionsFound, diff);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
            GenerationResponse response = new GenerationResponse(false, "An error occurred while processing the request : " + e.getMessage(), solutionsFound, diff);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/generate_rev_lex")
    public ResponseEntity<GenerationResponse> generate_rev_lex(@RequestBody ArrayList<Integer> degreeSequence,
                                                               @RequestParam(defaultValue = "all") String generationType,
                                                               @RequestParam(defaultValue = "false") String isSymmetryBreakingActive,
                                                               @RequestParam(defaultValue = "false") String isConnectivityActive,
                                                               @RequestParam(defaultValue = "false") String isConnectivityOptimizationActive,
                                                               @RequestParam(defaultValue = "false") String isPermMatrixActive) {
        List<int[][]> solutionsFound = new ArrayList<>();
        Date Start = new Date();
        double diff = 0;
        try {
            System.out.println("Received JSON degreeSequence : " + degreeSequence); // Log the received JSON
            if (generationType.equalsIgnoreCase("all")) {
                solutionsFound = generationService.generateAllMatrixRevLex(degreeSequence, isSymmetryBreakingActive, isConnectivityActive, isConnectivityOptimizationActive, isPermMatrixActive);
                diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
                GenerationResponse response = new GenerationResponse(true, "Trial finished successfully", solutionsFound, diff);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else { // generation of One solution

                GenerationResponse response = new GenerationResponse(true, "Not Yet implemented", solutionsFound, 0);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        } catch (BusinessException e) {
            diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
            GenerationResponse response = new GenerationResponse(false, "An error occurred while processing the request : " + e.getBody().getDetail(), solutionsFound, diff);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
            GenerationResponse response = new GenerationResponse(false, "An RuntimeException occurred while processing the request : " + e.getMessage(), solutionsFound, diff);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            diff = (new Date().getTime() - Start.getTime()) / 1000 % 60; // get diff in time
            GenerationResponse response = new GenerationResponse(false, "An error occurred while processing the request : " + e.getMessage(), solutionsFound, diff);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}

