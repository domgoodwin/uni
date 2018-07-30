package wmr.controller;

import java.util.ArrayList;
import java.util.HashMap;

import wmr.model.*;



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
        return "Termini: \n" + output.substring(0, output.length() - 2);
    }

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


    @Override
    public String listStationsInLine(String lineName) {
        Station startStation = lines.get(lineName).get(0);
        return lineName + ": \n" + startStation.getName() + listConStations(startStation, null, lineName);
    }


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

