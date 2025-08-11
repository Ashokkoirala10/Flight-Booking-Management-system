package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.util.List;
import java.util.Scanner;

/**
 * Command implementation that lists customers registered in the flight booking system.
 * <p>
 * Displays a summary table of all customers with basic details. The user is then
 * prompted to optionally view detailed information about a specific customer by
 * entering the customer ID.
 * </p>
 */
public class ListCustomers implements Command {

    /**
     * Executes the command to list all customers.
     * <p>
     * Retrieves all customers from the system and displays them in a formatted list.
     * If there are no customers, an appropriate message is shown. The user can
     * request detailed information about a specific customer by entering the ID.
     * </p>
     *
     * @param flightBookingSystem the flight booking system instance containing customers
     * @throws FlightBookingSystemException never thrown in this implementation, but required by interface
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        List<Customer> customers = flightBookingSystem.getAllCustomers();
        Scanner scanner = new Scanner(System.in);

        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        // Print table header for customer summary
        System.out.printf(
            "%-5s %-20s %-15s %-5s %-15s %-25s %-10s %-10s\n",
            "ID", "Name", "Phone", "Age", "Country", "Email", "Gender", "Disabled"
        );
        System.out.println("-----------------------------------------------------------------------------------------------------------------");

        // Print short details of each customer
        for (Customer customer : customers) {
            System.out.println(customer.getDetailsShort());
        }

        System.out.println(customers.size() + " customer(s) found.");
        System.out.print("Do you want to see detailed information about any customer? (Enter customer ID or press Enter to skip): ");

        // Prompt user for optional detailed view
        String customerIdInput = scanner.nextLine().trim();
        if (!customerIdInput.isEmpty()) {
            try {
                int customerId = Integer.parseInt(customerIdInput);
                Customer customer = getCustomerById(flightBookingSystem, customerId);

                if (customer != null) {
                    // Display detailed information for the selected customer
                    System.out.println(customer.getDetailsLong());
                } else {
                    System.out.println("Customer ID not found.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid customer ID.");
            }
        }
    }

    /**
     * Helper method to find a customer by their unique ID.
     *
     * @param flightBookingSystem the flight booking system instance
     * @param customerId          the ID of the customer to find
     * @return the Customer object if found; otherwise, null
     */
    private Customer getCustomerById(FlightBookingSystem flightBookingSystem, int customerId) {
        for (Customer customer : flightBookingSystem.getAllCustomers()) {
            if (customer.getId() == customerId) {
                return customer;
            }
        }
        return null; // Return null if no customer with the given ID is found
    }
}
