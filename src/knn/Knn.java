package knn;

import extraction.Place;
import extraction.TraitExctractor;
import extraction.Traits;
import knn.exceptions.FilterDoesNotFitException;
import knn.metrics.EuclidianMetric;

import java.io.File;
import java.util.*;

public class Knn {

    private final PlacesCounter placesCounter;

    private final Metric metric;

    private final List<Traits> trainSet;
    private final List<Traits> testSet;
    private final List<Place> testSetAssignedPlaces = new ArrayList<>();
    private final int k;
    private boolean[] filter = new boolean[Traits.getTraitsAmount()];

    public Knn(final List<Traits> data, final int k, final int trainSetRelation, final Metric metric) {
        int divider = trainSetRelation <= 0 || trainSetRelation >= 100 ?
                data.size() / 2 :
                data.size() * trainSetRelation / 100;

        trainSet = data.subList(0, divider);
        testSet = data.subList(divider, data.size());
        this.k = k;

        this.metric = metric;
        placesCounter = new PlacesCounter();

        setDefaultFilter();
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

    private boolean isDefaultFilter() {
        for(boolean condition: filter) {
            if (!condition) {
                return false;
            }
        }
        return true;
    }

    private Place classify(Traits obj) {
        placesCounter.reset();

        List<Object[]> distances = new ArrayList<>();

        if(isDefaultFilter()) {
            for (Traits trainSample : trainSet) {
                distances.add(new Object[]{trainSample, metric.getDistance(trainSample.getNumberTraits(), obj.getNumberTraits(), trainSample.getTextTraits(), obj.getTextTraits())});
            }
        } else {
            boolean[] numberFilter = new boolean[obj.getNumberTraits().size()];

            System.arraycopy(filter, 0, numberFilter, 0, numberFilter.length);

            boolean[] textFilter = new boolean[obj.getTextTraits().size()];

            System.arraycopy(filter, obj.getNumberTraits().size(), textFilter, 0, textFilter.length);

            for (Traits trainSample : trainSet) {
                distances.add(new Object[]{trainSample, metric.getDistance(trainSample.getNumberTraits(), obj.getNumberTraits(),  trainSample.getTextTraits(), obj.getTextTraits(), numberFilter, textFilter)});
            }
        }

        distances.sort(Comparator.comparingDouble(o -> (double) o[1]));

        for (int i = 0; i < k; i++) {
            Place place = ((Traits)distances.get(i)[0]).getPlace();
            placesCounter.incrementFor(place);
        }

        System.out.println(placesCounter);

        return placesCounter.getMax();
    }

    public void classifyTestSet() {
        testSetAssignedPlaces.clear();

        for (Traits sample: testSet) {
            testSetAssignedPlaces.add(classify(sample));
        }
    }

    public List<Traits> getTestSet() {
        return testSet;
    }

    public List<Place> getTestSetAssignedPlaces() {
        return testSetAssignedPlaces;
    }
}
