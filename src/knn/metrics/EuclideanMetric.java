package knn.metrics;

import knn.HammingDistance;

import java.util.List;


/**
 * Implements Euclidean Metric, where distance between two vectors is equal to:
 *
 * d_e(x,y) = sqrt(from i=1 to n [ sum((y_i - x_i)^2) ])
 *
 * where x and y are values vectors
 *
 * @see knn.metrics.Metric
 */

public class EuclideanMetric implements Metric {
    @Override
    public double getDistance(List<Double> v1, List<Double> v2, List<Character> c1, List<Character> c2)
    {
        double sum = 0;

        for (int i = 0; i < v1.size(); i++) {
            sum += Math.pow(v1.get(i) - v2.get(i), 2);
        }

        for (int i = 0; i < c1.size(); i++) {
            sum += Math.pow(HammingDistance.getDistance(c1.get(i), c2.get(i)), 2);
        }

        return Math.sqrt(sum);
    }

    @Override
    public double getDistance(List<Double> v1, List<Double> v2, List<Character> c1, List<Character> c2, boolean[] numberFilter, boolean[] textFilter) {
        double sum = 0;

        for (int i = 0; i < v1.size(); i++) {
            if (numberFilter[i]) {
                sum += Math.pow(v1.get(i) - v2.get(i), 2);
            }
        }

        for (int i = 0; i < c1.size(); i++) {
            if (textFilter[i]) {
                sum += Math.pow(HammingDistance.getDistance(c1.get(i), c2.get(i)), 2);
            }
        }

        return Math.sqrt(sum);
    }
}
