package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Command implementation that lists bookings in the flight booking system.
 * <p>
 * Allows the user to view either all bookings or only future bookings (those
 * with flights departing after the current date). Bookings are displayed in
 * a summary table and can be selected to view detailed information.
 * </p>
 */
public class ListBookings implements Command {

    /**
     * Executes the command to list bookings.
     * <p>
     * Prompts the user to choose between displaying all bookings or only future bookings.
     * Then, displays a sorted summary of bookings with key details. Users can input
     * a booking ID to see full booking details or return to the previous menu.
     * </p>
     *
     * @param flightBookingSystem the flight booking system instance containing customers and bookings
     * @throws FlightBookingSystemException never thrown in this implementation, but required by interface
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Type 'all' to show all bookings or 'future' to show only future bookings: ");
            String choice = reader.readLine().trim().toLowerCase();

            List<Booking> allBookings = getAllBookings(flightBookingSystem);
            List<Booking> filteredBookings;

            if (choice.equals("future")) {
                filteredBookings = new ArrayList<>();
                // Filter bookings with flights departing after today
                java.time.LocalDate today = java.time.LocalDate.now();
                for (Booking booking : allBookings) {
                    Flight flight = booking.getFlight();
                    if (flight != null && flight.getDepartureDate() != null && flight.getDepartureDate().isAfter(today)) {
                        filteredBookings.add(booking);
                    }
                }
            } else if (choice.equals("all")) {
                filteredBookings = allBookings;
            } else {
                System.out.println("Invalid choice, showing all bookings by default.");
                filteredBookings = allBookings;
            }

            if (filteredBookings.isEmpty()) {
                System.out.println("No bookings found for the selected option.");
                return;
            }

            // Sort bookings by booking ID ascending
            Collections.sort(filteredBookings, Comparator.comparingInt(Booking::getBookingId));

            // Display booking summary header
            System.out.println("Bookings Summary:");
            System.out.printf("%-5s %-20s %-25s %-15s %-10s %-10s %-10s\n", 
                              "ID", "Customer", "Flight", "Dept.Date", "Class", "Price", "Status");
            System.out.println("---------------------------------------------------------------------------------------------------");

            // Display each booking's summary info
            for (Booking booking : filteredBookings) {
                Customer customer = booking.getCustomer();
                Flight flight = booking.getFlight();
                String flightInfo = (flight != null)
                    ? flight.getFlightNumber() + " (" + flight.getOrigin() + "→" + flight.getDestination() + ")"
                    : "N/A";
                String date = (flight != null && flight.getDepartureDate() != null) ? flight.getDepartureDate().toString() : "N/A";

                System.out.printf("%-5d %-20s %-25s %-15s %-10s $%-6.1f %-10s\n",
                    booking.getBookingId(),
                    customer.getName(),
                    flightInfo,
                    date,
                    booking.getSeatClass(),
                    booking.getPrice(),
                    booking.getStatus());
            }

            // Allow user to select a booking ID for full details
            System.out.print("\nEnter Booking ID to view full details (or press Enter to go back): ");
            String input = reader.readLine().trim();
            if (input.isEmpty()) {
                System.out.println("Going back.");
                return;
            }

            int id = Integer.parseInt(input);
            Booking selectedBooking = null;
            for (Booking b : filteredBookings) {
                if (b.getBookingId() == id) {
                    selectedBooking = b;
                    break;
                }
            }

            if (selectedBooking != null) {
                System.out.println("\n--- Booking Details ---");
                System.out.println(selectedBooking.getBookingDetails());
                System.out.println("------------------------\n");
            } else {
                System.out.println("Booking ID not found.");
            }

        } catch (IOException e) {
            System.out.println("Error reading input.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid Booking ID.");
        }
    }

    /**
     * Helper method to collect all bookings from all customers in the system.
     *
     * @param flightBookingSystem the flight booking system instance
     * @return a list containing all bookings from all customers
     */
    private List<Booking> getAllBookings(FlightBookingSystem flightBookingSystem) {
        List<Booking> allBookings = new ArrayList<>();
        for (Customer customer : flightBookingSystem.getAllCustomers()) {
            if (customer.getBookings() != null) {
                allBookings.addAll(customer.getBookings());
            }
        }
        return allBookings;
    }
}
