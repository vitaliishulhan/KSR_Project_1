package knn.analyzer;

import extraction.Place;
import extraction.Traits;

import java.util.HashMap;
import java.util.Map.Entry;


/**
 * Repo of all assignment, i.e. for each country from Place enum
 *
 * @see Place
 */
public class Assignments {
    /**
     * Table of assignments
     */
    private final Assignment[] assignments = new Assignment[Place.getPlacesAmount() - 1];

    /**
     * A single calculator. Initializes assignments table
     */
    public Assignments() {
        for(int i = 0; i < assignments.length; i++) {
            assignments[i] = new Assignment(Place.getPlaceFromInt(i));
        }
    }

    /**
     * Delegates calculating results analysing to each assignment from table
     * @param assignedSet result from Knn algorithm
     *
     * @see knn.Knn
     */
    public void calculateFor(HashMap<Traits, Place> assignedSet) {
        for (Assignment assignment: assignments) {
            assignment.calculateFor(assignedSet);
        }
    }

    /**
     * get accuracy from assignment for the given country
     * @param place country representation
     * @return accuracy for the given country
     */
    public double getAccuracyFor(final Place place) {
        return assignments[place.getValue()].getAccuracy();
    }

    /**
     * get precision from assignment for the given country
     * @param place country representation
     * @return precision for the given country
     */
    public double getPrecisionFor(final Place place) {
        return assignments[place.getValue()].getPresicion();
    }

    /**
     * get recall from assignment for the given country
     * @param place country representation
     * @return recall for the given country
     */
    public double getRecallFor(final Place place) {
        return assignments[place.getValue()].getRecall();
    }

    /**
     * get F1 from assignment for the given country
     * @param place country representation
     * @return F1 for the given country
     */
    public double getF1For(final Place place) {
        return assignments[place.getValue()].getF1();
    }

    /**
     * Get all information about results analyzing, i.e. all assignments, i.e. all country
     * @return dictionary with all information (country -- information)
     */
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


/**
 * Class for calculating result analyzing for particular country
 */
class Assignment {
    /**
     * Country which analyzing will be performed for
     */
    private final Place place;

    /**
     * True positive counter
     */
    private int tp = 0;

    /**
     * False positive counter
     */
    private int fp = 0;

    /**
     * True negative counter
     */
    private int tn = 0;

    /**
     * False negative counter
     */
    private int fn = 0;

    /**
     * A single constructor, which takes place that this assignment will be responsible for
     * @param place country which assignment will be responsible for
     */
    public Assignment(final Place place) {
        this.place = place;
    }


    /**
     * Counts true/false positive and true/false negative
     * @param assignedSet result from Knn algorithm
     *
     * @see knn.Knn
     */
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
                    // else, it's false negative
                    fn++;
                }
                // real class is negative
            } else {
                // assigned class is positive
                if (assignedPlace == place) {
                    // so, it's false negative
                    fp++;
                } else {
                    // else, it's true negative
                    tn++;
                }
            }
        }
        System.out.println("TP: " + tp);
        System.out.println("FP: " + fp);
        System.out.println("FN: " + fn);
        System.out.println("TN: " + tn);
    }

    /**
     * Returns country of this assignment
     * @return country
     */
    public Place getPlace() {
        return place;
    }

    /**
     * Returns accuracy that is calculated following:
     *
     * accuracy = (tp + tn) / (tp + tn + fn + fp)
     * @return accuracy for given country
     */
    public double getAccuracy() {
        return 1.0 * (tp + tn) / (tp + tn + fn + fp);
    }


    /**
     * Returns precision that is calculated following:
     *
     * precision = tp / (tp + fp)
     * @return precision for given country
     */
    public double getPresicion() {
        if (tp == 0) return 0;

        return 1.0 * tp / (tp + fp);
    }

    /**
     * Returns recall that is calculated following:
     *
     * recall = tp / (tp + fn)
     * @return recall for given country
     */
    public double getRecall() {
        if (tp == 0) return 0;

        return 1.0 * tp / (tp + fn);
    }

    /**
     * Returns F1 that is harmonic avg for precision and recall and it is calculated following:
     *
     * F1 = (2 * precision * recall) / (precision + recall)
     * @return F1 for given country
     */
    public double getF1() {
        double precision = getPresicion();
        double recall = getRecall();

        if (precision == 0 || recall == 0) return 0;

        return (2.0 * precision * recall) / (precision + recall);
    }

    public int[] getMistakesMatrix() {
        return new int[] {tp, tn, fp, fn};
    }
}