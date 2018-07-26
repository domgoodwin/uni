package wmr;

import java.util.ArrayList;

public class RailwayController implements Controller {

    public RailwayController(ArrayList<Station> stations){
        this.stations = stations;
    }

    ArrayList<Station> stations;

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
}
