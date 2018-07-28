package wmr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;


public class RailwayController implements Controller {

    public RailwayController(ArrayList<Station> stations, HashMap<String, ArrayList<Station>> lines){
        this.stations = stations;
        this.lines = lines;
    }

    ArrayList<Station> stations;
    HashMap<String, ArrayList<Station>> lines;
    private ArrayList<Station> termini;


    @Override
    public String listAllTermini(String line) {
        String output = "";
        for (Station station : getTermini(line)) {
            output += station.getName() + ", ";
        }
        return output.substring(0, output.length() - 2);
    }

    private ArrayList<Station> getTermini(String lineName){
        termini = new ArrayList<>();
        ArrayList<Station> startStation = new ArrayList<Station>();
        startStation.add(lines.get(lineName).get(0));
        termini.add(startStation.get(0));
        followToTerminal(startStation, lineName);
        return termini;
    }


    private void followToTerminal(ArrayList<Station> stations, String lineName){
        for (Station station : stations) {
            ArrayList<Station> conStations = station.getConnectedStations(lineName);
            if(conStations.size() == 0){
                addTerminal(station);
            }
            followToTerminal(conStations, lineName);
        }
    }

    private void addTerminal(Station station){
        if(!termini.contains(station)){
            termini.add(station);
        }
    }
    @Override
    public String listStationsInLine(String lineName) {
        String stationList = "";
        ArrayList<Station> line = lines.get(lineName);
        int mins = 0;
        Station prevStation = null;
        Station curStation = null;
        for (int i = 0; i < line.size(); i++) {
//            curStation = i+1 <= line.size() - 1 ? line.get(i+1) : null;
            prevStation = line.get(i);
            curStation = prevStation.getC4onnectedStations(lineName).get(0);
            if(curStation != null){
                mins += prevStation.getDurationToStation(curStation, lineName);
            }
            stationList += prevStation.getName() + " <-> ";
        }
        stationList += prevStation.getConnectedStations(lineName).get(0).getName();
        String output = String.format("%s %d mins:\n",lineName, mins);
        return output + stationList;
    }

    @Override
    public String showPathBetween(String stationA, String stationB) {
        return null;
    }
}
