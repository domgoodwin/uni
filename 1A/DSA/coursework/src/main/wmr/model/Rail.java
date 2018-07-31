package wmr.model;

/**
 * Rail model for the links between stations
 */
public class Rail {
    /**
     * Creates Rail object
     * @param lineName name of the line of the rail
     * @param connectedStation the toStation object ref
     * @param duration time taken to travel rail
     */
    public Rail(String lineName, Station connectedStation, int duration){
        this.lineName = lineName;
        this.connectedStation = connectedStation;
        this.duration = duration;
    }

    private String lineName;
    private Station connectedStation;
    private int duration;

    /**
     * Get the name of the line
     * @return the name lane
     */
    public String getLineName(){
        return this.lineName;
    }

    /**
     * Gets the station connected to the rail
     * @return station object of the connected
     */
    public Station getConnectedStation(){
        return this.connectedStation;
    }

    /**
     * Gets the duration to travel the rail
     * @return duration to travel rail
     */
    public int getDuration(){
        return this.duration;
    }

    /**
     * String output of the rail
     * @return printout 
     */
    public String toString() {
        return "\tLineName: " + lineName + " / ConnectedStation: " + connectedStation.getName() + " / Duration: " + Integer.toString(duration);
    }
}
