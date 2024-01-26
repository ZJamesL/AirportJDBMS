import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Time;
import java.util.Locale.Category;

import javax.security.auth.callback.ConfirmationCallback;
import javax.swing.plaf.basic.BasicBorders.FieldBorder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

//export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
/*
 * JDBC.java -- This program connects to the user's oracle account,
 * then allows the user to edit and retrieve information about 
 * employees, passengers, flights, and benefits for an airport through the 
 * airport database.
 * 
 * This program first has the user input their username and password to connect to oracle.
 * The user can then choose from a menu to either insert, delete, update, or
 * query the flights, passengers, employees, and benefits for the Airport. There are sub-menus
 * for each option that allow the users to choose which relations to insert, delete, and update = and
 * which queries to retrieve results from. 
 * 
 * Notes: 
 * 		-All flights contained in the database are in 2021. 
 * 		-Do not input any SQL. This program takes in input and created the SQL queries from the input. 
 * 
 * The tables used in this program contain prewritten records and date inserted/updated by any users
 * using this system.
 * The tables' names are: Flight, Passenger, Employee, Benefit, Airline, PassFlight, EmpFlight,
 * and PassCategory
 *
 * At the time of this writing, the version of Oracle is 11.2g, and
 * the Oracle JDBC driver can be found at
 *   /usr/lib/oracle/19.8/client64/lib/ojdbc8.jar
 * on the lectura system in the UofA CS dept.
 *
 * To compile and execute this program on lectura:
 *
 *   Add the Oracle JDBC driver to your CLASSPATH environment variable:
 *         export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
 *
 *
 * Authors:  Kiana Thatcher, Zach Lopez, and Mario Marquez
 * Project: Airport Database and JDBC (Project 4)
 * Due Date: 12/5/2022
 */
public class JDBC {
	static Scanner gScan = new Scanner(System.in); //global scanner used in retrieving all inputs from user
	public static void main (String [] args) {
    	final String oracleURL =   // Magic lectura -> aloe access spell
                "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
		
		String username = null,   // Oracle DBMS username
	    	password = null;    // Oracle DBMS password
		
			//getting the user's oracle username and password
		System.out.println("Input your Oracle Username");
		if (gScan.hasNextLine()) {
			username = gScan.nextLine();
		}
		System.out.println("Input your Oracle Password");
		if (gScan.hasNextLine()) {
			password = gScan.nextLine();
		}
		
		    // load the (Oracle) JDBC driver by initializing its base
		    // class, 'oracle.jdbc.OracleDriver'.
		
		try {
		
		        Class.forName("oracle.jdbc.OracleDriver");
		
		} catch (ClassNotFoundException e) {
		
		        System.err.println("*** ClassNotFoundException:  "
		            + "Error loading Oracle JDBC driver.  \n"
		            + "\tPerhaps the driver is not on the Classpath?");
		        System.exit(-1);
		
		}
		
		    // make and return a database connection to the user's
		    // Oracle database
		
		Connection dbconn = null;
		
		
		try {
		        dbconn = DriverManager.getConnection
		                       (oracleURL,username,password);
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not open JDBC connection.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
		        System.exit(-1);
		
		}
    	
		Statement stmt = null; 
		try {
			stmt = dbconn.createStatement(); // create a statement variable in order to execute the queries within the database
			
			int chosen = 0; // holds the query number that the user has chosen
			
				// This continuously asks the user to choose a whether to insert, delete, update, or query into the database. Once one
				// is chosen, this program calls a function that displays another sub-menu. It exits the program once a user types -1.
			String temp = null; // holds the raw user input
			System.out.println("Type in '1', '2', '3' or '4' to either insert, delete, update, or query "
					+ "data respectively. Type in '-1' to exit the program.");
			while (chosen != -1 && gScan.hasNextLine()) {
				temp = gScan.nextLine();
				if (temp.matches("-?\\d+")) {
					chosen = Integer.parseInt(temp);
					if (chosen == 1) {
						insertTuple( stmt);
					} else if (chosen == 2) {
						deleteTuple(stmt);
					} else if (chosen == 3) {
						updateTuple(stmt);
					} else if (chosen == 4) {
						queryTuple(stmt);
					} else if (chosen == -1) {
						try {
        						gScan.close();
        						stmt.close();
            					dbconn.close();
        				} catch (SQLException e) {			
				
            					System.err.println("*** SQLException:  "
                					+ "Could not close.");
            					System.err.println("\tMessage:   " + e.getMessage());
            					System.err.println("\tSQLState:  " + e.getSQLState());
            					System.err.println("\tErrorCode: " + e.getErrorCode());
								e.printStackTrace();
            					System.exit(-1);
						}
						System.exit(0);
					} else {
						System.out.println("Invalid choice. Try Again.");
					}
					System.out.println("Type in '1', '2', '3' or '4' to either insert, delete, update, or query "
		                                        + "data respectively. Type in '-1' to exit the program.");
				} else {
					System.out.println("Select a number from the menu. Try Again.");
					System.out.println("Type in '1', '2', '3' or '4' to either insert, delete, update, or query "
							+ "data respectively. Type in '-1' to exit the program.");
				}
			}
			
		} catch (SQLException e) {

			System.err.println("*** SQLException:  "
                		+ "Could not fetch query results.");
            		System.err.println("\tMessage:   " + e.getMessage());
            		System.err.println("\tSQLState:  " + e.getSQLState());
            		System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            		System.exit(-1);
		}
		
        	// closes the connection to oracle
        	try {
        		gScan.close();
        		stmt.close();
            		dbconn.close();
        	} catch (SQLException e) {

			System.err.println("*** SQLException:  "
                		+ "Could not close.");
           	 	System.err.println("\tMessage:   " + e.getMessage());
            		System.err.println("\tSQLState:  " + e.getSQLState());
            		System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            		System.exit(-1);

        	}

    	}
    
	/* ---------------------------------------------------------------------------------------
	 *  Method queryTuple(Statement stmt)
	 *  Purpose:  This is a sub-menu for queries. It gives the user 5 queries to choose from then calls the associated function to 
	 * 				execute that query.
	 *			
	 * 
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: None
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void queryTuple(Statement stmt) {
		int chosen = 0; // holds the query number that the user has chosen
		// This continuously asks the user to choose a query, and once a query is chosen, this program calls a function to execute that
		// query. It exits the loop once a user types -1.
		while (chosen != -1) {
			System.out.println("Type in '1', '2', '3' or '4' to query one of the 4 following queries"
					+ " data respectively. Type in '-1' to go back to the main menu.\n");
			System.out.println("1) Display the list of distinct passenger names, who took flights from all\n" 
					+ "four airlines in the year 2021.\n");
			System.out.println("2) For any airline entered by the user, print the list of passengers, with the number\n" 
					+ "of checked-in bags. The list will be sorted in ascending order of the number of checked-in bags.\n"
					+ "Display the output grouped by flights for a particular date in March (inputted by the user).\n");
			System.out.println("3) Print the schedule of flights for a given date in June (inputted by the user). the Schedule\n" 
					+ "should contain the flight number, gate numbers, name of the airline of that flight, boarding time,\n"
					+ "departing time, duration of the flight (i.e. origin for arriving flights and destinations for the\n"
					+ "departing flights). Sort the schedule in ascending order of the boarding time\n");
			System.out.println("4) Print the list for the three categories of passengers (student, frequent flyer, and veteran\n" 
					+ "for each of the following queries for a particular airline of your choice who: \n"
					+ "traveled only once in the month of March, traveled with exactly one checked in bag anytime in the months of June\n"
					+ "and July, and ordered snacks/beverages on at least one flight.\n");
			System.out.println("5) Print out , for each month of 2021, the total volume of passengers and the total volume\n" 
					+ "of flights those months saw. Then, report which month saw the highest amount of passengers and which\n" 
					+ "month saw the highest amount of flights.\n");
			String temp = gScan.nextLine();
			if (temp.matches("-?\\d+")) {
				chosen = Integer.parseInt(temp);
				if (chosen == 1) {
					queryOne( stmt);
				} else if (chosen == 2) {
					queryTwo(stmt);
				} else if (chosen == 3) {
					queryThree(stmt);
				} else if (chosen == 4) {
					queryFour(stmt);
				} else if (chosen == 5){
					queryFive(stmt);
				}
			} else {
				System.out.println("Enter an Integer\n");
			}
		}
		
	}
	
	
	/* ---------------------------------------------------------------------------------------
	 *  Method queryOne(Statement stmt)
	 *  Purpose:  This function executes the query: "Display the list of distinct passenger names, who took flights from all
	 *				four airlines in the year 2021." Then displays the results to the user and returns to the query
	 *				sub-menu.
	 *			
	 * 
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: None
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
    	public static void queryOne(Statement stmt) {
    		ResultSet answer = null; // table of data representing the result from query1
    		String query = "SELECT DISTINCT firstName, lastName FROM "
    			+ "mfmarquez.passenger, mfmarquez.flight, mfmarquez.passflight "
    			+ "WHERE mfmarquez.passenger.passengerNo = mfmarquez.passflight.passengerNo "
    			+ "AND mfmarquez.flight.flightNo = mfmarquez.passflight.flightNo "
    			+ "GROUP BY firstName, lastName HAVING COUNT(DISTINCT airlineNo) = 4";
	      	try {
	            	answer = stmt.executeQuery(query); // executes the query
			if (answer != null) {
	                System.out.println("\nThe results of the query [" + query
	                                 + "] are:\n");
	
	                    // Get the data about the query result to learn
	                    // the attribute names and use them as column headers
	
	                System.out.println("Passenger Names:");
	
	                    // Use next() to advance cursor through the result
	                    // tuples and print their attribute values
	
	                	while (answer.next()) {
		                	System.out.println(answer.getString("firstName") + " "
		                	+ answer.getString("lastName"));
	                	}
	            	}
	            	System.out.println();
	        } catch (SQLException e) {
	                System.err.println("*** SQLException:  "
	                    + "Could not fetch query results.");
	                System.err.println("\tMessage:   " + e.getMessage());
	                System.err.println("\tSQLState:  " + e.getSQLState());
	                System.err.println("\tErrorCode: " + e.getErrorCode());
					e.printStackTrace();
	                System.exit(-1);
	        }
    	}
    
	/* ---------------------------------------------------------------------------------------
	 *  Method queryTwo(Statement stmt)
	 *  Purpose:  This function executes the following query and displays the results to the user:
	 * 				For any airline entered by the user, print the list of passengers, with the number
	 *				of checked-in bags. The list will be sorted in ascending order of the number of checked-in bags.
	 *				Display the output grouped by flights for a particular date in March (inputted by the user).
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: None
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
    	public static void queryTwo(Statement stmt) {
    		String airline = "";
    		String date = "";
    		System.out.println("Enter an airline for this query - Delta, United, Alaska, or Southwest");
    		if (gScan.hasNextLine()) {
    			airline = gScan.nextLine();
    		}
		while(true) {
			System.out.println("Enter a date in March (DD-MON-YYYY) for this query");
                	if (gScan.hasNextLine()) {
                        	date = gScan.nextLine().toUpperCase();
			}
			if(date.length() != 11) { 
				System.out.println("The date is not in the right format - (DD-MON-YYYY),  or its too short. Try Again!");
			} else if(!date.matches("\\d{2}-...-\\d{4}")) {
				System.out.println("The date is not in valid format. Try Again!");
			} else {
				break;
			}
		}
			
		String query = "SELECT firstName, lastName, bagNumber FROM "
					+ "mfmarquez.passenger, mfmarquez.passflight, mfmarquez.airline, mfmarquez.flight "
					+ "WHERE mfmarquez.passenger.passengerno = mfmarquez.passflight.passengerno "
					+ "AND mfmarquez.flight.flightno = mfmarquez.passflight.flightno "
					+ "AND mfmarquez.airline.airlineno = mfmarquez.flight.airlineno "
					+ "AND mfmarquez.airline.aname = '" + airline
					+ "' AND mfmarquez.flight.departingdate = '" + date
					+ "' ORDER BY bagnumber";

    		ResultSet answer = null; // table of data representing the result from query1
 
	      	try {
	            	answer = stmt.executeQuery(query);
	
	            	if (answer != null) {
	                	System.out.println("\nThe results of the query [" + query
	                                 + "] are:\n");
	
	                    	// Get the data about the query result to learn
	                    	// the attribute names and use them as column headers
	
	                	System.out.println("Passenger Names" + "\t" + "Number of Bags");
	
	                    	// Use next() to advance cursor through the result
	                    	// tuples and print their attribute values
	
	                	while (answer.next()) {
		                	System.out.println(answer.getString("firstName") + " "
		                	+ answer.getString("lastName")+ "\t" + answer.getString("bagNumber"));
	                	}
	            	}
	            	System.out.println();
	        } catch (SQLException e) {
	                System.err.println("*** SQLException:  "
	                    + "Could not fetch query results.");
	                System.err.println("\tMessage:   " + e.getMessage());
	                System.err.println("\tSQLState:  " + e.getSQLState());
	                System.err.println("\tErrorCode: " + e.getErrorCode());
					e.printStackTrace();
	                System.exit(-1);
	        }
	}	
    	
	/* ---------------------------------------------------------------------------------------
	 *  Method queryThree(Statement stmt)
	 *  Purpose:  This function executes the following query and displays the results to the user:
	 * 				Print the schedule of flights for a given date in June (inputted by the user). the Schedule
	 *				should contain the flight number, gate numbers, name of the airline of that flight, boarding time,
	 *				departing time, duration of the flight (i.e. origin for arriving flights and destinations for the
	 *				departing flights). Sort the schedule in ascending order of the boarding time.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: None
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
    	public static void queryThree(Statement stmt) { 
    		String inputDate = "";
			while(true) {
			System.out.println("Please select a date in June in the following format: ('DD-MON-YYYY')");
			if(gScan.hasNextLine()) {
				inputDate = gScan.nextLine().toUpperCase();
			}
			if(inputDate.length() != 11) {
				System.out.println("The input date is incorrect. Please Try Again.");
			} else if(!inputDate.matches("\\d{2}-...-\\d{4}")) {
				System.out.println("The format of the input date is incorrect. "
						   + "Make sure it is (DD-MON-YYYY). Try Again.");
			} else {
				break;
			}
		}

		String query = "SELECT flightno, boardinggate, aname, boardingtime, departingtime, duration, depart, arrive "
						+ "FROM mfmarquez.flight, mfmarquez.airline WHERE mfmarquez.flight.airlineno = "
						+ "mfmarquez.airline.airlineno AND mfmarquez.flight.departingdate = '" + inputDate 
						+ "' ORDER BY mfmarquez.flight.departingtime ASC";

		ResultSet answer = null;

		try {
			answer = stmt.executeQuery(query);

			if(answer != null) {
				System.out.println("The scheduled flights for " + inputDate + " are as follows:");
				System.out.println();
				while(answer.next()) {
						System.out.println("Flight number: " + answer.getString("flightno")
							+ "\nBoarding gate: " + answer.getString("boardinggate")
							+ "\nAirline: " + answer.getString("aname")
							+ "\nBoarding Time: " + answer.getString("boardingtime").substring(11)
							+ "\nDeparting Time: " + answer.getString("departingtime").substring(11)
							+ "\nFlight duration: " + answer.getString("duration")
							+ "\nRoute: " + answer.getString("depart") + " - " + answer.getString("arrive"));
						System.out.println();
				}
			}
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
	                    + "Could not fetch query 3 results.");
	                System.err.println("\tMessage:   " + e.getMessage());
	                System.err.println("\tSQLState:  " + e.getSQLState());
	                System.err.println("\tErrorCode: " + e.getErrorCode());
					e.printStackTrace();
	                System.exit(-1);
		}
    	}
    
	/* ---------------------------------------------------------------------------------------
	 *  Method queryFour(Statement stmt)
	 *  Purpose:  This function runs the following query then displays the results to the user:
	 * 				Print the list for the three categories of passengers (student, frequent flyer, and veteran
	 *				for each of the following queries for a particular airline of your choice who: 
	 *				traveled only once in the month of March, traveled with exactly one checked in bag anytime in the months of June
	 *				and July, and ordered snacks/beverages on at least one flight.
	 * 
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: Each of the 9 queries has been executed successfully
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
    public static void queryFour(Statement stmt) { 
		String c1 = "Student";
		String c2 = "Frequent-Flyer";
		String c3 = "Veteran";
		// strings for students 
		String s1 = query4StringBuilder(c1, 1);
		String s2 = query4StringBuilder(c1, 2);
		String s3 = query4StringBuilder(c1, 3);

		//strings for ffs
		String f1 = query4StringBuilder(c2, 1);
		String f2 = query4StringBuilder(c2, 2);
		String f3 = query4StringBuilder(c2, 3);

		//strings for vets
		String v1 = query4StringBuilder(c3, 1);
		String v2 = query4StringBuilder(c3, 2);
		String v3 = query4StringBuilder(c3, 3);

		// print out the overall query 
		System.out.println("Printing the of all 3 catgories of passengers (Student, Frequent Flyer, and Veterans) who, " + 
						   "on Delta Airlines, fall into 1 of the following three categories:");
		System.out.println("\t1.) Traveled only once in the month of March.");
		System.out.println("\t2.) Traveled with exactly one checked in bag anytime in the months of June and July.");
		System.out.println("\t3.) Ordered snacks/beverages on at least one flight.");
 
		// now go through the 9 query strings by calling query4Executor on each one
		System.out.println("------------------------------Students Who...------------------------------");		
		System.out.println("----> traveled only once in the month of March");	
		query4Executor(stmt, s1);
		System.out.println("----> Traveled with exactly one checked bag anytime in the months of June and July.");	
		query4Executor(stmt, s2);
		System.out.println("--> ordered snack/beverages on at least one flight");	
		query4Executor(stmt, s3);

		System.out.println("------------------------------Frequent Flyers------------------------------");		
		System.out.println("----> traveled only once in the month of March");	
		query4Executor(stmt, f1);
		System.out.println("----> Traveled with exactly one checked bag anytime in the months of June and July.");	
		query4Executor(stmt, f2);
		System.out.println("--> ordered snack/beverages on at least one flight");	
		query4Executor(stmt, f3);

		System.out.println("------------------------------Veteran------------------------------");		
		System.out.println("----> traveled only once in the month of March");	
		query4Executor(stmt, v1);
		System.out.println("----> Traveled with exactly one checked bag anytime in the months of June and July.");	
		query4Executor(stmt, v2);
		System.out.println("--> ordered snack/beverages on at least one flight");	
		query4Executor(stmt, v3);

    }
	/**
	 * Method: query4StringBuilder(String category, int whichOne)
	 * Purpose: builds one of the 3 large queries based off of which category is passed in. 
	 * Parameters: category - must either be student, frequent-flyer, or veteran
	 * 			   whichOne - must be 1, 2, or 3. 
	 */
	private static String query4StringBuilder(String category, int whichOne){
		String query = "";
		if (whichOne == 1){
			// part 1: traveled only once in month of march, for all 3 categories
			query = 
				"SELECT Passenger.passengerNo, passenger.firstname, passenger.lastname " + 
				"FROM mfmarquez.Passenger join mfmarquez.PassFlight on (Passenger.passengerNo = passFlight.passengerNo) " + 
				"JOIN mfmarquez.Flight on (flight.flightNo = passFlight.flightNo) " +
				"JOIN mfmarquez.passCategory ON (passCategory.passengerNo = passflight.passengerNo) " +  
				"WHERE EXTRACT(MONTH FROM Flight.departingDate) = 3 AND Flight.airlineNo = 4 " +
				"AND passCategory.category = '" + category +   
				"' GROUP BY Passenger.PassengerNo, passenger.firstname, passenger.lastname HAVING COUNT(Passenger.passengerNo) = 1";
		} else if (whichOne == 2){
			// part 2: traveled with exactly checked bag in june and july, for all 3 categories
			query = "SELECT distinct Passenger.passengerNo, passenger.firstname, passenger.lastname " + 
					"FROM mfmarquez.Flight join mfmarquez.passFlight on (Flight.flightNo = passFlight.flightNo) " + 
					"JOIN mfmarquez.passCategory on (passFlight.passengerNo = passCategory.passengerNo) " + 
					"JOIN mfmarquez.passenger on (passenger.passengerNo = passflight.passengerNo) " +
					"WHERE passflight.bagnumber = 1 AND Flight.airlineNo = 4 AND passCategory.category = '"  + category + 
					"' AND mfmarquez.passenger.passengerNo in (SELECT passenger.passengerNo " +  
							" FROM mfmarquez.passenger JOIN mfmarquez.passflight ON (passenger.passengerNo = passFlight.passengerNo) " +
							" JOIN mfmarquez.flight ON (flight.flightNo = passFlight.flightNo)" +
							" WHERE EXTRACT(MONTH FROM flight.departingDate) = 6) " +
					" AND mfmarquez.passenger.passengerNo in (SELECT passenger.passengerNo " +  
							" FROM mfmarquez.passenger JOIN mfmarquez.passflight ON (passenger.passengerNo = passFlight.passengerNo) " +
							" JOIN mfmarquez.flight ON (flight.flightNo = passFlight.flightNo)" +
							" WHERE EXTRACT(MONTH FROM flight.departingDate) = 7) ";
		} else {
			// part 3: ordered snacks/beverages on at least one flight, for all 3 categories
			query = "SELECT distinct Passenger.passengerNo, passenger.firstname, passenger.lastname "+
					"FROM mfmarquez.passFlight join mfmarquez.Flight on (Flight.flightNo = passFlight.flightNo) " + 
					"JOIN mfmarquez.passCategory ON (passFlight.passengerNo = passCategory.passengerNo) " +
					"JOIN mfmarquez.passenger ON (passFlight.passengerNo = passenger.passengerno) " + 
					"WHERE passFlight.orderedItem = 'T' AND Flight.airlineNo = 4 AND passCategory.category = '" + category + "'";
		}
		return query;
	}

