import java.io.*;
import java.util.*;

public class gen {
    static final int MAX_CASES = 200;
    static final int MAXN = 2000;
    static final int MAX_CHANGE = (int) (1e5);
    static final int MAX_LOC = (int) (1e5);
    static Random rand;
    static PrintWriter inputWriter;
    static PrintWriter outputWriter;
    static int testCase;
    static int[][] inputLoc;
    static int[] inputSize;
    static int[] inputChange;
    public static void main(String[] args) throws FileNotFoundException {
        testCase = 1;
        rand = new Random(System.currentTimeMillis());
        inputLoc = new int[MAX_CASES + 1][];
        inputSize = new int[MAX_CASES + 1];
        inputChange = new int[MAX_CASES + 1];

        addSampleData();
        addCustomData();
        addRandomData();
    }

    static void addSampleData() throws FileNotFoundException {
        createData(2, new int[] {0, 10}, 5);
        createData(2, new int[] {0, 10}, 50);
    }

    static void addCustomData() throws FileNotFoundException {
        createData(1, new int[] {0}, MAX_CHANGE);
        createData(1, new int[] {543}, 900);
        createData(1, new int[] {MAX_LOC}, 0);
        createData(2, new int[] {2000, 6000}, MAX_CHANGE / 2);
        createData(2, new int[] {2000, 6000}, 10);
        createData(2, new int[] {2000, 6000}, 4000);
        createData(4, new int[] {10, 125, 3000, 10000}, MAX_CHANGE);
    }

    static int randRange(int l, int r) {
        return rand.nextInt(r - l + 1) + l;
    }

    static void generateRandomData(int sizeBoundL, int sizeBoundR, int locBound, int changeBoundL, int changeBoundR) throws FileNotFoundException {
        int n = randRange(sizeBoundL, sizeBoundR);
        int c = randRange(changeBoundL, changeBoundR);
        locBound = Math.max(locBound, n - 1);

        ArrayList<Integer> possibleLoc = new ArrayList<>();
        for(int i = 0; i <= locBound; i++) possibleLoc.add(i);
        Collections.shuffle(possibleLoc, rand);

        int[] a = new int[n];
        for(int i = 0; i < n; i++) a[i] = possibleLoc.get(i);
        Arrays.sort(a);

        createData(n, a, c);
    }

    static void addRandomData() throws FileNotFoundException {
        int[] sizeRange = {0, 10, 300, MAXN - 1, MAXN};
        int[] changeRange = {-1, 10, 1000, MAX_CHANGE};
        int[] locRange = {10, 5000, MAX_LOC};

        for(int i = 1; i < sizeRange.length; i++) {
            for(int j = 1; j < changeRange.length; j++) {
                for(int k = 0; k < locRange.length; k++) {
                    for(int iter = 0; iter < 2; iter++) {
                        generateRandomData(sizeRange[i - 1] + 1, sizeRange[i], locRange[k], changeRange[j - 1] + 1,
                        changeRange[j]);
                    }
                }
            }
        }
    }

    static boolean isRepeat(int n, int[] a, int c) {
        outer:
        for(int i = 1; i < testCase; i++) {
            if(n != inputSize[i]) continue outer;
            if(c != inputChange[i]) continue outer;
            for(int j = 0; j < n; j++) {
                if(a[j] != inputLoc[i][j]) continue outer;
            }
            return true;
        }
        return false;
    }

    static void createData(int n, int[] a, int c) throws FileNotFoundException {
        assert(n == a.length);
        assert(1 <= n && n <= MAXN);
        assert(0 <= c && c <= MAX_CHANGE);
        for(int i = 0; i < n; i++) assert(0 <= a[i] && a[i] <= MAX_LOC);
        if(isRepeat(n, a, c)) return;
        
        inputSize[testCase] = n;
        inputLoc[testCase] = new int[n];
        for(int i = 0; i < n; i++) inputLoc[testCase][i] = a[i];
        inputChange[testCase] = c;

        long ans = solveData(n, a, c);
        outputData(n, a, c, ans);
    }

    static String getTestCase() {
        String ret = Integer.toString(testCase);
        while(ret.length() < 3) ret = "0" + ret;
        return ret;
    }

    static void outputData(int n, int[] a, int c, long ans) throws FileNotFoundException {
        String prefix = "zupp" + getTestCase();
        inputWriter = new PrintWriter(prefix + ".in");
        outputWriter = new PrintWriter(prefix + ".out");

        inputWriter.printf("%d %d%n", n, c);
        for(int i = 0; i < n; i++) {
            if(i != 0) inputWriter.print(' ');
            inputWriter.printf("%d", a[i]);
        }
        inputWriter.printf("%n");
        inputWriter.close();

        outputWriter.printf("%d", ans);
        outputWriter.printf("%n");
        outputWriter.close();

        testCase++;
    }

    static final long INF = (long) (1e18);

    static long solveSide(int n, int[] a, int c) {
        long[] dp = new long[n];
        Arrays.fill(dp, INF);

        for(int i = 0; i < n; i++) {
            dp[i] = Math.min(dp[i], (long) 2 * (a[i] - a[0]) + c);
            long prevCost = i > 0 ? dp[i - 1] + a[i] - a[i - 1] : 0;

            for(int j = i; j < n; j++) {
                long dist = a[j] - a[i];
                long currCost = 2 * dist + c;
                if(j != n - 1) currCost += dist;

                dp[j] = Math.min(dp[j], currCost + prevCost); 
            }
        }

        return dp[n - 1];
    }

    static int[] reverseAndNegate(int n, int[] a) {
        int[] res = new int[n];
        for(int i = 0; i < n; i++) res[i] = -a[n - i - 1];
        return res;
    }

    static long solveData(int n, int[] a, int c) {
        long ans = solveSide(n, a, c);
        long ansRev = solveSide(n, reverseAndNegate(n, a), c);

        return Math.min(ans, ansRev);
    }
}
