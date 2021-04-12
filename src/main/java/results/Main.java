package results;

import extraction.Place;
import extraction.TraitExctractor;
import extraction.Traits;
import knn.Knn;
import knn.PlacesCounter;
import knn.analyzer.Assignments;
import knn.metrics.CzebyszewMetric;
import knn.metrics.EuclideanMetric;
import knn.metrics.ManhattanMetric;
import knn.metrics.Metric;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static results.Histogram.saveHistogramAsPNG;

public class Main {
    private static void showTable(String title, JTable table) {
        JFrame window = new JFrame(title);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(1280, 720);
        window.add(new JScrollPane(table));
        window.setVisible(true);
    }

    private static void compareResultsFor10DifferentKValuesHistograms(Knn knn) throws IOException {
        final int[] ks = {1,2,4,5,6,8,10,15,20,30};
        final Metric[] metrics = {
                new EuclideanMetric(),
                new ManhattanMetric(),
                new CzebyszewMetric()
        };

        knn.setSetRelation(60);

        DefaultCategoryDataset[][] datasets = new DefaultCategoryDataset[Place.getPlacesAmount() - 1][3];
        DefaultCategoryDataset accuracyDataset = new DefaultCategoryDataset();

        String[][] accuracyData = new String[metrics.length][ks.length + 1];
        String[][][] precisionData = new String[Place.getPlacesAmount() - 1][metrics.length][ks.length + 1];
        String[][][] recallData = new String[Place.getPlacesAmount() - 1][metrics.length][ks.length + 1];
        String[][][] F1Data = new String[Place.getPlacesAmount() - 1][metrics.length][ks.length + 1];


        for (int i = 0; i < datasets.length; i++) {
            for(int j = 0; j < datasets[i].length; j++) {
                datasets[i][j] = new DefaultCategoryDataset();
            }
        }

        for (int i = 0; i < ks.length; i++) {
            knn.setK(ks[i]);

            for (int j = 0; j < metrics.length; j++) {
                knn.setMetric(metrics[j]);
                knn.classifyTestSet();

                String metricName = metrics[j].getName();

                accuracyData[j][0] = metricName;

                for (String[][] placeData: precisionData) {
                    placeData[j][0] = metricName;
                }
                for (String[][] placeData: recallData) {
                    placeData[j][0] = metricName;
                }
                for (String[][] placeData: F1Data) {
                    placeData[j][0] = metricName;
                }

                Assignments assignments = new Assignments();
                assignments.calculateFor(knn.getAssignedTextSet());

                for (Map.Entry<Place, Double[]> entry: assignments.getAll().entrySet()) {
                    DefaultCategoryDataset[] dataset = datasets[entry.getKey().getValue()];
                    Double[] params = entry.getValue();

                    precisionData[entry.getKey().getValue()][j][i + 1] = String.valueOf(params[1]);
                    recallData[entry.getKey().getValue()][j][i + 1] = String.valueOf(params[2]);
                    F1Data[entry.getKey().getValue()][j][i + 1] = String.valueOf(params[3]);

                    for(int w = 0; w < params.length - 1; w++) {
                        dataset[w].addValue(Math.round(params[w+1]*10000) / 100.0, metrics[j].getName(), String.valueOf(ks[i]));
                    }
                }

                accuracyDataset.addValue(Math.round(assignments.getAccuracy() * 10000) / 100.0, metrics[j].getName(), String.valueOf(ks[i]));
                accuracyData[j][i + 1] = String.valueOf(assignments.getAccuracy());
            }
        }

        String xLabel = "Liczba k najbliższych sąsiadów";

        saveHistogramAsPNG("Accuracy", xLabel, "Accuracy [%]", accuracyDataset);

        Place[] places = Arrays.copyOfRange(Place.values(), 0, Place.getPlacesAmount() - 1);

        for (Place place: places) {
            int placeInt = place.getValue();
            DefaultCategoryDataset[] placeDataset = datasets[placeInt];

            saveHistogramAsPNG("Precision dla " + place, xLabel, "Precision [%]", placeDataset[0]);
            saveHistogramAsPNG("Recall dla " + place, xLabel, "Recall [%]", placeDataset[1]);
            saveHistogramAsPNG("F1 dla " + place, xLabel, "F1 [%]", placeDataset[2]);
        }

        String[] column = {"Metryka", "1", "2", "4", "5", "6", "8", "10", "15", "20", "30"};

        showTable("Accuracy", new JTable(accuracyData, column));

        for(Place place: places) {
            showTable("Precision dla " + place, new JTable(precisionData[place.getValue()], column));
            showTable("Recall dla " + place, new JTable(recallData[place.getValue()], column));
            showTable("F1 dla " + place, new JTable(F1Data[place.getValue()], column));
        }
    }