	/* ---------------------------------------------------------------------------------------
	 *  Method query4Executor(Statement stmt, int query)
	 *  Purpose: this executes the 9 different queries of query 4. 
	 * 
	 *  Pre-Condition: Query 4 is being requested. Each of the 9 query strings has been executed 
	 *  
	 *  Post-Condition: Each of the 9 queries has been executed successfully
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 				String query - the queries themselves. 
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void query4Executor(Statement stmt, String query){
		ResultSet answer = null; // table of data representing the result from query1

		// now we need to execute the query 
		try {
			answer = stmt.executeQuery(query);
			System.out.println("PassengerNo\tFirst Name\tLast Name");
			if (answer != null) {
				// print out the header for the columns
				while (answer.next()) {
					System.out.println(answer.getString("passengerNo") + "\t"
					+ answer.getString("firstName")+ "\t" + answer.getString("lastName"));
				}
			}
			System.out.println();
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}
	}

	/* ---------------------------------------------------------------------------------------
	 *  Method query5(Statement stmt)
	 *  Purpose: this executes query 5
	 * 
	 *  Pre-Condition: the connection to the database has been made and query 5 has been queried
	 *  
	 *  Post-Condition: query 5 has been executed
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute query 5
	 *  
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
    	private static void queryFive(Statement stmt){
		/*
		 * Query 5: diplay the total amount of flights and passengers for each month and then report
		 *          which month saw the largest amount of flights and passengers
		 */
		// first create a hashMap where the keys are Integers (1-12) to represent months and the 
		// values are arraylists of integers. The first value will be represent the amount of flights 
		// in that month and the second will represent the amount of passengers in that month.  
		// set up the arraylist so that it contains 12 keys (1-12) and that each key has an arraylist of 
		// 2 values set to 0
		HashMap<Integer, ArrayList<Integer>> table = new HashMap<Integer, ArrayList<Integer>>();
		for (int i = 1; i <= 12; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.add(0);temp.add(0);
			table.put(i, temp);
		}

		// Now set up the query for the amount of flights in a month
		ResultSet answer = null; 
		String query1 = "SELECT EXTRACT(MONTH FROM Flight.departingDate), COUNT(distinct Flight.flightNo) FROM mfmarquez.flight "+ 
						"GROUP BY EXTRACT(MONTH FROM Flight.departingDate)";

