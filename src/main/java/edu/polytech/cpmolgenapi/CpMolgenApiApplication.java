package edu.polytech.cpmolgenapi;
import ilog.concert.IloException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@SpringBootApplication
public class CpMolgenApiApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CpMolgenApiApplication.class, args);
        runExperimentsHybrid();
    }
    public static void resetFile(String filename) throws IOException {
        new PrintWriter(new FileWriter(filename, false)).close();
    }
    public static void runExperimentsHybrid() throws IOException {

        for (TestCase sample : DataExperForPaperLexVsRevLex.GraphSamples) {
            // Create timestamp for filename
            resetFile("output_testLex.txt");
            resetFile("output_testRevLex.txt");
            resetFile("output_testOptimizedLex.txt");
            resetFile("output_testOptimizedRevLex.txt");


            Result Lex = OpLexVsOpRevLexVsHybrid.testLex(sample.degrees);
            Result RevLex = OpLexVsOpRevLexVsHybrid.testRevLex(sample.degrees); // or CoLex
            Result AntiLex = OpLexVsOpRevLexVsHybrid.testAntiLex(sample.degrees);
            Result Snake = OpLexVsOpRevLexVsHybrid.testSnake(sample.degrees);

            Result optLex =  OpLexVsOpRevLexVsHybrid.testOptimizedLex(sample.degrees); // With CanonicalChecker
            Result optRevLex = OpLexVsOpRevLexVsHybrid.testOptimizedRevLex(sample.degrees);
            Result optLexCon = OpLexVsOpRevLexVsHybrid.testOptimizedLexCon(sample.degrees);
            Result optRevLexCon = OpLexVsOpRevLexVsHybrid.testOptimizedRevLexCon(sample.degrees);;
            Result optRevLexConDiagRev =  OpLexVsOpRevLexVsHybrid.testOptimizedRevLexConDiag(sample.degrees);


            //Result optLexCon =   OpLexVsOpRevLexVsHybrid.testOptimizedLexConKKtree(sample.degrees);
            System.out.println("***********\n");
            //Result optRevLexCon =    OpLexVsOpRevLexVsHybrid.testOptimizedRevLexConKKtree(sample.degrees);;

            // Analyze isomorphism rates for each output file
            // Analyze isomorphism rates for each output file
            IsomorphismData isoLex = analyzeIsomorphism("output_testLex.txt");
            IsomorphismData isoRevLex = analyzeIsomorphism("output_testRevLex.txt");
            IsomorphismData isoOptLex = analyzeIsomorphism("output_testOptimizedLex.txt");
            IsomorphismData isoOptRevLex = analyzeIsomorphism("output_testOptimizedRevLex.txt");

            System.out.printf("LEX: isoRate: %.2f totalCount: %d uniqueCount: %d%n",
                    isoLex.isoRate, isoLex.totalCount, isoLex.uniqueCount);
            System.out.printf("REVLEX: isoRate: %.2f totalCount: %d uniqueCount: %d%n",
                    isoRevLex.isoRate, isoRevLex.totalCount, isoRevLex.uniqueCount);
            System.out.printf("OPTLEX: isoRate: %.2f totalCount: %d uniqueCount: %d%n",
                    isoOptLex.isoRate, isoOptLex.totalCount, isoOptLex.uniqueCount);
            System.out.printf("OPTREVLEX: isoRate: %.2f totalCount: %d uniqueCount: %d%n",
                    isoOptRevLex.isoRate, isoOptRevLex.totalCount, isoOptRevLex.uniqueCount);

            // Print each result individually
            System.out.println("\n=== Sample: " + sample.name + " ===\n");

            printSingleResult("Lex", Lex);
            printSingleResult("RevLex", RevLex);
            printSingleResult("AntiLex", AntiLex);
            printSingleResult("Snake", Snake);
            printSingleResult("OptLex", optLex);
            printSingleResult("OptRevLex", optRevLex);
            printSingleResult("OptLexCon", optLexCon);
            printSingleResult("OptRevLexCon", optRevLexCon);
            printSingleResult("OptRevLexConDiag", optRevLexConDiagRev);

            System.out.println("----------------------------------------\n");

        }
        System.out.printf("\n ********* End of Experimentation  **************   \n");
    }

    private static IsomorphismData analyzeIsomorphism(String filename) {
        try {
            List<Graph<Integer, DefaultEdge>> graphs =
                    GraphIsomorphismAnalyzer.readGraphsFromFile(filename);
            GraphIsomorphismAnalyzer.IsomorphismResult result =
                    GraphIsomorphismAnalyzer.computeIsomorphismRate(graphs);
            return new IsomorphismData(result.uniqueCount, result.total, 1-(result.isoRate));
        } catch (Exception e) {
            System.err.println("Error analyzing " + filename + ": " + e.getMessage());
            return new IsomorphismData(0, 0, 0.0);
        }
    }

    // Inner class to hold isomorphism data
    static class IsomorphismData {
        int uniqueCount;
        int totalCount;
        double isoRate;

        IsomorphismData(int uniqueCount, int totalCount, double isoRate) {
            this.uniqueCount = uniqueCount;
            this.totalCount = totalCount;
            this.isoRate = isoRate;
        }
    }



    private static void printSingleResult(String resultName, Result result) {
        String SEP = "+------------+----------------------+";
        String ROW = "| %-10s | %20s |%n";

        System.out.println("--- " + resultName + " ---");
        System.out.println(SEP);

        // Solutions + time (rounded to 2 decimal places)
        System.out.printf(ROW, "Solutions", String.format("%,d (%.2fs)", result.count, roundTo2Decimals(result.cpu / 1000.0)));

        // Fails
        System.out.printf(ROW, "Fails", fmt(result.fails));

        // Branches
        System.out.printf(ROW, "Branches", fmt(result.branches));

        // Choice points
        System.out.printf(ROW, "Choice points", fmt(result.choicePoints));

        // Constraints
        System.out.printf(ROW, "Constraints", fmt(result.constraints));

        System.out.println(SEP);
        System.out.println();
    }

    /**
     * Rounds a double to 2 decimal places
     */
    private static double roundTo2Decimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /** Right-aligns a long metric value; shows "-" if not collected (value == -1). */
    private static String fmt(long v) {
        return v == -1 ? String.format("%20s", "-") : String.format("%,20d", v);
    }

}
