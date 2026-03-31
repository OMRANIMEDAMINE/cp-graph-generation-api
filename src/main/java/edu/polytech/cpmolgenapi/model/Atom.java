package edu.polytech.cpmolgenapi.model;



import edu.polytech.cpmolgenapi.config.AtomConfig;

import java.util.*;

public class Atom {
    private int index;
    private String symbol;
    private int hybridization ;
    private int multiplicity ;
    private double nmrShift;
    public String getName() {
        return AtomConfig.getAtomName(symbol);
    }
    public int getValency() {
        return AtomConfig.getAtomValency(symbol);
    }
    public int getDegree() {
        return getValency() - getMultiplicity();
    }

    public Atom( ) { }

    public Atom(int index, String symbol, int hybridization, int multiplicity, double nmrShift) {
        this.index = index;
        this.symbol = symbol;
        this.hybridization = hybridization;
        this.multiplicity = multiplicity;
        this.nmrShift = nmrShift;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getHybridization() {
        return hybridization;
    }

    public void setHybridization(int hybridization) {
        this.hybridization = hybridization;
    }

    public int getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
    }

    public double getNmrShift() {
        return nmrShift;
    }

    public void setNmrShift(double nmrShift) {
        this.nmrShift = nmrShift;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Atom atom = (Atom) o;
        return Double.compare(getNmrShift(), atom.getNmrShift()) == 0 && Objects.equals(getSymbol(), atom.getSymbol()) && Objects.equals(getHybridization(), atom.getHybridization()) && Objects.equals(getMultiplicity(), atom.getMultiplicity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSymbol(), getHybridization(), getMultiplicity(), getNmrShift());
    }

    @Override
    public String toString() {
        return "Atom{" +
                "index=" + index +
                ", symbol='" + symbol + '\'' +
                ", name='" + getName() + '\'' +
                ", valency='" + getValency() + '\'' +
                ", hybridization=" + hybridization +
                ", multiplicity=" + multiplicity +
                ", nmrShift=" + nmrShift +
                '}';
    }
}


