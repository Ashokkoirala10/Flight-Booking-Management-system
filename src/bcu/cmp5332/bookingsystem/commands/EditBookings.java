package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.main.Main;
import bcu.cmp5332.bookingsystem.model.*;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Command to edit an existing booking by its booking ID.
 * <p>
 * Allows updating the flight, booking date, status, seat class, seat number,
 * price, and pet information associated with the booking.
 * An updating fee is added to the price on each modification.
 * </p>
 */
public class EditBookings implements Command {

    private final int bookingId;

    /**
     * Constructs an EditBookings command for the given booking ID.
     *
     * @param bookingId the ID of the booking to edit
     */
    public EditBookings(int bookingId) {
        this.bookingId = bookingId;
    }

    /**
     * Executes the command to edit the booking.
     * <p>
     * Interactively prompts the user for updates to various booking details.
     * </p>
     *
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if the booking is not found or input errors occur
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Booking booking = findBookingById(flightBookingSystem, bookingId);

        if (booking == null) {
            throw new FlightBookingSystemException("Booking with ID " + bookingId + " not found.");
        }

        try {
            // === Booking Overview ===
            System.out.println("\n=== 📝 Booking Overview ===");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Customer: " + booking.getCustomer().getName());
            System.out.println("\nCurrent Flight:");
            System.out.printf("%-5s %-10s %-20s %-15s %-15s %-22s %-22s %-12s %-12s | %-25s\n",
                    "ID", "#Flight", "Airline", "Origin", "Destination",
                    "Departure", "Arrival", "Status", "Flight Type", "Prices (E/B/F)");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            Flight flight1 = booking.getFlight();
            System.out.println(flight1.getDetailsShort());

            System.out.println("\nSeat Class: " + booking.getSeatClass());
            System.out.println("Seat Number: " + booking.getSeatNumber());
            System.out.printf("Discount: %.2f%%\n", booking.getDiscountPercent());
            System.out.printf("Price: $%.2f\n", booking.getPrice());
            System.out.println("Status: " + booking.getStatus());
            System.out.println("Pet: " + (booking.getPetType() != null && !booking.getPetType().isEmpty() ? booking.getPetType() : "No Pet"));
            System.out.printf("Pet Charge: $%.2f\n", booking.getPetCharge());

            // === Available Flights ===
            System.out.println("\n=== ✈️ Available Flights ===");
            System.out.printf("%-4s %-10s %-20s %-15s %-15s %-20s\n", "ID", "FlightNo", "Airline", "Origin", "Destination", "Departure");
            System.out.println("----------------------------------------------------------------------------------------------");
            for (Flight flight : flightBookingSystem.getAllFlights()) {
                System.out.printf("%-4d %-10s %-20s %-15s %-15s %-20s\n",
                        flight.getId(),
                        flight.getFlightNumber(),
                        flight.getAirlineName(),
                        flight.getOrigin(),
                        flight.getDestination(),
                        flight.getDepartureDate().toString());
            }

            // === Flight Update ===
            System.out.print("\nEnter new Flight ID (or press Enter to keep current): ");
            String input = Main.reader.readLine().trim();
            if (!input.isEmpty()) {
                try {
                    int newFlightId = Integer.parseInt(input);
                    Flight newFlight = flightBookingSystem.getFlightById(newFlightId);
                    if (newFlight != null) {
                        booking.setFlight(newFlight);
                        System.out.println(" Flight updated.");
                    } else {
                        System.out.println(" Flight ID not found. No change.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" Invalid flight ID format.");
                }
            }

            // === Booking Details Update ===
            System.out.println("\n===  Booking Details Update ===");

            System.out.print("Enter new Booking Date (YYYY-MM-DD, or Enter to keep current): ");
            input = Main.reader.readLine().trim();
            if (!input.isEmpty()) {
                try {
                    booking.setBookingDate(LocalDate.parse(input));
                    System.out.println(" Booking date updated.");
                } catch (Exception e) {
                    System.out.println(" Invalid date format.");
                }
            }

            System.out.print("Enter new Status (ACTIVE, CANCELLED, COMPLETED): ");
            input = Main.reader.readLine().trim();
            if (!input.isEmpty()) {
                try {
                    booking.setStatus(Booking.Status.valueOf(input.toUpperCase()));
                    System.out.println(" Status updated.");
                } catch (IllegalArgumentException e) {
                    System.out.println(" Invalid status.");
                }
            }

            System.out.print("Enter new Seat Class (ECONOMY, BUSINESS, FIRST): ");
            input = Main.reader.readLine().trim();
            if (!input.isEmpty()) {
                try {
                    booking.setSeatClass(Booking.SeatClass.valueOf(input.toUpperCase()));
                    System.out.println(" Seat class updated.");
                } catch (IllegalArgumentException e) {
                    System.out.println(" Invalid seat class.");
                }
            }

            System.out.print("Enter new Seat Number (or Enter to keep current): ");
            input = Main.reader.readLine().trim();
            if (!input.isEmpty()) {
                booking.setSeatNumber(input);
                System.out.println(" Seat number updated.");
            }

            System.out.print("Enter new Price (or Enter to keep current): ");
            input = Main.reader.readLine().trim();
            if (!input.isEmpty()) {
                try {
                    booking.setPrice(Double.parseDouble(input));
                    System.out.println(" Price updated.");
                } catch (NumberFormatException e) {
                    System.out.println(" Invalid price format.");
                }
            }

            // === Pet Update ===
            System.out.println("\n===  Pet Information ===");
            System.out.print("Is there a pet with this booking? (yes/no or Enter to keep current): ");
            input = Main.reader.readLine().trim().toLowerCase();

            if (input.equals("yes")) {
                while (true) {
                    System.out.print("Enter pet type (cat, dog, bird): ");
                    String petType = Main.reader.readLine().trim().toLowerCase();
                    if (petType.equals("cat") || petType.equals("dog") || petType.equals("bird")) {
                        booking.setPetType(petType);
                        if (booking.getPetCharge() == 0.0) {
                            booking.setPetCharge(15.0);
                            booking.setPrice(booking.getPrice() + 15.0);
                        }
                        System.out.println(" Pet updated and $15.00 fee applied.");
                        break;
                    } else {
                        System.out.println(" Invalid pet type. Choose from: cat, dog, bird.");
                    }
                }
            } else if (input.equals("no")) {
                if (booking.getPetCharge() > 0.0) {
                    booking.setPrice(booking.getPrice() - booking.getPetCharge());
                    System.out.printf(" Pet removed. $%.2f deducted from price.\n", booking.getPetCharge());
                    booking.setPetCharge(0.0);
                    booking.setPetType("None");
                } else {
                    System.out.println(" No pet associated with this booking.");
                }
            }

            // === Minimal Price Adjustment ===
            final double minimalUpdatingFee = 10.0;
            booking.setPrice(booking.getPrice() + minimalUpdatingFee);
            System.out.printf("Updating Fee : $%.2f added to the total price.\n", minimalUpdatingFee);

            System.out.println(" Booking updated successfully.\n");

        } catch (IOException e) {
            throw new FlightBookingSystemException("Error reading input.");
        }
    }

    /**
     * Finds a booking by its ID across all customers in the system.
     *
     * @param flightBookingSystem the flight booking system to search
     * @param bookingId           the booking ID to find
     * @return the booking if found; null otherwise
     */
    private Booking findBookingById(FlightBookingSystem flightBookingSystem, int bookingId) {
        for (Customer customer : flightBookingSystem.getAllCustomers()) {
            for (Booking booking : customer.getBookings()) {
                if (booking.getBookingId() == bookingId) {
                    return booking;
                }
            }
        }
        return null;
    }
}
