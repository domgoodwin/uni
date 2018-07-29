package wmr;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String path = new File("src/main/resources/WestMidlandsRailway.csv").getAbsolutePath();
        Controller ctl = Importer.createController(path);
        // TODO Add comments
	    TUI tui = new TUI(ctl);

    }
}
