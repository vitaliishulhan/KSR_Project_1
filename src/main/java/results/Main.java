package results;

import extraction.Place;
import extraction.TraitExctractor;
import extraction.Traits;
import knn.Knn;
import knn.analyzer.Assignments;
import knn.metrics.CzebyszewMetric;
import knn.metrics.EuclideanMetric;
import knn.metrics.ManhattanMetric;
import knn.metrics.Metric;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static results.Histogram.saveHistogramAsPNG;

public class Main {
    private static void showTable(String dir, String title, JTable... tables) {
        JFrame window = new JFrame(title);
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        window.getContentPane().setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));
        window.pack();
        for (JTable table: tables) {
            table.getColumnModel().getColumn(0).setMaxWidth(220);
            table.getColumnModel().getColumn(0).setMinWidth(220);

            for (int i = 1; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(90);
            }

            table.setSize(220 + (table.getColumnCount() - 1)*90, 150);

            window.getContentPane().add(new JScrollPane(table));
        }
        Container c = window.getContentPane();
        int width = tables[0].getWidth();
        int height = tables[0].getHeight()*tables.length;

        c.setSize(width, height);
        window.setSize(width, height);

        BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
        c.paint(im.getGraphics());

        try {
            ImageIO.write(im, "PNG", new File("tables/" + dir + '/' + title.replaceAll(" ", "_") + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }

    private static double roundTo(double target, int decimalsNumber) {
        double exp = Math.pow(10, decimalsNumber);
        return Math.round(target * exp) / exp;
    }

    private static void compareResultsFor10DifferentKValues(Knn knn) throws IOException {
        final int[] ks = {1,2,4,5,6,8,10,15,20,30};
        final Metric[] metrics = {
                new EuclideanMetric(),
                new ManhattanMetric(),
                new CzebyszewMetric()
        };

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
                knn.setSetRelation(40);
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
                assignments.calculateFor(knn.getAssignedTestSet());

                for (Map.Entry<Place, Double[]> entry: assignments.getAll().entrySet()) {
                    DefaultCategoryDataset[] dataset = datasets[entry.getKey().getValue()];
                    Double[] params = entry.getValue();

                    precisionData[entry.getKey().getValue()][j][i + 1] = String.valueOf(roundTo(params[1]*100, 2));
                    recallData[entry.getKey().getValue()][j][i + 1] = String.valueOf(roundTo(params[2]*100, 2));
                    F1Data[entry.getKey().getValue()][j][i + 1] = String.valueOf(roundTo(params[3]*100, 2));

                    for(int w = 0; w < params.length - 1; w++) {
                        dataset[w].addValue(roundTo(params[w+1]*100, 2), metrics[j].getName(), String.valueOf(ks[i]));
                    }
                }

                accuracyDataset.addValue(roundTo(assignments.getAccuracy()*100,2), metrics[j].getName(), String.valueOf(ks[i]));
                accuracyData[j][i + 1] = String.valueOf(roundTo(assignments.getAccuracy()*100, 2));
            }
        }

        String xLabel = "Liczba k najbliższych sąsiadów";

        saveHistogramAsPNG("1", "Różne k. Accuracy", xLabel, "Accuracy [%]", accuracyDataset);

        Place[] places = Arrays.copyOfRange(Place.values(), 0, Place.getPlacesAmount() - 1);

        for (Place place: places) {
            int placeInt = place.getValue();
            DefaultCategoryDataset[] placeDataset = datasets[placeInt];

            saveHistogramAsPNG("1", "Różne k. Precision dla " + place, xLabel, "Precision [%]", placeDataset[0]);
            saveHistogramAsPNG("1", "Różne k. Recall dla " + place, xLabel, "Recall [%]", placeDataset[1]);
            saveHistogramAsPNG("1", "Różne k. F1 dla " + place, xLabel, "F1 [%]", placeDataset[2]);
        }

        String[] column = {"Metryka", "1", "2", "4", "5", "6", "8", "10", "15", "20", "30"};

        showTable("1", "Różne k. Accuracy", new JTable(accuracyData, column));

        for(Place place: places) {
            showTable(
                    "1", "Różne k. " + place,
                    new JTable(precisionData[place.getValue()], column),
                    new JTable(recallData[place.getValue()], column),
                    new JTable(F1Data[place.getValue()], column)
            );
        }
    }

    private static void getResultsFor5DifferentSetRelations(Knn knn) throws IOException {
        final int[] trainSetRelations = {40, 50, 60, 70, 80};
        final Metric[] metrics = {
                new EuclideanMetric(),
                new ManhattanMetric(),
                new CzebyszewMetric()
        };

        knn.setK(5);

        DefaultCategoryDataset[][] datasets = new DefaultCategoryDataset[Place.getPlacesAmount() - 1][3];
        DefaultCategoryDataset accuracyDataset = new DefaultCategoryDataset();
        String[][] accuracyData = new String[metrics.length][trainSetRelations.length + 1];
        String[][][] precisionData = new String[Place.getPlacesAmount() - 1][metrics.length][trainSetRelations.length + 1];
        String[][][] recallData = new String[Place.getPlacesAmount() - 1][metrics.length][trainSetRelations.length + 1];
        String[][][] F1Data = new String[Place.getPlacesAmount() - 1][metrics.length][trainSetRelations.length + 1];

        for (int i = 0; i < datasets.length; i++) {
            for(int j = 0; j < datasets[i].length; j++) {
                datasets[i][j] = new DefaultCategoryDataset();
            }
        }

        for (int i = 0; i < trainSetRelations.length; i++) {
            for (int j = 0; j < metrics.length; j++) {
                knn.setSetRelation(trainSetRelations[i]);
                knn.setMetric(metrics[j]);
                knn.classifyTestSet();

                accuracyData[j][0] = metrics[j].getName();

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
                assignments.calculateFor(knn.getAssignedTestSet());

                final String columnKey = trainSetRelations[i] + "/" + (100 - trainSetRelations[i]);

                accuracyDataset.addValue(assignments.getAccuracy(), metrics[j].getName(), columnKey);
                accuracyData[j][i + 1] = String.valueOf(roundTo(assignments.getAccuracy()*100, 2));

                for (Map.Entry<Place, Double[]> entry: assignments.getAll().entrySet()) {
                    DefaultCategoryDataset[] dataset = datasets[entry.getKey().getValue()];
                    Double[] params = entry.getValue();

                    precisionData[entry.getKey().getValue()][j][i + 1] = String.valueOf(roundTo(params[1]*100, 2));
                    recallData[entry.getKey().getValue()][j][i + 1] = String.valueOf(roundTo(params[2]*100, 2));
                    F1Data[entry.getKey().getValue()][j][i + 1] = String.valueOf(roundTo(params[3]*100, 2));

                    for(int w = 0; w < params.length - 1; w++) {
                        dataset[w].addValue(roundTo(params[w+1]*100, 2), metrics[j].getName(), columnKey);
                    }
                }
            }
        }

        String[] column = {"Metryka", "40/60", "50/50", "60/40", "70/30", "80/20"};

        saveHistogramAsPNG("2", "Różne proporcje zbiorów. Accuracy", "Proporcja zbioru uczącego [%]", "Accuracy [%]", accuracyDataset);

        Place[] places = Arrays.copyOfRange(Place.values(), 0, Place.getPlacesAmount() - 1);

        String xLabel = "Relacje";
        for (Place place: places) {
            int placeInt = place.getValue();
            DefaultCategoryDataset[] placeDataset = datasets[placeInt];

            saveHistogramAsPNG("2", "Różne proporcje zbiorów. Precision dla " + place, xLabel, "Precision [%]", placeDataset[0]);
            saveHistogramAsPNG("2", "Różne proporcje zbiorów. Recall dla " + place, xLabel, "Recall [%]", placeDataset[1]);
            saveHistogramAsPNG("2", "Różne proporcje zbiorów. F1 dla " + place, xLabel, "F1 [%]", placeDataset[2]);
        }

        showTable("2", "Różne relacje zbiorów. Accuracy", new JTable(accuracyData, column));

        for(Place place: places) {
            showTable(
                    "2", "Różne relacje zbiorów. " + place,
                    new JTable(precisionData[place.getValue()], column),
                    new JTable(recallData[place.getValue()], column),
                    new JTable(F1Data[place.getValue()], column));
        }
    }

    private static void getResultsForDifferentMetrics(Knn knn) throws IOException {
        final Metric[] metrics = {
                new EuclideanMetric(),
                new ManhattanMetric(),
                new CzebyszewMetric()
        };

        knn.setK(5);

        DefaultCategoryDataset[][] datasets = new DefaultCategoryDataset[Place.getPlacesAmount() - 1][3];
        DefaultCategoryDataset accuracyDataset = new DefaultCategoryDataset();
        String[][] accuracyData = new String[3][2];
        String[][][] precisionData = new String[Place.getPlacesAmount() - 1][metrics.length][2];
        String[][][] recallData = new String[Place.getPlacesAmount() - 1][metrics.length][2];
        String[][][] F1Data = new String[Place.getPlacesAmount() - 1][metrics.length][2];

        for (int i = 0; i < datasets.length; i++) {
            for (int j = 0; j < datasets[i].length; j++) {
                datasets[i][j] = new DefaultCategoryDataset();
            }
        }

        for (int i = 0; i < metrics.length; i++) {
            knn.setSetRelation(40);
            knn.setMetric(metrics[i]);
            knn.classifyTestSet();

            accuracyData[i][0] = metrics[i].getName();

            String metricName = metrics[i].getName();

            for (String[][] placeData: precisionData) {
                placeData[i][0] = metricName;
            }
            for (String[][] placeData: recallData) {
                placeData[i][0] = metricName;
            }
            for (String[][] placeData: F1Data) {
                placeData[i][0] = metricName;
            }

            Assignments assignments = new Assignments();
            assignments.calculateFor(knn.getAssignedTestSet());

            accuracyDataset.addValue(assignments.getAccuracy(), metrics[i].getName(), "");
            accuracyData[i][1] = String.valueOf(roundTo(assignments.getAccuracy()*100, 2));

            for (Map.Entry<Place, Double[]> entry: assignments.getAll().entrySet()) {
                DefaultCategoryDataset[] dataset = datasets[entry.getKey().getValue()];
                Double[] params = entry.getValue();

                precisionData[entry.getKey().getValue()][i][1] = String.valueOf(roundTo(params[1]*100, 2));
                recallData[entry.getKey().getValue()][i][1] = String.valueOf(roundTo(params[2]*100, 2));
                F1Data[entry.getKey().getValue()][i][1] = String.valueOf(roundTo(params[3]*100, 2));

                for (int w = 0; w < params.length - 1; w++) {
                    dataset[w].addValue(roundTo(params[w + 1]*100, 2),metrics[i].getName(),"");
                }
            }
        }

        saveHistogramAsPNG("3", "Różne metryki. Accuracy", "Metryki", "Accuracy [%]", accuracyDataset);

        Place[] places = Arrays.copyOfRange(Place.values(), 0, Place.getPlacesAmount() - 1);

        String xLabel = "Metryki";
        for (Place place: places) {
            int placeInt = place.getValue();
            DefaultCategoryDataset[] placeDataset = datasets[placeInt];

            saveHistogramAsPNG("3", "Różne metryki. Precision dla " + place, xLabel, "Precision [%]", placeDataset[0]);
            saveHistogramAsPNG("3", "Różne metryki. Recall dla " + place, xLabel, "Recall [%]", placeDataset[1]);
            saveHistogramAsPNG("3","Różne metryki. F1 dla " + place, xLabel, "F1 [%]", placeDataset[2]);
        }


        String[] column = {"Metryka/miara", "Accuracy"};


        JTable table = new JTable(accuracyData, column);
        showTable("3","Różne metryki. Accuracy", table);

        for (Place place: places) {
            showTable(
                    "3","Różne metryki. " + place,
                    new JTable(precisionData[place.getValue()], new String[] {"Metryka/miara", "Precision"}),
                    new JTable(recallData[place.getValue()], new String[] {"Metryka/miara", "Recall"}),
                    new JTable(F1Data[place.getValue()], new String[] {"Metryka/miara", "F1"}));
        }
    }

    private static void getParamsFor4DifferentTraitsFilters(Knn knn) throws IOException {
        final boolean[][] filters = {
                {true,true,true,true,true,true,true,true,false,false}, // without text traits
                {false,true,false,true,true,true,true,true,false,false}, // without text traits, avg word length
                {false,true,false,false,true,false,true,true,false,false},
                {true,false,true,false,false,false,false,false,true,true}
        };
        final Metric[] metrics = {
                new EuclideanMetric(),
                new CzebyszewMetric(),
                new ManhattanMetric()
        };

        knn.setK(5);


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
                knn.setSetRelation(40);
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
                assignments.calculateFor(knn.getAssignedTestSet());

                accuracyDataSet.addValue(assignments.getAccuracy(), metrics[j].getName(), String.valueOf(i));
                accuracyData[j][i + 1] = String.valueOf(roundTo(assignments.getAccuracy()*100, 2));

                for (Map.Entry<Place, Double[]> entry: assignments.getAll().entrySet()) {
                    DefaultCategoryDataset[] dataset = datasets[entry.getKey().getValue()];
                    Double[] params = entry.getValue();

                    precisionData[entry.getKey().getValue()][j][i + 1] = String.valueOf(roundTo(params[1]*100, 2));
                    recallData[entry.getKey().getValue()][j][i + 1] = String.valueOf(roundTo(params[2]*100, 2));
                    F1Data[entry.getKey().getValue()][j][i + 1] = String.valueOf(roundTo(params[3]*100, 2));

                    for(int w = 0; w < params.length - 1; w++) {
                        dataset[w].addValue(Math.round(params[w+1]*10000) / 100.0, metrics[j].getName(), String.valueOf(i));
                    }
                }
            }
        }

        String xLabel = "Filters";

        saveHistogramAsPNG("4", "Filters. Accuracy", xLabel, "Accuracy [%]", accuracyDataSet);

        Place[] places = Arrays.copyOfRange(Place.values(), 0, Place.getPlacesAmount() - 1);

        for (Place place: places) {
            int placeInt = place.getValue();
            DefaultCategoryDataset[] placeDataset = datasets[placeInt];

            saveHistogramAsPNG("4", "Filters. Precision dla " + place, xLabel, "Precision [%]", placeDataset[0]);
            saveHistogramAsPNG("4", "Filters. Recall dla " + place, xLabel, "Recall [%]", placeDataset[1]);
            saveHistogramAsPNG("4", "Filters. F1 dla " + place, xLabel, "F1 [%]", placeDataset[2]);
        }

        String[] column = {"Metryka", "1", "2", "3", "4"};

        showTable("4", "Różne zbiory cech. Accuracy", new JTable(accuracyData, column));

        for(Place place: places) {
            showTable(
                    "4", "Różne zbiory cech. " + place,
                    new JTable(precisionData[place.getValue()], column),
                    new JTable(recallData[place.getValue()], column),
                    new JTable(F1Data[place.getValue()], column));
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

//        ArrayList<Traits> filteredData = new ArrayList<>();
//        int usaCounter = 0;
//
//        for (Traits sample: data) {
//            if (sample.getPlace() == Place.USA) {
//                if (usaCounter < 940) {
//                    filteredData.add(sample);
//                    usaCounter++;
//                }
//            } else {
//                filteredData.add(sample);
//            }
//        }
//
//        Collections.shuffle(filteredData);

        knn.setData(data);


        System.out.println("Making graphs...");

        System.out.println("Experyment #1");
        compareResultsFor10DifferentKValues(knn);

//        System.out.println("Experyment #2");
//        getResultsFor5DifferentSetRelations(knn);
//
//        System.out.println("Experyment #3");
//        getResultsForDifferentMetrics(knn);
//
//        System.out.println("Experyment #4");
//        getParamsFor4DifferentTraitsFilters(knn);

    }
}
