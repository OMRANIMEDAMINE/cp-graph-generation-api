package edu.polytech.cpmolgenapi.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class GenerationRequest {
    private int[] degreeSequence;
    private int[] hybridizationState;


    private HashMap<String, Boolean> forbiddenCycles = new HashMap<>();
    private HashMap<String, Boolean> imposedSubgraphs = new HashMap<>();

    public int[] getDegreeSequence() {
        if (degreeSequence == null || degreeSequence.length == 0) {
            // Set default degree sequence if null or empty
            degreeSequence = new int[]{2, 2, 2, 4, 4, 4};
        }
        return degreeSequence;
    }

    public void setDegreeSequence(int[] degreeSequence) {
        this.degreeSequence = degreeSequence;
    }

    public HashMap<String, Boolean> getForbiddenCycles() {
        // Ensure that all keys are present and set to false
        for (String key : Arrays.asList("threecycle", "fourcycle", "fivecycle", "sixcycle")) {
            forbiddenCycles.putIfAbsent(key, false);
        }

        return forbiddenCycles;
    }

    public void setForbiddenCycles(HashMap<String, Boolean> forbiddenCycles) {
        this.forbiddenCycles = forbiddenCycles;
    }

    public HashMap<String, Boolean> getImposedSubgraphs() {
        // Ensure that all keys are present and set to false
        for (String key : Arrays.asList("threecycle", "fourcycle", "fivecycle", "sixcycle")) {
            imposedSubgraphs.putIfAbsent(key, false);
        }
        return imposedSubgraphs;
    }

    public void setImposedSubgraphs(HashMap<String, Boolean> imposedSubgraphs) {
        this.imposedSubgraphs = imposedSubgraphs;
    }

    public int[] getHybridizationState() {
        return hybridizationState;
    }

    public void setHybridizationState(int[] hybridizationState) {
        this.hybridizationState = hybridizationState;
    }
}
