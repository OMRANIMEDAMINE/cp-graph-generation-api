package edu.polytech.cpmolgenapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

public class CpMolgenApiApplicationdraft {

    public static void main(String[] args) {

        SpringApplication.run(CpMolgenApiApplicationdraft.class, args);
       /*int[][] optimizedMatrix = MGGAdjacencyMarixBasedSolutionService.optimize();
        System.out.println("Optimized Matrix:");
        MGGAdjacencyMarixBasedSolutionController.displayMatrix(optimizedMatrix);*/


      // ArrayList optimize_optimize_all_solution = MGGAdjacencyMarixBasedSolutionService.optimize_all_solution();
      //  System.out.println("optimize_optimize_all_solution :");
     //   int solutionCount = 0;

       // if (optimize_optimize_all_solution != null) {
           /* System.out.println("Solution " + solutionCount + ":");
            for (Object elem : optimize_optimize_all_solution) {
                solutionCount++;

                System.out.println("Solution " + solutionCount + ":");
                MGGAdjacencyMarixBasedSolutionController.displayMatrix((int[][]) elem);
            }*/
     //   }

        // Display the new Model matrix

        // int[][] optimizedNewModel = MGGNewModelBasedSolutionService.optimize();
        //   int[][] optimizedNewModel = MGGNewModelBasedSolutionService.optimize_allsolution();
        //System.out.println("Optimized optimizedNewModel:");
        //MGGNewModelBasedSolutionController.displayMatrix(optimizedNewModel);
        // Get the max heap size in bytes
        //long maxHeapSize = Runtime.getRuntime().maxMemory();

        // Convert bytes to megabytes for better readability
        //long maxHeapSizeInMB = maxHeapSize / (1024 * 1024);

        //System.out.println("Maximum Heap Size: " + maxHeapSizeInMB + " MB");
        //Hub_MIP.version5(); //kooli project
        //test.testLex(); // Lex Paper
        //test.testRevLex(); // Lex Paper
       /* int[] DEGREE = {4,4,4,4,4,4,4}; // Example degree constraints
        System.out.println("  ------------> RevLEX is running ");
        System.out.println("N: " + DEGREE.length + " DEGREE: " + Arrays.toString(DEGREE));
        System.out.println("Lex: "+LexVsRevLex.testLex(DEGREE));
        System.out.println("RevLex: "+LexVsRevLex.testRevLex(DEGREE));
        System.out.println("Optimized RevLex: "+LexVsRevLex.testOptimizedRevLex(DEGREE));*/

        /*System.out.printf("%-10s | %-8s | %-7s | %-13s | %-10s | %-17s | %-15s | %-10s | %-14s | %-18s | %-18s \n",
                "Graph", "Lex Count", "Lex CPU", "RevLex Count", "RevLex CPU", "OptLex Count", "OptLex CPU", "OptRevLex Count", "OptRevLex CPU", "RevLex+ Count", "RevLex+ CPU");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.printf("%-10s | %-7s | %-10s | %-15s | %-14s  | %-18s \n",
                "Graph", "Lex Count",  "RevLex Count",  "OptLex Count",  "OptRevLex Count", "RevLex+ Count");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        */
       /* System.out.printf(
                "%-10s | %-12s | %-12s | %-15s | %-12s | %-15s | %-12s | %-15s| %-15s   %n",
                "Graph", "optLex cnt", "optLex Cpu", "optRevLex cnt", "optRevLex Cpu",
                "optAntiLex cnt", "optAntiLex Cpu", "optAntiRevLex cnt", "optAntiRevLex Cpu"
        );*/
        /*System.out.printf(
                "%-10s | %-12s  | %-15s  | %-15s  | %-15s   %n",
                "Graph", "optLex cnt", "optRevLex cnt",
                "optAntiLex cnt", "optAntiRevLex cnt"
        );*/

        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------");



        /*System.out.printf("%-10s   | %-7s | %-10s  | %-15s | %-14s   \n",
                "Graph",  "Lex Count",     "RevLex Count",  "OptLex Count",     "OptRevLex Count",   "OptRevLex+ Count");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
*/
       //runExperiments(6, 12, 12); //20
        /*runExperiments(7, 12, 12); //20
        runExperiments(8, 12, 12); //20
        runExperiments(9, 12, 12); //20
        runExperiments(10, 12, 12); //20*/

        //runExperiments(2, 10, 16); //20
        //runExperiments(3, 8, 12); //14
       /* runExperiments(4, 4, 12);//12
        runExperiments(5, 5, 12);//14   *** cas de test important :   runExperiments(5, 8, 8)  runExperiments(5, 8, 14);
        runExperiments(6, 6, 12);//12
        runExperiments(7, 7, 12);//12*/
        //runExperiments(8, 14, 14);//12*/ limite CPU
      //  runExperiments(9, 14, 14);//12 ** DONE

       /* runExperiments(8, 14, 14);
        runExperiments(7, 14, 14);
       */
       // runExperiments(6, 10, 11);
        //runExperiments(5, 10, 11);
        //runExperiments(4, 5, 12);
       // runExperiments(4, 8, 12);
        runExperimentsHybrid();

    }
    public static void runExperiments(int degreeValue, int startN, int endN) {
        for (int n = startN; n <= endN; n++) {
            int[] DEGREE = new int[n];
            Arrays.fill(DEGREE, degreeValue);


            Result lex = LexVsRevLex.testLex(DEGREE);
            Result revLex =LexVsRevLex.testRevLex(DEGREE);
            Result optLex = LexVsRevLex.testOptimizedLex(DEGREE);
            Result optAntiLex = LexVsRevLex.testOptimizedAntiLex(DEGREE);
            Result optRevLex = LexVsRevLex.testOptimizedRevLex(DEGREE);
            Result optAntiRevLex = LexVsRevLex.testOptimizedAntiRevLex(DEGREE);

            //Result HybridAntiLexTopOptRevLexBotom = LexVsRevLex.HybridAntiLexTopOptRevLexBotom(DEGREE);
            //Result Hybrid4 = LexVsRevLex.Hybrid4(DEGREE); // A blocs
            //Result optRevLexTwins = LexVsRevLex.testOptimizedRevLexTwins(DEGREE);
            //Result RevLexTwinsMatrix = LexVsRevLex.testRevLexTwinsMatrix(DEGREE);
            //Result testOptimizedRevLexTwinsTwins = LexVsRevLex.testOptimizedRevLexTwinsTwins(DEGREE); // Twin Of the Twin


            //System.out.printf("K%-2d(%d)    | %-10d  | %-13d | %-17d | %-17d | %-17d \n",
            System.out.printf(
                    //"%-10s | %-12d | %-12.3f | %-15d | %-12.3f | %-15d | %-12.3f | %-15d | %-13.3f  %n",
                    "%-10s | %-12d  | %-15d   | %-15d   | %-15d    %n",
                    String.format("K%-2d(%d)", n, degreeValue),
                   //lex.count, lex.cpu / 1_000.0,
                    //revLex.count, revLex.cpu / 1_000.0,
                    optLex.count,//  optLex.cpu / 1_000.0,
                    optRevLex.count,// optRevLex.cpu / 1_000.0,
                    optAntiLex.count, // optAntiLex.cpu / 1_000.0,
                    optAntiRevLex.count// optAntiRevLex.cpu / 1_000.0
                    //HybridAntiLexTopOptRevLexBotom.count,  HybridAntiLexTopOptRevLexBotom.cpu / 1_000.0,
                    //HybridAntiLexTopOptRevLexBotom.count, HybridAntiLexTopOptRevLexBotom.cpu / 1_000.0
                    //testOptimizedRevLexTwinsTwins.count, optRevLexTwins.cpu / 1_000.0
                    // optRevLexTwins.count, optRevLexTwins.cpu,
                    );
        }
    }

