package edu.polytech.cpmolgenapi.shared;

import edu.polytech.cpmolgenapi.model.Correlation;

import java.util.List;
import java.util.ArrayList;

public class GeneralFunction {
    public static List<Correlation> getBONDCorrelations(List<Correlation> correlations) {
        List<Correlation> filteredCorrelations = new ArrayList<>();
        for (Correlation correlation : correlations) {
            if (correlation.getCorrelationType().equals("BOND")) {
                filteredCorrelations.add(correlation);
            }
        }
        return filteredCorrelations;
    }
    public static List<Correlation> getHMBCCorrelations(List<Correlation> correlations) {
        List<Correlation> filteredCorrelations = new ArrayList<>();
        for (Correlation correlation : correlations) {
            if (correlation.getCorrelationType().equals("HMBC")) {
                filteredCorrelations.add(correlation);
            }
        }
        return filteredCorrelations;
    }
}
