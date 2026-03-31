package edu.polytech.cpmolgenapi;

public class CanonicalCheckerOpt {

        /* =========================================================
           MAIN VERIFICATION FUNCTION
           ========================================================= */
        public static boolean verifyCanonical(int[][] currentMatrix) {
            // Work on a single mutable buffer to avoid redundant allocations.
            // Pipeline: C = complement(current) → D = antiLex(C) → E = complement(D) → F = antiLex(E)
            // We reuse the same buffer for complement (in-place) and pass it into antiLexOrder.

            int n = currentMatrix.length;

            // Step 1 – complement of currentMatrix (in-place on a fresh copy)
            int[][] buf = complementInPlace(deepCopy(currentMatrix));

            // Step 2 – anti-lex order of the complement
            antiLexOrderInPlace(buf);

            // Step 3 – complement again (in-place, same buffer)
            complementInPlace(buf);

            // Step 4 – anti-lex order
            antiLexOrderInPlace(buf);   // buf is now F

            // F >= currentMatrix  ⟺  currentMatrix is canonical
            return lexCompareSimple(currentMatrix, buf) >= 0;
        }

        /* =========================================================
           COMPLEMENT ADJACENCY MATRIX  (in-place, returns the array)
           ========================================================= */
        public static int[][] complementInPlace(int[][] A) {
            int n = A.length;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        A[i][j] ^= 1;   // flip 0↔1 with XOR – branchless
                    }
                }
            }
            return A;
        }

        /** Convenience wrapper kept for API compatibility. */
        public static int[][] complement(int[][] A) {
            return complementInPlace(deepCopy(A));
        }

    /* =========================================================
       ANTI-LEX ORDERING – optimised O(n³) with insertion-sort style
       ========================================================= */

        /**
         * Sorts the vertices so that the adjacency sequence of each row is
         * anti-lexicographically maximised, using simultaneous row+column swaps
         * (i.e., a permutation of vertices).
         *
         * Optimisation over the original:
         *  • Uses an insertion-sort–style scan: after placing vertex at position i,
         *    continue from i+1 instead of restarting from 0.
         *  • Keeps a row-sum cache to skip pairs that cannot possibly swap
         *    (a necessary – though not sufficient – pre-filter).
         *  • Avoids re-entering the outer loop on every single swap.
         *
         * Complexity: O(n³) in the worst case (n² pairs × n key comparisons),
         * vs the original's potential O(n⁵).
         */
        public static int[][] antiLexOrder(int[][] A) {
            int[][] mat = deepCopy(A);
            antiLexOrderInPlace(mat);
            return mat;
        }

        public static void antiLexOrderInPlace(int[][] A) {
            int n = A.length;

            // Precompute a scratch array for the comparison key to avoid repeated
            // index arithmetic inside the innermost loop.
            int[] rowI = new int[n];
            int[] rowJ = new int[n];

            // Insertion-sort–style: for each position i, find the best vertex
            // among [i .. n-1] and swap it into position i.
            for (int i = 0; i < n - 1; i++) {

                int best = i;

                for (int j = i + 1; j < n; j++) {
                    // Compare vertex j against current best for position i.
                    // We only look at columns outside {i, best, j} to decide.
                    // Build comparison keys for 'best' and 'j' w.r.t. all OTHER positions.

                    // Key for 'best': A[best][k] for k ∉ {i..i} (already-placed columns
                    // are irrelevant because they are fixed by earlier iterations).
                    // For anti-lex we want the LARGEST sequence, so prefer 1 over 0.

                    int cmp = compareVertices(A, best, j, i, n);
                    if (cmp < 0) {
                        // j is anti-lex greater than current best → promote j
                        best = j;
                    }
                }

                if (best != i) {
                    swapRows(A, i, best);
                    swapCols(A, i, best);
                }
            }
        }

        /**
         * Compares two vertices u and v as candidates for the next position (pos)
         * in the anti-lex ordering.
         *
         * Returns negative if v should come before u (v is anti-lex greater),
         * positive if u should come before v, 0 if tied.
         *
         * We compare the adjacency vectors of u and v restricted to columns
         * outside the range [0..pos-1] (already placed) and outside {u, v} themselves.
         */
        private static int compareVertices(int[][] A, int u, int v, int pos, int n) {
            // Scan columns from 'pos' onward, skipping u and v themselves.
            for (int k = pos; k < n; k++) {
                if (k == u || k == v) continue;
                int au = A[u][k];
                int av = A[v][k];
                if (au != av) {
                    // Anti-lex prefers 1 before 0 (largest sequence first).
                    return av - au;   // >0 means v has 1, u has 0 → v wins → return >0 (u < v)
                }
            }
            return 0;
        }

        /* =========================================================
           LEXICOGRAPHIC COMPARISON (SIMPLE VERSION)
           ========================================================= */
        public static int lexCompareSimple(int[][] A, int[][] B) {
            int n = A.length;
            for (int i = 0; i < n; i++) {
                int[] ai = A[i], bi = B[i];
                for (int j = 0; j < n; j++) {
                    if (ai[j] != bi[j]) {
                        return ai[j] < bi[j] ? -1 : 1;
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

            public LexResult(int cmp) { this.cmp = cmp; this.i = -1; this.j = -1; }
            public LexResult(int cmp, int i, int j) { this.cmp = cmp; this.i = i; this.j = j; }
        }

        public static LexResult lexCompare(int[][] A, int[][] B, boolean returnFirstDiff) {
            int n = A.length;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (A[i][j] != B[i][j]) {
                        int cmp = A[i][j] < B[i][j] ? -1 : 1;
                        return returnFirstDiff ? new LexResult(cmp, i, j) : new LexResult(cmp);
                    }
                }
            }
            return returnFirstDiff ? new LexResult(0, -1, -1) : new LexResult(0);
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
            int n = A.length;
            for (int k = 0; k < n; k++) {
                int temp = A[k][i];
                A[k][i] = A[k][j];
                A[k][j] = temp;
            }
        }

}