    public static void runExperimentsHybrid( ) {
       //   DEGREE = new int[]{3, 3, 3, 3, 2, 2, 2, 2, 2};

            /*    AntiLex   |        Lex
                RevLex     |      AntiRevLex
                 2280              2112*/
        // Define test samples in a structured way
        TestCase[] bipartiteSamples = {
                new TestCase("K5(4)", new int[]{4,4,4,4,4}, "descr "),
                //new TestCase("B000", new int[]{8,8,8,8,8,8,4,4,4,4,4,4}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                //new TestCase("B000", new int[]{4,4,4,4,4,2,2,2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                //new TestCase("B000", new int[]{6,6,6,6,6,4,4,4,4,4}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
              //  new TestCase("B000", new int[]{8,8,8,8,8,4,4,4,4,4}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
               // new TestCase("B000", new int[]{4,4,4,4,2,2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
               // new TestCase("B000", new int[]{2,2,2,2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                //new TestCase("B000", new int[]{4,4,4,3,3,3}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                //new TestCase("B000", new int[]{4,4,4,4,4,4}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                //new TestCase("B000", new int[]{6,6,6,4,4,4}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                //new TestCase("B000", new int[]{2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
               // new TestCase("B000", new int[]{2,2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
              //  new TestCase("B000", new int[]{2,2,2,2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
              //  new TestCase("B000", new int[]{3,3,3,3}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                //new TestCase("B000", new int[]{4,4,4,2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                //new TestCase("B000", new int[]{4,4,4,4,4,6,6,6,6,6}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
               // new TestCase("B001", new int[]{6,6,6,6,6,4,4,4,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
              //  new TestCase("B001", new int[]{6,6,6,6,6,6,4,4,2,2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
               // new TestCase("B001", new int[]{6,6,6,6,6,6,4,4,4,2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                /*new TestCase("B002", new int[]{6,6,6,6,6,4,4,4,4,4}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                new TestCase("B002", new int[]{3,3,3,3,2,2,2,2,2,2,2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                new TestCase("B002", new int[]{4,4,4,4,2,2,2,2,2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                new TestCase("B003", new int[]{4,4,4,4,4,3,3,3,3,3,2,2,2,2,2}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                new TestCase("K8(4)", new int[]{4,4,4,4,4,4,4,4}, "U={0,1,2}(3,3,3), V={3,4,5,6}(2,2,2,2)"),
                        // new TestCase("B5", new int[]{3, 3, 2, 2, 2, 2, 1, 1}, "U={0,1,2,3}(3,3,2,2), V={4,5,6,7}(2,2,1,1)")*/
        };




        // printHeader();
        //printHeaderOnlyCount();
        printHeaderPaperLexRevLex();
        for (TestCase sample : DataExperForPaperLexVsRevLex.BipartiteSamples) {
            Result Lex = OpLexVsOpRevLexVsHybrid.testLex(sample.degrees);
            Result RevLex = OpLexVsOpRevLexVsHybrid.testRevLex(sample.degrees);
            Result optLex = OpLexVsOpRevLexVsHybrid.testOptimizedLex(sample.degrees);
            Result optRevLex = OpLexVsOpRevLexVsHybrid.testOptimizedRevLex(sample.degrees);
            printResults(sample.name, Lex, RevLex, optLex, optRevLex);

             //System.out.printf("\n * *******end **************   \n");
            //Result hybridLexRevLex = OpLexVsOpRevLexVsHybrid.testHybridLexRevLex(sample.degrees);
            //Result hybridLexRevLexByGroup = OpLexVsOpRevLexVsHybrid.testHybridLexRevLexByGroup(sample.degrees);
            //Result optAntiLex = LexVsRevLex.optAntiLex(sample.degrees);
            //Result optAntiRevLex = LexVsRevLex.optAntiRevLex(sample.degrees);

            //Result HybridAntiLexTopOptRevLexBotom = LexVsRevLex.HybridAntiLexTopOptRevLexBotom(sample.degrees);
            //System.out.printf("\n * *******end HybridAntiLexTopOptRevLexBotom **************   \n");
            //Result HybridLexTopOptAntiRevLexBotom = LexVsRevLex.HybridLexTopOptAntiRevLexBotom(sample.degrees);
            //System.out.printf("\n * **********end HybridLexTopOptAntiRevLexBotom***********   \n");

            //Result test = LexVsRevLex.test(sample.degrees);

           // printResults(sample.name, sample.degrees.length, optLex, optRevLex, hybridLexRevLex, HybridAntiLexTopOptRevLexBotom);
           // printResults(sample.name, sample.degrees.length, Lex, RevLex, optLex, optRevLex);
           // printResults(sample.name, Lex, RevLex, optLex, optRevLex);
        }
        System.out.printf("\n ********* End of Experimentation  **************   \n");

    }