		// this block will get the amount of flights in each month
		try {
			answer = stmt.executeQuery(query1);
			ResultSetMetaData meta = answer.getMetaData();

			// get the column names
			String mColumn = meta.getColumnName(1);
			String fColumn = meta.getColumnName(2);
			if (answer != null) {
				while (answer.next()) {
					// for each answer, get the month and the amount of flights in said month 
					// then get the array from hashmap and insert the amount of flights
					int month = answer.getInt(mColumn);
					int flightNumber = answer.getInt(fColumn);
					ArrayList<Integer> arr = table.get(month);
					arr.set(0, flightNumber);
					table.replace(month, arr);
				}
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}


		String query2 = "SELECT EXTRACT (MONTH from Flight.departingdate), COUNT(distinct Passenger.passengerNo) FROM mfmarquez.passenger join mfmarquez.passFlight on " +
                        "(Passenger.passengerNo = PassFlight.passengerNo) join mfmarquez.Flight on " +
                        "(Flight.flightNo = PassFlight.flightNo) GROUP BY EXTRACT (MONTH FROM Flight.departingdate)";

		// this block will get the amount of flights in each month
		try {
			answer = stmt.executeQuery(query2);
			ResultSetMetaData meta = answer.getMetaData();

			// get the column names
			String mColumn = meta.getColumnName(1);
			String pColumn = meta.getColumnName(2);

			if (answer != null) {
				while (answer.next()) {
					// for each answer, get the month and the amount of passengers in said month 
					// then get the array from hashmap and insert the amount of passengers
					int month = answer.getInt(mColumn);
					int passNumber = answer.getInt(pColumn);
					ArrayList<Integer> arr = table.get(month);
					arr.set(1, passNumber);
					table.replace(month, arr);
				}
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}

		// now that the hashMap has been built and each month number has it's corresponding 
		// amount of flights and passengers in each month, we can display the results of 
		// the query and give which month had the highest amount of flights and passengers 
		System.out.println("Displaying the results for the query [Display the total amount of flights\n" +
						"and passengers for each month and then report which months saw the highest\n" + 
						"amount of passengers and the highest amount of flights]");
		System.out.println("MONTH\t\tFlights\t\tPassengers");

		// these 2 vars keep track of which month had the most flights and passengers.
		// first index is the month and the second is the amount
		String[] monthsStrings = {"January  ", "February ", "March    ", "April    ", "May      ", "June     ", "July     ", "August   ", "September", "October  ", "November ", "December "};
		int[] mostFs = {0,0};
		int[] mostPs = {0,0};
		for (int i = 1; i<=12; i++){
			int flights = table.get(i).get(0);
			int passengers = table.get(i).get(1);
			if (flights > mostFs[1]){
				mostFs[0] = i; 
				mostFs[1] = flights; 
			}
			if (passengers > mostPs[1]){
				mostPs[0] = i;
				mostPs[1] = passengers;
			}
			System.out.println(monthsStrings[i-1] +":\t" + flights + "\t\t" + passengers);
		}
		System.out.println("MONTH WITH HIGHEST FLIGHT VOLUME:");
		System.out.println("\tMonth: " + mostFs[0] + "\n\tFlights: " + mostFs[1]);
		System.out.println("MONTH WITH HIGHEST PASSENGER VOLUME:");
		System.out.println("\tMonth: " + mostPs[0] + "\n\tPassengers: " + mostPs[1] + "\n");
	}

	/* ---------------------------------------------------------------------------------------
	 *  Method deleteTuple(Statement stmt)
	 *  Purpose:  This is a sub-menu for deletes. It gives the user 6 deleted options to choose from then calls the associated function to 
	 * 				delete the tuples specified by user input.
	 *			
	 * 
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: None
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
    	private static void deleteTuple(Statement stmt) {
    		int chosen = 0; // holds the query number that the user has chosen
		// This continuously asks the user to choose a query, and once a query is chosen, this program calls a function to execute that
		// query. It exits the loop once a user types -1.
		while (chosen != -1) {
			System.out.println("Type in '1', '2', '3', '4','5', or '6' to either delete a passenger, employee, flight, reservation, benefit, or an employee from a flight"
					+ " respectively. Type in '-1' to go back to the main menu.");
			String temp = gScan.nextLine();
			if (temp.matches("-?\\d+")) {
				chosen = Integer.parseInt(temp);
				if (chosen == 1) {
					deletePassenger( stmt);
				} else if (chosen == 2) {
					deleteEmployee(stmt);
				} else if (chosen == 3) {
					deleteFlight(stmt);
				} else if (chosen == 4) {
					deleteReservation(stmt);
				} else if (chosen == 5) {
					deleteBenefit(stmt);
				} else if (chosen == 6) {
					deleteEmployeeFromFlight(stmt);
				} else if (chosen == -1) {
					return;
				} else {
					System.out.println("Enter a valid choice.");
				}
			} else {
				System.out.println("Enter an integer");
			}
		}
    	}
    
	/* ---------------------------------------------------------------------------------------
	 *  Method deletePassenger(Statement stmt)
	 *  Purpose:  This function prompts the user to enter a passengerNo to be deleted. It then
	 * 				goes through all tables involving passengerNo and deletes all records associated
	 * 				with the passengerNo specified by the user.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: if passengerNo exists, then all instances of it are removed from the database
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
    	private static void deletePassenger(Statement stmt) { 
		String passNo = "";
		int passengerNo = 0;
		while(true) {
    		System.out.println("Please enter the passenger number to delete: ");
			passNo = gScan.nextLine();
			if(passNo.matches("\\d+")) {
				passengerNo = Integer.parseInt(passNo);
				break;
			} else if(passNo.equals("-1")) {
				return;
			} else {
				System.out.println("Please input a valid passenger number.");
			}
		}
			
		String delQuery = "DELETE FROM mfmarquez.passenger WHERE passengerNo = " + passengerNo;
		try {
			stmt.executeUpdate(delQuery);
			stmt.executeUpdate("DELETE FROM mfmarquez.passflight WHERE passengerNo = " + passengerNo);
			stmt.executeUpdate("DELETE FROM mfmarquez.passcategory WHERE passengerNo = " + passengerNo);
		
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
                + "Issues with deleting from multiple tables in DEL passenger.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            System.exit(-1);
		}
		System.out.println("Passenger with passenger number " + passengerNo  
							+ " has been deleted from all relevant tables.");
		
    }
    
	/* ---------------------------------------------------------------------------------------
	 *  Method deleteEmployee(Statement stmt)
	 *  Purpose:  This function prompts the user to enter an employeeNo to be deleted. It then
	 * 				goes through all tables involving employeeNo and deletes all records associated
	 * 				with the employeeNo specified by the user.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: if employeeNo exists, then all instances of it are removed from the database
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void deleteEmployee(Statement stmt) { 
		int employeeNo = 0;
		String empNo = "";
		while(true) {
    		System.out.println("Please enter the employee number to delete: ");
			empNo = gScan.nextLine();
			if(empNo.matches("\\d+")) {
				employeeNo = Integer.parseInt(empNo);
				break;
			} else if(empNo.equals("-1")) {
				return;
			} else {
				System.out.println("Please enter a valid employee number.");
			}
		}

		String delQuery = "DELETE FROM mfmarquez.employee WHERE employeeNo = " + employeeNo;
		try {
			stmt.executeUpdate(delQuery);
			stmt.executeUpdate("DELETE FROM mfmarquez.empflight WHERE employeeNo = " + employeeNo);

		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
                		+ "Issues with deleting from multiple tables in DEL employee.");
            		System.err.println("\tMessage:   " + e.getMessage());
            		System.err.println("\tSQLState:  " + e.getSQLState());
            		System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            		System.exit(-1);
		}
		System.out.println("Employee with employee number " + employeeNo  
							+ " has been deleted from all relevant tables.");
	}
	
	/* ---------------------------------------------------------------------------------------
	 *  Method frequentFlyerDelCheck(Statement stmt, int flightNo)
	 *  Purpose:  This function collects the passengers from the passflight list and determines 
	 * 			  if the flight deletion causes a passenger to fall out of the frequent flyer 
	 * 			  benefits category.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: Will drop passengers frequent-flyer status if flight removal causes them 
	 * 					to go below 10 flights in a year.
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void frequentFlyerDelCheck(Statement stmt, int flightNo) {
		ResultSet checker = null;
		ArrayList<Integer> passengers = new ArrayList<Integer>();
		String checkQuery = "SELECT passengerno FROM mfmarquez.passflight where flightno = " + flightNo;

		try {
			checker = stmt.executeQuery(checkQuery);
			if(checker != null) {
				while(checker.next()) {
					passengers.add(checker.getInt("passengerno"));
				}
			}
		} catch (SQLException e) {
						System.err.println("*** SQLException:  "
								 + "Issues with checking frequent flyer for delete flight.");
						System.err.println("\tMessage:   " + e.getMessage());
						System.err.println("\tSQLState:  " + e.getSQLState());
						System.err.println("\tErrorCode: " + e.getErrorCode());
						e.printStackTrace();
						System.exit(-1);
		}
		for(int i = 0; i < passengers.size() ; i++) {
			addingAndRemovingFrequentFliers(stmt, passengers.get(i), false);
		}
	}
	
	/* ---------------------------------------------------------------------------------------
	 *  Method deleteFlight(Statement stmt)
	 *  Purpose:  This function prompts the user to enter an flightNo to be deleted. It then
	 * 				goes through all tables involving flightNo and deletes all records associated
	 * 				with the flightNo specified by the user.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: if flightNo exists, then all instances of it are removed from the database
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void deleteFlight(Statement stmt) { 
		int flightNo = 0;
		String flyNo = "";
		while(true) {
    		System.out.println("Please enter the flight number to delete: ");
			flyNo = gScan.nextLine();
			if(flyNo.matches("\\d+")) {
				flightNo = Integer.parseInt(flyNo);
				break;
			} else if(flyNo.equals("-1")) {
				return;
			} else {
				System.out.println("Please enter a valid flight number.");
			}
		}

		frequentFlyerDelCheck(stmt, flightNo);
		String delQuery = "DELETE FROM mfmarquez.flight WHERE flightNo = " + flightNo;
		try {

			stmt.executeUpdate(delQuery);
			stmt.executeUpdate("DELETE FROM mfmarquez.passflight WHERE flightNo = " + flightNo);
			stmt.executeUpdate("DELETE FROM mfmarquez.empflight WHERE flightNo = " + flightNo);	
		
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
		                + "Issues with deleting from multiple tables in DEL flight.");
            		System.err.println("\tMessage:   " + e.getMessage());
            		System.err.println("\tSQLState:  " + e.getSQLState());
            		System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            		System.exit(-1);
		}
		System.out.println("Flight with flight number " + flightNo  
							+ " has been deleted from all relevant tables.");
	}
	
	/* ---------------------------------------------------------------------------------------
	 *  Method deleteReservation(Statement stmt)
	 *  Purpose:  This function prompts the user to enter an flightNo and passengerNo associated with
	 * 				the reservation that is to be deleted. It then deletes all instances in the passFlight
	 * 				relation that have the specified passengerNo and flightNo.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: if flightNo and passengerNo exists, then all instances of it are removed from the passFlight table
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void deleteReservation(Statement stmt) { 
		int passengerNo = -1;
		int flightNo = -1;
		String passNo = "";
		String flyNo = "";
		while(true) {
    		System.out.println("Please enter the passenger number to delete their flight reservation: ");
			passNo = gScan.nextLine();
			if(passNo.matches("\\d+")) {
				passengerNo = Integer.parseInt(passNo);
				if(passengerNoExists(stmt, passengerNo)) {
					break;
				} else {
					System.out.println("The passenger does not exist. Pick a valid passenger.");
				}
			} else if(passNo.equals("-1")) {
				return;
			} else { 
				System.out.println("Enter a valid passenger number.");
			}
		}
		while(true) {
            System.out.println("Enter the flight number for the flight reservation: ");
            flyNo = gScan.nextLine();
            if(flyNo.matches("\\d+")) {
                flightNo = Integer.parseInt(flyNo);
				if(flightNoExists(stmt,flightNo)) {
					break;
				} else {
					System.out.println("This flight does not exist. Pick a valid flight.");
				}
        	} else if(flyNo.equals("-1")) {
				return;
			} else {
                System.out.println("Enter a valid flight number.");
            }
    	}
		String delQuery = "DELETE FROM mfmarquez.passflight WHERE passengerNo = " + passengerNo + " AND flightno = " + flightNo;

		try {
			addingAndRemovingFrequentFliers(stmt, passengerNo, false);
			stmt.executeUpdate(delQuery);

		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
                		+ "Issues with deleting from table(s) in DEL reservation.");
            		System.err.println("\tMessage:   " + e.getMessage());
            		System.err.println("\tSQLState:  " + e.getSQLState());
            		System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            		System.exit(-1);
		}
		System.out.println("Reservation with passenger number " + passengerNo + "and flight number " + flightNo 
							+ " has been deleted from the reserved flight table.");
	}

	/* ---------------------------------------------------------------------------------------
	 *  Method deleteBenefit(Statement stmt)
	 *  Purpose:  This function prompts the user to enter an Category and passengerNo associated with
	 * 				a benefit that is to be deleted. It then deletes all instances in the passCategory
	 * 				relation that have the specified passengerNo and Category.Ex: person is not a 
	 * 				student anymore, etc.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: if category and passengerNo exists, then all instances of it are removed 
	 * 					from the passCategory table
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void deleteBenefit(Statement stmt) { 
		int passengerNo = 0;
		String passNo = "";
		String benType = "";
		while(true) {
    		System.out.println("Please enter the passenger number to delete their benefits: ");
			passNo = gScan.nextLine();
			if(passNo.matches("\\d+")) {
				passengerNo = Integer.parseInt(passNo);
				if(passengerNoExists(stmt, passengerNo)) {
					break;
				} else {
					System.out.println("This is not a valid passenger. Pick a valid passenger.");
				}
			} else if(passNo.equals("-1")){
				return;
			} else {
				System.out.println("Enter a valid passenger number.");
			}
		}
		System.out.println("Enter which benefit is being removed: (Student, or Veteran)");
        if(gScan.hasNextLine()) {
			benType = gScan.nextLine();
        }
		if(benType.equals("Student") || benType.equals("Veteran")) {
			if(!isStudentOrVeteran(stmt,passengerNo,benType)) {
				System.out.println("This passenger is not a " + benType + ". Returning to delete menu.");
				return;
			}
			String delQuery = "DELETE FROM mfmarquez.passcategory WHERE passengerNo = " + passengerNo
							+ " AND category = '" + benType + "'";
			try {
				stmt.executeUpdate(delQuery);
			} catch (SQLException e) {
				System.err.println("*** SQLException:  "
                	+ "Issues with deleting from table(s) in DEL benefits.");
            	System.err.println("\tMessage:   " + e.getMessage());
            	System.err.println("\tSQLState:  " + e.getSQLState());
            	System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
            	System.exit(-1);
			}
		} else if(benType.equals("-1")){
			return;
		} else {
			System.out.println("Enter Student or Veteran benefits to remove.");
		}
		System.out.println("Passenger with passenger number " + passengerNo  
							+ " has had their benefits deleted.");	
	}

	/* ---------------------------------------------------------------------------------------
	 *  Method deleteEmployeeFromFlight(Statement stmt)
	 *  Purpose:  This function prompts the user to enter an flightNo and employeeNo associated with
	 * 				the reservation that is to be deleted. It then deletes all instances in the empFlight
	 * 				relation that have the specified employeeNo and flightNo.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: if flightNo and employeeNo exists, then all instances of it are removed from the empFlight table
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void deleteEmployeeFromFlight(Statement stmt) {
		int employeeNo = -1;
		int flightNo = -1;
		String empNo = "";
		String flyNo = "";
		while(true) {
			System.out.println("Please enter the employee number to delete from all flights: ");
			empNo = gScan.nextLine();
			if(empNo.matches("\\d+")) {
				employeeNo = Integer.parseInt(empNo);
				if(employeeNoExists(stmt, employeeNo)) {
					break;
				} else {
					System.out.println("Employee not valid. Enter a valid employee number.");
				}
			} else if(empNo .equals("-1")) {
				return;
			} else {
				System.out.println("Enter a valid employee number.");
			}
		}
		while(true) {
			System.out.println("Now enter the flight number you wish to remove the employee from: ");
			flyNo = gScan.nextLine();
			if(flyNo.matches("\\d+")) {
				flightNo = Integer.parseInt(flyNo);
				if(flightNoExists(stmt, flightNo)) {
					break;
				} else {
					System.out.println("Flight does not exist. Enter a valid flight number.");
				}
			} else if(flyNo.equals("-1")) {
				return;
			} else {
				System.out.println("Enter a valid flight number.");
			}
		}
		String delQuery = "DELETE FROM mfmarquez.empflight WHERE employeeNo = " + employeeNo + "AND flightno = " + flightNo;

		try {
			stmt.executeUpdate(delQuery);
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
                	+ "Issues with deleting from table(s) in DEL benefits.");
            		System.err.println("\tMessage:   " + e.getMessage());
            		System.err.println("\tSQLState:  " + e.getSQLState());
            		System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            		System.exit(-1);
		}
		System.out.println("Employee with employee number " + employeeNo   
							+ " has been deleted from flight " + flightNo);
	}
    
	/* ---------------------------------------------------------------------------------------
	 *  Method insertTuple(Statement stmt)
	 *  Purpose:  This is a sub-menu for inserts. It gives the user 6 inserts options to choose from then calls the associated function to 
	 * 				insert the tuples specified by user input.
	 *			
	 * 
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: None
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
    	private static void insertTuple(Statement stmt) {
    		int chosen = 0; // holds the query number that the user has chosen
		// This continuously asks the user to choose a query, and once a query is chosen, this program calls a function to execute that
		// query. It exits the loop once a user types -1.
		while (chosen != -1) {
			System.out.println("Type in '1', '2', '3', '4', '5', or '6' to either insert a passenger, employee, flight, reservation, benefit, or employees onto flights"
					+ " respectively. Type in '-1' to go back to the main menu.");
			String temp = gScan.nextLine();
			if (temp.matches("-?\\d+")) {
				chosen = Integer.parseInt(temp);
				if (chosen == 1) {
					insertPassenger( stmt);
				} else if (chosen == 2) {
					insertEmployee(stmt);
				} else if (chosen == 3) {
					insertFlight(stmt);
				} else if (chosen == 4) {
					insertReservation(stmt);
				} else if (chosen == 5) {
					insertBenefit(stmt);
				} else if (chosen == 6) {
					System.out.println("Please enter flight number to add employees on");
					temp = gScan.nextLine();
					if (temp.matches("\\d+")) {
						insertEmployeesToFlight(stmt, Integer.parseInt(temp));
					} else {
						System.out.println("Please enter an integer");
					}
				}
			} else {
				System.out.println("Enter an integer.\n");
			}
		}
    	}
    
	/* ---------------------------------------------------------------------------------------
	 *  Method insertPassenger(Statement stmt)
	 *  Purpose:  This function prompts the user to enter information about a new passenger to be
	 * 				added, creates a passengerNo for the passenger, then inserts the user into
	 * 				the passenger relation. 
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: new passenger is added to the passenger relation.
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
    private static void insertPassenger(Statement stmt) { 
		ArrayList<String> months = new ArrayList<>(Arrays.asList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP","OCT","NOV","DEC"));
    	ResultSet answer = null; // table of data representing the result from query
    	int passengerNo = 0; // holds the passengerNo that is assigned to new passenger
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
    	 
	      try {
	            answer = stmt.executeQuery("SELECT MAX(passengerNo) AS maxNo FROM mfmarquez.passenger"); // finds the maximum passengerNo
	
	            if (answer != null) {
	                while (answer.next()) {
		                passengerNo = 1 + answer.getInt("maxNo"); //sets passengerNo to 1+max(passengerNo)
	                }
	            }
	        } catch (SQLException e) {
	                System.err.println("*** SQLException:  "
	                    + "Could not fetch query results.");
	                System.err.println("\tMessage:   " + e.getMessage());
	                System.err.println("\tSQLState:  " + e.getSQLState());
	                System.err.println("\tErrorCode: " + e.getErrorCode());
					e.printStackTrace();
	                System.exit(-1);
	        }

		String temp;
    	String query = "INSERT INTO mfmarquez.passenger VALUES (" + passengerNo + ", ";
    	System.out.println("Enter your first name");
    	if (gScan.hasNextLine()) {
			temp = gScan.nextLine();
			if (temp.equals("-1")){
				System.out.println("INSERT cancelled.");
				return;
			}
    		query += "'"+temp+"', "; //adds first name to insert statement
    	}
    	System.out.println("Enter your last name"); //adds last name to insert statement
    	if (gScan.hasNextLine()) {
			temp = gScan.nextLine();
			if (temp.equals("-1")){
				System.out.println("INSERT cancelled.");
				return;
			}
    		query += "'"+temp+"', ";
    	}
		while (true){
			System.out.println("Enter your birthday in the form 'DD-MON-YY':");
			if (gScan.hasNextLine()) {
				String birthday = gScan.nextLine();
				if (birthday.equals("-1")){
					System.out.println("INSERT cancelled.");
					return;
					//validating the birthday input
				} else if (!birthday.matches("\\d{2}-[a-zA-Z][a-zA-Z][a-zA-Z]-\\d{2}") || !months.contains(birthday.substring(3, 6).toUpperCase())){
					System.out.println("Enter a valid DD-MON-YY date");
				} else {
					try {
						dateFormat.parse(birthday);
						query += "'"+birthday.toUpperCase()+"')";  //adds birthday to insert statement
						break;
					} catch (ParseException e) {
						System.out.println("invalid birthday has been inputted");
					}
				}
			}
		}
    	try {
	        stmt.executeUpdate(query);
	        
		} catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results 2");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            System.exit(-1);
		}
    	System.out.println("Here is your passenger number: " + passengerNo 
    			+ "\nYou can use this number for creating reservations.");
    	
    }
	
	/* ---------------------------------------------------------------------------------------
	 *  Method insertEmployee(Statement stmt)
	 *  Purpose:  This function prompts the user to enter information about a new employee to be
	 * 				added, creates an employeeNo for the employee, then inserts the employee into
	 * 				the employee relation. 
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: new employee is added to the employee relation.
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void insertEmployee(Statement stmt) { 
		ArrayList<String> airlines = new ArrayList<>(Arrays.asList("United", "SouthWest", "Alaska", "Delta"));
		ResultSet answer = null; // table of data representing the result from query1
		String airline = ""; // holds the airline of the employee
    	int employeeNo = 0; // holds the assigned employeeNo
    	 
	      try {
	            answer = stmt.executeQuery("SELECT MAX(employeeNo) AS maxNo FROM mfmarquez.employee"); // gets maximum employeeNo in employee relation
	
	            if (answer != null) {
	                while (answer.next()) {
		                employeeNo = 1 + answer.getInt("maxNo"); // sets employeeNo to max(employeeNo) + 1
	                }
	            }
	        } catch (SQLException e) {
	                System.err.println("*** SQLException:  "
	                    + "Could not fetch query results.");
	                System.err.println("\tMessage:   " + e.getMessage());
	                System.err.println("\tSQLState:  " + e.getSQLState());
	                System.err.println("\tErrorCode: " + e.getErrorCode());
					e.printStackTrace();
	                System.exit(-1);
	        }

    	String query = "INSERT INTO mfmarquez.employee VALUES (" + employeeNo + ", ";
		String temp;
    	System.out.println("Enter employee first name");
    	if (gScan.hasNextLine()) {
			temp = gScan.nextLine();
			if (temp.equals("-1")){
				System.out.println("INSERT cancelled.");
				return;
			}
    		query += "'"+temp+"', ";  //adds first name to insert statement
    	}
    	System.out.println("Enter employee last name");
    	if (gScan.hasNextLine()) {
			temp = gScan.nextLine();
			if (temp.equals("-1")){
				System.out.println("INSERT cancelled.");
				return;
			}
    		query += "'"+temp+"', ";  //adds last name to insert statement
    	}
    	
		while (true){
			System.out.println("Enter airline employee is working for (United, SouthWest, Alaska, or Delta):");
			if (gScan.hasNextLine()) {
				airline = gScan.nextLine();
				if (airline.equals("-1")){
					System.out.println("INSERT cancelled.");
					return;
				} else if (!airlines.contains(airline)){ // validates airline
					System.out.println("Enter a valid airline. Make sure the capitalization is accurate.");
				} else {
					break;
				}
			}
		}
		String job;
		while (true){
			System.out.println("Enter job title: 'pilot', 'cabin-crew', or 'ground-crew':");
			if (gScan.hasNextLine()) {
				job = gScan.nextLine();
				if (job.equals("-1")){
					System.out.println("INSERT cancelled.");
					return;
				} else if (job.equals("pilot") || job.equals("cabin-crew") || job.equals("ground-crew")){ // validates the jobTitle
					break;
				} else {
					System.out.println("Enter a valid airline. Make sure the capitalization is accurate.");
				}
			}
		}
    	try {
    		answer = stmt.executeQuery("SELECT DISTINCT airlineNo FROM mfmarquez.airline WHERE aname = " + "'"+airline+"'"); // used to find airlineNo associated with airline
	    	
            if (answer != null) {
				if (answer.next()) query += answer.getInt("airlineNo") + ", " + "'"+job+"'" + ")"; 
            }
	        stmt.executeUpdate(query); //executes the insert
	        
	        
		} catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results 2");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            System.exit(-1);
		}
	}

	/* ---------------------------------------------------------------------------------------
	 *  Method insertFlight(Statement stmt)
	 *  Purpose:  This function prompts the user to enter information about a new flight to be
	 * 				added, creates an flight for the flight, then inserts the flight into
	 * 				the flight relation if there are no boarding gate overlaps or duration issues.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: new flight is added to the flight relation.
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void insertFlight(Statement stmt) { 
		//need to check that there are no overlapping times for a gate
		ArrayList<String> airlines = new ArrayList<>(Arrays.asList("United", "SouthWest", "Alaska", "Delta"));
		ResultSet answer = null; // table of data representing the result from query1
    	int flightNo = 0; // holds the new flightNo for flight being inserted
		String departingTime; // holds departing time as inputted by user
		String boardingTime; // holds departing time as inputted by user
		Date departingDate = new Date(); // holds departing date as a Date type
		String date; // holds departing date as inputted by user
		String gate = "";  // holds departing gate as inputted by user
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //holds the date format for parsing String into Date type
    	
	      try {
	            answer = stmt.executeQuery("SELECT MAX(flightNo) AS maxNo FROM mfmarquez.flight"); // gets max flightNo from flight relation
	
	            if (answer != null) {
	                while (answer.next()) {
		                flightNo = 1 + answer.getInt("maxNo"); // sets new flightNo to max(flightNo) + 1
	                }
	            }
	        } catch (SQLException e) {
	                System.err.println("*** SQLException:  "
	                    + "Could not fetch query results.");
	                System.err.println("\tMessage:   " + e.getMessage());
	                System.err.println("\tSQLState:  " + e.getSQLState());
	                System.err.println("\tErrorCode: " + e.getErrorCode());
					e.printStackTrace();
	                System.exit(-1);
	        }
	     
    	String query = "INSERT INTO mfmarquez.flight VALUES (" + flightNo + ", ";
    	while (true){
			System.out.println("Enter the boarding gate");
			if (gScan.hasNextLine()) {
				gate = gScan.nextLine();
				if (gate.equals("-1")){
					System.out.println("INSERT cancelled.");
					return;
				}
				else if (!gate.matches("[A-Z]\\d{1,2}")){ // validates gate format
					System.out.println("Didn't match. Put in a valid gate please.");
				} else {
					query += "'"+gate+"', "; // adds gate number to query
					break;
				}
			}
		}
    	System.out.println("Enter the departing time in the form HH:MM in 24 hour format");
		while (true){
			if (gScan.hasNextLine()) {
				//System.out.println("Enter airline the flight is associated with (united, southwest, alaska, or delta:");
				departingTime = gScan.nextLine();
				if (departingTime.matches("\\d{2}:\\d{2}")){ // validates departure time
					if (!(Integer.parseInt(departingTime.substring(0, 2)) > 23) && !(Integer.parseInt(departingTime.substring(3, 5)) > 59)) {
						int hour = Integer.parseInt(departingTime.substring(0, 2)); // holds hours of departing time
						int min = Integer.parseInt(departingTime.substring(3, 5)); // holds minutes of departing time
						if (min < 30) {
							hour -= 1;
							min += 30;
						} else {
							min -= 30;
						}
						boardingTime = ""; // holds the boarding time
						if (hour<10) {boardingTime+="0";}
						boardingTime+= Integer.toString(hour)+":";
						if (min<10) {boardingTime+="0";}
						boardingTime+=Integer.toString(min);
						query += "TO_DATE('"+boardingTime+"', 'HH24:MI'), TO_DATE('"+departingTime+"', 'HH24:MI'), "; // enters bearding and departure time into the query
						break;
					} else {
						System.out.println("Enter a valid departure time.");
					}
				} else if (departingTime.equals("-1")){
					System.out.println("INSERT cancelled.");
					return;
				} else {
					System.out.println("Enter a valid departure time.");
				}
			}
		}
		
		while (true){
			System.out.println("Enter the departing date in the form yyyy-MM-dd");
			if (gScan.hasNextLine()) {
				date = gScan.nextLine();
				if (date.equals("-1")){
					System.out.println("INSERT cancelled.");
					return;
				} else if (!date.matches("\\d{4}-\\d{2}-\\d{2}")){ // validating the departure date
					System.out.println("Enter a valid yyyy-MM-dd date");
				} 
				else {
					try {
						departingDate = dateFormat.parse(date);
						query += "TO_DATE('" + date + "', 'YYYY-MM-DD'), "; //adding departure date to the query
						break;
					} catch (ParseException e) {
						System.out.println("invalid date has been inputted");
					}
				}
			}
		}

		//validating the boarding gate
		Date departureTime = addDateAndTime(departingDate, departingTime);
		Date boardTime = addDateAndTime(departingDate, boardingTime);
		if (!validateBoardingGate(stmt, gate, flightNo, departingDate,boardTime, departureTime)) {
			System.out.println("Time overlaps with another flight at the same gate. Will return to menu.");
			return;
		}
		while (true){
			System.out.println("Enter the duration of the flight in the form H:MM");
			if (gScan.hasNextLine()) {
				String dur = gScan.nextLine();
				if (dur.equals("-1")){
					System.out.println("INSERT cancelled.");
					return;
				} else if (!dur.matches("[0-9]:[0-5][0-9]")){ // validating the duration format
					System.out.println("Enter a valid  H:MM duration");
				} else {
					Date landingTime = addDateAndTime(departureTime, dur);
					if (dur.compareTo("5:00") > 0) {System.out.println("The flight duration needs to be under 5 hours");} 
					else if (validateDuration(landingTime, departureTime)) {
						query += "'"+dur+"', ";
						break;
					} else {
						System.out.println("The flight goes into the next day. Change the duration to be shorter.");
					}
				}
			}
		}
		String temp;
		//need to validate that the flight starts and ends on the same day.
    	System.out.println("Enter the city you are departing from");
    	if (gScan.hasNextLine()) {
			temp = gScan.nextLine();
			if (temp.equals("-1")){
				System.out.println("INSERT cancelled.");
				return;
			}
    		query += "'"+temp+"', "; // adds the from city to the query
    	}
		System.out.println("Enter the city you are arriving to");
    	if (gScan.hasNextLine()) {
			temp = gScan.nextLine();
			if (temp.equals("-1")){
				System.out.println("INSERT cancelled.");
				return;
			}
    		query += "'"+temp+"', "; // adds to city to the query
    	}
    	String airline = "";
    	while (true){
			System.out.println("Enter airline the flight is associated with (United, SouthWest, Alaska, or Delta:");
			if (gScan.hasNextLine()) {
				airline = gScan.nextLine();
				if (airline.equals("-1")){
					System.out.println("INSERT cancelled.");
					return;
				} else if (!airlines.contains(airline)){ // validates the airline
					System.out.println("Enter a valid airline. Make sure capitalization is correct");
				} else {
					break;
				}
			}
		}
    	
    	try {
    		answer = stmt.executeQuery("SELECT DISTINCT airlineNo FROM mfmarquez.airline WHERE aname = " + "'"+airline+"'"); // gets airline number associated with airline
            if (answer != null) {
				if (answer.next()) query += answer.getInt("airlineNo") + ")";  // adds airlineNo to query
            }
	        stmt.executeUpdate(query); // executes insert statement
			// allows user to add employees to the flight after flight is inserted
	        System.out.println("Do you want to add employees to this flight right now? Type 'yes' or 'no'."); 
	    	if (gScan.hasNextLine()) {
	    		if (gScan.nextLine().equals("yes")) {
	    			insertEmployeesToFlight(stmt, flightNo); 
	    		}
	    	}
	        
		} catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results 2");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            System.exit(-1);
		}
    	System.out.println("Here is the flight number: " + flightNo 
    			+ "\nYou can use this number for creating reservations.");
	}
	
	/* ---------------------------------------------------------------------------------------
	 *  Method insertReservation(Statement stmt)
	 *  Purpose:  This function prompts the user to enter information about a new reservation to be
	 * 				added. The user must input the passengerNo, flightNo, and bag number. This function
	 * 				ensures there are no overlapping flights with this one for the passenger 
	 * 				and ensures their number of bags does not exceed the limit depending on their status.
	 * 				THis then creates an reservation for the passenger on the flight in the PassFlight.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: new reservation in added into the PassFlight relation.
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void insertReservation(Statement stmt) {
		//need to check the flight doesn't overlap with other flights the passenger is on. Make function for this.
		// also should have option where the user can check what flights are available. (already created the method)
		int passNo = 0; // passengerNo inserted by user
    		String query = "INSERT INTO mfmarquez.passflight VALUES (";
		String temp; // holds inputs of user
		String oItem = "";
		while(true){
			System.out.println("Enter the passenger number");
			temp = gScan.nextLine();
			if (temp.matches("-?\\d+")){
				passNo = Integer.parseInt(temp);
				if(passNo == -1) {
					System.out.println("INSERT cancelled.");
					return;
				}
				if (passengerNoExists(stmt, passNo)){
					//query += temp +", ";
					break;
				} else {
					System.out.println("Please enter a valid passenger number");
				}
			} else {
				System.out.println("You must enter a number");
			}
		}
		while(true){
			System.out.println("Enter the flight number this passenger should be added on. Enter 'f' if you want to see the list of flights");
			temp = gScan.nextLine();
			if (temp.matches("-?\\d+")){
				int flightNo = Integer.parseInt(temp);
				if (flightNo == -1) {
					return;
				}
				if (flightNoExists(stmt, flightNo)){
					if (checkForUsersNewFlightOverlaps(stmt, passNo, flightNo, -1, false, true)) { //checks for flight overlaps with the passenger
						query += temp + ", " + passNo +", "; // if no overlaps, then adds the passNo and flightNo to query
						break;
					} else {
						System.out.println("This flight overlaps with another one of this passenger's booked flights. Please choose another flight.");
					}
				} else {
					System.out.println("Please enter a valid flight number");
				}
			} else {
				if (temp.equals("f")) {
					viewFlightInfo(stmt);
				} else {
					System.out.println("You must enter a number");
				}
			}
		}

		while(true){
			System.out.println("How many bags will this passenger take on the flight?");
			temp = gScan.nextLine();
			boolean isStudent = isStudentOrVeteran(stmt, passNo, "Student"); // represents whether passenger is student or not
			if (temp.matches("-?\\d+")){
				int num = Integer.parseInt(temp);
				if (num == -1){
					System.out.println("INSERT cancelled");
					return;
				} //this validates the number of bags checked in depending on if user is student or not
				if (isStudent){
					if (num >= 0 && num <=5){
						query += temp + ", ";
						break;
					} else {
						System.out.println("Bag number for student must be between 0 and 5");
					}
				} else {
					if (num >= 0 && num < 3){
						query += temp + ", ";
						break;
					} else {
						System.out.println("Bag number must be between 0 and 2");
					}
				}
			} else {
				System.out.println("You must enter a number");
			}
		}
		System.out.println("Are you planning on ordering anything on the flight?");
		oItem =gScan.nextLine();
		if(oItem.toLowerCase().equals("yes")) {
			query += "'T')";
		} else if(oItem.toLowerCase().equals("no")) {
			query += "'F')";
		} else {
			query += "NULL)";
		}
    	
    	try {
    		stmt.executeUpdate(query);    //executes insert statement
			addingAndRemovingFrequentFliers(stmt, passNo, true); // checks if passenger has become frequent flier after adding flight and makes them one if so.
		} catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results 2");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            System.exit(-1);
		}
	}
	
	/* ---------------------------------------------------------------------------------------
	 *  Method insertBenefit(Statement stmt)
	 *  Purpose:  This function prompts the user to enter information about a new benefit to be
	 * 				added. The user must input the passengerNo adn a category to add. This function
	 * 				ensures that the passenger is not already associated with the benefit before adding it.
	 * 				THis then creates a new benefit for the passenger in the PassBenefit relation.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: new benefit in added into the PassBenefit relation.
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void insertBenefit(Statement stmt) {
    	String query = "INSERT INTO mfmarquez.passcategory VALUES (";
		int passengerNo; // holds the passengerNo inputted by user
		String temp; // holds the current user input
		while(true){
			System.out.println("Enter your passenger number");
			temp = gScan.nextLine();
			if (temp.matches("-?\\d+")) {
				passengerNo = Integer.parseInt(temp);
				if (passengerNo == -1) {return;}
				if (passengerNoExists(stmt, passengerNo)) { // validates passengerNo
					query += passengerNo + ", "; //adds passengerNo to the query
					break;
				} else {
					System.out.println("Input a valid passenger number.");
				}
			} else {
				System.out.println("Input must be an integer");
			}
		}
		while (true) {
			System.out.println("Type '1' if you are a student and want to have the associated benefits "
    			+ "or type '2' if you are a veteran and want to add the associated benefits");
    	
			temp = gScan.nextLine();
			if (temp.matches("-?\\d+")) {
				int input = Integer.parseInt(temp);
				if (input == -1) {
					return;
				}
				else if (input == 1) {//adds passenger to the student category if not already a student
					if (isStudentOrVeteran(stmt, passengerNo, "Student")) {System.out.println("You're already a student.");}
					else {
						query += "'Student')";
						break;
					}
				}
				else if (input == 2) { //adds passenger to the veteran category if not already a veteran
					if (isStudentOrVeteran(stmt, passengerNo, "Veteran")) {System.out.println("You're already a veteran.");}
					else {
						query += "'Veteran')";
						break;
					}
				}
				else {System.out.println("Input must either be a 1 or 2");}
			} else {
				
				System.out.println("Input must be an integer");
			}
		}
    	
    	try {
    		stmt.executeUpdate(query);    // executes the insert into the passCategory relation
		} catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results 2");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            System.exit(-1);
		}
	}

	/* ---------------------------------------------------------------------------------------
	 *  Method insertEmployeesToFlight(Statement stmt, int flightNo)
	 *  Purpose:  This function prompts the user to enter information about a new reservation to be
	 * 				added. The user must input the employeeNo. This function
	 * 				ensures there are no overlapping flights with this one for the employee 
	 * 				THis then creates an reservation for the employee on the flight in the EmpFlight.
	 *			
	 *  Pre-Condition: flightNo must be a valid flightNo.
	 *  
	 *  Post-Condition: new reservation in added into the EmpFlight relation.
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 				int flightNo - the flightNo to add the employee onto
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void insertEmployeesToFlight(Statement stmt, int flightNo) {
		//need to check the flight doesn't overlap with other flights the employee is on
		int employeeNo = 0; // holds the employee number that the user has chosen
		// This continuously asks the user to choose a query, and once a query is chosen, this program calls a function to execute that
		// query. It exits the loop once a user types -1.
		ResultSet answer = null; 
		while (true) {
			System.out.println("Type in an employeeNo to add to flight " + flightNo + ". Type in '-1' to go back to the main menu. Type in"
					+ "'-2' to see employee information.");
			String temp = gScan.nextLine();
			if (temp.matches("-?\\d+")) {
				employeeNo = Integer.parseInt(temp);
				if (employeeNo == -1) {return;}
				if (employeeNo == -2) {
					viewEmployeeInfo(stmt);
				} else if (employeeNo != -1 && employeeNoExists(stmt, employeeNo)){ //validates that employee exists
					int employeeAirline = -1; // holds the airline associated with the employee
					int flightAirline = -2; // holds the airline associated with the flight
					try {
						answer = stmt.executeQuery("SELECT DISTINCT airlineNo FROM mfmarquez.employee WHERE employeeNo=" + employeeNo);
						if (answer != null) {
							if (answer.next()) {
								employeeAirline = answer.getInt("airlineNo");
							}
						}
						answer = stmt.executeQuery("SELECT DISTINCT airlineNo FROM mfmarquez.flight WHERE flightNo=" + flightNo);
						if (answer != null) {
							if (answer.next()) {
								flightAirline = answer.getInt("airlineNo");
							}
						}

					} catch (SQLException e) {
						System.err.println("*** SQLException:  "
							+ "Could not fetch query results.");
						System.err.println("\tMessage:   " + e.getMessage());
						System.err.println("\tSQLState:  " + e.getSQLState());
						System.err.println("\tErrorCode: " + e.getErrorCode());
						e.printStackTrace();
						System.exit(-1);
					}
					try { 
						if (employeeAirline == flightAirline) { // checks if employee and flight are associated with the same airline. If not, then cannot execute insert.
								if (checkForUsersNewFlightOverlaps(stmt, employeeNo, flightNo, -1, true, true)) {
									stmt.executeUpdate("INSERT INTO mfmarquez.empflight VALUES (" + employeeNo + ", " + flightNo + ")"); // executes the insert in the empFlight relation
									break;
								} else {
									System.out.println("This flight overlaps with another one of this employee's booked flights. Please choose another flight.");
								}   
						} else {
							System.out.println("This employee does not work for the airline that the flight is associated with. Please choose another employee to add to this flight.");
						}
					} catch (SQLException e) {

			            System.err.println("*** SQLException:  "
			                + "Could not fetch query results 2");
			            System.err.println("\tMessage:   " + e.getMessage());
			            System.err.println("\tSQLState:  " + e.getSQLState());
			            System.err.println("\tErrorCode: " + e.getErrorCode());
						e.printStackTrace();
			            System.exit(-1);
					}
				} else if (employeeNo != -1 && !employeeNoExists(stmt, employeeNo)) {
					System.out.println("Enter a valid employee number");
				}
			} else {
				System.out.println("Enter an integer");
			}
		}
	}

	/* ---------------------------------------------------------------------------------------
	 *  Method addingAndRemovingFrequentFliers(Statement stmt, int passengerNo, boolean isInsert)
	 *  Purpose:  This function takes in a passengerNo and isInsert. If isInsert is true, then
	 * 				this function checks if the passenger should be added as a frequent flier
	 * 				(if number of flights > 10 in a year). If so, then it adds this to the 
	 * 				PassBenefit relation. If isInsert is false, it checks if the user should be removed
	 * 				as a frequent flier. If so, then it removes the benefit from the passBenefit relation.
	 *			
	 *  Pre-Condition: passengerNo must be valid.
	 *  
	 *  Post-Condition: None.
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 				int passengerNo - represents the passengerNo of passenger to be tested
	 * 				boolean isInsert - true if this is called after insert, false if called after delete
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void addingAndRemovingFrequentFliers(Statement stmt, int passengerNo, boolean isInsert) {
		ResultSet answer = null; // table of data representing the result from query1
		int count = 0; // holds the number of flights a passenger has in 2021
		try {
    		answer = stmt.executeQuery("SELECT COUNT(flightNo) AS count FROM mfmarquez.passFlight WHERE passengerNo = " + passengerNo);
            if (answer != null) {
				if (answer.next()) count = answer.getInt("count"); // gets number of flights passenger has in 2021
            }  
			//adds/removes passengenger as a frequent-flier if they meet the conditions.
			if (isInsert && count==10) {
				stmt.executeUpdate("INSERT INTO mfmarquez.passCategory VALUES ("+passengerNo+", 'Frequent-Flyer')"); 
			} else if (!isInsert && count == 9) {
				stmt.executeQuery("DELETE FROM mfmarquez.passCategory WHERE passengerNo = " + passengerNo + "AND category = 'Frequent-Flyer'");
			}
		} catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch information on passenger");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
            System.exit(-1);
		}
	}

	/* ---------------------------------------------------------------------------------------
	 *  Method viewFlightInfo(Statement stmt)
	 *  Purpose:  This function displays all flights in the flight relation.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: None.
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void viewFlightInfo(Statement stmt) {
		ResultSet answer = null; // table of data representing the result from query1
    	String query = "SELECT DISTINCT * FROM mfmarquez.flight, mfmarquez.airline WHERE mfmarquez.flight.airlineNo = mfmarquez.airline.airlineNo ORDER BY flight.flightno";
	      try {
	            answer = stmt.executeQuery(query);
	
	            if (answer != null) {
	                System.out.println("\nThe following lists all of the flights [" + query
	                                 + "] are:\n");
	
	                    // Get the data about the query result to learn
	                    // the attribute names and use them as column headers

	                ResultSetMetaData answermetadata = answer.getMetaData();
	                for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
	                    System.out.print(answermetadata.getColumnName(i) + "\t");
	                }
	                System.out.println();
	                
	
	                    // Use next() to advance cursor through the result
	                    // tuples and print their attribute values
	
	                while (answer.next()) {
		                System.out.println(answer.getString("flightNo") + "\t\t" + answer.getString("boardingGate") + "\t\t" + answer.getString("boardingTime").substring(11) +  "\t" 
				        + answer.getString("departingTime").substring(11) + "\t" + answer.getString("departingDate") + "\t"
						+ answer.getString("duration") + "\t" + answer.getString("depart") + "\t"+ answer.getString("arrive") + "\t"
						+ answer.getString("aname"));
	                }
	            }
	            System.out.println();
	        } catch (SQLException e) {
	                System.err.println("*** SQLException:  "
	                    + "Could not fetch query results.");
	                System.err.println("\tMessage:   " + e.getMessage());
	                System.err.println("\tSQLState:  " + e.getSQLState());
	                System.err.println("\tErrorCode: " + e.getErrorCode());
					e.printStackTrace();
	                System.exit(-1);
	        }
	}
	
	
	/* ---------------------------------------------------------------------------------------
	 *  Method viewEmployeeInfo(Statement stmt)
	 *  Purpose:  This function displays all employees in the employee relation.
	 *			
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: None.
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
	private static void viewEmployeeInfo(Statement stmt) {
		ResultSet answer = null; // table of data representing the result from query1
    	String query = "SELECT DISTINCT * FROM mfmarquez.employee, mfmarquez.airline WHERE mfmarquez.employee.airlineNo = mfmarquez.airline.airlineNo";
	      try {
	            answer = stmt.executeQuery(query);
	
	            if (answer != null) {
	                System.out.println("\nThe following lists all of the employees [" + query
	                                 + "] are:\n");
	
	                    // Get the data about the query result to learn
	                    // the attribute names and use them as column headers

	                ResultSetMetaData answermetadata = answer.getMetaData();
	                for (int i = 1; i <= answermetadata.getColumnCount(); i++) {
	                    System.out.print(answermetadata.getColumnName(i) + "\t");
	                }
	                System.out.println();
	                
	
	                    // Use next() to advance cursor through the result
	                    // tuples and print their attribute values
	
	                while (answer.next()) {
		                System.out.println(answer.getString("employeeNo") + "\t" + answer.getString("firstName") + "\t"
				        + answer.getString("lastName") + "\t" + answer.getString("aname") + "\t" + answer.getString("jobtitle"));
	                }
	            }
	            System.out.println();
	        } catch (SQLException e) {
	                System.err.println("*** SQLException:  "
	                    + "Could not fetch query results.");
	                System.err.println("\tMessage:   " + e.getMessage());
	                System.err.println("\tSQLState:  " + e.getSQLState());
	                System.err.println("\tErrorCode: " + e.getErrorCode());
					e.printStackTrace();
	                System.exit(-1);
	        }
	}
	
	/* ---------------------------------------------------------------------------------------
	 *  Method updateTuple(Statement stmt)
	 *  Purpose:  This is a sub-menu for updates. It gives the user 6 update options to choose from then calls the associated function to 
	 * 				update the tuples specified by user input.
	 *			
	 * 
	 *  Pre-Condition: None
	 *  
	 *  Post-Condition: None
	 * 
	 *  Parameters: Statement stmt - the statement object being used to execute these queries 
	 * 	
	 *  Return: void
	 *----------------------------------------------------------------------------------------*/
    private static void updateTuple(Statement stmt) {
    	int chosen = 0; // holds the query number that the user has chosen
		
		// This continuously asks the user to choose a query, and once a query is chosen, this program calls a function to execute that
		// query. It exits the loop once a user types -1.
		while (chosen != -1) {
			System.out.println("Type in '1', '2', '3', '4', or '5' to either update a passenger, employee, flight, reservation or employee assignment"
					+ " respectively. Type in '-1' to go back to the main menu.");
			String temp = gScan.nextLine();
			if (temp.matches("-?\\d+")) {
				chosen = Integer.parseInt(temp);
				if (chosen == 1) {
					updatePassenger( stmt);
				} else if (chosen == 2) {
					updateEmployee(stmt);
				} else if (chosen == 3) {
					updateFlight(stmt);
				} else if (chosen == 4) {
					updateReservation(stmt);
				} else if (chosen == 5){
					upadteEmpFlight(stmt);
				}
			} else {
				System.out.println("Enter an integer");
			}
		}
    }
    
	/*
	 * Method: updatePassenger(Statement stmt)
	 * Purpose: This method is used to update a passenger record in the passenger relation 
	 * Parameters: Statement stmt - used to execute queries 
	 * returns: void
	 */
    private static void updatePassenger(Statement stmt) { 
		// ----------------- MAY DELETE QUERY LATER ---------------------
		// reason being that we may not want to show this table to the user 
    	ResultSet answer = null; // result sets for the different sets 

		// now we need a passenger number to access the correct tuple
		// ask teh user to enter a valid passengerNo. To do this, do a query on the passenger table 
		// to see if the value exists by checking if the size of the result set is 1 
		int oldPassengerNo = 0; 
		while (true){
			System.out.println("Enter the Passenger Number for the passenger you want to update. " + 
				"Enter a -1 to exit. Enter 'f' to see the passenger list");
			String temp = gScan.nextLine();
			if (temp.matches("-?\\d+")){
				oldPassengerNo = Integer.parseInt(temp);
				if (oldPassengerNo == -1){
					System.out.println("UPDATE cancelled.\n");
					return; 
				} else { // check to see if the passengerNo is valid
					if (passengerNoExists(stmt, oldPassengerNo)){
						break;
					} else {
						System.out.println("That passengerNo does not exist. Enter a valid passengerNo.");
					}
				}
			} else if (temp.equals("f")){
				showPassengerTable(stmt);
			} else {
				System.out.println("Enter an integer");
			}
		} 

		// this will continuously ask the user to input an integer for which 
		// field of the passenger table they want to update. When an accetable 
		// integer is inputted then it will move on. This will build the query string
		String category = "";
		int chosen = 0;
		while (chosen != -1){
			System.out.println("Enter a 1, 2, or 3 to choose which column of the passenger " 
							+"relation to update: First Name,"+ 
							" Last Name, or birthday respectively. To exit enter a -1");
			String temp = gScan.nextLine();
			if (temp.matches("-?\\d+")){
				chosen = Integer.parseInt(temp);
				if (chosen == 1){category = "firstName";break;} 
				else if (chosen == 2){category = "lastName";break;} 
				else if (chosen == 3){category = "birthday";break;} 
				else if (chosen == -1){
					System.out.println("update cancelled.\n");
					return;
				}
			} else {
				System.out.println("Enter an integer");
			}
		}

		// now we can have the user input the updated value
		String upStr = "";
		System.out.println("Enter the value you want to replace the current value. Enter a -1 at " + 
					"anytime to cancel the update.");
		// since dates and passengerNo have specific format/type, validate them
		if (category.equals("birthday")){ 
			System.out.println("For birthdays enter a valid date in the format of DD-MMM-YY like so - 28-Mar-78");
			while (true){
				upStr = gScan.nextLine();
				if (upStr.equals("-1")){
					System.out.println("update cancelled.\n");
					return;
				} else if (!upStr.matches("\\d{2}-...-\\d{2}")){
					System.out.println("Enter a valid DD-MMM-YY date");
				} else {
					break;
				}
			}
		} else { // this covers first and last name
			upStr = gScan.nextLine();
		}

		// now execute the query
    	String query = "UPDATE mfmarquez.passenger SET " + category + " = '" + upStr +
					   "' WHERE passengerNo = " + oldPassengerNo;
		genericExeStmt(stmt, query);

		
		System.out.println("The " + category + " for passengerNo " + oldPassengerNo + 
							" has been updated.");
		
    }

	/*
	 * Method: updateEmployee(Statement stmt)
	 * Purpose: This method is used to update the employee relation 
	 * Parameters: Statement stmt - used to execute queries 
	 * returns: void
	 */
	private static void updateEmployee(Statement stmt) { 
		ResultSet answer = null;		
		// testing purposes

		// now get the employeeNo from the user. It has to be one that already exists
		int employeeNo = 0;
		while(true){
			System.out.println("Enter the employeeNo of the employee's info that you want to update."+ 
					" Enter a -1 to cancel this update. Press 'f' if you want to see the employees");
			String temp = gScan.nextLine();
			if (temp.matches("-?\\d+")){
				employeeNo = Integer.parseInt(temp);
				 if (employeeNo == -1){
					System.out.println("UPDATE cancelled\n");
					return;
				 } else {
					if (employeeNoExists(stmt, employeeNo)){
						break;
					} else {
						System.out.println("The employeeNo entered does not exist. Please enter another.");
					}
				 }
			} else if (temp.equals("f")){
				showEmployeeTable(stmt);
			} else {
				System.out.println("Please enter a valid integer to search for");
			}
		}

		// now we have the employeeNo. now the user tells us which category it wants to change
		String category = "";
		int chosen = 0;
		while (chosen != -1){
			System.out.println("Enter a 1, 2, 3, or 4 to choose which column of the employee " 
							+"relation to update: First Name,"+ 
							" Last Name, airlineNo or jobTitle respectively. To exit enter a -1");
			String temp = gScan.nextLine();
			if (temp.matches("-?\\d+")){
				chosen = Integer.parseInt(temp);
				if (chosen == 1){
					category = "firstName"; break;
				} else if (chosen == 2){
					category = "lastName"; break;
				} else if (chosen == 3){
					category = "airlineNo"; break;
				} else if (chosen == 4){
					category = "jobTitle"; break;
				} else if (chosen == -1){
					System.out.println("UPDATE cancelled\n");
					return;
				}
			} else {
				System.out.println("Enter an integer");
			}
		}

		// now that we have the category we can update the chosen categories
		String upStr = "";
		System.out.println("Enter the data that you want to replace the current value. Enter a -1 " + 
					"at any tmie to cancel the update.");  
		if (category.equals("airlineNo")){
			while (true){
				System.out.println("Enter 1, 2, 3, or 4 for either United, Southwestern, Alaska, or Delta.");
				upStr = gScan.nextLine();
				if (upStr.matches("-?\\d+")){
					if (upStr.equals("1") || upStr.equals("2") ||
					    upStr.equals("3") || upStr.equals("4")){
						break;
					} else if (upStr.equals("-1")) {
						System.out.println("update cancelled.\n");
						return;
					}
				} else {
					System.out.println("Enter an Integer");
				}
			}
		} else if (category.equals("jobTitle")){
			while (true){
				System.out.println("For job title enter either a pilot, cabin-crew or ground-crew");
				upStr = gScan.nextLine().toLowerCase();
				if (upStr.equals("pilot") || upStr.equals("cabin-crew") || upStr.equals("ground-crew")){
					break; 
				} else if (upStr.equals("-1")){
					System.out.println("update cancelled.\n");
					return;
				}
			}
		} else{
			// this means were are updating either the first or last name
			System.out.println("To update " + category + " enter any name.");
			upStr = gScan.nextLine();
			if (upStr.equals("-1")){
				System.out.println("update cancelled.\n");
				return;
			}
		}

		// when an employee changes their airline, should we delete all instances of that employee
		// in the employee flight since they aren't apart of that airline anymore? 
		// Or do we keep those airlines as is?
		
		// now that we have the which one we want to update we can go ahead and update them
		// updating the airlineNo and employeeNo are special cases that require extra work 
		String query;
		if (category.equals("airlineNo")){
			query = "UPDATE mfmarquez.employee SET " + category + " = " + upStr +
					   " WHERE employeeNo = " + employeeNo;
		} else {
			query = "UPDATE mfmarquez.employee SET " + category + " = '" + upStr +
					   "' WHERE employeeNo = " + employeeNo;
		}
		
		genericExeStmt(stmt, query);

		System.out.println("The " + category + " for employeeNo" + employeeNo + 
							"has been updated.");
	}

	/*
	 * Method: updateFlight(Statement stmt)
	 * Purpose: This method is used to update a flight record in the flight relation 
	 * Parameters: Statement stmt - used to execute queries 
	 * returns: void
	 */
	private static void updateFlight(Statement stmt) { 
		ResultSet answer = null; // result sets for the different sets 

		// now we need an employee number to access the correct tuple
		// ask the user to enter a valid employeeNo. To do this, do a query on the passenger table 
		// to see if the value exists by checking if the size of the result set is 1 
		int flightNo = 0; 
		while (true){
			System.out.println("Enter the Flight Number for the flight you want to update. Enter 'f' to see flights. " + 
				"Enter a -1 to exit");
			String temp = gScan.nextLine();
			if (temp.matches("-?\\d+")){
				flightNo = Integer.parseInt(temp);
				if (flightNo == -1){
					System.out.println("update cancelled.\n");
					return; 
				} else { // check to see if the passengerNo is valid
					if (flightNoExists(stmt, flightNo)){
						break;
					} else {
						System.out.println("That flightNo does not exist. Enter a valid flightNo.");
					}
				}
			} else if (temp.equals("f")){
				viewFlightInfo(stmt);
			} else {
				System.out.println("Enter an integer");
			}
		}

		// now that we have the flightNo, query for the flightNo so we can get all of the data 
		// from that flight. This is necessary for data validation
		Date baseDepartDate = null;
		Date departureTime = null;
		Date boardingTime = null;
		Date landingTime = null;
		String dTime = "";
		String bTime = "";
		String duration = "";
		String gate = "";
		SimpleDateFormat hmFormat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		try {
			answer = genericExeStmt(stmt, "SELECT * FROM mfmarquez.flight where flightNo = " + flightNo);
			if (answer != null){
				answer.next();
				baseDepartDate = answer.getDate("departingDate");
				dTime = hmFormat.format(answer.getDate("departingTime"));
				bTime = hmFormat.format(answer.getDate("boardingTime"));
				duration = answer.getString("duration");
				departureTime = addDateAndTime(baseDepartDate, dTime);
				boardingTime = addDateAndTime(baseDepartDate, bTime);
				landingTime = addDateAndTime(departureTime,duration);
				gate = answer.getString("boardingGate");
			}
			String query = "SELECT DISTINCT * FROM mfmarquez.flight, mfmarquez.airline WHERE mfmarquez.flight.airlineNo = mfmarquez.airline.airlineNo AND mfmarquez.flight.flightNo = " + flightNo;
			answer = genericExeStmt(stmt, query);
			System.out.println("FLIGHTNO\tBOARDINGGATE\tBOARDINGTIME\tDEPARTINGTIME\tDEPARTINGDATE\tDURATION\tDEPART\tARRIVE\tAIRLINENAME");
			if (answer != null){
				answer.next();
				System.out.println(answer.getString("flightNo") + "\t\t" + answer.getString("boardingGate") + "\t\t" + answer.getString("boardingTime").substring(11) +  "\t" 
				        + answer.getString("departingTime").substring(11) + "\t" +  dateFormat.format(answer.getDate("departingDate")) + "\t"
						+ answer.getString("duration") + "\t" + answer.getString("depart") + "\t"+ answer.getString("arrive") + "\t"
						+ answer.getString("aname"));
			}
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1);
		}


		// now ask the user which category of the flight they want to update
		String category = "";
		int chosen = 0;
		while (chosen != -1){
			System.out.println("Enter a 1, 2, 3, 4, 5, 6, 7, or 8 to choose which category of the flight " 
							+"relation to update: the Boarding gate, Boarding time, Departing time,"+ 
							" Date Departing, Duration, arrive, depart, or airlineNo "+ 
							" respectively. To exit enter a -1");
			String temp = gScan.nextLine();
			if (temp.matches("-?\\d+")){
				chosen = Integer.parseInt(temp);
				if (chosen == 1){
					category = "boardingGate"; break;
				} else if (chosen == 2){
					category = "boardingTime"; break;
				} else if (chosen == 3){
					category = "departingTime"; break;
				} else if (chosen == 4){
					category = "departingDate"; break;
				} else if (chosen == 5){
					category = "duration"; break;
				} else if (chosen == 6){
					category = "arrive"; break;
				} else if (chosen == 7){
					category = "depart"; break;
				} else if(chosen == 8){
					category = "airlineNo";break;
				} else if (chosen == -1){
					System.out.println("update cancelled.\n"); return;
				} 
			} else {
				System.out.println("Enter an Integer");
			}
		}

		// we have a category and a flightNo, we can go about updating the flight by 
		// asking what they want to update the category to
		String upStr = ""; 
		System.out.println("Enter the data that you want to replace the " + category + " category of flight " + flightNo + ".");
		System.out.println("Enter -1 to exit at anytime");
		if (category.equals("boardingGate")){
			while(true){
				System.out.println("Boarding gates must be in the format [capital letter][single digit #] ");
				upStr = gScan.nextLine();
				if (upStr.equals("-1")){
					System.out.println("UPDATE cancelled\n");
					return;
				} else if (validateBoardingGate(stmt, upStr, flightNo, baseDepartDate, boardingTime, landingTime)){
					break;
				}
			}
		} else if (category.equals("boardingTime") || category.equals("departingTime")){
			// for bTime and dTime just get the time entered by user in 24 hour format
			while (true){
				System.out.println("Enter boarding or departing time in 24 hour format (Ex: 3AM = 03:00). Must be 5 characters long. Enter -1 to exit.");
				upStr = gScan.nextLine();
				if (upStr.matches("\\d{2}:\\d{2}")){
					if (upStr.equals("-1")){
						System.out.println("UPDATE cancelled\n"); 
						return;
					} else if (validateSchedChanges(stmt, category, flightNo, upStr, boardingTime, 
								departureTime, baseDepartDate, duration, gate)){
						break;
					}
				} else if (upStr.equals("-1")){
					System.out.println("UPDATE cancelled\n"); 
						return;
				}
				
			}
		} else if (category.equals("departingDate")){
			// for departingDate get the 
			while(true){
				System.out.println("Enter a valid date in the format 2021-MM-DD for the year 2021");
				upStr = gScan.nextLine();
				if (upStr.equals("-1")){
					System.out.println("UPDATE cancelled\n");
					return;
				} else if (validateSchedChanges(stmt, category, flightNo, upStr, boardingTime, departureTime, baseDepartDate, duration, gate)){
					break;
				}
			}
		} else if (category.equals("duration")){
			while(true){
				System.out.println("Enter the hours and minutes for the duration of the flight in a 24 hour format (Ex: 1 hour, 3 minutes = 1:03) but it must be 4 characters long.");
				upStr = gScan.nextLine();
				if (upStr.equals("-1")){
					System.out.println("UPDATE cancelled\n");
					return;
				} else if (validateSchedChanges(stmt, category, flightNo, upStr, boardingTime, departureTime, baseDepartDate, duration, gate)){
					break;
				}
			}
		} else if (category.equals("airlineNo")){
			// any change to this column will result in having to get rid of all employees associated with this flight
			// since employees are only associated with one airline. This means having to update the employee flight 
			// table / delete all instances of this flight number from it. 
			while(true){
				System.out.println("Enter a 1, 2, 3, or 4 for United, Southwestern, Alaska, and Delta respectively.");
				upStr = gScan.nextLine();
				if (upStr.equals("-1")){
					System.out.println("update cancelled.\n");
					return;
				} else if (upStr.equals("1") || upStr.equals("2") || upStr.equals("3") || upStr.equals("4")){
					// go through and delete all instances of the flight from the employee flight table 
					changeFlightAirlineNo(stmt, flightNo, upStr, baseDepartDate);
					break;
				} 
			}
		} else if (category.equals("arrive") || category.equals("depart")) {
			upStr = gScan.nextLine();
			if (upStr.equals("-1")){
				System.out.println("update cancelled.\n");
				return;
			}
		}


		String query = "";
		// now we can execute the query :D
		
		if (category.equals("departingDate")){
			query = "UPDATE mfmarquez.flight SET " + category + " = TO_DATE('" + upStr + 
					  "', 'YYYY-MM-DD') WHERE flightNo = " + flightNo;
		} else if (category.equals("boardingTime") || category.equals("departingTime")){
			query =  "UPDATE mfmarquez.flight SET " + category + " = TO_DATE('" + upStr + 
			"', 'hh24:mi') WHERE flightNo = " + flightNo;
		} else {
			query = "UPDATE mfmarquez.flight SET " + category + " = '" + upStr + 
					  "' WHERE flightNo = " + flightNo;
		}
		
					  
		genericExeStmt(stmt, query);

		System.out.println("The " + category + " for flightNo " + flightNo + 
							" has been updated! :D ");
	}

