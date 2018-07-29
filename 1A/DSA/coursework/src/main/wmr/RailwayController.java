package wmr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class RailwayController implements Controller {

    public RailwayController(HashMap<String, Station> stations, HashMap<String, ArrayList<Station>> lines) {
        this.stations = stations;
        this.lines = lines;
    }

    HashMap<String, Station> stations;
    HashMap<String, ArrayList<Station>> lines;


    @Override
    public String listAllTermini(String line) {
        String output = "";
        for (Station station : getTermini(line)) {
            output += station.getName() + ", ";
        }
        return output.substring(0, output.length() - 2);
    }

    private ArrayList<Station> getTermini(String lineName) {
        ArrayList<Station> termini = new ArrayList<>();
        ArrayList<Station> stationsInLine = lines.get(lineName);
        for(Station station : stationsInLine){
            if(station.getConnectedStations(lineName).size() == 1 && !termini.contains(station)){
                termini.add(station);
            }
        }
        return termini;
    }


    @Override
    public String listStationsInLine(String lineName) {
        String stationList = "";
        ArrayList<Station> line = lines.get(lineName);
        int mins = 0;
        Station prevStation = null;
        Station curStation = null;
        for (int i = 0; i < line.size(); i++) {
            prevStation = line.get(i);
            curStation = prevStation.getConnectedStations(lineName).get(0);
            if (curStation != null) {
                mins += prevStation.getDurationToStation(curStation, lineName);
            }
            stationList += prevStation.getName() + " <-> ";
        }
        stationList += prevStation.getConnectedStations(lineName).get(0).getName();
        String output = String.format("%s %d mins:\n", lineName, mins);
        return output + stationList;
    }

    ArrayList<Route> routes = new ArrayList<>();
    Route shortestRoute = null;
    int currentShortestDuration = -1;
    @Override
    public String showPathBetween(String stationA, String stationB) {
        Station stationAObj = stations.get(stationA);
        Station stationBObj = stations.get(stationB);
        routes.add(new Route(stationAObj));
        while (true){
            ArrayList<Route> newRoutes = new ArrayList<>();
            for(Route route : routes){
                for(Rail rail : route.getCurrentStation().getConnectedRails()){
                    Route newRoute = new Route(route.getJourney(), route.getTotalDuration());
                    newRoute.addToRoute(rail);
                    if(newRoute.shouldContinue()){
                        if(newRoute.getCurrentStation() == stationBObj &&
                                (newRoute.getTotalDuration() < currentShortestDuration
                                        || currentShortestDuration == - 1)){
                            shortestRoute = newRoute;
                            currentShortestDuration = newRoute.getTotalDuration();
                        } else{
                            newRoutes.add(newRoute);
                        }
                    }
                }
            }
            if(newRoutes.size() == 0){
//                if(loop == 0){
//                    routes = newRoutes;
//                }
                break;
            }
            routes = newRoutes;
        }
//        Route shortestRoute = null;
//        int curShorest = -1;
//        for(Route route : validRoutes){
//            if(route.getCurrentStation() == stationBObj && (curShorest > route.getTotalDuration() || curShorest == -1) ){
//                shortestRoute = route;
//                curShorest = shortestRoute.getTotalDuration();
//            }
//        }

        System.out.println(shortestRoute);

        return shortestRoute.getJourneyPrintout();
    }

    private void addToRoutes(ArrayList<Station> stations){

    }


        private ArrayList<Station> checkStations(ArrayList<Station> stations, Station goalStation){
        ArrayList<Station> newStations = new ArrayList<>();
        for(Station station : stations){
            ArrayList<Rail> rails = station.getLines();
            for(Rail rail: rails){
                newStations.add(rail.getConnectedStation());
            }
        }

        if(newStations.contains(goalStation)){
            return newStations;
        } else {
            return checkStations(newStations, goalStation);
        }
    }
}

