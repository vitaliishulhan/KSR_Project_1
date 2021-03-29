import extraction.Place;
import extraction.TraitExctractor;
import extraction.Traits;
import knn.Knn;
import knn.PlacesCounter;
import knn.metrics.EuclidianMetric;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) throws IOException {
        List<Traits> data = new ArrayList<>();

        File[] listOfFiles = new File("articles").listFiles();

        System.out.println("File parsering and traits extracting...");

        for (File file: listOfFiles) {
            System.out.println(file.getName());
            data.addAll(Objects.requireNonNull(TraitExctractor.getTraitsVectorFor("articles/" + file.getName())));
        }

        PlacesCounter placesCounter = new PlacesCounter();

        for (Traits sample: data) {
            placesCounter.incrementFor(sample.getPlace());
        }

        System.out.println(placesCounter);

        Knn knn = new Knn(data, 100, 90, new EuclidianMetric());

        System.out.println("Classifying...");
        knn.classifyTestSet();


    }

}
