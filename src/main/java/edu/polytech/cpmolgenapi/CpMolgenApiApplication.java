package edu.polytech.cpmolgenapi;
import ilog.concert.IloException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import java.util.List;

@SpringBootApplication
public class CpMolgenApiApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CpMolgenApiApplication.class, args);

        runExperimentsHybrid();
    }

    public static void runExperimentsHybrid() {
        printHeaderPaperLexRevLex();
        for (TestCase sample : DataExperForPaperLexVsRevLex.GraphSamples) {
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
            /*IsomorphismData isoLex = analyzeIsomorphism("output_testLex.txt");
            IsomorphismData isoRevLex = analyzeIsomorphism("output_testRevLex.txt");
            IsomorphismData isoOptLex = analyzeIsomorphism("output_testOptimizedLex.txt");
            IsomorphismData isoOptRevLex = analyzeIsomorphism("output_testOptimizedRevLex.txt");
                printResults(sample.name, Lex, RevLex, optLex, optRevLex,
                    isoLex, isoRevLex, isoOptLex, isoOptRevLex);
*/
            printResults(sample.name, Lex, RevLex, optLexCon, optRevLexCon, optRevLexConDiagRev);

        }
        System.out.printf("\n ********* End of Experimentation  **************   \n");
    }

    private static IsomorphismData analyzeIsomorphism(String filename) {
        try {
            List<Graph<Integer, DefaultEdge>> graphs =
                    GraphIsomorphismAnalyzer.readGraphsFromFile(filename);
            GraphIsomorphismAnalyzer.IsomorphismResult result =
                    GraphIsomorphismAnalyzer.computeIsomorphismRate(graphs);
            return new IsomorphismData(result.uniqueCount, result.total, result.isoRate);
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

    // ======= PRINT RESULTS =======
    /*
    private static void printResults(String sampleName,
                                     Result Lex, Result RevLex, Result optLex, Result optRevLex,
                                     IsomorphismData isoLex, IsomorphismData isoRevLex,
                                     IsomorphismData isoOptLex, IsomorphismData isoOptRevLex) {
        System.out.printf("%-10s | %-8d (%-3d, %5.1f%%) | %-8d (%-3d, %5.1f%%) | %-8d (%-3d, %5.1f%%) | %-8d (%-3d, %5.1f%%)%n",
                sampleName,
                Lex.count, isoLex.uniqueCount,  (100 - (isoLex.isoRate * 100)),
                RevLex.count, isoRevLex.uniqueCount,   (100 - (isoRevLex.isoRate * 100)),
                optLex.count, isoOptLex.uniqueCount,    (100 - (isoOptLex.isoRate * 100)),
                optRevLex.count, isoOptRevLex.uniqueCount, (100 - (isoOptRevLex.isoRate * 100))
        );
    }

    private static void printHeaderPaperLexRevLex() {
        System.out.printf("%-10s | %-25s | %-25s | %-25s | %-25s%n",
                "Sample", "Lex (Uniq, Iso%)", "RevLex (Uniq, Iso%)",
                "OptLex (Uniq, Iso%)", "OptRevLex (Uniq, Iso%)");
        System.out.println("-".repeat(120));
    }*/
    private static void printResults(String sampleName,
                                     Result optLex, Result optRevLex,
                                     Result optLexCon, Result optRevLexCon, Result optRevLexConDiagRev) {

        System.out.printf(
                "%-10s | %8d (%6.2f) | %8d (%6.2f) | %8d (%6.2f) | %8d (%6.2f)  | %8d (%6.2f)%n",
                sampleName,
                optLex.count, (optLex.cpu/1000),
                optRevLex.count, (optRevLex.cpu/1000),
                optLexCon.count, (optLexCon.cpu/1000),
                optRevLexCon.count, (optRevLexCon.cpu/1000),
                optRevLexConDiagRev.count, (optRevLexConDiagRev.cpu/1000)
        );
    }

    private static void printHeaderPaperLexRevLex() {
        System.out.printf(
                "%-10s | %-15s | %-15s | %-15s | %-15s | %-15s%n",
                "Sample",
                "OptLex (C,CPU)",
                "OptRevLex (C,CPU)",
                "OptLexCon (C,CPU)",
                "OptRevLexCon (C,CPU)",
                "OptRevLexConDiag (C,CPU)"
        );
        System.out.println("-".repeat(80));
    }

}