	/*
	 * Method: updateReservation(Statement stmt)
	 * Purpose: This method is used to update a flight-passenger reservation in the passFlight relation.
	 * Parameters: Statement stmt - used to execute queries 
	 * returns: void
	 */
	private static void updateReservation(Statement stmt) { 
		ResultSet answer = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		// to be able to update a reservation from the passFlight
		// table we first need to get a flightNo and an passengerNo from 
		// the user
		int flightNo = 0;
		int passengerNo = 0; 
		String input = "";
		// get the input from the users. Validate the inputs by checking to see if the passengerNo
		// and flightNo number are integers and that they exist

		// first get passengerNo
		while (true){
			System.out.println("Enter your Passenger Number. Enter -1 to exit.");
			input = gScan.nextLine();
			System.out.println();
			if (input.equals("-1")){
				System.out.println("update cancelled.\n");
				return;
			}
			try {
				passengerNo = Integer.parseInt(input);
			} catch (NumberFormatException nfe){
				System.out.println("Please enter in a PassengerNo");
				continue;
			}
			if (passengerNoExists(stmt, passengerNo)){
				break;
			} else {
				System.out.println("Passenger Number does not exist");
			}
		}
		// display the reservations this user has made 
		// display the user's flight history 
		String query = "SELECT flight.FlightNo, flight.departingDate, passflight.bagNumber, passflight.orderedItem " + 
					   "FROM mfmarquez.PassFlight JOIN mfmarquez.Flight ON (PassFlight.flightNo = Flight.flightNo) " + 
					   "WHERE passengerNo = " + passengerNo;
		try {
			answer = stmt.executeQuery(query);
			if (answer != null){
				System.out.println("FlightNo\tDeparting Date\tBagNumber\tOrderedItem(T/F)");
				while (answer.next()){
					int fno = answer.getInt(1); Date dDate = answer.getDate(2); 
					int bagNo = answer.getInt(3); String ordered = answer.getString(4);
					System.out.println(fno+"\t\t"+dateFormat.format(dDate)+"\t"+
							bagNo+"\t\t"+ordered);
				}
			}
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1);
		}
		// get the flightNo
		while (true){
			System.out.println("Enter one of the above Flight Numbers. Enter -1 to exit.");
			input = gScan.nextLine();
			System.out.println();
			if (input.equals("-1")){
				System.out.println("update cancelled.\n");
				return;
			}
			try {
				flightNo = Integer.parseInt(input);
			} catch (NumberFormatException nfe){
				System.out.println("Please enter in a flightNo.");
				continue;
			}
			if (reservationExists(stmt, passengerNo, flightNo)){
				break;
			} else {
				System.out.println("Flight Number does not exist");
			}
		}


