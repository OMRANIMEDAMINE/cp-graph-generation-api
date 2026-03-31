package edu.polytech.cpmolgenapi;

public class Result {
    public int count;
    public double cpu;

    public Result(int count, double cpu) {
        this.count = count;
        this.cpu = cpu;
    }

    public Result() {
    }

    @Override
    public String toString() {
        return "Result{" +
                "count=" + count +
                ", cpu=" + cpu +
                '}';
    }
}
