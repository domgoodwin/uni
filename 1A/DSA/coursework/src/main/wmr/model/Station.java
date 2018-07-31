package wmr.model;

import java.util.ArrayList;
import java.util.stream.*;

/**
 * Station Model
 */
public class Station {

    /**
     * Creates station with empty list
     * @param name Name of the station
     */
    public Station(String name){
        this.name = name;
        this.rails = new ArrayList<Rail>();
    }

    /**
     * Creates station with predefined rails
     * @param name Name of statio
     * @param rails Rails of station
     */
    public Station(String name, ArrayList<Rail> rails){
        this.name = name;
        this.rails = rails;
    }

    private String name;
    private ArrayList<Rail> rails;

    /**
     * Gets name of the station
     * @return name of station
     */
    public String getName(){
        return this.name;
    }

    /**
     * Gets rails of station
     * @return rails of station
     */
    public ArrayList<Rail> getLines(){
        return this.rails;
    }

    /**
     * Checks if the station is connected to this
     * @param stationName Name of station to check
     * @return Indiactor if station connected
     */
    public boolean checkStationConnected(String stationName){
        // Returns if any connected station is same name
        return rails.stream().filter(o -> o.getConnectedStation().equals(stationName)).findFirst().isPresent();
    }

    /**
     * Checks what the next station on a certain line is
     * @param lineName line name
     * @return indiactor is station connected
     */
    public boolean checkNextStationOnLine(String lineName){
        // Returns if any station connected is on supplied line
        return rails.stream().anyMatch(o -> o.getLineName().equals(lineName));
    }

    /**
     * Gets rails connected to station
     * @return rails connected to station
     */
    public ArrayList<Rail> getConnectedRails(){
        return this.rails;
    }

    /**
     * Gets connected stations by line name
     * @param lineName Name of line for which to check stations
     * @return List of stations connected on specified line
     */
    public ArrayList<Station> getConnectedStations(String lineName){
        ArrayList<Rail> rails = this.rails.stream().filter(o -> o.getLineName().equals(lineName)).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Station> stations = new ArrayList<>();
        for (Rail rail : rails) {
            stations.add(rail.getConnectedStation());
        }
        return stations;
    }

    /**
     * Adds a rail to this station
     * @param rail
     */
    public void addRail(Rail rail){
        this.rails.add(rail);
    }

    /**
     * Gets duration to station directly connected
     * @param station Station to check for
     * @param lineName Line name to check for
     * @return Duration to station
     */
    public int getDurationToStation(Station station, String lineName){
        for(Rail rail: rails){
            if(rail.getLineName().equals(lineName) && rail.getConnectedStation() == station){
                return rail.getDuration();
            }
        }
        return 0;
    }

    /**
     * Gets printout of station
     * @return Printout
     */
    public String toString() {
        String lines = "";
        for (Rail line : rails) {
            lines +=  line.toString() + "\n";
        }
        return "Name: " + name + " \n" + lines;
    }

}
