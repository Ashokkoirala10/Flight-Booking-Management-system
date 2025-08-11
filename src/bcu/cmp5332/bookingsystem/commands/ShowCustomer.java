package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * The {@code ShowCustomer} command displays the main details and booking
 * history of a customer identified by a given customer ID.
 * <p>
 * It fetches the customer from the system and prints detailed information,
 * including personal details and all bookings.
 * </p>
 */
public class ShowCustomer implements Command {

    /** The ID of the customer to be displayed. */
    private final int customerId;

    /**
     * Constructs a {@code ShowCustomer} command with the specified customer ID.
     *
     * @param customerId the ID of the customer to retrieve and display
     */
    public ShowCustomer(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Executes the command to retrieve and display a customer's full information.
     *
     * @param flightBookingSystem the flight booking system context
     * @throws FlightBookingSystemException if the customer is not found or another error occurs
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Try to retrieve the customer by ID
        Customer customer = flightBookingSystem.getCustomerById(customerId);

        // Check if customer exists before accessing properties
        if (customer != null) {
            // Print basic customer information
            System.out.println("Customer ID: " + customer.getId());
            System.out.println("Name: " + customer.getName());
            System.out.println("Phone: " + customer.getPhone());
            System.out.println("---------------------------");
            System.out.println("Bookings:");

            // Print detailed booking info (long format)
            System.out.println(customer.getDetailsLong());
        } else {
            // Notify user if customer was not found
            System.out.println("Customer ID not found.");
        }
    }
}
