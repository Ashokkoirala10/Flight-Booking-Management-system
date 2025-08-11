package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Command implementation to list flights available in the flight booking system.
 * <p>
 * Displays a summary of all upcoming flights (excluding past flights) with essential details.
 * Allows the user to optionally view detailed information about a specific flight, 
 * including passenger details, by entering the flight ID.
 * </p>
 */
public class ListFlights implements Command {

    /**
     * Executes the command to display a list of all upcoming flights.
     * <p>
     * Retrieves all flights from the system, filters out flights that have already departed,
     * and displays them in a formatted list. The user can then choose to view full details
     * for a particular flight by entering its ID.
     * </p>
     *
     * @param flightBookingSystem the flight booking system instance containing flights
     * @throws FlightBookingSystemException not thrown directly but declared by interface
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        List<Flight> flights = flightBookingSystem.getAllFlights();
        LocalDateTime now = LocalDateTime.now();

        // Remove flights whose departure time is before now
        flights.removeIf(flight ->
            LocalDateTime.of(flight.getDepartureDate(), flight.getDepartureTime()).isBefore(now)
        );

        Scanner scanner = new Scanner(System.in);

        if (flights.isEmpty()) {
            System.out.println("No flights available.");
            return;
        }

        // Print table header for flight summary
        System.out.printf("%-5s %-10s %-20s %-15s %-15s %-22s %-22s %-12s %-12s | %-25s\n",
                "ID", "#Flight", "Airline", "Origin", "Destination",
                "Departure", "Arrival", "Status", "Flight Type", "Prices (E/B/F)");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        // Print short details for each flight
        for (Flight flight : flights) {
            System.out.println(flight.getDetailsShort());
        }

        System.out.println(flights.size() + " flight(s) found.");
        System.out.print("Do you want to see detailed information about any flight? (Enter flight ID or press Enter to skip): ");
        
        // Optionally show detailed flight info including passengers
        String flightIdInput = scanner.nextLine().trim();
        if (!flightIdInput.isEmpty()) {
            try {
                int flightId = Integer.parseInt(flightIdInput);
                Flight selectedFlight = getFlightById(flightBookingSystem, flightId);
                
                if (selectedFlight != null) {
                    System.out.println("\n----------------- Flight Details ------------------");
                    System.out.println(selectedFlight.getFlightDetailsWithPassengers());
                    System.out.println("------------------------------------------------------------");
                } else {
                    System.out.println(" Flight ID not found.");
                }
            } catch (NumberFormatException e) {
                System.out.println(" Invalid input. Please enter a valid flight ID.");
            }
        }
    }

    /**
     * Helper method to find a flight by its unique ID.
     *
     * @param flightBookingSystem the flight booking system instance
     * @param flightId            the ID of the flight to find
     * @return the Flight object if found; otherwise, null
     */
    private Flight getFlightById(FlightBookingSystem flightBookingSystem, int flightId) {
        for (Flight flight : flightBookingSystem.getAllFlights()) {
            if (flight.getId() == flightId) {
                return flight;
            }
        }
        return null;
    }
}
