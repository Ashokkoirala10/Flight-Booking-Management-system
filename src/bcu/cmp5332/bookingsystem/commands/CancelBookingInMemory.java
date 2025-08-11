package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.io.IOException;
import java.util.*;

/**
 * Command to cancel a booking and store the cancelled booking in memory.
 * <p>
 * This command allows the user to cancel an existing active or completed booking for a specified customer and flight.
 * The cancelled booking is marked as CANCELLED and stored in an in-memory static list for reference.
 * </p>
 */
public class CancelBookingInMemory implements Command {

    /**
     * A static list that holds all cancelled bookings stored in memory.
     * This allows tracking of cancelled bookings during the runtime of the application.
     */
    public static final List<Booking> cancelledBookingsMemory = new ArrayList<>();

    /**
     * Executes the cancellation process by interacting with the user via console input.
     * <p>
     * Prompts the user to input a customer ID and lists that customer's bookings.
     * The user then selects a flight ID from the bookings to cancel.
     * The booking status is updated to CANCELLED, the passenger is removed from the flight,
     * and the cancelled booking is added to the in-memory list.
     * The booking file is updated immediately.
     * </p>
     *
     * @param flightBookingSystem the flight booking system instance used to fetch customers and their bookings
     * @throws FlightBookingSystemException if an input error occurs or the booking cannot be cancelled
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Enter customer ID to cancel booking: ");
            int customerId = Integer.parseInt(reader.readLine());

            Customer customer = flightBookingSystem.getCustomerById(customerId);
            List<Booking> bookings = customer.getBookings();

            if (bookings.isEmpty()) {
                System.out.println("This customer has no bookings.");
                return;
            }

            System.out.println("Bookings for customer " + customer.getName() + ":");
            for (Booking booking : bookings) {
                System.out.println("Flight ID: " + booking.getFlight().getId() +
                        ", Destination: " + booking.getFlight().getDestination() +
                        ", Date: " + booking.getFlight().getDepartureDate());
            }

            System.out.print("Enter Flight ID from the above list to cancel: ");
            int flightId = Integer.parseInt(reader.readLine());

            Booking bookingToCancel = null;
            for (Booking booking : bookings) {
                if (booking.getFlight().getId() == flightId &&
                   (booking.getStatus() == Booking.Status.ACTIVE || booking.getStatus() == Booking.Status.COMPLETED)) {
                    bookingToCancel = booking;
                    break;
                }
            }

            if (bookingToCancel == null) {
                System.out.println("Active booking not found for given flight ID.");
                return;
            }

            // Update booking status in memory
            bookingToCancel.setStatus(Booking.Status.CANCELLED);

            // Remove passenger from flight
            bookingToCancel.getFlight().removePassenger(customer, bookingToCancel.getSeatClass());
            bookingToCancel.getFlight().releaseSeat(bookingToCancel.getSeatClass(), bookingToCancel.getSeatNumber());


            // Update the booking status in the bookings.txt file immediately
            updateBookingStatusInFile(bookingToCancel.getBookingId());

            // Save to cancelled booking file for record (optional)
            saveCancelledBookingToFile(customer, bookingToCancel);

            // Add to in-memory cancelled bookings list (optional)
            cancelledBookingsMemory.add(bookingToCancel);

            // Remove from customer's booking list
            bookings.remove(bookingToCancel);
            bookingToCancel.getFlight().removeBooking(bookingToCancel);


            System.out.println("Booking for customer " + customer.getName() +
                    " on flight " + flightId + " has been cancelled and updated in the booking file.");

        } catch (IOException | NumberFormatException ex) {
            throw new FlightBookingSystemException("Error during input: " + ex.getMessage());
        }
    }

    private void saveCancelledBookingToFile(Customer customer, Booking booking) {
        try (FileWriter writer = new FileWriter("resources/data/cancelbooking.txt", true)) {
            writer.write("Customer ID: " + customer.getId() +
                    ", Flight ID: " + booking.getFlight().getId() +
                    ", SeatClass: " + booking.getSeatClass() +
                    ", SeatNumber: " + booking.getSeatNumber() +
                    ", Price: $" + booking.getPrice() +
                    ", DiscountPercent: " + booking.getDiscountPercent() +
                    ", ManualDiscount: " + booking.isManualDiscount() +
                    ", PetType: " + booking.getPetType() +
                    ", PetCharge: " + booking.getPetCharge() + "\n");
        } catch (IOException e) {
            System.out.println("Failed to write cancelled booking to file: " + e.getMessage());
        }
    }

    private void updateBookingStatusInFile(int bookingId) throws IOException {
        File bookingsFile = new File("resources/data/bookings.txt");
        List<String> lines = new ArrayList<>(Files.readAllLines(bookingsFile.toPath()));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(bookingsFile))) {
            for (String line : lines) {
                if (line.startsWith(bookingId + "::")) {
                    // Modify line to mark status as CANCELLED (assuming 7th field is status)
                    String[] parts = line.split("::");
                    parts[6] = "CANCELLED"; // Update status field
                    writer.write(String.join("::", parts));
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        }
    }

    /**
     * Retrieves the list of cancelled bookings stored in memory.
     *
     * @return an unmodifiable list of cancelled bookings currently held in memory
     */
    public static List<Booking> getCancelledBookingsMemory() {
        return Collections.unmodifiableList(cancelledBookingsMemory);
    }
}
