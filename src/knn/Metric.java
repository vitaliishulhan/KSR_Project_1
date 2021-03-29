package knn;

import java.util.List;

public interface Metric {
    double getDistance(List<Double> v1, List<Double> v2);
    double getDistance(List<Double> v1, List<Double> v2, boolean[] filter);
}