		// ask the user which column they would like to update 
		String updateStr = "";
		String category = "";
		while(true){
			System.out.println("To choose which category to update, enter a 1, 2, or 3 for the" + 
					" flightNo, Bag Number, or ordered item. Enter a -1 to exit.");
			updateStr = gScan.nextLine();
			System.out.println();
			if (updateStr.equals("1")){category = "flightNo"; break;} 
			else if (updateStr.equals("2")){category = "bagnumber"; break;} 
			else if (updateStr.equals("3")){category = "ordereditem"; break;}
			else if (updateStr.equals("-1")){
				System.out.println("UPDATE cancelled\n");
				return;
			}
		}

		// now lets get the user input and validate it. But before we do that 
		// lets just check to see if the passenger is a student 
		boolean student = isStudentOrVeteran(stmt, passengerNo, "Student");
		System.out.println("Enter the data that you want to replace the " + category + " category of the reservation for " + 
		"passenger " + passengerNo + " for their flight " + flightNo + ".");
		System.out.println("Enter -1 to exit at anytime");
		
		if (category.equals("flightNo")){
			while (true){
				System.out.println("Enter the Flight Number.");
				// for this we get the flightNo that the user wants to update this reservation to,
				// check to see if exists, then check to see if this flight has any overlap with any
				// of the passenger's other flights (but not the one). 
				// we are currently on. If it passes all these then we break out. 
				String temp = gScan.nextLine();
				if (temp.matches("-?\\d+")){
					updateStr = temp;
					if (updateStr.equals("-1")){
						System.out.println("UPDATE cancelled\n");
						return;
					} else if (flightNoExists(stmt, Integer.parseInt(updateStr))){
						// check to see if the new flight would overlap with any other flights this 
						boolean noOverlaps = checkForUsersNewFlightOverlaps(stmt, passengerNo, 
									Integer.parseInt(updateStr), flightNo, false, false);
						if (noOverlaps){
							break;
						}
					} else {
						System.out.println("Enter a valid flightNo.");
					}
				} else {
					System.out.println("Please enter an integer");
				}
			}
		} else if (category.equals("bagnumber")){
			System.out.println("For updating the bag number please enter the number of bags.");
			while(true){
				String temp = gScan.nextLine();
				if (temp.matches("-?\\d+")){
					updateStr = temp;
					int num = Integer.parseInt(updateStr);
					if (num == -1){
						System.out.println("UPDATE cancelled\n");
						return;
					} 
					if (student){
						if (num >= 0 && num <5){
							break;
						} else {
							System.out.println("Bag number for student must be between 0 and 5");
						}
					} else {
						if (num >= 0 && num < 3){
							break;
						} else {
							System.out.println("Bag number must be between 0 and 2");
						}
					}
				} else {
					System.out.println("You must enter a number");
				}
			}
		} else if (category.equals("ordereditem")){
			System.out.println("To update ordered item either enter T or F for true or false.");
			while(true){
				updateStr = gScan.nextLine().toUpperCase();
				if (updateStr.equals("-1")){
					System.out.println("UPDATE cancelled\n");
					return;
				} else if (updateStr.equals("T") || updateStr.equals("F")){
					break;
				} else {
					System.out.println("Either enter a T or an F for true or false.");
				}
			}
		}

