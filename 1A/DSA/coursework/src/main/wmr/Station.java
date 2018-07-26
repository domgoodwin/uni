package wmr;

import java.util.ArrayList;
import java.util.*;
import java.util.stream.*;


public class Station {

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
        return rails.stream().filter(o -> o.getConnectedStation().equals(stationName)).findFirst().isPresent();
    }

    public void addRail(Rail rail){
        this.rails.add(rail);
    }

    public String toString() {
        String lines = "";
        for (Rail line : rails) {
            lines += "\t" + line.toString() + "\n";
        }
        return "Name: " + name + " \n\t" + lines;
    }

}
