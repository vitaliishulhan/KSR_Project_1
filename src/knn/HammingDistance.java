package knn;

/**
 * Calculates distance between two text values
 */
public class HammingDistance {

    /**
     * Returns distance between two chars in bits representation
     * @param c1 text trait of the first article
     * @param c2 text trait of the second article
     * @return distance between the given text traits
     */
    public static int getDistance(char c1, char c2) {
        int res = 0;

        for(int i = 0; i < 8; i++) {
            if (getBit(c1, i) != getBit(c2, i)) {
                res++;
            }
        }

        return res;
    }

    /**
     * Take the given bit from the given character
     * @param c char for bit extracting
     * @param i bit order number
     * @return bit, i.e. 1 or 0
     */
    private static int getBit(char c, int i) {
        return (c >> i) & 1;
    }
}