		// now that we have all the necessary data for all the different scenarios, we can 
		// execute the update 
		if (category.equals("ordereditem")){
			query = "UPDATE mfmarquez.PassFlight SET " + category + " = '" + updateStr +
		 "' WHERE passengerNo = " + passengerNo + " AND flightNo = " + flightNo;
		} else {
			query = "UPDATE mfmarquez.PassFlight SET " + category + " = " + updateStr +
		 " WHERE passengerNo = " + passengerNo + " AND flightNo = " + flightNo;
		}
		
		genericExeStmt(stmt, query);

		System.out.println("Update for passenger " + passengerNo + " and their flight " + flightNo+" has been made. The category " + category + " is now " + updateStr+".");
	}

	/*
	 * Method updateEmpFlight(Statement stmt)
	 * Purpose: this method handles the user updating an employee assignment to a flight 
	 * Parameters: Statement stmt - executes queries 
	 * return: void
	 */
	private static void upadteEmpFlight(Statement stmt){
		ResultSet answer = null; // result sets for the different sets 
		String input = "";
		String query = "";
		int airlineNo = 0;
		int selectedflightNo = 0;
		SimpleDateFormat justTime = new SimpleDateFormat("HH:mm");
		SimpleDateFormat justDate = new SimpleDateFormat("yyyy/MM/dd");
		// first get the employee number they want to update. 
		// then display all of the flights that they are assocated with. 
		// then show them which flights they can be changed to 
		int empNo = 0;
		while (true){
			System.out.println("Enter in an employee that you want to update. Enter -1 to exit at any time. ");
			input = gScan.nextLine();
			if (input.equals("-1")){
				System.out.println("UPDATE cancelled");
				return;
			}
			try {
				empNo = Integer.parseInt(input);
			} catch (NumberFormatException nfe){
				System.out.println("Please enter in an EmployeeNo");
				continue;
			}
			if (employeeNoExists(stmt, empNo)){
				break;
			}  else {
				System.out.println("Employee Number does not exist");
			}
		}
		
		// now display the flights this employee is assigned to. if there are none then display that and 
		// return 
		// arraylist of flights that the user can choose from TO UPDATE
		ArrayList<Integer> chooseToUpdateFrom = new ArrayList<Integer>();

		// after that get the airlineNo that the employee is apart of 
		// an arraylist of flights that the user can choose to UPDATE TO
		ArrayList<Integer> allowedFlights = new ArrayList<Integer>();

		if (isEmployeeAssignedToFlights(stmt, empNo) == false){
			System.out.println("The employee was not assigned to any flights. Please assign this employee to flights before updating");
			return;
		}
		
		try {
			// get airline no 
			query = "Select airlineNo from mfmarquez.employee where employeeNo = " + empNo + " ORDER BY employeeNo";
			answer = stmt.executeQuery(query);
			if (answer != null){
				answer.next();
				airlineNo = answer.getInt("airlineno");
			}

			query = "SELECT flightNo, employeeNo from mfmarquez.empFlight where employeeNo = " + empNo;
			// show the flights employee is assinged to 
			// and build a list of choosable flights to update from 
			System.out.println("------------------These are the flights the employee is assigned to---------------");
			
			answer = stmt.executeQuery(query);
			System.out.println("flightNo\tEmployeeNo");
			if (answer != null){

				while (answer.next()){
					chooseToUpdateFrom.add(answer.getInt("flightNo"));
					System.out.println(answer.getInt(1) + "\t" + answer.getInt(2));
				}
			}

			// show flights the employee can be assigned to 
			System.out.println("----------These are the flights that the employee can be assinged to--------");
			query = "SELECT distinct flight.flightNo, flight.boardingTime, flight.departingTime, flight.departingDate, flight.duration "+
					"FROM mfmarquez.flight join mfmarquez.empflight on (mfmarquez.empflight.flightNo = mfmarquez.flight.flightNo) " + 
					"WHERE flight.airlineNo = " + airlineNo + " ORDER BY flightNo";
			answer = stmt.executeQuery(query);
			if (answer != null){
				System.out.println("FlightNo\tBoardingTime\tDepartingTime\tDepartingDate\tDuration");
				while (answer.next()){
					allowedFlights.add(answer.getInt(1));
					System.out.println(answer.getInt(1)+"\t\t"+ justTime.format(answer.getDate("boardingTime"))+"\t\t"+
					justTime.format(answer.getDate("departingTime")) +"\t\t"+ justDate.format(answer.getDate("departingDate")) +"\t"+ answer.getString("duration"));
				}
			}
			System.out.println();

		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1);
		}


		// grab the flight
		// checks - we have to make sure that they aren't assigned to a flight that overlaps 
		// with any of their other assignments. Should be similar to the check that is done when 
		// a passenger updates their flight
		while (true){
			System.out.println("Choose one of the flights the employee is already on to update. Enter a -1 to exit out.");
			input = gScan.nextLine();
			if (input.equals("-1")){
				System.out.println("UPDATE cancelled");
				return;
			}
			try {
				selectedflightNo = Integer.parseInt(input);
			} catch (NumberFormatException nfe){
				System.out.println("Please enter in an integer");
				continue;
			}
			if (flightNoExists(stmt, empNo)){
				if (chooseToUpdateFrom.contains(selectedflightNo)){
					break;
				} else {
					System.out.println("Flight enetered must be one of the flights listed above.");
				}
			}  else {
				System.out.println("Flight Number does not exist");
			}
		}

		// now ask which flight you want to update to 
		int flightToUpdateTo = 0;
		while (true){
			System.out.println("Now choose which flight you want to update flight " + selectedflightNo + " to. To exit, enter -1. It must be one of the above flights.");
			input = gScan.nextLine();
			if (input.equals("-1")){
				System.out.println("UPDATE cancelled.\n");
				return;
			}
			try {
				flightToUpdateTo = Integer.parseInt(input);
			} catch (NumberFormatException nfe){
				System.out.println("Please enter in an integer");
				continue;
			}
			if (flightNoExists(stmt, flightToUpdateTo)){
				if (chooseToUpdateFrom.contains(flightToUpdateTo)){
					System.out.println("The employee is already on this flight, choose another flight.");
					continue;
				}
				if (allowedFlights.contains(flightToUpdateTo)){
					boolean noOverlaps = checkForUsersNewFlightOverlaps(stmt, empNo, flightToUpdateTo, selectedflightNo, true, false);
					if (noOverlaps == false){
						System.out.println("This flight overlaps with another one of this employee's booked flights. Please choose another flight.");
					}else {
						break;
					}
				} else {
					System.out.println("You must enter one of the allowed flights displayed above");
				}
			} else {
				System.out.println("Flight does not exist.");
			}

		}
		
		// delete query 
		String delQuery = "DELETE FROM mfmarquez.empFlight where flightNo = " + selectedflightNo + " and employeeNo = " + empNo;
		String delQueryCleanUp = "DELETE FROM mfmarquez.empFlight where flightNo = " + flightToUpdateTo + " and employeeNo = " + empNo;
		String insertQuery = "INSERT INTO mfmarquez.empFlight (employeeNo, flightNo) VALUES (" + empNo+", "+flightToUpdateTo+")";

		// queries to update the string 
		genericExeStmt(stmt, delQuery);
		genericExeStmt(stmt, delQueryCleanUp);
		genericExeStmt(stmt, insertQuery);

		/* now that we are here we need to build the query string 
		query = "UPDATE mfmarquez.empFlight SET flightNo = " + flightToUpdateTo+ " where flightNo = " + selectedflightNo;
		genericExeStmt(stmt, query);
		*/
		System.out.println("Employee has been assinged from " + selectedflightNo+ " to flight "+flightToUpdateTo+".");

	}

	/**
	 * Method isEmployeeAssignedToFlights(Statement stmt, int empNo)
	 * Purpose: checks to see if an employee is assigned to a flight
	 * Parameters - stmt - executes queries 
	 * 				empNo - employee we are checking 
	 * returns: true if the employee is assigned. false if no 
	 */
	private static boolean isEmployeeAssignedToFlights(Statement stmt, int empNo){
		ResultSet answer = null;
		try {
			answer = stmt.executeQuery("SELECT COUNT(employeeNo) FROM mfmarquez.empFlight WHERE employeeNo = " + 
						empNo);
			if (answer != null) {
				// check to see if there is an answer
				answer.next();
				int count = answer.getInt(1);
				if (count > 0){
					return true;
				} else {
					return false;
				} 
					
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}
		return false;
	}


	/**
	 * Method: checkForUsersNewFlightOverlaps()
	 * Pupose: This is used when a employee or a passenger is assigned a new flight. It checks to see 
	 * 		   if the flight the pass/emp is being assigned overlaps with any other 
	 * 		   reservation the user could have made. With this you pass in a passenger or employee Number 
	 * 		   and the flightNo of the flight you want to assign the passenger to. 
	 * 		   Then it goes through the passenger's history and checks to see if any 
	 * 		   of passenger's other flights overlap or not. the 
	 * Parameters: stmt - the statement used to execute queries
	 * 			   passengerNo - the passenger we want to check 
	 * 			   newFlightNo - the flight we want to assign the passenger to 
	 * 			   oldFlightNo - the flightNo of the reservation that we are changing
	 * 			   isEmpNum - boolean that tells if the empOrPassNo is an employeeNo
	 * return - boolean - true if the flight doesn't overlap with any of the user's flights
	 * 				false if the flight does. 
	 */
	private static boolean checkForUsersNewFlightOverlaps(Statement stmt, int empOrPassNo, int newFlightNo,
					int oldFlightNo, boolean isEmpNum, boolean isInsert){
		ResultSet answer = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

		// testing
		if ((!flightNoExists(stmt, oldFlightNo) || !flightNoExists(stmt, newFlightNo)) && !isInsert ) {
			System.out.println("One of the entered flight numbers does not exist.");
			return false;
		}

		// first query for the new flight and construct the boardingDate and landingDate from it
		String query = "SELECT to_char(boardingTime, 'HH24:MI') AS boardingTime, to_char(departingTime, 'HH24:MI') AS departingTime, departingDate, duration " + 
					   "FROM mfmarquez.Flight WHERE flightNo = " + newFlightNo;
		Date newLandingDate = null;
		Date newBoardingDate = null;
		Date departingDate = null;
		try {
			answer = stmt.executeQuery(query);
			if (answer != null){
				answer.next();
				// get data from tuple 
				String boardingTime = answer.getString("boardingTime");
				String departingTime = answer.getString("departingTime");
				departingDate = answer.getDate("departingDate");
				String duration = answer.getString("duration");
				
				// now compute the boarding and landing date
				newBoardingDate = addDateAndTime(departingDate, boardingTime);
				Date tempToGetLanding = addDateAndTime(departingDate, departingTime);
				newLandingDate = addDateAndTime(tempToGetLanding, duration);
			}
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1);
		}


		// now query for all the the passenger's flights and do a join 
		if (isEmpNum){
			if (isInsert){
				query = "SELECT mfmarquez.Flight.flightNo, to_char(boardingTime, 'HH24:MI') AS boardingTime, to_char(departingTime, 'HH24:MI') AS departingTime, departingDate, duration " + 
					   "FROM mfmarquez.Flight JOIN mfmarquez.empFlight ON (mfmarquez.Flight.flightNo = mfmarquez.empFlight.flightNo) " +
					   "WHERE mfmarquez.Flight.flightNo != " + newFlightNo+" AND employeeNo = "+empOrPassNo;
			}else {
				query = "SELECT mfmarquez.Flight.flightNo, to_char(boardingTime, 'HH24:MI') AS boardingTime, to_char(departingTime, 'HH24:MI') AS departingTime, departingDate, duration " + 
						"FROM mfmarquez.Flight JOIN mfmarquez.empFlight ON (mfmarquez.Flight.flightNo = mfmarquez.empFlight.flightNo) " +
						"WHERE mfmarquez.Flight.flightNo != " + oldFlightNo+" AND employeeNo = "+empOrPassNo;
			}
		} else {
			if (isInsert){
				query = "SELECT mfmarquez.Flight.flightNo, to_char(boardingTime, 'HH24:MI') AS boardingTime, to_char(departingTime, 'HH24:MI') AS departingTime, departingDate, duration " + 
					   "FROM mfmarquez.Flight JOIN mfmarquez.PassFlight ON (mfmarquez.Flight.flightNo = mfmarquez.PassFlight.flightNo) " +
					   "WHERE mfmarquez.Flight.flightNo != " + newFlightNo +" AND passengerNo = "+empOrPassNo;
			} else {
				query = "SELECT mfmarquez.Flight.flightNo, to_char(boardingTime, 'HH24:MI') AS boardingTime, to_char(departingTime, 'HH24:MI') AS departingTime, departingDate, duration " + 
					   "FROM mfmarquez.Flight JOIN mfmarquez.PassFlight ON (mfmarquez.Flight.flightNo = mfmarquez.PassFlight.flightNo) " +
					   "WHERE mfmarquez.Flight.flightNo != " + oldFlightNo +" AND passengerNo = "+empOrPassNo;
			}
		}
		try {
			answer = stmt.executeQuery(query);
			if (answer != null){
				// iterate through all flights and check for boardingTime and landing Time overlaps
				while (answer.next()){
					// get the current flight no 
					int otherFlightNo = answer.getInt(1);
					
					// get data from tuple 
					String otherBoardingTime = answer.getString("boardingTime");
					String otherDepartingTime = answer.getString("departingTime");
					Date otherDepartingDate = answer.getDate("departingDate");
					String duration = answer.getString("duration");

					// now compute the boarding and landing date
					Date otherBoardDate = addDateAndTime(otherDepartingDate, otherBoardingTime);
					Date tempToGetLanding = addDateAndTime(otherDepartingDate, otherDepartingTime);
					Date otherLandDate = addDateAndTime(tempToGetLanding, duration);
					// now compare the times to see if there is any overlap
					// if newboardingtime falls between the current flight time space
					boolean conflict = 
						landingBoardingOverlaps(newBoardingDate, newLandingDate, otherBoardDate, otherLandDate);

					/* 
					System.out.println("conflict? = " + conflict);
					System.out.println("currFlight = , other flightNo = " + otherFlightNo);
					System.out.println("\tData: newboardingTime = " + dateFormat.format(newBoardingDate) + ", newlandingTime = " + dateFormat.format(newLandingDate));
					System.out.println("\tData: other boardingtime = " + dateFormat.format(otherBoardDate) + ", other landingTime = " + dateFormat.format(otherLandDate));
					System.out.println("\t\tbtime = " + otherBoardingTime + ", dtime = " + otherDepartingTime + ", duration = " + duration);
					*/
					if (conflict){
						System.out.println("unable to add/update this flight " +
								 "because of a conflict with the following flight");
						System.out.println("FlightNo\tBoarding Time\tLandingTime\n"+answer.getInt("flightNo") + 
						"\t\t" + dateFormat.format(otherBoardDate) + "\t" + dateFormat.format(otherLandDate));
						return false;
					}
					
				}
			}
			// if we reached here that means we are A-okay 
			return true;
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}

	/**
	 * Method: isStudentOrVeteran(Statement stmt, int PassengerNo, String benefit);
	 * Purpose: this checks if a passenger is a student/veteran
	 * Parameters: stmt - statement used to execute query 
	 * 			   passengerNo - the passenger we are checking
	 * 				String - a category to check for (either student or veteran)
	 * returns: boolean - true if the query returns a tuple. false if there none
	 */
	private static boolean isStudentOrVeteran(Statement stmt, int passengerNo, String benefit){
		String query = "SELECT COUNT(*) FROM mfmarquez.PassCategory " + 
					   "WHERE passengerNo = " + passengerNo + " " + 
					   "AND category = '" + benefit + "'";
		ResultSet answer = null;
		try {
			answer = stmt.executeQuery(query);
			if (answer != null){
				answer.next();
				int count = answer.getInt(1);
				if (count != 0){
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1);
		}
		return false; 
	}

	/**
	 * Method check for landingBoardingOverlaps(Date boading1, Date landing1, Date boarding2, Date landing2);
	 * Purpose: this takes the boarding date and landing date for 2 different flights and checks 
	 * 			to see if there is any overlap. 
	 * Paremeters: boarding1 - boarding date/time for the first flight
	 * 			   landing1 - landing date/time for the first flight 
	 * 			   boarding2 - boarding date/time for the second flight
	 * 			   landing2 - landing date/time for the second flight 
	 * returns - true if there is an overlap in flight time, false if there isn't
	 */
	private static boolean landingBoardingOverlaps(Date boading1, Date landing1, Date boarding2, Date landing2){
		boolean conflict = false; 
		if ( (boading1.compareTo(boarding2) >= 0) && (boading1.compareTo(landing2) <= 0) ){
			conflict = true;
		} 
		// if newLandingTime falls between the current flight time space 
		else if ( (landing1.compareTo(boarding2)  >= 0) && (landing1.compareTo(landing2)  <= 0) ) {
			conflict = true; 
		} 
		// if the current landing and boarding time is engulfed by the newlandingtime 
		// and newBoardingTime
		else if ( ( (boarding2.compareTo(boading1) >= 0) && (boarding2.compareTo(landing1) <= 0) ) 
		  && ( (landing2.compareTo(boading1) >= 0) && (landing2.compareTo(landing1) <= 0) ) ) {
			conflict = true; 
		}
		return conflict;
	}


	/**
	 * Method: validateSchedChanges()
	 * Purpose: This method validates any input from the user that has anything to do with 
	 * 			the scheduling related fields of a flight (duration, boarding time, departing time,
	 * 			duration). What this does is check which category we are updating, then modifies 
	 * 			that field (in this method those variables are denoted with newXDate). If everything 
	 * 			goes right then this calls validateFlightTimings to see if the changes made would lead 
	 * 			to flight overlaps for the passengers 
	 * Parameters: stmt - the statement to execute queries 
	 * 			   category - the category that is being updated 
	 * 			   updateString - the string input from the user for updating data 
	 * 			   boardingDate - the boarding date/time for the flight 
	 * 			   departureDate - the departine date/time for the flight
	 * 			   baseDepartDate - the date taken directly from the flight relation, doesn't have time
	 * 								 only date 
	 * 			   duration - the duration of the flight 
	 * returns boolean based on if the shedule changes are valid. False if the schedule change is not valid, 
	 * 				otherwise true
	 */
	private static boolean validateSchedChanges(Statement stmt, String category, int flightNo, 
			String updateString, Date boardingDate, Date departureDate, Date baseDepartDate, String duration, String gate){
		// we need to call validateFlightTiming. To do this we need to get all the 
		// necessary info, specifically, boardingTime(already ha1ve), departureTime(already have), 
		// duration + boarding time(need to get). We also need to update these values depending on what 
		// the user changed. 
		Date newBoardingDate = boardingDate;
		Date newDepartureDate = departureDate;
		Date newBaseDepartDate = baseDepartDate;
		Date landingTime = addDateAndTime(newDepartureDate, duration);
		String newDuration = duration;
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String bTime = timeFormat.format(newBoardingDate);
		String dTime = timeFormat.format(newDepartureDate);


		// now go about checking the inputted by the user
		if (category.equals("boardingTime") || category.equals("departingTime")){

			// check to make sure the format is correct.
			if (updateString.matches("\\d{2}:\\d{2}")){

				// check to see if the boarding or departing time entered comes before or 
				// after the flight's current boarding or departing time. 
				Date newTime = addDateAndTime(baseDepartDate, updateString);
				if (category.equals("boardingTime")){
					if (newTime.compareTo(newDepartureDate) < 0){ // if newboardingTime is before departure
						newBoardingDate = newTime;
					}  else {
						System.out.println("boarding time entered is greater than or equal to the departing time - " +
						 dTime + " - for this flight.");
						return false;
					}
				} else if (category.equals("departingTime")){

					if (newTime.compareTo(newBoardingDate) > 0){ // if newdepartureTime is after boarding
						newDepartureDate = newTime;
						// check to make sure that the new departure time doesn't cause the flight to stretch into the next day 
						landingTime = addDateAndTime(newDepartureDate, duration);
						if (validateDuration(landingTime, newDepartureDate) == false){
							System.out.println("The flights can not stretch into the next day.");
							System.out.println("\tDeparture Time = "+dateFormat.format(newDepartureDate));
							System.out.println("\tLanding Time = "+dateFormat.format(landingTime));
							return false;
						}

					} else {
						System.out.println("Departing time entered is less than or equal to the boarding time - " + 
						 bTime + " - for this flight.");
						return false;
					}
				} else {
					System.out.println("The date enetered - "+ updateString + " - was in an incorrect format.");
					return false;
				}
			}
		} else if (category.equals("departingDate")){
			// need to update the day then go ahead and update the days for departure and 
			// boardingTime
			try {
				newBaseDepartDate = new SimpleDateFormat("yyyy-MM-dd").parse(updateString);
			} catch (ParseException e) {
				System.out.println("Problem parsing the date " + updateString);
				return false;
			}
			// make sure it's in 2021 
			if(!updateString.substring(0, 4).equals("2021")){
				System.out.println("Date must be from 2021");
				return false; 
			}
			newBoardingDate = addDateAndTime(newBaseDepartDate, bTime);
			newDepartureDate = addDateAndTime(newBaseDepartDate, dTime);
			landingTime = addDateAndTime(newDepartureDate, duration);



		} else if(category.equals("duration")){
			if (updateString.matches("\\d{1}:\\d{2}")){
				// since it matches the format, check to see if duration entered is between 1-5 hours
				int hours = Integer.parseInt((""+ updateString.charAt(0)+""));
				int minutes = Integer.parseInt((""+ updateString.charAt(2) + "" + updateString.charAt(3) +""));
				if ( ( (minutes >= 0 && minutes < 60) && (hours >= 1 && hours <= 5) ) ){
					if (hours == 5 && minutes != 0){
						System.out.println("Any duration past, but not equal to, 5 hours is not allowed.");
						return false;
					}
					newDuration = updateString;
					// now that we have updated duration we need to make sure that this new duration 
					// does not cause the flight to stretch into the next day 
					// first update the landing time and then check 
					// create the landing time 
					landingTime = addDateAndTime(newDepartureDate, newDuration);
					// check to make sure that the landing time and departure time aren't on different day
					if (validateDuration(landingTime, newDepartureDate) != true ){
						System.out.println("The flights can not stretch into the next day.");
						System.out.println("\tDeparture Time = "+dateFormat.format(newDepartureDate));
						System.out.println("\tLanding Time = "+dateFormat.format(landingTime));
						return false;
					}
				} else {
					System.out.println("The duration inputted must be between 1 and 5 hours and in 0:00 format." );
					return false;
				}
			} else {
				System.out.println("Duration was in incorrect format ");
				return false;
			}
		}

		boolean boardingGateConflict = validateBoardingGate(stmt, gate, flightNo, newBaseDepartDate, newBoardingDate, landingTime);

		if (boardingGateConflict == false){
			System.out.println("There was a conflict with the schedule change: the boarding gate - " + gate + " - is "+
				"currently occupied by another flight.");
			return false;
		}

		// Now that we have the correct data we can check to see if any passengers have 
		// overlapping flights
		//System.out.println("landing time = " + dateFormat.format(landingTime));
		ArrayList<ArrayList<String>> passengerConflictList =  
			validateFlightTiming(stmt, flightNo, newBoardingDate, landingTime, false);
		if (passengerConflictList.size() != 0){
			// print passenger conflict list and terminated function 
			System.out.println("==============================================================================================");
			System.out.println("				PASSENGER SCHEDULE CONFLICT									  ");
			System.out.println("The changes made to flight " + flightNo + " has created scheduling conflicts for " + 
						"the following passengers and their flights");
			System.out.println("PassengerNo\tFlightNo\t\tBoardingTime\tLandingtime");
			for (int i = 0; i < passengerConflictList.size(); i++){
				String pno = passengerConflictList.get(i).get(0);
				String fno = passengerConflictList.get(i).get(1);
				String boardDate = passengerConflictList.get(i).get(2);
				String landingDate = passengerConflictList.get(i).get(3);
				System.out.println(pno+"\t\t"+fno+"\t\t"+boardDate+"\t"+landingDate);
			}
			System.out.println("					Please try again.");
			System.out.println("==============================================================================================");
			return false; 
		}

		ArrayList<ArrayList<String>> employConflictList = 
			validateFlightTiming(stmt, flightNo, newBoardingDate, landingTime, true);
		if (employConflictList.size() != 0){
			System.out.println("==============================================================================================");
			System.out.println("				EMPLOYEE SCHEDULE CONFLICT									  ");
			System.out.println("The changes made to flight " + flightNo + " has created scheduling conflicts for " +
						"the following employees and their flights");
			System.out.println("EmployeeNo\tFlightNo\t\tBoardingTime\tLandingtime");
			for (int i = 0; i < employConflictList.size(); i++){
				String pno = employConflictList.get(i).get(0); String fno = employConflictList.get(i).get(1);
				String boardDate = employConflictList.get(i).get(2); String landingDate = employConflictList.get(i).get(3);
				System.out.println(pno+"\t\t"+fno+"\t\t"+boardDate+"\t"+landingDate);
			}
			System.out.println("					Please try again.");
			System.out.println("==============================================================================================");
			return false; 
		}
		return true;
	}


	/**
	 * Method: validateFlightTiming(int flightNo, Date boardingTime, Date landingTime) 
	 * Purpose: This method validates whether an update in any one of the date related fields 
	 * 			of a flight (dateDeparture, boardingTime, departureTime, duration) is valid. 
	 * 			It takes in a flightNo, boardingTime, and landingDate. The 2 time variables  
	 * 			represent the new time range of the flightNo. With this, we must go through each
	 * 			passengers on the flight and make sure any update to this flight time does not 
	 * 			overlap with the flights of any passenger. If overlaps are found, then the program
	 * 			will print out the passenger and the flight that is conflicting with the current 
	 * 			flight. The caller can tell if there are any conflicts by checking the size of teh 
	 * 			returned arraylist. If 0 that means it is valid. 
	 * Parameters: flightNo - the flightNo of the flight being updated
	 * 			   boardingTime - the boarding time and date 
	 * 			   landingtime - the landing time and date (departure time + duration)
	 * 			   isEmp - tells if we are look for employee schedule overlaps instead of passenger ones 
	 * return: ArrayList<ArrayList<String>> - returns a 2d list of strings. Each inner list should 
	 * 			have 4 items <pno, fno, flights BoardingTime&Date, flights LandingTime&Date>
	 * 			user calling 
	 */
	private static ArrayList<ArrayList<String>> validateFlightTiming(Statement stmt, 
					int flightNo, Date newBoardingTime, Date newLandingTime, boolean isEmp){
		// initial variables 
		ResultSet answer = null;
		String query = "";
		if (isEmp){ // ell
			query = "SELECT employeeNo FROM mfmarquez.empflight where flightNo = " + flightNo; 
		} else {
			query = "SELECT passengerNo FROM mfmarquez.passflight where flightNo = " + flightNo; 
		}
		

		// this will keep track of any passengers if they have any flight conflicts. 
		// the inner array list is suppose to have the format of <string pno, string fno,  
		// string boardingDatetime, String landingDatetime
		ArrayList<ArrayList<String>> ConflictList = new ArrayList<ArrayList<String>>();

		// this is a date string formatter 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		SimpleDateFormat hmFormat = new SimpleDateFormat("HH:mm");

		// first get the resultset of all of the passengers on this flight 
		answer = genericExeStmt(stmt, query);
		ArrayList<String> passNums = new ArrayList<String>();
		try {
			if (answer!=null){
				while (answer.next()){
					passNums.add(""+answer.getInt(1)+"");
				}
			}

		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1);
		}
	
		// now that we have each of the passengers/employees on the flight, we need to iterate 
		// through each tuple, getting the passengerNo, and then query using the passengerNO 
		// and joining the flight and passFlight tables together and then extracting the 
		// date time data to check to see if there is any overlap between the updated boarding
		// and landing times that were passed in and the boarding and landing times of the program 
		try {
			for (int i = 0; i < passNums.size(); i++){
				// query passFlight join flights on (fno) where passenger = currpassNo
				// then iterate through that, get the time data, and compare it to passed in values 
				ResultSet flightDataSet = null;
				String currNo = passNums.get(i);
				if (isEmp){
					query = "Select flight.flightNo, flight.boardingTime, flight.departingTime, flight.departingDate, flight.duration, flight.boardingGate " + 
						"from mfmarquez.empFlight join mfmarquez.flight on (mfmarquez.empflight.flightNo = mfmarquez.flight.flightNo) " + 
						"where mfmarquez.empflight.employeeNo = " + currNo + " AND mfmarquez.flight.flightNo != " + flightNo;
				} else {
					query = "Select flight.flightNo, flight.boardingTime, flight.departingTime, flight.departingDate, flight.duration, flight.boardingGate " + 
						"from mfmarquez.passflight join mfmarquez.flight on (mfmarquez.passflight.flightNo = mfmarquez.flight.flightNo) " + 
						"where mfmarquez.passflight.passengerNo = " + currNo + " AND mfmarquez.flight.flightNo != " + flightNo;
				}
				
				flightDataSet = genericExeStmt(stmt, query);
				if (flightDataSet != null){
					while (flightDataSet.next()){
						// boolean to tell us if there was a conflict
						boolean conflict = false;
						int currFlightNo = flightDataSet.getInt("flightNo");
						if (flightDataSet.getInt("flightNo") == flightNo){
							System.out.println("This happened");
							continue;
						}
						// get the date and times of boarding and departure
						Date baseDate = flightDataSet.getDate("departingDate");
						String bTime =  hmFormat.format(flightDataSet.getDate("boardingTime"));
						String dTime = hmFormat.format(flightDataSet.getDate("departingTime"));
						String duration = flightDataSet.getString("duration");
						
						// add the 2 times to baseDate to get the boarding and departure dates
						// and get 2nd landing date by adding duration to departureDate2 
						Date departingdDate = addDateAndTime(baseDate, dTime);
						Date boardingDate = addDateAndTime(baseDate, bTime);
						Date landingDate = addDateAndTime(departingdDate, duration);
						conflict = landingBoardingOverlaps(newBoardingTime, newLandingTime, boardingDate, landingDate);
						
						/*
						if (isEmp){
							System.out.println("conflict? = " + conflict);
							System.out.println("Flight = " + currFlightNo + ", empNo = " + currNo);
							System.out.println("\tData: newboarding time = " + dateFormat.format(newBoardingTime) + ", new landingTime = " + dateFormat.format(newLandingTime));
							System.out.println("\tData: old boardingtime = " + dateFormat.format(boardingDate) + ", old landingTime = " + dateFormat.format(landingDate));
							System.out.println("\t\tbtime = " + bTime + ", dtime = " + dTime + ", duration = " + duration);
						}
						*/
						// if conflict is true, then add the current passenger/employee, flight and time data to 
						// conflicts arraylist
						if (conflict){
							ArrayList<String> temp = new ArrayList<String>();
							temp.add(currNo);
							temp.add(""+currFlightNo+"");
							temp.add(dateFormat.format(boardingDate));
							temp.add(dateFormat.format(landingDate));	
							ConflictList.add(temp);
						}
						
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1);
		}
		
		return ConflictList; 
	}

	/**
	 * Method: validBoardingGate(Statement stmt, String upStr, Date newDayDeparture,
	 * 			 Date newBoardingDate, Date landingTime )
	 * Purpose: this method checks to make sure that any inputted boarding gate for a flight 
	 * 			does not conflict with any existing flights. It checks to make sure that there 
	 * 			are no overlapping flights for a boarding gate
	 * Parameters: stmt - statment used to execute query 
	 * 			   updateStr - the string holding the boarding gate value we want to replace the current 
	 * 			   dayDeparture - the date of the flights departure that we are updating 
	 * 			   newBoardingDate - the boardingDate (holds both date and time) for the flight 
	 * 			   landingTime - the landing time (duration + departingTime) of the flight we are updating
	 * return boolean - true if the boarding gate chosen is valid. False if the boarding gate chosen is 
	 * 				already occupied in that time frame. 
	 */
	private static boolean validateBoardingGate(Statement stmt, String updateStr, int flightNo,
								 Date baseDayDeparture, Date newBoardingDate, Date landingTime){
		// do a query to find all flights that use the same gate on the same date 
		SimpleDateFormat dateTime  = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat hmFormat = new SimpleDateFormat("HH:mm");

		if (!updateStr.matches("[A-Z]\\d{1,2}")){
			System.out.println("Didn't match");
			return false;
		}
		
		String dateString = dateFormat.format(baseDayDeparture);
		String query = "SELECT flightNo, boardingTime, departingTime, departingDate, duration " +
			"FROM mfmarquez.Flight WHERE boardingGate = '" + updateStr + "' AND departingDate = TO_DATE('" + dateString + "', 'YYYY-MM-DD') " + 
			"AND flightNo != " + flightNo;
		try {
			ResultSet ans = stmt.executeQuery(query);
			// now iterate through each of the tuples	
			if (ans != null){
				while (ans.next()){
					Date baseDate = ans.getDate("departingDate");
					String boardTime = hmFormat.format(ans.getDate("boardingTime"));
					String departTime = hmFormat.format(ans.getDate("departingTime"));

					Date currDepartDate = addDateAndTime(baseDate, departTime);
					Date currBoardDate = addDateAndTime(baseDate, boardTime);
					Date currLandingTime = addDateAndTime(currDepartDate, boardTime);
					
					
					// now check for overlap
					
					boolean conflict = landingBoardingOverlaps(newBoardingDate, landingTime, currBoardDate, currLandingTime);
					/*
					System.out.println("conflict = " + conflict);
					System.out.println("\tnewboardingdate = " + dateTime.format(newBoardingDate) + ", new landingTime = "+ dateTime.format(landingTime) );
					System.out.println("\tnewboardingdate = " + dateTime.format(currBoardDate) + ", new landingTime = "+ dateTime.format(currLandingTime));
					*/

					// if conflict is true then print out a message saying so and exit out of the program 
					if (conflict){
						System.out.println("There is a conflict on the updated boarding gate " + updateStr + " with flightNo " + ans.getInt("flightNo") );
						System.out.println("Please either change the boarding gate for flight " + ans.getInt("flightNo") + " or choose a different " + 
												"flightNo in order to update this.");
						return false;
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1);
		}
		// if reached here that means the boarding gate is valid
		return true;
	}

	/**
	 * Method: changeFlightAirlineNo()
	 * Purpose: this method is called when a flight's airlineNo column is changed. Since
	 * 			the airline changed, we have to remove all employees associated to that 
	 * 			flight by deleting all instances of the flight from the EmpFlight relation. 
	 * 			Then we need to add some employees to the flight
	 * Parameters: stmt - the statement used to execute queries 
	 * 			   flightNo - the flight number 
	 * 			   newAirlineNo - the new airlineNo inputted by the user 
	 */
	private static void changeFlightAirlineNo(Statement stmt, int flightNo, String newAirlineNo, Date flightDate){
		ResultSet answer = null;
		String query = "";
		// first go though and delete all instances of the flight from the employeeflight table 
		query = "DELETE FROM mfmarquez.empflight WHERE flightNo = " + flightNo;
		genericExeStmt(stmt, query);

		// now we need to go through and add at least 1 pilot, 1 cabin crew, and 1 flight crew 
		// first the pilot, query for a pilot who isn't assigned to any flight on the date of 
		// the this flight 	
		try {
			int pilotNo = 0;
			int cCrewNo = 0;
			int gCrewNo = 0;

			query = "SELECT employee.employeeNo " + 
				"FROM mfmarquez.employee JOIN mfmarquez.empflight ON (mfmarquez.employee.employeeNo = mfmarquez.empflight.employeeNo) " +
				"JOIN mfmarquez.flight ON (mfmarquez.flight.flightNo = mfmarquez.empflight.flightNo) " + 
				"WHERE mfmarquez.flight.departingDate != TO_DATE('" + flightDate + "', 'YYYY-MM-DD') " +
				"AND mfmarquez.employee.jobTitle = 'pilot' AND mfmarquez.employee.airlineNo = " + newAirlineNo;	
			answer = stmt.executeQuery(query);
			if (answer != null){
				// just grab the first pilot
				answer.next();
				pilotNo = answer.getInt("employeeNo");
			}

			query = "SELECT employee.employeeNo " + 
				"FROM mfmarquez.employee JOIN mfmarquez.empflight ON (mfmarquez.employee.employeeNo = mfmarquez.empflight.employeeNo) " +
				"JOIN mfmarquez.flight ON (mfmarquez.flight.flightNo = mfmarquez.empflight.flightNo) " + 
				"WHERE mfmarquez.flight.departingDate != TO_DATE('" + flightDate + "', 'YYYY-MM-DD') " +
				"AND mfmarquez.employee.jobTitle = 'cabin-crew' AND mfmarquez.employee.airlineNo = " + newAirlineNo;
			answer = stmt.executeQuery(query);
			if (answer != null){
				// just grab the first cabin crew
				answer.next();
				cCrewNo = answer.getInt("employeeNo");
			}

			query = "SELECT employee.employeeNo " + 
				"FROM mfmarquez.employee JOIN mfmarquez.empflight ON (mfmarquez.employee.employeeNo = mfmarquez.empflight.employeeNo) " +
				"JOIN mfmarquez.flight ON (mfmarquez.flight.flightNo = mfmarquez.empflight.flightNo) " + 
				"WHERE mfmarquez.flight.departingDate != TO_DATE('" + flightDate + "', 'YYYY-MM-DD') " +
				"AND mfmarquez.employee.jobTitle = 'ground-crew' AND mfmarquez.employee.airlineNo = " + newAirlineNo;
			answer = stmt.executeQuery(query);
			if (answer != null){
				// just grab the first ground crew
				answer.next();
				gCrewNo = answer.getInt("employeeNo");
			}

			// assuming we have all three we can now go ahead and insert these guys
			query = "INSERT INTO mfmarquez.empflight VALUES (" + pilotNo +", " + flightNo + ")";
			stmt.executeQuery(query);
			query = "INSERT INTO mfmarquez.empflight VALUES (" + cCrewNo +", " + flightNo + ")";
			stmt.executeQuery(query);
			query = "INSERT INTO mfmarquez.empflight VALUES (" + gCrewNo +", " + flightNo + ")";
			stmt.executeQuery(query);
			
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1);
		}
	}



	/**
	 * Method: addDateAndTime(Date date, String time)
	 * Purpose: this takes in a date and then adds the time string that should be in a 
	 * 			24 hour format
	 * Parameters: date - the date we are adding hours and minutes to 	   
	 * returns: new Date with added minutes and hours 
	 */
	private static Date addDateAndTime(Date date, String time){
		if (time.length() == 4){
			time = "0" + time;
		} else if (time.length() == 3){
			time = "00" + time;
		}
		int hours = Integer.parseInt(""+time.charAt(0)+""+time.charAt(1)+"");
		int minutes = Integer.parseInt(""+time.charAt(3) + "" + time.charAt(4)+"");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, hours);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}


	/*
	 * Method: validateDuration(String duration, departureTime)
	 * Purpose: This method takes 2 dates, a landing date and a departure time 
	 * 			and then checks to see if they are on the same day. If they are not 
	 * 			it returns false meaning the duration stretched into the next day. 
	 * 			if true then that means that the duration plus the departure time 
	 * 			is ok.
	 * Parameters: duration - the duration in date format 
	 * 			   departuretime - the departure in date format
	 * return: boolean - false if the sum is greater than 2400, true otherwise 
	 */
	private static boolean validateDuration(Date landingDate, Date departureTime){
		Calendar landingCal = Calendar.getInstance();
		landingCal.setTime(landingDate);
		Calendar departCal = Calendar.getInstance();
		departCal.setTime(departureTime);
		if (departCal.get(Calendar.DAY_OF_MONTH) != landingCal.get(Calendar.DAY_OF_MONTH)){
			return false;
		}
		return true;
	}
    
	/**
	 * Method: genericExeStmt(Statement stmt, String query)
	 * Purpose: This method executes the passed in query string 
	 * Parameters: Statement stmt - used for executing query
	 * 			   String query - the query we are executing
	 * returns: void
	 */
    private static ResultSet genericExeStmt(Statement stmt, String query){
		ResultSet ans = null;
		try {
			ans = stmt.executeQuery(query);
		} catch (SQLException e) {
			System.err.println("*** SQLException:  "
				+ "Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1);
		}
		return ans;
	}

	/**
	 * Method: passengerNoExists(int passengerNo)
	 * Purpose: This checks to see if the passengerNo being used is valid or not 
	 * 			by counting the occurences
	 * Parameters: int passengerNo  - the passenger no we are seeing exists
	 * 			   Statement stmt - statement to execute our query on
	 * return: true if value exists, false if not
	 */
	private static boolean passengerNoExists(Statement stmt, int passengerNo){
		ResultSet answer = null;
		try {
			answer = stmt.executeQuery("SELECT COUNT(passengerNo) FROM mfmarquez.passenger WHERE passengerNo = " + 
						passengerNo);
			if (answer != null) {
				// check to see if there is an answer
				answer.next();
				int count = answer.getInt(1);
				if (count == 1){
					return true;
				} else if (count == 0){
					return false;
				} 
					
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}
		return false;
	}

	/**
	 * Method: employeeNoExists(int employeeNo)
	 * Purpose: This checks to see if the employeeNo being used is valid or not 
	 * 			by counting the occurences
	 * Parameters: int employeeNo  - the passenger no we are seeing exists
	 * 			   Statement stmt - statement to execute our query on
	 * return: true if value exists, false if not
	 */
	private static boolean employeeNoExists(Statement stmt, int employeeNo){
		ResultSet answer = null;
		try {
			answer = stmt.executeQuery("SELECT COUNT(employeeNo) FROM mfmarquez.employee WHERE employeeNo = " + 
						employeeNo);
			if (answer != null) {
				// check to see if there is an answer
				answer.next();
				int count = answer.getInt(1);
				if (count == 1){return true;}
				if (count == 0){return false;} 
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}
		return false;
	}


	/**
	 * Method: employeeNoExists(int employeeNo)
	 * Purpose: This checks to see if the employeeNo being used is valid or not 
	 * 			by counting the occurences
	 * Parameters: int employeeNo  - the passenger no we are seeing exists
	 * 			   Statement stmt - statement to execute our query on
	 * return: true if value exists, false if not
	 */
	private static boolean flightNoExists(Statement stmt, int flightNo){
		ResultSet answer = null;
		try {
			answer = stmt.executeQuery("SELECT COUNT(flightNo) FROM mfmarquez.flight WHERE flightNo = " + 
						flightNo);
			if (answer != null) {
				// check to see if there is an answer
				answer.next();
				int count = answer.getInt(1);
				if (count == 1){return true;}
				if (count == 0){return false;} 
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}
		return false;
	}

	/**
	 * Method: employeeNoExists(int employeeNo)
	 * Purpose: This checks to see if the employeeNo being used is valid or not 
	 * 			by counting the occurences
	 * Parameters: int employeeNo  - the passenger no we are seeing exists
	 * 			   Statement stmt - statement to execute our query on
	 * return: true if value exists, false if not
	 */
	private static boolean reservationExists(Statement stmt, int passNo, int flightNo){
		ResultSet answer = null;
		try {
			answer = stmt.executeQuery("SELECT COUNT(flightNo) FROM mfmarquez.passFlight WHERE flightNo = " + 
						flightNo + " AND passengerNo = " + passNo);
			if (answer != null) {
				// check to see if there is an answer
				answer.next();
				int count = answer.getInt(1);
				if (count == 1){return true;}
				if (count == 0){return false;} 
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}
		return false;
	}

	/*====================================================================================
	 *====================================================================================
	 * 
	 *=============================   Testing Functions   ================================
	 * 
	 *==================================================================================== 
	 *====================================================================================*/


	/**
	 * Method: showEmployeeTable(Statement stmt)
	 * Purpose: Testing. Just shows us the employee table
	 * Parameters: stmt - statement used to execute this query
	 * returns: void
	 */
	private static void showEmployeeTable(Statement stmt){
    	ResultSet answer = null; // result sets for the different sets 
		try {
			answer = stmt.executeQuery("SELECT * FROM mfmarquez.employee");
			System.out.println("employeeNo\tfirstName\tLastName\tairLineNo\tjobTitle");
			if (answer != null) {
				while (answer.next()) {
					System.out.println(answer.getString(1)+"\t\t" + answer.getString(2) +
								"\t\t" + answer.getString(3)+"\t\t" + answer.getString(4)+"\t\t"+ answer.getString(5));
				}
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}
	}

	/**
	 * Method: showPassengerTable(Statement stmt)
	 * Purpose: Testing. Just shows us the passenger table
	 * Parameters: stmt - statement used to execute this query
	 * returns: void
	 */
	private static void showPassengerTable(Statement stmt){
    	ResultSet answer = null; // result sets for the different sets 
		try {
			answer = stmt.executeQuery("SELECT * FROM mfmarquez.passenger");
			System.out.println("passengerNo\tfirstName\tLastName\tbirthDay");
			if (answer != null) {
				while (answer.next()) {
					System.out.println(answer.getInt(1)+"\t\t" + answer.getString(2) +
							"\t\t" + answer.getString(3)+"\t\t" + answer.getString(4));
				}
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}
	}

	/**
	 * Method: showPassengerTable(Statement stmt)
	 * Purpose: Testing. Just shows us the passenger table
	 * Parameters: stmt - statement used to execute this query
	 * returns: void
	 */
	private static void showFlightTable(Statement stmt){
    	ResultSet answer = null; // result sets for the different sets 
		try {
			answer = stmt.executeQuery("SELECT * FROM mfmarquez.flight");
			System.out.println("flightNo\tboardingGate\tboardingTime\tdepartingTime\tdepartingDate" + 
						"\tduration\tdepart\tarrive\tairlineNo");
			if (answer != null) {
				while (answer.next()) {
					System.out.println(answer.getInt(1)+"\t" + answer.getString(2) + 
							"\t" + answer.getString(3)+"\t" + answer.getString(4) + 
							"\t" + answer.getDate(5)+"\t" + answer.getString(6) + 
							"\t" + answer.getString(7)+"\t" + answer.getString(8) + 
							"\t" + answer.getInt(9));
				}
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}
	}

	
	/**
	 * Method: showPassengerTable(Statement stmt)
	 * Purpose: Testing. Just shows us the passenger table
	 * Parameters: stmt - statement used to execute this query
	 * returns: void
	 */
	private static void showempFlightTable(Statement stmt){
    	ResultSet answer = null; // result sets for the different sets 
		try {
			answer = stmt.executeQuery("SELECT * FROM mfmarquez.empFlight ORDER BY flightNo");
			System.out.println("EmployeeNo\tflightNo");
			if (answer != null) {
				while (answer.next()) {
					System.out.println(answer.getInt(1)+"\t" + answer.getInt(2));
				}
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}
	}

	private static void showPassFlightTable(Statement stmt){
    	ResultSet answer = null; // result sets for the different sets 
		try {
			answer = stmt.executeQuery("SELECT * FROM mfmarquez.PassFlight ORDER BY flightNo");
			System.out.println("flightNo\tpassNo");
			if (answer != null) {
				while (answer.next()) {
					System.out.println(answer.getInt(1)+"\t\t" + answer.getInt(2));
				}
			}
		} catch (SQLException e) {
				System.err.println("*** SQLException:  "
					+ "Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				e.printStackTrace();
				System.exit(-1);
		}
	}
}
