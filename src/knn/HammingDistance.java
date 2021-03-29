package knn;

public class HammingDistance {
    public static int getDistance(char c1, char c2) {
        int res = 0;

        for(int i = 0; i < 8; i++) {
            if (getBit(c1, i) != getBit(c2, i)) {
                res++;
            }
        }

        return res;
    }

    private static int getBit(char c, int i) {
        return (c >> i) & 1;
    }
}
