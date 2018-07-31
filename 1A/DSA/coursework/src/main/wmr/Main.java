package wmr;

import wmr.controller.*;
import wmr.view.*;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        String path = new File("src/main/resources/WestMidlandsRailway.csv").getAbsolutePath();
        Controller ctl = Importer.createController(path);
	    TUI tui = new TUI(ctl);

    }
}
