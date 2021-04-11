package knn;

import extraction.Place;

import java.util.Arrays;

/**
 * Data structure for counting how many articles of the particular country are
 *
 * @see Place
 */
public class PlacesCounter {
    /**
     * Table for counting of articles according to country
     */
    private final int[] counters;

    /**
     * A single constructor. Initializes counting table for country from Place enum
     *
     * @see Place
     */
    public PlacesCounter() {
        counters = new int[Place.getPlacesAmount() - 1];
        reset();
    }

    /**
     * Set all integers in the table to 0
     *
     */
    public void reset() {
        Arrays.fill(counters, 0);
    }

    /**
     * Increment counter of the given country
     *
     * @param place country representation
     */
    public void incrementFor(Place place) {
        counters[place.getValue()]++;
    }

    /**
     * Returns place, which has the biggest counter
     *
     * @return country with the biggest counter
     */
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

        return res.delete(res.length()-2, res.length()).append("}").toString();
    }
}
