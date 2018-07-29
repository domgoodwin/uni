package wmr;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Importer {

    static HashMap<String, Station> stations = new HashMap<>();
    static HashMap<String, ArrayList<Station>> lines = new HashMap<>();

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

    static private void addToStation(Station fromStation, String lineName, Station toStation, int duration){
        fromStation.addRail(new Rail(lineName, toStation, duration));
    }

    static private void processLine(Station station, String lineName){
        if(!lines.containsKey(lineName)){
            lines.put(lineName, new ArrayList<Station>());
        }
        addStation(lineName, station);
    }

    static private void addStation(String lineName, Station station){
        ArrayList<Station> stations = lines.get(lineName);
        if(!stations.contains(station)){
            stations.add(station);
        }
    }

}
