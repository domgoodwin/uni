package wmr.model;

import java.util.LinkedList;

public class Route {
    public Route(Station startStation){
        this.journey = new LinkedList<>();
        this.journey.add(startStation);
        this.totalDuration = 0;
    }

    public Route(LinkedList<Station> journey, int totalDuration){
        LinkedList<Station> newJourney = new LinkedList<>();
        newJourney.addAll(journey);
        this.journey = newJourney;
        this.totalDuration = totalDuration;
    }

    private LinkedList<Station> journey;
    private int totalDuration;
    private boolean continueLine = true;

    public int getTotalDuration() {
        return totalDuration;
    }

    public LinkedList<Station> getJourney() {
        return journey;
    }

    public Station getCurrentStation(){
        return journey.getLast();
    }

    public void addToRoute(Rail rail){
        if(journey.contains(rail.getConnectedStation())){
            continueLine = false;
            return;
        }
        addDuration(rail.getDuration());
        journey.add(rail.getConnectedStation());
    }

    public boolean shouldContinue() {
        return continueLine;
    }

    private void addDuration(int duration){
        totalDuration += duration;
    }

    public String toString(){
        return "Duration: " + this.getTotalDuration() + " / Current Station: " + this.getCurrentStation().getName() + " / Route Len: " + this.getJourney().size();
    }

    public String getJourneyPrintout(){
        String output = journey.getFirst().getName() + " --> " + journey.getLast().getName()
                + " / Duration: " + this.getTotalDuration() + "\n\t";
        for(Station station : this.journey){
            output += station.getName() + "\n\t";
        }
        return output;
    }
}
