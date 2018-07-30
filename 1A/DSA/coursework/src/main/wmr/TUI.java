/**
 * 
 */
package wmr;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * A simple text-based user interface for showing various information 
 * about a West Midlands Railway network.
 * 
 * @author S H S Wong
 * @version 23/06/2018
 */
public class TUI {

	private Controller controller;  
	private Scanner stdIn;
	
	public TUI(Controller controller) {
		
		this.controller = controller;
		
		// Creates a Scanner object for obtaining user input
		stdIn = new Scanner(System.in);
		
		while (true) {
			displayMenu();
			getAndProcessUserOption();
		}
	}

	/**
	 * Displays the header of this application and a summary of menu options.
	 */
	private void displayMenu() {
		display(header());
		display(menu());
	}
	
	/**
	 * Obtains an user option and processes it.
	 */
	private void getAndProcessUserOption() {
		String command = stdIn.nextLine().trim();
		String lineName;
		switch (command) {
		case "1" : // Lists all termini along a specified line
			display("Lists all termini along a line...");
			display("Enter the ID of the required line.");
			display(allWMRlines());
			String line = stdIn.nextLine().trim();
			lineName = mapLineName(line);
			display(controller.listAllTermini(lineName));
			break;
		case "2" : // Lists all stations in a line
			display("Lists all stations along a line...");
			display("Enter the ID of the line you'd like to view:");
			display(allWMRlines());
			lineName = mapLineName(stdIn.nextLine().trim());
			display(controller.listStationsInLine(lineName));
			break;
		case "3" : // Finds a path between two stations
			display("Finds a path between two stations...");
			display("Enter the name of the start station:");
			String stationA = stdIn.nextLine().trim();
			display("Enter the name of the end station:");
			String stationB = stdIn.nextLine().trim();
			display(controller.showPathBetween(stationA, stationB));
			break;
		case "4" : // Exits the application
			display("Goodbye!");
			System.exit(0);
			break;
		default : // Not a known command option
			display(unrecogniseCommandErrorMsg(command));
		}
	}

	/*
	 * Returns a string representation of all railway lines in this application.
	 * @return info about all West Midlands Railway lines
	 */
	private static String allWMRlines() {
		return "\na. Birmingham -- Dorridge -- Leamington Spa" +
		       "\nb. Cross City Line" + 
			   "\nc. Birmingham -- Rugby -- Northampton -- London" +
		       "\nd. Nuneaton -- Coventry" +
			   "\ne. Watford -- St Albans Abbey" +
		       "\nf. Bletchley -- Bedford" +
			   "\ng. Crewe -- Stoke -- Stafford -- London" +
		       "\nh. Worcester -- Birmingham" + 
			   "\ni. Smethwick Galton Bridge Connections" +
		       "\nj. Birmingham -- Stratford-upon-Avon" +
			   "\nk. Birmingham -- Wolverhampton -- Telford -- Shrewsbury" +
		       "\nl. Birmingham -- Worcester -- Hereford";
	}

	private static String mapLineName(String line){
		switch (line) {
			case "a":
				return "Birmingham -- Dorridge -- Leamington Spa";
			case "b":
				return "Cross City Line";
			case "c":
				return "Birmingham -- Rugby -- Northampton -- London";
			case "d":
				return "Nuneaton -- Coventry";
			case "e":
				return "Watford -- St Albans Abbey";
			case "f":
				return "Bletchley -- Bedford";
			case "g":
				return "Crewe -- Stoke -- Stafford -- London";
			case "h":
				return "Worcester -- Birmingham";
			case "i":
				return "Smethwick Galton Bridge Connections";
			case "j":
				return "Birmingham -- Stratford-upon-Avon";
			case "k":
				return "Birmingham -- Wolverhampton -- Telford -- Shrewsbury";
			case "l":
				return "Birmingham -- Worcester -- Hereford";
			default:
				return null;
		}
	}

	/*
	 * Returns a string representation of a brief title for this application as the header.
	 * @return	a header
	 */
	private static String header() {
		return "\nMTR Information Centre\n";
	}
	
	/*
	 * Returns a string representation of the user menu.
	 * @return	the user menu
	 */
	private static String menu() {
		return "Enter the number associated with your chosen menu option.\n" +
			   "1: List all termini in a specified line\n" +
			   "2: List all stations along a specific line\n" +
			   "3: Find a path between two stations\n" +
			   "4: Exit this application\n";
	}
	
	/*
	 * Displays the specified info for the user to view.
	 * @param info	info to be displayed on the screen
	 */
	private void display(String info) {
		System.out.println(info);
	}
	
    /*
     * Returns an error message for an unrecognised command.
     * 
     * @param error the unrecognised command
     * @return      an error message
     */
    private static String unrecogniseCommandErrorMsg(String error) {
            return String.format("Cannot recognise the given command: %s.%n", error);
    }

}
