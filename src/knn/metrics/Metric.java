package knn.metrics;

import java.util.List;

public interface Metric {
    double getDistance(List<Double> v1, List<Double> v2, List<Character> c1, List<Character> c2);
    double getDistance(List<Double> v1, List<Double> v2, List<Character> c1, List<Character> c2, boolean[] numberFilter, boolean[] textFilter);
}
