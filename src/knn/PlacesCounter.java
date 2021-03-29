package knn;

import extraction.Place;

import java.util.Arrays;

public class PlacesCounter {
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

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("{");

        for (int i = 0; i < counters.length; i++) {
            res.append(Place.getPlaceFromInt(i)).append("=").append(counters[i]).append(", ");
        }

        return res.append("}").toString();
    }
}
