package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.time.LocalDate;
import java.time.LocalTime;

public class AddBooking implements Command {

	/**
	 * The unique identifier of the customer who makes the booking.
	 */
	private final int customerId;

	/**
	 * The unique identifier of the flight to be booked.
	 */
	private final int flightId;

	/**
	 * The seat class chosen for the booking.
	 */
	private final Booking.SeatClass seatClass;

	/**
	 * Flag indicating whether a manual discount was applied by the user.
	 */
	boolean isManualDiscount = false;

	/**
	 * Constructs a new AddBooking command.
	 *
	 * @param customerId the ID of the customer making the booking
	 * @param flightId the ID of the flight to be booked
	 * @param seatClass the seat class selected for the booking
	 */
	public AddBooking(int customerId, int flightId, Booking.SeatClass seatClass) {
	    this.customerId = customerId;
	    this.flightId = flightId;
	    this.seatClass = seatClass;
	}

	/**
	 * Executes the add booking command.
	 * <p>
	 * This method performs the following steps:
	 * <ol>
	 *   <li>Retrieves customer and flight based on IDs.</li>
	 *   <li>Checks if the flight exists and has not already departed.</li>
	 *   <li>Prompts user input for seat number, discount, and pet details.</li>
	 *   <li>Validates that the customer does not already have a booking for this flight.</li>
	 *   <li>Verifies seat availability for the selected seat class and seat number.</li>
	 *   <li>Calculates the price applying age, disability discounts or manual discount.</li>
	 *   <li>Includes pet charge if applicable.</li>
	 *   <li>Creates and completes the booking.</li>
	 *   <li>Reserves the seat on the flight and updates customer and flight booking lists.</li>
	 *   <li>Updates the booking records file with the new booking.</li>
	 * </ol>
	 * </p>
	 *
	 * @param flightBookingSystem the flight booking system instance providing system data and operations
	 * @throws FlightBookingSystemException if validation fails, input errors occur, or booking cannot be completed
	 */
	@Override
	public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
	    try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	        // Retrieve the customer and flight objects by their IDs
	        Customer customer = flightBookingSystem.getCustomerById(customerId);
	        Flight flight = flightBookingSystem.getFlightById(flightId);

	        if (flight == null) {
	            System.out.println(" Flight not found.");
	            return;  // Exit if flight does not exist
	        }
	        if (customer == null) {
	            System.out.println(" Customer not found.");
	            return;  // Exit if customer does not exist
	        }

	        // Check flight departure date/time to prevent booking past flights
	        LocalDate flightDate = flight.getDepartureDate();
	        LocalTime flightTime = flight.getDepartureTime();
	        LocalDate nowDate = flightBookingSystem.getSystemDate();
	        LocalTime nowTime = LocalTime.now();

	        if (flightDate.isBefore(nowDate) || (flightDate.isEqual(nowDate) && flightTime.isBefore(nowTime))) {
	            System.out.println(" Cannot book a flight that has already departed.");
	            return;  // Exit without exception if flight already departed
	        }

	        // Prompt user for seat number and convert to uppercase
	        System.out.print("Enter Seat Number (e.g., 12A): ");
	        String seatNumber = reader.readLine().toUpperCase();

	        // Prompt user for manual discount input or allow default discount logic
	        System.out.print("Enter manual discount % (or press Enter to use default): ");
	        String discountInput = reader.readLine();

	        // Prompt user about pet accompaniment and gather pet details if any
	        System.out.print("Is there a pet accompanying the customer? (yes/no): ");
	        String petResponse = reader.readLine().trim().toLowerCase();

	        boolean hasPet = petResponse.equals("yes");
	        String petType = "None";
	        double petCharge = 0.0;

