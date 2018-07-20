/**
 * 
 */
package wmr;

/**
 * A controller for the West Midlands Railway Information Centre system.
 * This controller includes the 3 features that the intended
 * prototype West Midlands Railway Information Centre system 
 * is expected to have.
 * 
 * @author Sylvia Wong
 * @version 19/05/2018
 */
public interface Controller {
	
	/**
	 * Lists all termini of a specified railway line.
	 * @param line The number of the required line as shown in the TUI.
	 * @return the name of all stations that are the end point of the specified lines in the network. 
	 */
	String listAllTermini(String line);
	
	/**
	 * Lists the stations in their respective order in the specified West Midlands Railway line.
	 * @param line	a specified line in the West Midlands Railway network
	 * @return	a String representation of all stations in the specified line.
	 */
	String listStationsInLine(String line);

	/**
	 * Lists a path between the specified stations.
	 * The path is represented as a sequence of the name of the stations between the specified stations. 
	 * @param stationA	the name of a station
	 * @param stationB	the name of another station
	 * @return	a String representation of a path between the specified stations and the expected travel time.
	 */
	String showPathBetween(String stationA, String stationB);
}
