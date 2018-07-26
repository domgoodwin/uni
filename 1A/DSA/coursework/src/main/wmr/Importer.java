package wmr;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class Importer {

    static ArrayList<Station> stations = new ArrayList<>();

    static public ArrayList<Station> processStations(String filePath){
        stations = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        lines.remove(0);
        for (String textLine: lines) {
//            System.out.println(line);
            processTextLine(textLine);
        }
        return stations;
    }

    static private void processTextLine(String textLine){
        String[] parts = textLine.split(",");
        String lineName = parts[0].trim();
        String fromStation = parts[1].trim();
        String toStation = parts[2].trim();
        int duration = Integer.parseInt(parts[3].trim());

        checkAndAddStation(fromStation);
        addLine(lineName, fromStation, toStation, duration);
    }

    static private void checkAndAddStation(String fromStation){
        boolean found = false;
        for (Station station: stations) {
            found = station.getName().equals(fromStation);
            if(found){
                break;
            }
        }
        if(!found){
            stations.add(new Station(fromStation, new ArrayList<Rail>()));
        }
    }

    static private void addLine(String lineName, String fromStation, String toStation, int duration){
        int stationIndex = 0;
        for(int i = 0; i < stations.size(); i++) {
            if(stations.get(i).getName() == fromStation){
                stationIndex = i;
                break;
            }
        }
        stations.get(stationIndex).addRail(new Rail(lineName, toStation, duration));
    }
}