	        if (hasPet) {
	            while (true) {
	                System.out.print("Enter pet type (cat, dog, bird): ");
	                petType = reader.readLine().trim().toLowerCase();
	                if (petType.equals("cat") || petType.equals("dog") || petType.equals("bird")) {
	                    petCharge = 15.0;  // fixed pet charge
	                    break;
	                } else {
	                    System.out.println("Invalid pet type. Only cat, dog, or bird are allowed. Please try again.");
	                }
	            }
	        }

	        // Check for duplicate booking of the same flight by this customer
	        for (Booking booking : customer.getBookings()) {
	            if (booking.getFlight().getId() == flight.getId()) {
	                throw new FlightBookingSystemException("This customer already has a booking for this flight.");
	            }
	        }

	        // Verify seat availability for the selected class
	        if (flight.getAvailableSeatsForClass(seatClass) <= 0) {
	            throw new FlightBookingSystemException("No available seats in " + seatClass + " class.");
	        }

	        // Verify seat availability for the selected seat number
	        if (!flight.isSeatAvailable(seatClass, seatNumber)) {
	            throw new FlightBookingSystemException("Seat " + seatNumber + " in " + seatClass + " is already booked.");
	        }

	        // Calculate base price and determine discount
	        double basePrice = flight.getPriceForClass(seatClass);
	        double discountPercent;

	        if (discountInput.isEmpty()) {
	            discountPercent = 0.0;
	            int age = customer.getAge();

	            // Apply age and disability discounts
	            if (age <= 12) discountPercent += 10.0;
	            else if (age >= 60) discountPercent += 15.0;

	            if (customer.isDisabled()) discountPercent += 5.0;
	        } else {
	            // Manual discount specified
	            isManualDiscount = true;
	            try {
	                discountPercent = Double.parseDouble(discountInput);
	                if (discountPercent < 0 || discountPercent > 100) {
	                    throw new FlightBookingSystemException("Manual discount must be between 0 and 100.");
	                }
	            } catch (NumberFormatException e) {
	                throw new FlightBookingSystemException("Invalid discount input.");
	            }
	        }

	        // Calculate final price including discount and pet charge
	        double discountedPrice = basePrice * (1 - discountPercent / 100);
	        double finalPrice = discountedPrice + petCharge;

	        // Get current booking date from system
	        LocalDate bookingDate = flightBookingSystem.getSystemDate();

	        // Create new booking object with all details
	        Booking booking = new Booking(customer, flight, bookingDate, seatClass, finalPrice, seatNumber,
	                discountPercent, isManualDiscount, petType, petCharge);

	        booking.completeBooking();  // mark booking as completed

	        // Reserve seat on flight and update booking lists
	        flight.reserveSeat(seatClass, seatNumber);
	        flight.addBooking(booking);
	        customer.addBooking(booking);

	        // Persist booking to file
	        updateBookingFile(flightBookingSystem, booking);

	        System.out.println(" Booking successfully added:");
	        System.out.println(booking.getBookingDetails());

	    } catch (IOException e) {
	        throw new FlightBookingSystemException("Error reading input.");
	    } catch (IllegalArgumentException e) {
	        throw new FlightBookingSystemException("Invalid seat class entered.");
	    }
	}

	/**
	 * Appends the newly created booking details to the booking data file.
	 *
	 * @param flightBookingSystem the flight booking system instance (not directly used here but kept for consistency)
	 * @param booking the booking to save
	 * @throws IOException if an error occurs while writing to the bookings file
	 */
	private void updateBookingFile(FlightBookingSystem flightBookingSystem, Booking booking) throws IOException {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter("./resources/data/bookings.txt", true))) {
	        String bookingDetails = booking.getBookingId() + "::" +
	                                booking.getCustomer().getId() + "::" +
	                                booking.getFlight().getId() + "::" +
	                                booking.getBookingDate() + "::" +
	                                booking.getSeatClass() + "::" +
	                                booking.getPrice() + "::" +
	                                booking.getStatus() + "::" +
	                                booking.getSeatNumber() + "\n";
	        writer.write(bookingDetails);
	    }
	}
}
