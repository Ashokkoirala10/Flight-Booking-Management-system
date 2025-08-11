package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * The {@code ShowBookings} command is responsible for displaying all booking
 * information associated with a specific customer.
 * <p>
 * This command retrieves the customer by their ID and prints out their detailed
 * booking history if found.
 * </p>
 */
public class ShowBookings implements Command {

    /** The ID of the customer whose bookings are to be shown. */
    private final int customerId;

    /**
     * Constructs a new {@code ShowBookings} command with the specified customer ID.
     *
     * @param customerId the ID of the customer whose bookings should be displayed
     */
    public ShowBookings(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Executes the command to display the booking history of a specific customer.
     *
     * @param flightBookingSystem the flight booking system containing customer data
     * @throws FlightBookingSystemException if an error occurs during execution
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Retrieve the customer using the provided ID
        Customer customer = flightBookingSystem.getCustomerById(customerId);

        if (customer != null) {
            // Display detailed information of the customer including all bookings
            System.out.println(customer.getDetailsLong());
        } else {
            // Inform the user if the customer ID was not found in the system
            System.out.println("Customer ID not found.");
        }
    }
}
