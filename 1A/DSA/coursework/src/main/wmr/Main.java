package wmr;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String path = new File("src/main/resources/WestMidlandsRailway.csv").getAbsolutePath();
        ArrayList<Station> stations = Importer.processStations(path);
        Controller ctl = new RailwayController(stations);
	    TUI tui = new TUI(ctl);

    }
}
