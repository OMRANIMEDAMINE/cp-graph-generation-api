package edu.polytech.cpmolgenapi.model;

import java.util.ArrayList;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cp.IloCP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CyclesGeneration {
    private static int getMaxValue(int[] array) {
        int max = Integer.MIN_VALUE;
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    public static Map<Integer, List<Integer>> getIndexGroups(int[] degreeSequence) {
        Map<Integer, List<Integer>> indexGroups = new HashMap<>();

        // Iterate through the array
        for (int i = 0; i < degreeSequence.length; i++) {
            int value = degreeSequence[i];

            // If the value is not already in the map, create a new list for it
            indexGroups.putIfAbsent(value, new ArrayList<>());

            // Add the index to the corresponding list
            indexGroups.get(value).add(i);
        }

        // Add entries for values that do not appear in the array with empty groups
        for (int value = 1; value <= getMaxValue(degreeSequence); value++) {
            indexGroups.putIfAbsent(value, new ArrayList<>());
        }

        return indexGroups;
    }

    public static int[] generateArray(int n) {
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = i;
        }
        return result;
    }


    public static void displayArrayList(ArrayList<int[]> arrayList) {
        for (int[] array : arrayList) {
            // Print each element of the array
            System.out.print("[ ");
            for (int element : array) {
                System.out.print(element + " ");
            }
            System.out.println("]");
        }
    }
    public static ArrayList<int []> Cycles(int [] Values, int P) throws IloException {

        ArrayList<int []> ExplicitCycle= new ArrayList();
        // CP Modelisation
        IloCP cp = new IloCP();
        try {
            cp.setParameter(IloCP.DoubleParam.TimeLimit, 10000000);
            cp.setParameter(IloCP.IntParam.SearchType,IloCP.ParameterValues.DepthFirst);
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            cp.setParameter(IloCP.IntParam.Workers, 1);
        } catch (IloException ex) {
            Logger.getLogger(CyclesGeneration.class.getName()).log(Level.SEVERE, null, ex);
        }


        //IloIntVar[] Mesvariables = cp.intVarArray(P,0,N-1); model based on N
        IloIntVar[] Mesvariables = cp.intVarArray(P,Values,"Mesvariables"); // Model based on [0,1,2,3,4,5,..,]



        //**** CP Constraint  *****
        //**  Constraint 1: Tous les sommets sont differents ( AllDiff )  **//
        try {
            cp.add( cp.allDiff(Mesvariables));
        } catch (IloException e) {
            throw new RuntimeException(e);
        }


        //**  Constraint 2: min = first  **//
        for (int i=1;i<P;i++ ){
            try {
                cp.add(cp.lt(Mesvariables[0], Mesvariables[i]));
            } catch (IloException ex) {
                Logger.getLogger(CyclesGeneration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //**  Constraint 3: Second < last  **//
        try {
            cp.add(cp.lt(Mesvariables[1], Mesvariables[P-1]));
        } catch (IloException ex) {
            Logger.getLogger(CyclesGeneration.class.getName()).log(Level.SEVERE, null, ex);
        }



   /*
        try {
            // Configuration à faire et à verifier avec les resultats de l'article
            /*
            cp.param.FailLimit = 10000;
            cp.param.SearchType="DepthFirst";
            // no redundant solutions
            cp.param.logVerbosity="Quiet";
            cp.param.workers=1;
            // end no redundant solutions
            var model =thisOplModel;




        } catch (IloException ex) {
            Logger.getLogger(CyclesGeneration.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        try{

            boolean ok = false;
            cp.startNewSearch();
            int compteur = 0;
            while (cp.next()) {
                int [] Aux = new int [P];
                ok = true;
                compteur++;
                //System.out.println("\n ");
                for (int i=0;i<P;i++ ){
                    // System.out.print(" "+ (int)cp.getValue(Mesvariables[i]));
                    Aux[i]=(int)cp.getValue(Mesvariables[i]);

                }
                ExplicitCycle.add(Aux);

            }
            cp.endSearch();
            //System.out.println("Total: "+ compteur);
            if (!ok){
                System.out.println(" No Solution found");
            }
            cp.clearAbort();
            cp.end();
            return ExplicitCycle;
        } catch (IloException e) {
            System.err.println("Error " + e);
        }
        return ExplicitCycle;
    }

    public static ArrayList<int []> Chaines(int [] Degree2Values, int [] Degree1Values, int P) throws IloException {
        ArrayList<int []> ExplicitChaine= new ArrayList();
        IloCP cp = new IloCP();
        try {
            cp.setParameter(IloCP.DoubleParam.TimeLimit, 10000000);
            cp.setParameter(IloCP.IntParam.SearchType,IloCP.ParameterValues.DepthFirst);
            cp.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            cp.setParameter(IloCP.IntParam.Workers, 1);
        } catch (IloException ex) {
            Logger.getLogger(CyclesGeneration.class.getName()).log(Level.SEVERE, null, ex);
        }

        //IloIntVar[] Mesvariables = cp.intVarArray(P,0,N-1); model based on N
        IloIntVar[] Mesvariables = new IloIntVar[P+1];
        Mesvariables[0] = cp.intVar(Degree1Values);
        Mesvariables[P] = cp.intVar(Degree1Values);
        for (int i=1;i<P;i++ ){
            Mesvariables[i] = cp.intVar(Degree2Values); // Model based on [0,1,2,3,4,5,..,]
        }
       /* IloIntVar[] Degree2Vars = cp.intVarArray(P, Degree2Values,"Degree2Vars"); // Model based on [0,1,2,3,4,5,..,]

        IloIntVar Extremity1Var = cp.intVar(Degree1Values,"Extremity1Var"); // Extremity 1 Var based on [0,1,2,3,4,5,..,]
        IloIntVar Extremity2Var = cp.intVar(Degree1Values,"Extremity2Var"); //  Extremity 2 Var based on [0,1,2,3,4,5,..,]
*/
        //**** CP Constraint  *****
        //**  Constraint 1: Tous les sommets sont differents ( AllDiff )  **//
        try {

            cp.add( cp.allDiff(Mesvariables));// All degree 2 are differents

            cp.add( cp.lt(Mesvariables[0], Mesvariables[P]));//Extremity 1 is "<" then extremity 2
        } catch (IloException e) {
            throw new RuntimeException(e);
        }

        try{

            boolean ok = false;
            cp.startNewSearch();
            int compteur = 0;
            while (cp.next()) {
                int [] Aux = new int [P+1];
                ok = true;
                compteur++;
                //System.out.println("\n ");
                for (int i=0;i<=P;i++ ){
                     Aux[i]=(int)cp.getValue(Mesvariables[i]);
                 }
                ExplicitChaine.add(Aux);
            }
            cp.endSearch();
            //System.out.println("Total: "+ compteur);
            if (!ok){
                System.out.println(" No Solution found");
            }
            cp.clearAbort();
            cp.end();
            return ExplicitChaine;
        } catch (IloException e) {
            System.err.println("Error " + e);
        }
        return ExplicitChaine;
    }
 }