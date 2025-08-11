package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.CustomerDataManager;
import bcu.cmp5332.bookingsystem.data.BookingDataManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import java.util.ArrayList;

/**
 * Command to remove a customer and all their associated bookings from the system.
 * <p>
 * This command locates the customer by their ID, cancels and removes all bookings
 * associated with that customer, then removes the customer from the system.
 * It then persists the updated customer and booking data.
 * </p>
 */
public class RemoveCustomer implements Command {

    /** The ID of the customer to be removed */
    private final int customerId;

    /**
     * Constructs the RemoveCustomer command with the specified customer ID.
     * 
     * @param customerId the ID of the customer to remove
     */
    public RemoveCustomer(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Executes the removal of the customer and their bookings.
     * <p>
     * Throws an exception if the customer does not exist.
     * All bookings for the customer are removed from both the flights and the system.
     * Data managers are called to persist the changes.
     * </p>
     * 
     * @param flightBookingSystem the flight booking system instance
     * @throws FlightBookingSystemException if the customer ID is invalid or data persistence fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerById(customerId);
        if (customer == null) {
            throw new FlightBookingSystemException(" Customer ID " + customerId + " not found.");
        }

        // Cancel and remove all bookings associated with this customer
        // Use a copy of the bookings list to avoid concurrent modification
        for (Booking booking : new ArrayList<>(flightBookingSystem.getAllBookings())) {
            if (booking.getCustomer().getId() == customerId) {
                booking.getFlight().removeBooking(booking);         // Remove booking from flight's list
                flightBookingSystem.removeBooking(booking);         // Remove booking from system-wide list
            }
        }

        // Remove the customer from the system
        flightBookingSystem.removeCustomer(customer);

        // Persist changes to customer and booking data files
        try {
            new CustomerDataManager().storeData(flightBookingSystem);
            new BookingDataManager().storeData(flightBookingSystem);
        } catch (Exception e) {
            throw new FlightBookingSystemException(" Error saving data after removing customer: " + e.getMessage());
        }

        System.out.println(" Passenger '" + customer.getName() + "' (ID: " + customerId + ") and all associated bookings removed successfully.");
    }
}
