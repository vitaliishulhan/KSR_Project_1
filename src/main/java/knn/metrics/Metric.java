package knn.metrics;

import java.util.List;


/**
 *Interface for implementing metric to get distance between traits
 */
public interface Metric {
    /**
     * Calculates distance between two vectors of number traits. For two vector of text traits Hamming distance is used
     * @param v1 numerical traits vector of the first article
     * @param v2 numerical traits vector of the second article
     * @param c1 text traits vector of the first article
     * @param c2 text traits vector of the second article
     * @return distance between traits of two articles
     */
    double getDistance(List<Double> v1, List<Double> v2, List<Character> c1, List<Character> c2);

    /**
     * Does the same what the previous getDistance method does, but there are filters for using determined traits
     * @param v1 numerical traits vector of the first article
     * @param v2 numerical traits vector of the second article
     * @param c1 text traits vector of the first article
     * @param c2 text traits vector of the second article
     * @param numberFilter filter for numerical traits
     * @param textFilter filter for text traits
     * @return distance between traits of two articles
     */
    double getDistance(List<Double> v1, List<Double> v2, List<Character> c1, List<Character> c2, boolean[] numberFilter, boolean[] textFilter);
    String getName();
}
