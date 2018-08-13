package wmr.controller;

import java.util.ArrayList;
import java.util.HashMap;

import wmr.model.*;


/**
 * Controller object for manipulating the models
 */
public class RailwayController implements Controller {

    /** Create a RailwayController
     *
     * @param stations HashMap of station name and station objects
     * @param lines HashMap of line names and line objects
     */
    public RailwayController(HashMap<String, Station> stations, HashMap<String, ArrayList<Station>> lines) {
        this.stations = stations;
        this.lines = lines;
    }

    HashMap<String, Station> stations;
    HashMap<String, ArrayList<Station>> lines;

    /**
     * Lists termini of a specific line
     * @param line The number of the required line as shown in the TUI.
     * @return String outlining the termini
     */
    @Override
    public String listAllTermini(String line) {
        String output = "";
        for (Station station : getTermini(line)) {
            output += station.getName() + ", ";
        }
        return "Termini: \n" + output.substring(0, output.length() - 2);
    }

    /**
     * Gets a list of Station objects which are termini of the suppled line
     * @param lineName Line name to get the termini of
     * @return ArrayList of station objects, the termini
     */
    private ArrayList<Station> getTermini(String lineName) {
        ArrayList<Station> termini = new ArrayList<>();
        ArrayList<Station> stationsInLine = lines.get(lineName);
        for (Station station : stationsInLine) {
            if (station.getConnectedStations(lineName).size() == 1 && !termini.contains(station)) {
                termini.add(station);
            }
        }
        return termini;
    }


    /**
     * Lists every station in the supplied line
     * @param lineName Line for stations on it
     * @return Output string of stations
     */
    @Override
    public String listStationsInLine(String lineName) {
        Station startStation = lines.get(lineName).get(0);
        return lineName + ": \n" + startStation.getName() + listConStations(startStation, null, lineName);
    }


    /**
     * Recursive method for listing connected stations to the supplied
     * @param newStation Station to get connected from
     * @param prevStation Station previously visited to avoid looping
     * @param lineName Name of the line to follow
     * @return String of connected stations
     */
    private String listConStations(Station newStation, Station prevStation, String lineName){
        String output = "";
        ArrayList<Station> conStations = newStation.getConnectedStations(lineName);
        conStations.remove(prevStation);
        if(conStations.size() > 1){
            for(Station conStation : conStations){
                output += " <--> " + conStation.getName();
                output += listConStations(conStation, newStation, lineName);
            }
        } else {
            output += " <--> " + conStations.get(0).getName();
            if(conStations.get(0).getConnectedStations(lineName).size() > 1){
                output += listConStations(conStations.get(0), newStation, lineName);
            }
        }
        return output;
    }


    /**
     * Gets any path between the two supplied stations
     * @param stationA	the name of a station
     * @param stationB	the name of another station
     * @return Path overview and detail route
     */
    @Override
    public String showPathBetween(String stationA, String stationB) {
        Station stationAObj = stations.get(stationA);
        Station stationBObj = stations.get(stationB);
        if(stationAObj == null || stationBObj == null){
            return "One or both of the station names were not found";
        }
        if(stationAObj == stationBObj){
            return "You cannot plot the route to the same station";
        }
        ArrayList<Route> routes = new ArrayList<>();
        Route shortestRoute = null;
        int currentShortestDuration = -1;
        routes.add(new Route(stationAObj));
        boolean found = false;
        int validCount = 0;
        while (shortestRoute == null){
            ArrayList<Route> newRoutes = new ArrayList<>();
            for(Route route : routes){
                for(Rail rail : route.getCurrentStation().getConnectedRails()){
                    Route newRoute = new Route(route.getJourney(), route.getTotalDuration());
                    newRoute.addToRoute(rail);
                    if(newRoute.shouldContinue()){
                        if(newRoute.getCurrentStation() == stationBObj &&
                                (newRoute.getTotalDuration() < currentShortestDuration
                                        || currentShortestDuration == - 1)){
                            validCount += 1;
                            shortestRoute = newRoute;
                            currentShortestDuration = newRoute.getTotalDuration();
                            // TODO Make this not check all of them but still be efficient
//                            found = true;
                        } else{
                            newRoutes.add(newRoute);
                        }
                    }
                }
            }
            if(newRoutes.size() == 0){
                break;
            }
            routes = newRoutes;
        }

        System.out.println(shortestRoute);

        return shortestRoute.getJourneyPrintout();
    }
}

