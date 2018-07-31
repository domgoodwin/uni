package wmr.model;

import java.util.ArrayList;

/**
 * Line model for representing a train line of stations
 * Contains every station on the line and the name of the line
 */
public class Line {
    /**
     * Create a Line object
     * @param lineName Name of the line
     */
    public Line(String lineName){
        this.lineName = lineName;
        this.stations = new ArrayList<>();
    }

    private String lineName;
    private ArrayList<Station> stations;
    private Station startStation;


    /**
     * Get the name of the line
     * @return name of the line
     */
    public String getLineName() {
        return lineName;
    }

    /**
     * Get the stations in the line
     * @return stations in the line
     */
    public ArrayList<Station> getStations() {
        return stations;
    }

    /**
     * Add a station to the line if it doesn't exist
     * @param station To be added
     */
    public void addStation(Station station){
        if(!this.stations.contains(station)){
            this.stations.add(station);
        }
    }

    /**
     * Override of to string for line object
     * @return String output of line information
     */
    @Override
    public String toString(){
        return "Name: " + lineName + " / Stations: " + stations.size();
    }
}
