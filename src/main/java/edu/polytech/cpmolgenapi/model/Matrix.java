package edu.polytech.cpmolgenapi.model;

import java.util.Arrays;
public class Matrix {
    private final int[][] data;

    public Matrix(int[][] data) {
        // Create a deep copy of the input array to ensure immutability
        this.data = Arrays.stream(data)
                .map(row -> Arrays.copyOf(row, row.length))
                .toArray(int[][]::new);
    }

    public int getSize() {
        // Return the size based on the number of rows (assumed to be square)
        return data.length;
    }

    public int[][] getData() {
        // Return a copy of the internal data to ensure immutability
        return Arrays.stream(data)
                .map(row -> Arrays.copyOf(row, row.length))
                .toArray(int[][]::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        return Arrays.deepEquals(data, matrix.data);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(data);
    }
}