    // Helper method to print results in consistent format

    private static void printResultsOnllyCount(String sampleName, int n, Result optLex, Result optRevLex, Result hybrid, Result hybridbyGroup) {
        System.out.printf("%-10s | %-12d  | %-15d   | %-15d   | %-15d  %n",
                sampleName,
                optLex.count,
                optRevLex.count,
                hybrid.count,
                hybridbyGroup.count
        );
    }

    // Helper method to print table header


    // ======= PRINT RESULTS =======
    private static void printResults(String sampleName, Result Lex, Result RevLex, Result optLex, Result optRevLex) {
        System.out.printf("%-10s | %-15d | %-15d | %-15d | %-15d%n",
                sampleName,
                Lex.count,
                RevLex.count,
                optLex.count,
                optRevLex.count
        );
    }

    private static void printHeaderPaperLexRevLex() {
        System.out.printf("%-10s | %-15s | %-15s | %-15s | %-15s%n",
                "Sample", "Lex Count", "RevLex Count", "OptLex Count", "OptRevLex Count");
        System.out.println("-".repeat(80));
    }
    private static void printHeader() {
        System.out.printf("%-10s | %-12s | %-12s | %-15s | %-12s | %-15s | %-12s | %-15s | %-12s%n",
                "Sample", "Lex Count", "Lex Time(s)", "RevLex Count", "RevLex Time(s)",
                "Hybrid Count", "Hybrid Time(s)", "HybridGroup Count", "HybridGroup Time(s)");
        System.out.println("-".repeat(100));
    }

    private static void printHeaderOnlyCount() {
        System.out.printf("%-10s | %-12s | %-12s | %-15s | %-12s | %-15s | %-12s %n",
                "Sample", "Lex Count",  "AntiLex Count",  "RevLex Count","AntiRevLex Count",
                "AntiLexRevLex Count", "LexAntiRevLex Count");
        System.out.println("-".repeat(100));
    }
}
