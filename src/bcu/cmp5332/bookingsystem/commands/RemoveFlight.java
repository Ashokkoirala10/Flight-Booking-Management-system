package bcu.cmp5332.bookingsystem.commands;



import bcu.cmp5332.bookingsystem.data.FlightDataManager;
import bcu.cmp5332.bookingsystem.data.BookingDataManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import java.util.ArrayList;

/**
 * Command to remove a flight and all its associated bookings from the system.
 * <p>
 * This command locates the flight by its ID, cancels and removes all bookings
 * associated with that flight, then removes the flight from the system.
 * It then persists the updated flight and booking data.
 * </p>
 */
public class RemoveFlight implements Command {

    /** The ID of the flight to be removed */
    private final int flightId;

    /**
     * Constructs the RemoveFlight command with the specified flight ID.
     * 
     * @param flightId the ID of the flight to remove
     */
    public RemoveFlight(int flightId) {
        this.flightId = flightId;
    }

    /**
     * Executes the removal of the flight and its bookings.
     * <p>
     * Throws an exception if the flight does not exist.
     * All bookings for the flight are removed from both the customers and the system.
     * Data managers are called to persist the changes.
     * </p>
     * 
     * @param flightBookingSystem the flight booking system instance
     * @throws FlightBookingSystemException if the flight ID is invalid or data persistence fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Flight flight = flightBookingSystem.getFlightById(flightId);
        if (flight == null) {
            throw new FlightBookingSystemException(" Flight ID " + flightId + " not found.");
        }

        // Cancel and remove all bookings associated with this flight
        // Use a copy of the bookings list to avoid concurrent modification
        for (Booking booking : new ArrayList<>(flightBookingSystem.getAllBookings())) {
            if (booking.getFlight().getId() == flightId) {
                booking.getCustomer().removeBooking(booking);       // Remove booking from customer's list
                flightBookingSystem.removeBooking(booking);          // Remove booking from system-wide list
            }
        }

        // Remove the flight from the system
        flightBookingSystem.removeFlight(flight);

        // Persist changes to flight and booking data files
        try {
            new FlightDataManager().storeData(flightBookingSystem);
            new BookingDataManager().storeData(flightBookingSystem);
        } catch (Exception e) {
            throw new FlightBookingSystemException(" Error saving data after removing flight: " + e.getMessage());
        }

        System.out.println(" Flight '" + flight.getFlightNumber() + "' (ID: " + flightId + ") and all associated bookings removed successfully.");
    }
}
