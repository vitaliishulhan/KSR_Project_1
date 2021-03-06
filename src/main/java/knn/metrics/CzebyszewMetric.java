package knn.metrics;

import knn.HammingDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements Czebyszew's Metric, where distance between two vectors is equal to:
 *
 * d_cz(x,y) = from i=1 to n [ max(abs(y_i - x_i)) ])
 *
 * where x and y are values vectors
 *
 * @see knn.metrics.Metric
 */
public class CzebyszewMetric implements Metric {
    private static final String name = "Metryka Czebyszewa";

    @Override
    public double getDistance(List<Double> v1, List<Double> v2, List<Character> c1, List<Character> c2) {

        List<Double> distances = new ArrayList<>();

        for (int i = 0; i < v1.size(); i++) {
            distances.add(Math.abs(v1.get(i)-v2.get(i)));
        }

        for (int i = 0; i < c1.size(); i++) {
            distances.add((double) HammingDistance.getDistance(c1.get(i), c2.get(i)));
        }

        return Collections.max(distances);
    }

    @Override
    public double getDistance(List<Double> v1, List<Double> v2, List<Character> c1, List<Character> c2, boolean[] numberFilter, boolean[] textFilter) {
        List<Double> distances = new ArrayList<>();

        for (int i = 0; i < v1.size(); i++) {
            if (numberFilter[i]) {
                distances.add(Math.abs(v1.get(i) - v2.get(i)));
            }
        }

        for (int i = 0; i < c1.size(); i++) {
            if (textFilter[i]) {
                distances.add((double) HammingDistance.getDistance(c1.get(i), c2.get(i)));
            }
        }

        return Collections.max(distances);
    }

    public String getName() {
        return name;
    }
}
