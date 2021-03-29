package knn.metrics;

import knn.Metric;
import knn.exceptions.FilterDoesNotFitException;

import java.util.List;

public class EuclidianMetric implements Metric {
    @Override
    public double getDistance(List<Double> v1, List<Double> v2)
    {
        double sum = 0;

        for (int i = 0; i < v1.size(); i++) {
            sum += Math.pow(v1.get(i) - v2.get(i), 2);
        }

        return Math.sqrt(sum);
    }

    @Override
    public double getDistance(List<Double> v1, List<Double> v2, boolean[] filter){
        double sum = 0;

        for (int i = 0; i < v1.size(); i++) {
            if (filter[i]) {
                sum += Math.pow(v1.get(i) - v2.get(i), 2);
            }
        }

        return Math.sqrt(sum);
    }
}
