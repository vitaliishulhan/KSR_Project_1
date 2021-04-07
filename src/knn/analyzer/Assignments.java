package knn.analyzer;

import extraction.Place;
import extraction.Traits;

import java.util.HashMap;
import java.util.Map.Entry;

public class Assignments {
    private final Assignment[] assignments = new Assignment[Place.getPlacesAmount() - 1];

    public Assignments() {
        for(int i = 0; i < assignments.length; i++) {
            assignments[i] = new Assignment(Place.getPlaceFromInt(i));
        }
    }

    public void calculateFor(HashMap<Traits, Place> assignedSet) {
        for (Assignment assignment: assignments) {
            assignment.calculateFor(assignedSet);
        }
    }

    public double getAccuracyFor(final Place place) {
        return assignments[place.getValue()].getAccuracy();
    }

    public double getPrecisionFor(final Place place) {
        return assignments[place.getValue()].getPresicion();
    }

    public double getRecallFor(final Place place) {
        return assignments[place.getValue()].getRecall();
    }

    public double getF1For(final Place place) {
        return assignments[place.getValue()].getF1();
    }

    public HashMap<Place, Double[]> getAll() {
        HashMap<Place, Double[]> res = new HashMap<>();

        for (Assignment assignment: assignments) {
            Double[] params = new Double[4];

            params[0] = assignment.getAccuracy();
            params[1] = assignment.getPresicion();
            params[2] = assignment.getRecall();
            params[3] = assignment.getF1();

            res.put(assignment.getPlace(), params);
        }

        return res;
    }
}

class Assignment {
    private final Place place;
    private int tp = 0;
    private int fp = 0;
    private int tn = 0;
    private int fn = 0;

    public Assignment(final Place place) {
        this.place = place;
    }

    public void calculateFor(HashMap<Traits, Place> assignedSet) {
        for(Entry<Traits, Place> entry : assignedSet.entrySet()) {
            Place realPlace = entry.getKey().getPlace();
            Place assignedPlace = entry.getValue();

            // real class is positive
            if (realPlace == place) {
                // assigned class is positive
                if (assignedPlace == place) {
                    // so, it's true positive
                    tp++;
                } else {
                    // else, it's false positive
                    fp++;
                }
                // real class is negative
            } else {
                // assigned class is negative
                if (assignedPlace == place) {
                    // so, it's true negative
                    fn++;
                } else {
                    // else, it's false negative
                    tn++;
                }
            }
        }
    }

    public Place getPlace() {
        return place;
    }

    public double getAccuracy() {
        return 1.0 * (tp + tn) / (tp + tn + fn + fp);
    }

    public double getPresicion() {
        return 1.0 * tp / (tp + fp);
    }

    public double getRecall() {
        return 1.0 * tp / (tp + fn);
    }

    public double getF1() {
        double precision = getPresicion();
        double recall = getRecall();

        return (2.0 * precision * recall) / (precision + recall);
    }
}