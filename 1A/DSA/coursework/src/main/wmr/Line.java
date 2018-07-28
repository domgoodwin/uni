package wmr;

import java.util.ArrayList;

public class Line {
    public Line(String lineName){
        this.lineName = lineName;
        this.stations = new ArrayList<>();
    }

    private String lineName;
    private ArrayList<Station> stations;
    private Station startStation;

    public String getLineName() {
        return lineName;
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void addStation(Station station){
        if(!this.stations.contains(station)){
            this.stations.add(station);
        }
    }

    public String toString(){
        return "Name: " + lineName + " / Stations: " + stations.size();
    }
}
