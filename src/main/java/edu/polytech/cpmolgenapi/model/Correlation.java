package edu.polytech.cpmolgenapi.model;

import java.util.ArrayList;

public class Correlation {
    private ArrayList<Integer> atomSet1 = new ArrayList();
    private ArrayList<Integer> atomSet2 = new ArrayList();
    private String correlationType; // BOND, COSY, HMBC, PROP

    // private int distance;
    //private String distanceType; // EXACT_DISTANCE, MAX_DISTANCE, MIN_DISTANCE
    public Correlation() {
    }

    public Correlation(ArrayList<Integer> atomSet1, ArrayList<Integer> atomSet2, int distance,  String correlationType) {
        this.atomSet1 = atomSet1;
        this.atomSet2 = atomSet2;
        this.correlationType = correlationType;
    }

    public ArrayList<Integer> getAtomSet1() {
        return atomSet1;
    }

    public void setAtomSet1(ArrayList<Integer> atomSet1) {
        this.atomSet1 = atomSet1;
    }

    public ArrayList<Integer> getAtomSet2() {
        return atomSet2;
    }

    public void setAtomSet2(ArrayList<Integer> atomSet2) {
        this.atomSet2 = atomSet2;
    }

    public String getCorrelationType() {
        return correlationType;
    }

    public void setCorrelationType(String correlationType) {
        this.correlationType = correlationType;
    }

    @Override
    public String toString() {
        return "Correlation{" +
                "atomSet1=" + atomSet1 +
                ", atomSet2=" + atomSet2 +
                ", correlationType='" + correlationType + '\'' +
                '}';
    }
}
