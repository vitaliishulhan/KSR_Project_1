import extraction.Place;
import extraction.TraitExctractor;
import extraction.Traits;
import knn.Knn;
import knn.PlacesCounter;
import knn.analyzer.Assignments;
import knn.metrics.EuclidianMetric;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws IOException {
        List<Traits> data = new ArrayList<>();

        File[] listOfFiles = new File("articles").listFiles();

        System.out.println("File parsering and traits extracting...");

        assert listOfFiles != null;

        ExecutorService executor = Executors.newFixedThreadPool(listOfFiles.length);
        List<Future<List<Traits>>> list = new ArrayList<>();

        for (File file: listOfFiles) {

            list.add(executor.submit(() -> {
                List<Traits> res =  Objects.requireNonNull(TraitExctractor.getTraitsVectorFor("articles/" + file.getName()));
                System.out.println(file.getName());
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

        PlacesCounter placesCounter = new PlacesCounter();

        for (Traits sample: data) {
            placesCounter.incrementFor(sample.getPlace());
        }

        System.out.println(placesCounter);

        Knn knn = new Knn(data, 4, 30, new EuclidianMetric());

        System.out.println("Classifying...");
        knn.classifyTestSet();
        Assignments assignments = new Assignments();

        System.out.println("Analyzing...");
        assignments.calculateFor(knn.getAssignedTextSet());

        for (Map.Entry<Place, Double[]> entry: assignments.getAll().entrySet()) {
            System.out.println("Place: " + entry.getKey().toString());
            Double[] params = entry.getValue();
            System.out.println("Accuracy: " + params[0]);
            System.out.println("Precision: " + params[1]);
            System.out.println("Recall: " + params[2]);
            System.out.println("F1: " + params[3]);
        }
    }

}