    private static void getAccuracyFor5DifferentSetRelationsHistograms(Knn knn) throws IOException {
        final int[] trainSetRelations = {40, 50, 60, 70, 80};
        final Metric[] metrics = {
                new EuclideanMetric(),
                new ManhattanMetric(),
                new CzebyszewMetric()
        };

        knn.setK(5);

        DefaultCategoryDataset accuracyDataset = new DefaultCategoryDataset();
        String[][] data = new String[metrics.length][trainSetRelations.length + 1];

        for (int i = 0; i < trainSetRelations.length; i++) {
            knn.setSetRelation(trainSetRelations[i]);

            for (int j = 0; j < metrics.length; j++) {
                knn.setMetric(metrics[j]);
                knn.classifyTestSet();

                data[j][0] = metrics[j].getName();

                Assignments assignments = new Assignments();
                assignments.calculateFor(knn.getAssignedTextSet());

                accuracyDataset.addValue(assignments.getAccuracy(), metrics[j].getName(), String.valueOf(trainSetRelations[i]));
                data[j][i + 1] = String.valueOf(assignments.getAccuracy());
            }
        }

        String[] column = {"Metryka", "40/60", "50/50", "60/40", "70/30", "80/20"};

        JTable table = new JTable(data, column);
        showTable("Accuracy dla różnych proporcji zbiorów", table);

        saveHistogramAsPNG("Accuracy dla różnych proporcji zbiorów", "Proporcja zbioru uczącego [%]", "Accuracy [%]", accuracyDataset);
    }

    private static void getAccuracyForDifferentMetrics(Knn knn) throws IOException {
        final Metric[] metrics = {
                new EuclideanMetric(),
                new ManhattanMetric(),
                new CzebyszewMetric()
        };

        knn.setK(7);
        knn.setSetRelation(60);

        DefaultCategoryDataset accuracyDataset = new DefaultCategoryDataset();
        String[][] data = new String[3][2];

        for (int i = 0; i < metrics.length; i++) {
            knn.setMetric(metrics[i]);
            knn.classifyTestSet();

            data[i][0] = metrics[i].getName();

            Assignments assignments = new Assignments();
            assignments.calculateFor(knn.getAssignedTextSet());

            accuracyDataset.addValue(assignments.getAccuracy(), metrics[i].getName(), "");
            data[i][1] = String.valueOf(assignments.getAccuracy());
        }

        saveHistogramAsPNG("Accuracy dla różnych metryk", "Metryki", "Accuracy [%]", accuracyDataset);

        String[] column = {"Metryka/miara", "Accuracy"};


        JTable table = new JTable(data, column);
        showTable("Accuracy dla różnych metryk", table);
    }

