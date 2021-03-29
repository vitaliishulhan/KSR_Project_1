package knn;

import extraction.Place;
import extraction.Traits;
import knn.exceptions.FilterDoesNotFitException;
import knn.metrics.EuclidianMetric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Knn {

    private final PlacesCounter placesCounter;

    private final DistancesComparator dc = new DistancesComparator();
    private final Metric metric;

    private final List<Traits> trainSet;
    private final List<Traits> testSet;
    private final int k;
    private boolean[] filter;

    public Knn(final List<Traits> data, final int k, final int trainSetRelation, final Metric metric) {
        int divider = trainSetRelation <= 0 || trainSetRelation >= 100 ?
                data.size() / 2 :
                data.size() * trainSetRelation / 100;

        trainSet = data.subList(0, divider);
        testSet = data.subList(divider, data.size());
        this.k = k;

        this.metric = metric;
        placesCounter = new PlacesCounter();
    }

    public Knn(final List<Traits> data, final int k, final int trainSetRelation, final Metric metric, boolean[] filter) {
        this(data, k, trainSetRelation, metric);

        try {
            changeFilter(filter);
        } catch (FilterDoesNotFitException e) {
            System.out.println(e);
            System.out.println("Set default filter");
            setDefaultFilter();
        }
    }

    public void changeFilter(boolean[] newFilter) throws FilterDoesNotFitException {
        if (newFilter.length != trainSet.get(0).getTraitsAmount())
            throw new FilterDoesNotFitException("Filter length and traits amount are not the same");

        filter = newFilter;
    }

    public void setDefaultFilter() {
        Arrays.fill(filter, true);
    }

    private Place classify(Traits obj) {
        placesCounter.reset();

        List<Object[]> distances = new ArrayList<>();

        for (Traits trainSample: trainSet) {
            distances.add(new Object[] {trainSample, metric.getDistance(trainSample.getNumberTraits(), obj.getNumberTraits())});
        }

        distances.sort(dc);

        for (int i = 0; i < k; i++) {
            Place place = ((Traits)distances.get(i)[0]).getPlace();
            placesCounter.incrementFor(place);
        }

        return placesCounter.getMax();
    }

    public void classifyTestSet() {
        
    }


    public static void main(String[] args) throws Exception {

        List<Traits> data = new ArrayList<>();

        for (int i = 1; i < 200; i++) {
            Traits vector = new Traits("a", "b",i,i,i,i,i,i,i,i, Place.WEST_GERMANY);
            data.add(vector);
        }


        Knn knn = new Knn(data, 4, 30, new EuclidianMetric());

    }
}


class DistancesComparator implements Comparator<Object[]> {

    @Override
    public int compare(Object[] o1, Object[] o2) {
        return Double.compare((double) o1[1], (double) o2[1]);
    }
}

class PlacesCounter {
    private final int[] counters;

    public PlacesCounter() {
        counters = new int[Place.getPlacesAmount()];
        reset();
    }

    public void reset() {
        Arrays.fill(counters, 0);
    }

    public void incrementFor(Place place) {
        counters[place.getValue()]++;
    }

    public Place getMax() {
        int max = counters[0];
        int index = 0;

        for (int i = 1; i < counters.length; i++) {
            if (counters[i] > max) {
                max = counters[i];
                index = i;
            }
        }

        return Place.getPlaceFromInt(index);
    }
}