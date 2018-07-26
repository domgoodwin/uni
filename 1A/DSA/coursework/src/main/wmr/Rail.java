package wmr;

public class Rail {
    public Rail(String lineName, String connectedStation, int duration){
        this.lineName = lineName;
        this.connectedStation = connectedStation;
        this.duration = duration;
    }

    private String lineName;
    private String connectedStation;
    private int duration;

    public String getLineName(){
        return this.lineName;
    }

    public String getConnectedStation(){
        return this.connectedStation;
    }

    public int getDuration(){
        return this.duration;
    }

    public String toString() {
        return "LineName: " + lineName + " / ConnectedStation: " + connectedStation + " / Duration: " + Integer.toString(duration);
    }
}
