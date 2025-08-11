package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to show full details of a flight by its ID.
 */
public class ShowFlight implements Command {

    private final int flightId;

    /**
     * Constructor initializing the flight ID.
     * @param flightId the ID of the flight to display
     */
    public ShowFlight(int flightId) {
        this.flightId = flightId;
    }

    /**
     * Executes the command to fetch and display detailed information for the specified flight.
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Flight flight = flightBookingSystem.getFlightById(flightId);
        if (flight != null) {
            System.out.println("Flight Details:");
            System.out.println("---------------------------");
            System.out.println(flight.getFlightDetailsWithPassengers());
        } else {
            throw new FlightBookingSystemException("Flight with ID " + flightId + " not found.");
        }
    }
}
