package edu.polytech.cpmolgenapi;

public class CanonicalChecker_v0 {

    /* =========================================================
       MAIN VERIFICATION FUNCTION
       ========================================================= */
    public static boolean verifyCanonical(int[][] currentMatrix) {
        int[][] C = complement(currentMatrix);
        int[][] D = antiLexOrder(C);
        int[][] E = complement(D);
        int[][] F = antiLexOrder(E);

        return lexCompareSimple(currentMatrix, F) >= 0;
    }

    /* =========================================================
       COMPLEMENT ADJACENCY MATRIX
       ========================================================= */
    public static int[][] complement(int[][] A) {
        int n = A.length;
        int[][] C = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    C[i][j] = 0;
                } else {
                    C[i][j] = 1 - A[i][j];
                }
            }
        }
        return C;
    }

    /* =========================================================
       ANTI-LEX ORDERING (ROW + COLUMN SWAPS)
       ========================================================= */
    public static int[][] antiLexOrder(int[][] A) {
        int n = A.length;
        int[][] mat = A; //deepCopy(A);
        boolean improved = true;
        int iteration = 0;
        int maxIter = 10000;

        while (improved && iteration < maxIter) {
            improved = false;
            iteration++;
            outer:
            for (int i = n - 1; i >= 1; i--) {
                for (int j = 0; j < i; j++) {
                    for (int k = 0; k < n; k++) {
                        if (k != i && k != j) {
                            if (mat[i][k] == 1 && mat[j][k] == 0) {
                                swapRows(mat, i, j);
                                swapCols(mat, i, j);
                                improved = true;
                                break outer;
                            }
                            else if (mat[i][k] == 0 && mat[j][k] == 1) {
                                break;
                            }
                        }
                    }
                }
            }
            /*for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    for (int k = 0; k < n; k++) {
                        if (k != i && k != j) {
                            if (mat[i][k] == 0 && mat[j][k] == 1) {
                                swapRows(mat, i, j);
                                swapCols(mat, i, j);
                                improved = true;
                                break outer;
                            }
                            else if (mat[i][k] == 1 && mat[j][k] == 0) {
                                break;
                            }
                        }
                    }
                }
            }*/
        }

        if (improved) {
            System.out.println("⚠ Warning: antiLex may not have converged");
        }

        return mat;
    }

    /* =========================================================
       LEXICOGRAPHIC COMPARISON (SIMPLE VERSION)
       ========================================================= */
    public static int lexCompareSimple(int[][] A, int[][] B) {
        int n = A.length;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (A[i][j] != B[i][j]) {
                    return (A[i][j] < B[i][j]) ? -1 : 1;
                }
            }
        }
        return 0;
    }

    /* =========================================================
       OPTIONAL: FULL VERSION WITH FIRST DIFFERENCE
       ========================================================= */
    public static class LexResult {
        public int cmp;
        public int i;
        public int j;

        public LexResult(int cmp) {
            this.cmp = cmp;
            this.i = -1;
            this.j = -1;
        }

        public LexResult(int cmp, int i, int j) {
            this.cmp = cmp;
            this.i = i;
            this.j = j;
        }
    }

    public static LexResult lexCompare(int[][] A, int[][] B, boolean returnFirstDiff) {
        int n = A.length;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (A[i][j] != B[i][j]) {
                    int cmp = (A[i][j] < B[i][j]) ? -1 : 1;

                    if (returnFirstDiff) {
                        return new LexResult(cmp, i, j);
                    } else {
                        return new LexResult(cmp);
                    }
                }
            }
        }

        if (returnFirstDiff) {
            return new LexResult(0, -1, -1);
        } else {
            return new LexResult(0);
        }
    }

    /* =========================================================
       UTILITIES
       ========================================================= */
    public static int[][] deepCopy(int[][] A) {
        int n = A.length;
        int[][] copy = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, copy[i], 0, n);
        }
        return copy;
    }

    public static void swapRows(int[][] A, int i, int j) {
        int[] temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }

    public static void swapCols(int[][] A, int i, int j) {
        for (int k = 0; k < A.length; k++) {
            int temp = A[k][i];
            A[k][i] = A[k][j];
            A[k][j] = temp;
        }
    }
}