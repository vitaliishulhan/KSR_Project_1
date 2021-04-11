import extraction.Place;
import extraction.TraitExctractor;
import extraction.Traits;
import gui.GUI;
import knn.Knn;
import knn.PlacesCounter;
import knn.analyzer.Assignments;
import knn.metrics.CzebyszewMetric;
import knn.metrics.EuclideanMetric;
import knn.metrics.ManhattanMetric;
import knn.metrics.Metric;
import extraction.FileParser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Implements logic of the program, combines main.gui and algorithm
 *
 * @see FileParser
 * @see TraitExctractor
 * @see GUI
 */
public class Main {
    /**
     * GUI object
     */
    private final GUI gui;

    /**
     * Object of main.knn algorithm
     */
    private final Knn knn;

    /**
     * Constant object of metric for using by main.knn
     */
    private final Metric EUCLIDEAN_METRIC = new EuclideanMetric();
    /**
     * Constant object of metric for using by main.knn
     */
    private final Metric MANHATTAN_METRIC = new ManhattanMetric();
    /**
     * Constant object of metric for using by main.knn
     */
    private final Metric CZEBYSHEW_METRIC = new CzebyszewMetric();


    /**
     * A single constructor, initializes main.gui object and listener for load and submit buttons
     * @param gui GUI object for graphical representing of program
     */
    public Main(GUI gui) {
        this.gui = gui;
        this.gui.setVisible(true);
        this.gui.loadButton.addActionListener(e -> new Thread(this::getData).start());
        this.gui.submitButton.addActionListener(e -> new Thread(this::generate).start());
        this.gui.submitButton.setEnabled(false);
        knn = new Knn();
    }

    /**
     * Gets data from articles folder and send it to main.knn
     */
    private void getData() {
        List<Traits> data = new ArrayList<>();

        File[] listOfFiles = new File("articles").listFiles();

        gui.println("File parsering and traits extracting...");

        assert listOfFiles != null;

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<List<Traits>>> list = new ArrayList<>();

        for (File file: listOfFiles) {

            list.add(executor.submit(() -> {
                List<Traits> res =  Objects.requireNonNull(TraitExctractor.getTraitsVectorFor("articles/" + file.getName()));
                return res;
            }));
        }

        for (Future<List<Traits>> fut: list) {
            try {
                data.addAll(fut.get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                gui.println(e);
            }
        }

        executor.shutdown();


        Collections.shuffle(data);

        PlacesCounter placesCounter = new PlacesCounter();

        ArrayList<Traits> filteredData = new ArrayList<>();
        int usaCounter = 0;

        for (Traits sample: data) {
            if (sample.getPlace() == Place.USA) {
                if (usaCounter < 2000) {
                    filteredData.add(sample);
                    usaCounter++;
                }
            } else {
                filteredData.add(sample);
            }
        }

        for (Traits sample: filteredData) {
            placesCounter.incrementFor(sample.getPlace());
        }

        gui.println("Number of articles according to country:");
        gui.println(placesCounter);

        Collections.shuffle(filteredData);

        knn.setData(filteredData);
        gui.submitButton.setEnabled(true);
    }

    /**
     * Takes parameters from corresponding text fields, set them into main.knn algorithm and execute classification
     * Output will be showed in the corresponding text area.
     */
    private void generate() {
        //Take params
        int k = (int) gui.kValueSpinner.getValue();
        int trainSetRelation = (int) gui.trainSetRelationSpinner.getValue();
        boolean[] filter = gui.getTraitsFilter();
        Metric metric;

        // try to convert metric into ojbect
        try {
            metric = convertMetricIndexFromGui();
            knn.setMetric(metric);
        } catch (IllegalStateException e) {
            // else take euclidean metric
            gui.println("BŁĄD! Wybrana została metryka eukidesowa");
            gui.println(e);
            metric = EUCLIDEAN_METRIC;
        }

        // Set params
        knn.setK(k);
        knn.setSetRelation(trainSetRelation);
        knn.setFilter(filter);
        knn.setMetric(metric);

        // Send information about classifying params to output
        gui.println("\n~~~Classifying...");
        gui.println("PARAMETERS:");
        gui.println("k = " + k);
        gui.println("train set percentage = " + trainSetRelation + "%");
        gui.println("Metric: " + metric.getClass().getName().substring(17));
        gui.println("Filter:");
        gui.println("avg word length: " + filter[0]);
        gui.println("words with first big letter: " + filter[1]);
        gui.println("digits : " + filter[2]);
        gui.println("punctuation marks: " + filter[3]);
        gui.println("words amount: " + filter[4]);
        gui.println("words with max 4 letters: " + filter[5]);
        gui.println("words with min 11 letters: " + filter[6]);
        gui.println("words with only large letter: " + filter[7]);
        gui.println("the most common letter: " + filter[8]);
        gui.println("the least common letter: " + filter[9]);

        knn.classifyTestSet();

        // Execute analyzing algorithm
        Assignments assignments = new Assignments();
        gui.println("\n~~~Analyzing...");

        assignments.calculateFor(knn.getAssignedTextSet());

        //Show results
        gui.println("\n~~~RESULTS:");
        for (Map.Entry<Place, Double[]> entry: assignments.getAll().entrySet()) {
            gui.println("Place: " + entry.getKey().toString());
            Double[] params = entry.getValue();
            gui.println("Accuracy: " + params[0]);
            gui.println("Precision: " + params[1]);
            gui.println("Recall: " + params[2]);
            gui.println("F1: " + params[3]);
        }
    }

    /**
     * Converts active index in metric combobox into corresponding Metric object
     * @return Metric object
     *
     * @see knn.metrics.Metric
     */
    private Metric convertMetricIndexFromGui() {
        return switch (gui.metricComboBox.getSelectedIndex()) {
            case 0 -> EUCLIDEAN_METRIC;
            case 1 -> MANHATTAN_METRIC;
            case 2 -> CZEBYSHEW_METRIC;
            default -> throw new IllegalStateException("Unexpected value: " + gui.metricComboBox.getSelectedIndex());
        };
    }

    /**
     * Program start point
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Main main = new Main(new GUI("KSR_LAB_PROJECT_No_1"));
    }

}