    private static void getParamsFor4DifferentTraitsFilters(Knn knn) throws IOException {
        final boolean[][] filters = {
                {true,true,true,true,true,true,true,true,false,false},
                {false,true,false,true,true,true,true,true,false,false},
                {false,true,false,false,true,false,true,true,false,false},
                {true,false,true,false,false,false,false,false,true,true}
        };
        final Metric[] metrics = {
                new EuclideanMetric(),
                new CzebyszewMetric(),
                new ManhattanMetric()
        };

        knn.setK(5);
        knn.setSetRelation(60);

        DefaultCategoryDataset accuracyDataSet = new DefaultCategoryDataset();
        DefaultCategoryDataset[][] datasets = new DefaultCategoryDataset[Place.getPlacesAmount() - 1][3];

        String[][] accuracyData = new String[metrics.length][filters.length + 1];
        String[][][] precisionData = new String[Place.getPlacesAmount() - 1][metrics.length][filters.length + 1];
        String[][][] recallData = new String[Place.getPlacesAmount() - 1][metrics.length][filters.length + 1];
        String[][][] F1Data = new String[Place.getPlacesAmount() - 1][metrics.length][filters.length + 1];

        for (int i = 0; i < datasets.length; i++) {
            for(int j = 0; j < datasets[i].length; j++) {
                datasets[i][j] = new DefaultCategoryDataset();
            }
        }

        for (int i = 0; i < filters.length; i++) {
            knn.setFilter(filters[i]);

            for (int j = 0; j < metrics.length; j++) {
                knn.setMetric(metrics[j]);
                knn.classifyTestSet();

                String metricName = metrics[j].getName();

                accuracyData[j][0] = metricName;

                for (String[][] placeData: precisionData) {
                    placeData[j][0] = metricName;
                }
                for (String[][] placeData: recallData) {
                    placeData[j][0] = metricName;
                }
                for (String[][] placeData: F1Data) {
                    placeData[j][0] = metricName;
                }

                Assignments assignments = new Assignments();
                assignments.calculateFor(knn.getAssignedTextSet());

                accuracyDataSet.addValue(assignments.getAccuracy(), metrics[j].getName(), String.valueOf(i));
                accuracyData[j][i + 1] = String.valueOf(assignments.getAccuracy());

                for (Map.Entry<Place, Double[]> entry: assignments.getAll().entrySet()) {
                    DefaultCategoryDataset[] dataset = datasets[entry.getKey().getValue()];
                    Double[] params = entry.getValue();

                    precisionData[entry.getKey().getValue()][j][i + 1] = String.valueOf(params[1]);
                    recallData[entry.getKey().getValue()][j][i + 1] = String.valueOf(params[2]);
                    F1Data[entry.getKey().getValue()][j][i + 1] = String.valueOf(params[3]);

                    for(int w = 0; w < params.length - 1; w++) {
                        dataset[w].addValue(Math.round(params[w+1]*10000) / 100.0, metrics[j].getName(), String.valueOf(i));
                    }
                }
            }
        }

        String xLabel = "Filters";

        saveHistogramAsPNG("Filters. Accuracy", xLabel, "Accuracy [%]", accuracyDataSet);

        Place[] places = Arrays.copyOfRange(Place.values(), 0, Place.getPlacesAmount() - 1);

        for (Place place: places) {
            int placeInt = place.getValue();
            DefaultCategoryDataset[] placeDataset = datasets[placeInt];

            saveHistogramAsPNG("Filters. Precision dla " + place, xLabel, "Precision [%]", placeDataset[0]);
            saveHistogramAsPNG("Filters. Recall dla " + place, xLabel, "Recall [%]", placeDataset[1]);
            saveHistogramAsPNG("Filters. F1 dla " + place, xLabel, "F1 [%]", placeDataset[2]);
        }

        String[] column = {"Metryka", "1", "2", "3", "4"};

        showTable("Różne zbiory cech. Accuracy", new JTable(accuracyData, column));

        for(Place place: places) {
            showTable("Różne zbiory cech. Precision dla " + place, new JTable(precisionData[place.getValue()], column));
            showTable("Różne zbiory cech. Recall dla " + place, new JTable(recallData[place.getValue()], column));
            showTable("Różne zbiory cech. F1 dla " + place, new JTable(F1Data[place.getValue()], column));
        }
    }

    public static void main(String[] args) throws IOException {
        Knn knn = new Knn();
        knn.setDefaultFilter();

        List<Traits> data = new ArrayList<>();

        System.out.println("Parsering...");
        File[] listOfFiles = new File("articles").listFiles();

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
            }
        }

        executor.shutdown();

        Collections.shuffle(data);

        ArrayList<Traits> filteredData = new ArrayList<>();
        int usaCounter = 0;

        for (Traits sample: data) {
            if (sample.getPlace() == Place.USA) {
                if (usaCounter < 940) {
                    filteredData.add(sample);
                    usaCounter++;
                }
            } else {
                filteredData.add(sample);
            }
        }

        Collections.shuffle(filteredData);

        knn.setData(filteredData);

        System.out.println("Making graphs...");

        compareResultsFor10DifferentKValuesHistograms(knn);
        getAccuracyFor5DifferentSetRelationsHistograms(knn);
        getAccuracyForDifferentMetrics(knn);
        getParamsFor4DifferentTraitsFilters(knn);
    }
}
