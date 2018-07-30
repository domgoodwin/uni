package wmr.model;

public class Rail {
    public Rail(String lineName, Station connectedStation, int duration){
        this.lineName = lineName;
        this.connectedStation = connectedStation;
        this.duration = duration;
    }

    private String lineName;
    private Station connectedStation;
    private int duration;

    public String getLineName(){
        return this.lineName;
    }

    public Station getConnectedStation(){
        return this.connectedStation;
    }

    public int getDuration(){
        return this.duration;
    }

    public String toString() {
        return "\tLineName: " + lineName + " / ConnectedStation: " + connectedStation.getName() + " / Duration: " + Integer.toString(duration);
    }
}
