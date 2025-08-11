package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Flight;

import java.util.Iterator;

/**
 * Command to cancel and remove a booking for a specific customer and flight.
 * <p>
 * This command locates a booking matching the given customer ID and flight ID,
 * cancels it by updating its status, removes it from the customer's and flight's
 * booking lists, and persists the changes.
 * </p>
 */
public class CancelBookings implements Command {

    /** The ID of the customer whose booking is to be cancelled. */
    private final int customerId;

    /** The ID of the flight for which the booking is to be cancelled. */
    private final int flightId;

    /**
     * Constructs a CancelBookings command with the specified customer and flight IDs.
     *
     * @param customerId the ID of the customer
     * @param flightId the ID of the flight
     */
    public CancelBookings(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    /**
     * Executes the cancellation of the booking for the specified customer and flight.
     * <p>
     * Searches the customer's bookings for one matching the flight ID.
     * If found, marks the booking as cancelled, removes it from both customer and flight,
     * and saves the changes via the data manager.
     * </p>
     *
     * @param flightBookingSystem the flight booking system instance used for data access and persistence
     * @throws FlightBookingSystemException if no booking is found or if saving data fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerById(customerId);
        Flight flight = flightBookingSystem.getFlightById(flightId);

        boolean bookingFound = false;

        // Use iterator to safely remove booking while iterating
        Iterator<Booking> iterator = customer.getBookings().iterator();
        while (iterator.hasNext()) {
            Booking booking = iterator.next();
            if (booking.getFlight().getId() == flightId) {
                // Remove booking from customer's list
                iterator.remove();

                // Remove the booking from flight's list of bookings
                flight.removeBooking(booking);

                // Cancel the booking (update status to CANCELLED)
                booking.cancel();

                // Persist the changes to the file
                try {
                    flightBookingSystem.getDataManager().storeData(flightBookingSystem);
                } catch (Exception e) {
                    throw new FlightBookingSystemException("Error saving data after booking cancellation: " + e.getMessage());
                }

                System.out.println("Booking for customer " + customer.getName() +
                        " on flight " + flightId + " has been cancelled and removed.");
                bookingFound = true;
                break;
            }
        }

        if (!bookingFound) {
            throw new FlightBookingSystemException(
                "No booking found for customer " + customer.getName() + " on flight " + flightId + ".");
        }
    }
}
