import extraction.Place;
import extraction.TraitExctractor;
import extraction.Traits;
import gui.GUI;
import knn.Knn;
import knn.PlacesCounter;
import knn.analyzer.Assignments;
import knn.metrics.CzebyszewMetric;
import knn.metrics.EuclidianMetric;
import knn.metrics.ManhattanMetric;
import knn.metrics.Metric;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    private final GUI gui;
    private final Knn knn;

    private final EuclidianMetric EUCLIDIAN_METRIC = new EuclidianMetric();
    private final ManhattanMetric MANHATTAN_METRIC = new ManhattanMetric();
    private final CzebyszewMetric CZEBYSHEW_METRIC = new CzebyszewMetric();

    public Main(GUI gui) {
        this.gui = gui;
        this.gui.setVisible(true);
        this.gui.loadButton.addActionListener(e -> new Thread(this::getData).start());
        this.gui.submitButton.addActionListener(e -> new Thread(this::generate).start());
        this.gui.submitButton.setEnabled(false);
        knn = new Knn();
    }

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
                gui.println(e);
            }
        }

        executor.shutdown();

        Collections.shuffle(data);

        PlacesCounter placesCounter = new PlacesCounter();

        for (Traits sample: data) {
            placesCounter.incrementFor(sample.getPlace());
        }

        gui.println("Number of articles according to country:");
        gui.println(placesCounter);
        knn.setData(data);
        gui.submitButton.setEnabled(true);
    }

    private void generate() {
        int k = (int) gui.kValueSpinner.getValue();
        int trainSetRelation = (int) gui.trainSetRelationSpinner.getValue();
        boolean[] filter = gui.getTraitsFilter();
        Metric metric;



        try {
            metric = convertMetricIndexFromGui();
            knn.setMetric(metric);
        } catch (IllegalStateException e) {
            gui.println("BŁĄD! Wybrana została metryka eukidesowa");
            gui.println(e);
            metric = EUCLIDIAN_METRIC;
        }

        knn.setK(k);
        knn.setSetRelation(trainSetRelation);
        knn.setFilter(filter);
        knn.setMetric(metric);

        gui.println("\nClassifying...");
        gui.println("k = " + k);
        gui.println("train set percentage = " + trainSetRelation + "%");
        gui.println("Metric: " + metric.getClass().getName().substring(12));
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
        Assignments assignments = new Assignments();

        gui.println("\nAnalyzing...");
        assignments.calculateFor(knn.getAssignedTextSet());

        gui.println("\nRESULTS:");
        for (Map.Entry<Place, Double[]> entry: assignments.getAll().entrySet()) {
            gui.println("Place: " + entry.getKey().toString());
            Double[] params = entry.getValue();
            gui.println("Accuracy: " + params[0]);
            gui.println("Precision: " + params[1]);
            gui.println("Recall: " + params[2]);
            gui.println("F1: " + params[3]);
        }
    }

    private Metric convertMetricIndexFromGui() {
        return switch (gui.metricComboBox.getSelectedIndex()) {
            case 0 -> EUCLIDIAN_METRIC;
            case 1 -> MANHATTAN_METRIC;
            case 2 -> CZEBYSHEW_METRIC;
            default -> throw new IllegalStateException("Unexpected value: " + gui.metricComboBox.getSelectedIndex());
        };
    }


    public static void main(String[] args) throws IOException {
        Main main = new Main(new GUI("KSR_LAB_PROJECT_No_1"));
    }

}
