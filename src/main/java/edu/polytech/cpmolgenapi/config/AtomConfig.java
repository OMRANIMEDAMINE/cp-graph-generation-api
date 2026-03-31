package edu.polytech.cpmolgenapi.config;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final public class AtomConfig {
    private static final Map<String, String> SYMBOL_TO_NAME_MAP;
    private static final Map<String, Integer> SYMBOL_TO_VALENCY_MAP;

    static {
        Map<String, String> tempNameMap = new HashMap<>();
        // Add mappings for all chemical symbols and their corresponding names
        tempNameMap.put("H", "Hydrogen");
        tempNameMap.put("He", "Helium");
        tempNameMap.put("Li", "Lithium");
        tempNameMap.put("Be", "Beryllium");
        tempNameMap.put("B", "Boron");
        tempNameMap.put("C", "Carbon");
        tempNameMap.put("N", "Nitrogen");
        tempNameMap.put("O", "Oxygen");
        tempNameMap.put("F", "Fluorine");
        tempNameMap.put("Ne", "Neon");
        tempNameMap.put("Na", "Sodium");
        tempNameMap.put("Mg", "Magnesium");
        tempNameMap.put("Al", "Aluminium");
        tempNameMap.put("Si", "Silicon");
        tempNameMap.put("P", "Phosphorus");
        tempNameMap.put("S", "Sulfur");
        tempNameMap.put("Cl", "Chlorine");
        tempNameMap.put("Ar", "Argon");
        // Add more mappings as needed
        SYMBOL_TO_NAME_MAP = Collections.unmodifiableMap(tempNameMap);

        Map<String, Integer> tempValencyMap = new HashMap<>();
        // Add mappings for all chemical symbols and their corresponding valencies
        tempValencyMap.put("H", 1);
        tempValencyMap.put("He", 0);
        tempValencyMap.put("Li", 1);
        tempValencyMap.put("Be", 2);
        tempValencyMap.put("B", 3);
        tempValencyMap.put("C", 4);
        tempValencyMap.put("N", 3); // Nitrogen can have valency 3 or 5
        tempValencyMap.put("O", 2);
        tempValencyMap.put("F", 1);
        tempValencyMap.put("Ne", 0);
        tempValencyMap.put("Na", 1);
        tempValencyMap.put("Mg", 2);
        tempValencyMap.put("Al", 3);
        tempValencyMap.put("Si", 4);
        tempValencyMap.put("P", 3); // Phosphorus can have valency 3 or 5
        tempValencyMap.put("S", 2); // Sulfur can have valency 2, 4, or 6
        tempValencyMap.put("Cl", 1);
        tempValencyMap.put("Ar", 0);
        // Add more mappings as needed
        SYMBOL_TO_VALENCY_MAP = Collections.unmodifiableMap(tempValencyMap);
    }

    public static String getAtomName(String symbol) {
        String atomName = SYMBOL_TO_NAME_MAP.get(symbol);
        return (atomName != null) ? atomName : "Unknown"; // Return "Unknown" if symbol is not found
    }

    public static int getAtomValency(String symbol) {
        Integer valency = SYMBOL_TO_VALENCY_MAP.get(symbol);
        return (valency != null) ? valency : 0; // Return 0 if symbol is not found
    }
}
