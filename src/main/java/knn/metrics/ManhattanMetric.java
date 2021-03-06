package knn.metrics;

import knn.HammingDistance;

import java.util.List;


/**
 * Implements Manhattan Metric, where distance between two vectors is equal to:
 *
 * d_m(x,y) = from i=1 to n [ sum(abs(y_i - x_i)) ]
 *
 * where x and y are values vectors
 *
 * @see knn.metrics.Metric
 */
public class ManhattanMetric implements Metric {
    private static final String name = "Metryka uliczna";

    @Override
    public double getDistance(List<Double> v1, List<Double> v2, List<Character> c1, List<Character> c2) {

        double sum = 0;

        for (int i = 0; i < v1.size(); i++) {
            sum += Math.abs(v1.get(i) - v2.get(i));
        }

        for (int i = 0; i < c1.size(); i++) {
            sum += HammingDistance.getDistance(c1.get(i), c2.get(i));
        }

        return sum;
    }

    @Override
    public double getDistance(List<Double> v1, List<Double> v2, List<Character> c1, List<Character> c2, boolean[] numberFilter, boolean[] textFilter) {
        double sum = 0;

        for (int i = 0; i < v1.size(); i++) {
            if (numberFilter[i]) {
                sum += Math.abs(v1.get(i) - v2.get(i));
            }
        }

        for (int i = 0; i < c1.size(); i++) {
            if (textFilter[i]) {
                sum += HammingDistance.getDistance(c1.get(i), c2.get(i));
            }
        }

        return sum;
    }

    public String getName() {
        return name;
    }
}
