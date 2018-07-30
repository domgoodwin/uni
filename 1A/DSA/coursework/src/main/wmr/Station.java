package wmr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.*;
import java.util.stream.*;


public class Station {

    public Station(String name){
        this.name = name;
        this.rails = new ArrayList<Rail>();
    }

    public Station(String name, ArrayList<Rail> rails){
        this.name = name;
        this.rails = rails;
    }

    private String name;
    private ArrayList<Rail> rails;

    public String getName(){
        return this.name;
    }

    public ArrayList<Rail> getLines(){
        return this.rails;
    }

    public boolean checkStationConnected(String stationName){
        // Returns if any connected station is same name
        return rails.stream().filter(o -> o.getConnectedStation().equals(stationName)).findFirst().isPresent();
    }

    public boolean checkNextStationOnLine(String lineName){
        // Returns if any station connected is on supplied line
        return rails.stream().anyMatch(o -> o.getLineName().equals(lineName));
    }



    public ArrayList<Rail> getConnectedRails(){
        return this.rails;
    }

    public ArrayList<Station> getConnectedStations(String lineName){
        ArrayList<Rail> rails = this.rails.stream().filter(o -> o.getLineName().equals(lineName)).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Station> stations = new ArrayList<>();
        for (Rail rail : rails) {
            stations.add(rail.getConnectedStation());
        }
        return stations;
    }

    public void addRail(Rail rail){
        this.rails.add(rail);
    }

    public int getDurationToStation(Station station, String lineName){
        for(Rail rail: rails){
            if(rail.getLineName().equals(lineName) && rail.getConnectedStation() == station){
                return rail.getDuration();
            }
        }
        return 0;
    }

    public String toString() {
        String lines = "";
        for (Rail line : rails) {
            lines +=  line.toString() + "\n";
        }
        return "Name: " + name + " \n" + lines;
    }

}
