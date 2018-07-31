package wmr;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import wmr.controller.*;
import wmr.model.*;

/**
 * Importer class to read in CSV into objects
 * Contains static methods to get objects
 */
public class Importer {

    static HashMap<String, Station> stations = new HashMap<>();
    static HashMap<String, ArrayList<Station>> lines = new HashMap<>();

    /**
     * Creates railway controller from a filepath csv
     * @param filePath Path to csv of stations
     * @return RailwayController object to be used in simulation
     */
    static public RailwayController createController(String filePath){
        stations = new HashMap<>();
        List<String> fileLines = new ArrayList<>();
        try {
            fileLines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileLines.remove(0);
        for (String textLine: fileLines) {
            processTextLine(textLine);
        }
        System.out.println("Station map read in, stations: " + stations.size());
        return new RailwayController(stations, lines);
    }

    /**
     * Processes a line in the CSV
     * @param textLine Line in csv
     */
    static private void processTextLine(String textLine){
        String[] parts = textLine.split(",");
        String lineName = parts[0].trim();
        String fromStation = parts[1].trim();
        String toStation = parts[2].trim();
        int duration = Integer.parseInt(parts[3].trim());

        Station curStation = checkAndAddStation(fromStation);
        Station toStationObj = checkAndAddStation(toStation);
        addToStation(curStation, lineName, toStationObj, duration);
        addToStation(toStationObj, lineName, curStation, duration);
        processLine(curStation, lineName);
        processLine(toStationObj, lineName);
    }

    /**
     * Adds or finds station then returns it
     * @param fromStation station name to be checked
     * @return Station object found or created
     */
    static private Station checkAndAddStation(String fromStation){
        boolean found = false;
        for (Station station: stations.values()) {
            found = station.getName().equals(fromStation);
            if(found){
                return station;
            }
        }
        stations.put(fromStation, new Station(fromStation, new ArrayList<Rail>()));
        return stations.get(fromStation);
    }

    /**
     * Adds a rail connection to given station
     * @param fromStation Station to add rail to
     * @param lineName Name of line rail is on
     * @param toStation Station object conncted via rail
     * @param duration Duration taken to travel line
     */
    static private void addToStation(Station fromStation, String lineName, Station toStation, int duration){
        fromStation.addRail(new Rail(lineName, toStation, duration));
    }

    /**
     * Processes a line object
     * @param station Station object on line
     * @param lineName Line name
     */
    static private void processLine(Station station, String lineName){
        if(!lines.containsKey(lineName)){
            lines.put(lineName, new ArrayList<Station>());
        }
        addStation(lineName, station);
    }

    /**
     * Adds a station to the line
     * @param lineName Name of line
     * @param station Station object to add
     */
    static private void addStation(String lineName, Station station){
        ArrayList<Station> stations = lines.get(lineName);
        if(!stations.contains(station)){
            stations.add(station);
        }
    }

}
