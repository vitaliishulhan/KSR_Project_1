package knn;

import extraction.Place;
import extraction.Traits;
import knn.exceptions.FilterDoesNotFitException;
import knn.metrics.EuclidianMetric;
import knn.metrics.Metric;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Knn {

    private final PlacesCounter placesCounter;

    private Metric metric;

    private List<Traits> data;
    private List<Traits> trainSet;
    private List<Traits> testSet;
    private final HashMap<Traits, Place> assignedTextSet = new HashMap<>();
    private int k;
    private boolean[] filter;
    private int trainSetRelation;

    public void setSetRelation(int trainSetRelation) {
        this.trainSetRelation = trainSetRelation <= 0 || trainSetRelation >= 100 ? 50 : trainSetRelation;

        int divider = trainSetRelation <= 0 || trainSetRelation >= 100 ?
                data.size() / 2 :
                data.size() * trainSetRelation / 100;

        trainSet = data.subList(0, divider);
        testSet = data.subList(divider, data.size());
    }

    public void setData(final List<Traits> data) {
        this.data = data;

        setSetRelation(trainSetRelation);
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public void setK(int k) {
        this.k = k;
    }

    public Knn() {
        placesCounter = new PlacesCounter();
    }

    public void setFilter(boolean[] newFilter) {
        if (newFilter.length != 10) {
            setDefaultFilter();
        }
        else {
            filter = newFilter;
        }
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

        return placesCounter.getMax();
    }

    public void classifyTestSet() {
        assignedTextSet.clear();

        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<Callable<Object>> tasks = new ArrayList<>();

        for (Traits sample: testSet) {
            tasks.add(() -> assignedTextSet.put(sample, classify(sample)));
        }

        try {
            executor.invokeAll(tasks);
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public List<Traits> getTestSet() {
        return testSet;
    }

    public HashMap<Traits, Place> getAssignedTextSet() {
        return assignedTextSet;
    }
}
