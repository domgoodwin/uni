package wmr.model;

import java.util.LinkedList;

/**
 * Route model for calculating optimal routes between stations
 */
public class Route {
    /**
     * Creates Route object with a start station
     * @param startStation Station object as starting point
     */
    public Route(Station startStation){
        this.journey = new LinkedList<>();
        this.journey.add(startStation);
        this.totalDuration = 0;
    }

    /**
     * Creates Route object with predefined journey and total duration
     * @param journey LinkedList of the journey taken
     * @param totalDuration Total duration of all lines taken
     */
    public Route(LinkedList<Station> journey, int totalDuration){
        LinkedList<Station> newJourney = new LinkedList<>();
        newJourney.addAll(journey);
        this.journey = newJourney;
        this.totalDuration = totalDuration;
    }

    private LinkedList<Station> journey;
    private int totalDuration;
    private boolean continueLine = true;

    /**
     * Gets total duration travelled
     * @return Total duration
     */
    public int getTotalDuration() {
        return totalDuration;
    }

    /**
     * Gets whole journey list of station taken
     * @return LinkedList of stations
     */
    public LinkedList<Station> getJourney() {
        return journey;
    }

    /**
     * Gets last station visited
     * @return last station from linkedlist
     */
    public Station getCurrentStation(){
        return journey.getLast();
    }

    /**
     * Adds a new station and duration to the route
     * @param rail rail object being travelled on
     */
    public void addToRoute(Rail rail){
        if(journey.contains(rail.getConnectedStation())){
            continueLine = false;
            return;
        }
        addDuration(rail.getDuration());
        journey.add(rail.getConnectedStation());
    }

    /**
     * Returns stop indicator
     * @return boolean indicator
     */
    public boolean shouldContinue() {
        return continueLine;
    }

    /**
     * Adds duration to total duration
     * @param duration duration to be added
     */
    private void addDuration(int duration){
        totalDuration += duration;
    }

    /**
     * Gets summary of route object
     * @return printout
     */
    public String toString(){
        return "Duration: " + this.getTotalDuration() + " / Current Station: " + this.getCurrentStation().getName() + " / Route Len: " + this.getJourney().size();
    }

    /**
     * Gets full journey printout
     * @return Full journey stringjt
     */
    public String getJourneyPrintout(){
        String output = journey.getFirst().getName() + " --> " + journey.getLast().getName()
                + " / Duration: " + this.getTotalDuration() + "\n\t";
        for(Station station : this.journey){
            output += station.getName() + "\n\t";
        }
        return output;
    }
}
