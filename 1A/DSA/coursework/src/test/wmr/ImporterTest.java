package wmr;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class ImporterTest {

    @org.junit.Test
    public void basicImport() {
        String path = new File("src/main/resources/WestMidlandsRailway.csv").getAbsolutePath();
        ArrayList<Station> stations = Importer.processStations(path);
        System.out.println(stations.size());
        for (Station station : stations) {
            System.out.println(station.toString());
        }
    }
}
