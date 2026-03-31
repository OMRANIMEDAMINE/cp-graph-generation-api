package edu.polytech.cpmolgenapi;

import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GraphIsomorphismAnalyzer {

    public static class IsomorphismResult {
        double isoRate;
        int uniqueCount;
        int total;

        public IsomorphismResult(double isoRate, int uniqueCount, int total) {
            this.isoRate = isoRate;
            this.uniqueCount = uniqueCount;
            this.total = total;
        }
    }

    public static List<Graph<Integer, DefaultEdge>> readGraphsFromFile(String filename) throws IOException {
        List<Graph<Integer, DefaultEdge>> graphs = new ArrayList<>();
        List<List<Integer>> currentMatrix = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    if (!currentMatrix.isEmpty()) {
                        Graph<Integer, DefaultEdge> graph = createGraphFromMatrix(currentMatrix);
                        graphs.add(graph);
                        currentMatrix.clear();
                    }
                } else {
                    List<Integer> row = new ArrayList<>();
                    String[] values = line.split("\\s+");
                    for (String val : values) {
                        row.add(Integer.parseInt(val));
                    }
                    currentMatrix.add(row);
                }
            }
            // Handle last matrix if file doesn't end with blank line
            if (!currentMatrix.isEmpty()) {
                Graph<Integer, DefaultEdge> graph = createGraphFromMatrix(currentMatrix);
                graphs.add(graph);
            }
        }
        return graphs;
    }

    private static Graph<Integer, DefaultEdge> createGraphFromMatrix(List<List<Integer>> matrix) {
        int n = matrix.size();
        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        // Add vertices
        for (int i = 0; i < n; i++) {
            graph.addVertex(i);
        }

        // Add edges based on adjacency matrix
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (matrix.get(i).get(j) != 0) {
                    graph.addEdge(i, j);
                }
            }
        }

        return graph;
    }

    public static IsomorphismResult computeIsomorphismRate(List<Graph<Integer, DefaultEdge>> graphs) {
        int total = graphs.size();
        List<Graph<Integer, DefaultEdge>> uniqueGraphs = new ArrayList<>();

        for (Graph<Integer, DefaultEdge> g : graphs) {
            boolean isDuplicate = false;
            for (Graph<Integer, DefaultEdge> u : uniqueGraphs) {
                VF2GraphIsomorphismInspector<Integer, DefaultEdge> inspector =
                        new VF2GraphIsomorphismInspector<>(g, u);
                if (inspector.isomorphismExists()) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                uniqueGraphs.add(g);
            }
        }

        int uniqueCount = uniqueGraphs.size();
        double isoRate = 1.0 - ((double) uniqueCount / total);
        return new IsomorphismResult(isoRate, uniqueCount, total);
    }

    public static void processFile1() {
        try {
            List<Graph<Integer, DefaultEdge>> graphs =
                    readGraphsFromFile("/home/mapsit-admin/IdeaProjects/CP-Molgen-API/output_testLex.txt");
            IsomorphismResult result = computeIsomorphismRate(graphs);
            System.out.printf("Process 1 - output_testLex: %d unique, %d total, %.2f%% iso rate%n",
                    result.uniqueCount, result.total, result.isoRate * 100);
        } catch (IOException e) {
            System.err.println("Error processing file 1: " + e.getMessage());
        }
    }

    public static void processFile2() {
        try {
            List<Graph<Integer, DefaultEdge>> graphs =
                    readGraphsFromFile("/home/mapsit-admin/IdeaProjects/CP-Molgen-API/output_testRevLex.txt");
            IsomorphismResult result = computeIsomorphismRate(graphs);
            System.out.printf("Process 2 - output_testRevLex: %d unique, %d total, %.2f%% iso rate%n",
                    result.uniqueCount, result.total, result.isoRate * 100);
        } catch (IOException e) {
            System.err.println("Error processing file 2: " + e.getMessage());
        }
    }

    public static void processFile3() {
        try {
            List<Graph<Integer, DefaultEdge>> graphs =
                    readGraphsFromFile("/home/mapsit-admin/IdeaProjects/CP-Molgen-API/output_testOptimizedLex.txt");
            IsomorphismResult result = computeIsomorphismRate(graphs);
            System.out.printf("Process 3 - output_testOptimizedLex: %d unique, %d total, %.2f%% iso rate%n",
                    result.uniqueCount, result.total, result.isoRate * 100);
        } catch (IOException e) {
            System.err.println("Error processing file 3: " + e.getMessage());
        }
    }

    public static void processFile4() {
        try {
            List<Graph<Integer, DefaultEdge>> graphs =
                    readGraphsFromFile("/home/mapsit-admin/IdeaProjects/CP-Molgen-API/output_testOptimizedRevLex.txt");
            IsomorphismResult result = computeIsomorphismRate(graphs);
            System.out.printf("Process 4 - output_testOptimizedRevLex: %d unique, %d total, %.2f%% iso rate%n",
                    result.uniqueCount, result.total, result.isoRate * 100);
        } catch (IOException e) {
            System.err.println("Error processing file 4: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Submit tasks
        executor.submit(GraphIsomorphismAnalyzer::processFile1);
        executor.submit(GraphIsomorphismAnalyzer::processFile2);
        executor.submit(GraphIsomorphismAnalyzer::processFile3);
        executor.submit(GraphIsomorphismAnalyzer::processFile4);

        // Shutdown executor
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Execution interrupted: " + e.getMessage());
        }

        System.out.println("All processes completed!");
    }
}
