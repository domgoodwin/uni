package wmr;

public class Main {

    public static void main(String[] args) {
        Controller ctl = new Controller() {
            @Override
            public String listAllTermini(String line) {
                return null;
            }

            @Override
            public String listStationsInLine(String line) {
                return null;
            }

            @Override
            public String showPathBetween(String stationA, String stationB) {
                return null;
            }
        };
	    TUI tui = new TUI(ctl);

    }
}
