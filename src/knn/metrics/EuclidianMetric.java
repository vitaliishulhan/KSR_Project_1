package knn.metrics;

import knn.Metric;
import knn.exceptions.FilterDoesNotFitException;
import knn.exceptions.VectorsDoesNotFitException;

import java.util.List;

public class EuclidianMetric implements Metric {
    @Override
    public double getDistance(List<Double> v1, List<Double> v2) throws VectorsDoesNotFitException
    {
        if (v1.size() != v2.size())
            throw new VectorsDoesNotFitException("v1 and v2 does not have the same size");

        double sum = 0;

        for (int i = 0; i < v1.size(); i++) {
            sum += Math.pow(v1.get(i) - v2.get(i), 2);
        }

        return Math.sqrt(sum);
    }

    @Override
    public double getDistance(List<Double> v1, List<Double> v2, boolean[] filter) throws VectorsDoesNotFitException {
        if (v1.size() != v2.size())
            throw new VectorsDoesNotFitException("v1 and v2 does not have the same size");

        double sum = 0;

        for (int i = 0; i < v1.size(); i++) {
            if (filter[i]) {
                sum += Math.pow(v1.get(i) - v2.get(i), 2);
            }
        }

        return Math.sqrt(sum);
    }
}
