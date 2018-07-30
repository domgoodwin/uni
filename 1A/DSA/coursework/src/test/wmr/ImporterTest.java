package wmr;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RunWith(JUnit4.class)
public class ImporterTest {

    @org.junit.Test
    public void basicImport() {
        String path = new File("src/main/resources/WestMidlandsRailway.csv").getAbsolutePath();
        HashMap<String, Station> stations = Importer.createController(path).stations;
        HashMap<String, ArrayList<Station>> lines = Importer.createController(path).lines;
        System.out.println(stations.size());
        for (Station station : stations.values()) {
            System.out.println(station.toString());
        }
    }
}
