package edu.polytech.cpmolgenapi.model;

import edu.polytech.cpmolgenapi.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.*;

import static edu.polytech.cpmolgenapi.exception.BusinessExceptionReason.*;

public class Molecule {
    private String name;
    private String mf;

    private ArrayList<Atom> atoms = new ArrayList();

    private String desc;
    private ArrayList<Correlation> correlations = new ArrayList();
    private HashMap<String, Boolean> forbiddenFragments = new HashMap<>();
    private HashMap<String, Boolean> imposedFragments = new HashMap<>();

    public Molecule() {
    }

    public Molecule(String name, String mf, ArrayList<Atom> atoms, String desc, ArrayList<Correlation> correlations, HashMap<String, Boolean> forbiddenFragments, HashMap<String, Boolean> imposedFragments) {
        this.name = name;
        this.mf = mf;
        this.atoms = atoms;
        this.desc = desc;
        this.correlations = correlations;
        this.forbiddenFragments = forbiddenFragments;
        this.imposedFragments = imposedFragments;
        this.validateMoleculeConfig();

    }


    public Molecule(String name, String mf, ArrayList<Atom> atoms) {
        this.name = name;
        this.atoms = atoms;
        this.mf = mf;
        this.validateMoleculeConfig();

    }

    public static void main(String[] args) {
        //public Atom(String index, String symbol, ArrayList<Integer> hybridization, ArrayList<Integer> multiplicity, double nmrShift) {
        Atom atom1 = new Atom(1, "C", 0, 0, 12.23);
        Atom atom2 = new Atom(2, "O", 0, 0, 24.66);
        Atom atom3 = new Atom(3, "C", 0, 0, 15.23);
        Atom atom4 = new Atom(4, "O", 0, 0, 24.23);
        ArrayList<Atom> atoms = new ArrayList<Atom>();
        atoms.add(atom1);
        atoms.add(atom2);
        atoms.add(atom3);
        atoms.add(atom4);
        Molecule molecule = new Molecule("Pinene", "C4H10", atoms);
        System.out.println(molecule);
    }

    private double getTotalNmrShift() {
        double totalNmrShift = 0.0;

        for (Atom atom : atoms) {
            totalNmrShift += atom.getNmrShift();
        }

        return totalNmrShift;
    }

    public int[] getDegreeSequence() {
        int[] degree = new int[atoms.size()];
        for (int i = 0; i < atoms.size(); i++) {
            //System.out.println ("val: "+ atoms.get(i).getValency() + "Mult: "+ atoms.get(i).getMultiplicity() + " deg : "+ atoms.get(i).getDegree());
            degree[i] = atoms.get(i).getDegree();
        }
        return degree;
    }

    public int[] getHybridizationState() {
        // Sort the atoms array by index
        //Collections.sort(atoms, (atom1, atom2) -> Integer.compare(atom1.getIndex(), atom2.getIndex()));

        int[] hybridizations = new int[atoms.size()];

        for (int i = 0; i < atoms.size(); i++) {
            hybridizations[i] = atoms.get(i).getHybridization();
        }

        return hybridizations;
    }

    // Method to validate atoms set
    public void validateAtoms() throws BusinessException {
        if (atoms == null)
            throw new BusinessException(INVALID_MOLECULE_CONFIG_ATOM_SET_ISEMPTY_EXCEPTION);
        if (atoms.isEmpty() || atoms.size() <= 0)
            throw new BusinessException(INVALID_MOLECULE_CONFIG_ATOM_SET_ISEMPTY_EXCEPTION);
        Collections.sort(atoms, Comparator.comparingInt(Atom::getIndex));
        int n = atoms.size();
        boolean[] indexCheck = new boolean[n + 1]; // Indexes should be from 1 to n
        for (Atom atom : atoms) {
            int index = atom.getIndex();
            if (index < 1 || index > n)
                throw new BusinessException(INVALID_MOLECULE_CONFIG_INDEXRANGE_EXCEPTION);
            if (indexCheck[index])
                throw new BusinessException(INVALID_MOLECULE_CONFIG_INDEXDUPLICATED_EXCEPTION);
            if (atom.getHybridization() < 1 || atom.getHybridization() > 3)
                throw new BusinessException(INVALID_MOLECULE_CONFIG_HYBRIDIZATION_EXCEPTION);
            if (atom.getValency() <= atom.getMultiplicity())
                throw new BusinessException(INVALID_MOLECULE_CONFIG_MULTIPLICITY_EXCEPTION);

            indexCheck[index] = true;
        }

        for (int i = 1; i <= n; i++) {
            if (!indexCheck[i]) {
                throw new BusinessException(INVALID_MOLECULE_CONFIG_INDEXMISSSING_EXCEPTION);
                //throw new InvalidMoleculeConfigException("Missing atom index: " + i);
            }
        }
    }

    // Method to validate correlations
    public void validateCorrelations() throws BusinessException {
        for (Correlation correlation : correlations) {
            String type = correlation.getCorrelationType();
            if ("BOND".equals(type) || "COSY".equals(type) || "HMBC".equals(type)) {
                if (correlation.getAtomSet1().size() != correlation.getAtomSet2().size()) {
                    throw new BusinessException(INVALID_MOLECULE_CONFIG_CORRELATION_EXCEPTION);
                }
            }
        }
    }

    public void validateMoleculeConfig() throws BusinessException {
        validateAtoms();
        validateCorrelations();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Atom> getAtoms() {
        return atoms;
    }

    public void setAtoms(ArrayList<Atom> atoms) {
        this.atoms = atoms;
        this.validateAtoms();
    }

    public String getMf() {
        return mf;
    }

    public void setMf(String mf) {
        this.mf = mf;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList<Correlation> getCorrelations() {
        return correlations;
    }

    public void setCorrelations(ArrayList<Correlation> correlations) {
        this.correlations = correlations;
        this.validateCorrelations();
    }

    public HashMap<String, Boolean> getForbiddenFragments() {
        // Ensure that all keys are present and set to false
        for (String key : Arrays.asList("threecycle", "fourcycle", "fivecycle", "sixcycle")) {
            forbiddenFragments.putIfAbsent(key, false);
        }

        return forbiddenFragments;
    }

    public void setForbiddenFragments(HashMap<String, Boolean> forbiddenFragments) {
        this.forbiddenFragments = forbiddenFragments;
    }

    public HashMap<String, Boolean> getImposedFragments() {
        // Ensure that all keys are present and set to false
        for (String key : Arrays.asList("threecycle", "fourcycle", "fivecycle", "sixcycle")) {
            imposedFragments.putIfAbsent(key, false);
        }
        return imposedFragments;
    }

    public void setImposedFragments(HashMap<String, Boolean> imposedFragments) {
        this.imposedFragments = imposedFragments;
    }

    @Override
    public String toString() {
        return "Molecule{" +
                "\n name='" + name + '\'' +
                ",\n mf='" + mf + '\'' +
                ",\n atoms=" + atoms +
                ",\n desc='" + desc + '\'' +
                ",\n correlations=" + correlations +
                ",\n imposedFragments=" + imposedFragments +
                ",\n forbiddenFragments=" + forbiddenFragments +
                ",\n " +
                '}';
    }

}

