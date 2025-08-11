package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Command to search flights based on multiple criteria such as origin, destination,
 * departure date range, airline, and flight status.
 * <p>
 * The search results are displayed in a summarized format, with an option to view
 * detailed information for each matched flight.
 * </p>
 */
public class SearchFlight implements Command {

    /** Search criteria fields */
    public final String origin;
    public final String destination;
    public final LocalDate exactDepartureDate;
    public final LocalDate exactArrivalDate;
    public final String airline;
    public final String status;

    /**
     * Constructs a SearchFlight command with the given search parameters.
     *
     * @param origin          the origin airport (nullable)
     * @param destination     the destination airport (nullable)
     * @param departureStart  the start of the departure date range (nullable)
     * @param departureEnd    the end of the departure date range (nullable)
     * @param airline         the airline name (nullable)
     * @param status          the flight status as string (nullable)
     */
    public SearchFlight(String origin, String destination,
            LocalDate exactDepartureDate, LocalDate exactArrivalDate,
            String airline, String status) {
			this.origin = origin;
			this.destination = destination;
			this.exactDepartureDate = exactDepartureDate;
			this.exactArrivalDate = exactArrivalDate;
			this.airline = airline;
			this.status = status;
			}

    /**
     * Executes the flight search on the provided FlightBookingSystem instance.
     * <p>
     * Matches flights based on the provided criteria and displays short details
     * of matching flights. Offers the user an option to view detailed information
     * for each matched flight.
     * </p>
     *
     * @param fbs the FlightBookingSystem instance
     * @throws FlightBookingSystemException if an invalid flight status is provided
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        List<Flight> flights = fbs.getAllFlights();
        List<Flight> matchedFlights = new ArrayList<>();
        boolean found = false;

        // Parse the status string into FlightStatus enum if provided
        FlightStatus parsedStatus = null;
        if (status != null) {
            parsedStatus = FlightStatus.fromString(status);
            if (parsedStatus == null) {
                throw new FlightBookingSystemException("Invalid flight status: " + status);
            }
        }

        // Print header for flight list
        System.out.printf("\n%-5s %-10s %-20s %-15s %-15s %-20s %-20s %-10s %-10s\n",
                "ID", "Flight#", "Airline", "Origin", "Destination",
                "Departure", "Arrival", "Status", "Type");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");

        // Iterate through all flights and check if they match the search criteria
        for (Flight flight : flights) {
            boolean match = (origin == null || flight.getOrigin().equalsIgnoreCase(origin)) &&
                            (destination == null || flight.getDestination().equalsIgnoreCase(destination));

            LocalDate flightDeparture = flight.getDepartureDate();
            LocalDate flightArrival = flight.getArrivalDate();

            if (exactDepartureDate != null && !flightDeparture.isEqual(exactDepartureDate)) {
                continue; // Skip if departure date doesn't match exactly
            }

            if (exactArrivalDate != null && (flightArrival == null || !flightArrival.isEqual(exactArrivalDate))) {
                continue; // Skip if arrival date doesn't match exactly or is missing
            }


            if (airline != null) {
                match &= flight.getAirlineName().equalsIgnoreCase(airline);
            }

            if (parsedStatus != null) {
                match &= flight.getStatus().equals(parsedStatus);
            }

            if (match) {
                System.out.println(flight.getDetailsShort());
                matchedFlights.add(flight);
                found = true;
            }
        }

        // If no flights found, inform the user and return early
        if (!found) {
            System.out.println("No flights found from " + origin + " to " + destination + ".");
            return;
        }

        // Prompt user to view detailed info for matched flights
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nDo you want to see detailed info for the above flights? (yes/no): ");
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("yes")) {
            System.out.println("\nDetailed flight information:");
            for (Flight flight : matchedFlights) {
                System.out.println(flight.getFlightDetailsWithPassengers());
                System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");
            }
        } else {
            System.out.println("Skipping detailed information.");
        }
    }
}
