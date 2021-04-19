package knn;

import extraction.Place;
import extraction.Traits;
import knn.metrics.Metric;

import java.io.*;
import java.util.*;

public class Knn {

    /**
     * Counters for countries
     */
    private final PlacesCounter placesCounter;

    /**
     * Metric for distance calculating
     */
    private Metric metric;

    /**
     * Traits vector of all articles
     */
    private List<Traits> data;

    /**
     * Traits vector for training
     */
    private List<Traits> trainSet = new ArrayList<>();

    /**
     * Traits vector for testing
     */
    private List<Traits> testSet = new ArrayList<>();

    /**
     * The percentage of train set length relative to the whole data set
     */
    private int trainSetRelation = 0;

    /**
     * Dictionary with traits vector as a key and assigned place for it as a value
     */
    private final HashMap<Traits, Place> assignedTestSet = new HashMap<>();

    /**
     * The nearest neighbours amount
     */
    private int k;

    /**
     * Traits filter
     */
    private boolean[] filter = new boolean[Traits.getTraitsAmount()];

    /**
     * Divides data set to train and test ones according to the given train set percentage and store this percent.
     * If the diven relation is out of condition, it will be set on 50%
     * @param trainSetRelation new percentage of the train set
     */
    public void setSetRelation(int trainSetRelation) {
        this.trainSetRelation = trainSetRelation <= 0 || trainSetRelation >= 100 ? 50 : trainSetRelation;

        if (this.k != 0) {
            trainSet = new ArrayList<>();

            int divider = trainSetRelation <= 0 || trainSetRelation >= 100 ?
                    data.size() / 2 :
                    data.size() * trainSetRelation / 100;

            List<Traits> datacopy = new ArrayList<>(data);

            Iterator<Traits> it = datacopy.iterator();

            int[] countryCounter = new int[Place.getPlacesAmount() - 1];

            while (it.hasNext()) {

                Traits sample = it.next();

                Place place = sample.getPlace();

                if (countryCounter[place.getValue()] < k) {
                    trainSet.add(sample);
                    it.remove();
                    countryCounter[place.getValue()]++;
                }
            }

            it = datacopy.iterator();

            int trainSetSize = trainSet.size();

            while (it.hasNext() && trainSetSize++ < divider) {
                Traits sample = it.next();
                trainSet.add(sample);
                it.remove();
            }

            testSet = new ArrayList<>(datacopy);
        }
    }

    /**
     * Sets traits vector for research
     * @param data traits vector
     */
    public void setData(final List<Traits> data) {
        this.data = data;

        if (trainSetRelation != 0 && k != 0)
            setSetRelation(trainSetRelation);
    }

    /**
     * Changes metric used by algorithm
     * @param metric new metric
     */
    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    /**
     * Changes the nearest neighbours amount
     * @param k new the nearest neighbours amount
     */
    public void setK(int k) {
        this.k = k;
    }

    /**
     * A single constructor. Initializes counter
     */
    public Knn() {
        placesCounter = new PlacesCounter();
    }

    /**
     * Changes traits filter. If new filter is out of condition, default one will be set
     * @param newFilter new traits filter
     */
    public void setFilter(boolean[] newFilter) {
        if (newFilter.length != 10) {
            setDefaultFilter();
        }
        else {
            filter = newFilter;
        }
    }

    /**
     * Sets default filter, i.e. all traits are in scope
     */
    public void setDefaultFilter() {
        Arrays.fill(filter, true);
    }

    /**
     * Verifies if the current filter is default
     * @return if the current filter is default, then true
     */
    private boolean isDefaultFilter() {
        for(boolean condition: filter) {
            if (!condition) {
                return false;
            }
        }
        return true;
    }

    /**
     * Classifies single article to the country and store this infromation in the hash map
     *
     * @param obj traits vector representing article
     * @return country which the given article belongs to according to algorithm result
     */
    private Place classify(Traits obj) {
        //refresh counter
        placesCounter.reset();

        //list, which element is 2-element table
        // the first one is train Sample
        // the second value is distance between this sample and the given vector
        List<Object[]> distances = new ArrayList<>();

        // if there are no restrictions for traits...
        if(isDefaultFilter()) {
            for (Traits trainSample : trainSet) {
                //get distances between the given vector and each one from train set
                distances.add(new Object[]{trainSample, metric.getDistance(trainSample.getNumberTraits(), obj.getNumberTraits(), trainSample.getTextTraits(), obj.getTextTraits())});
            }
        } else {
            // else, take filter for the numerical traits...
            boolean[] numberFilter = new boolean[obj.getNumberTraits().size()];
            System.arraycopy(filter, 0, numberFilter, 0, numberFilter.length);

            // ... and for the text ones...
            boolean[] textFilter = new boolean[obj.getTextTraits().size()];
            System.arraycopy(filter, obj.getNumberTraits().size(), textFilter, 0, textFilter.length);

            // And get distances paying attention to filter
            for (Traits trainSample : trainSet) {
                distances.add(new Object[]{trainSample, metric.getDistance(trainSample.getNumberTraits(), obj.getNumberTraits(),  trainSample.getTextTraits(), obj.getTextTraits(), numberFilter, textFilter)});
            }
        }

        // sort distances ascending
        distances.sort(Comparator.comparingDouble(o -> (double) o[1]));

        // for the first k samples count their countries
        for (int i = 0; i < k; i++) {
            Place place = ((Traits) distances.get(i)[0]).getPlace();
            placesCounter.incrementFor(place);
        }

        // get result
        return placesCounter.getMax();
    }

    /**
     * Classifies the whole test set, using classify method
     *
     */
    public void classifyTestSet() {
        // clear previous results storing in the hash map
        assignedTestSet.clear();

        for (Traits sample: testSet) {
            Place place = classify(sample);
            assignedTestSet.put(sample, place);

            if (sample.getPlace() != Place.USA) {
                trainSet.add(sample);
            }
        }

        setSetRelation(trainSetRelation);
    }

    /**
     * Returns test set
     * @return test set
     */
    public List<Traits> getTestSet() {
        return testSet;
    }

    public List<Traits> getTrainSet() {
        return trainSet;
    }

    /**
     * Returns hash map, i.e. dictionary, with traits vectors and places assigned to them
     * @return hash map trait vector -- place
     */
    public HashMap<Traits, Place> getAssignedTestSet() {
        return assignedTestSet;
    }
}
