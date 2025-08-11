package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.commands.InputUtils;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;
import bcu.cmp5332.bookingsystem.data.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Command implementation that facilitates interactive flight booking via console input.
 * <p>
 * This command prompts the user for customer details (new or existing),
 * displays available flights, and collects booking information including seat selection,
 * pet details, and discounts. It performs validations at each step and stores
 * the booking in the system upon successful completion.
 * </p>
 */
public class InteractiveBookings implements Command {

    private final CustomerDataManager customerDataManager = new CustomerDataManager();
    private final BookingDataManager bookingDataManager = new BookingDataManager();

    /**
     * Executes the interactive booking process.
     * <p>
     * It asks if the user is a new or existing customer, collects relevant details,
     * displays flights, handles seat and pet selection, applies discounts,
     * creates the booking, and saves the data.
     * </p>
     *
     * @param system the FlightBookingSystem instance managing customers, flights, and bookings
     * @throws FlightBookingSystemException if any error or invalid input occurs during the process
     */
    @Override
    public void execute(FlightBookingSystem system) throws FlightBookingSystemException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Are you a new or existing customer? (new/existing): ");
            String userType = reader.readLine().trim().toLowerCase();

            Customer customer = null;

            if (userType.equals("existing")) {
                System.out.print("Enter Customer ID: ");
                int customerId = Integer.parseInt(reader.readLine().trim());
                customer = system.getCustomerById(customerId);

                if (customer == null) {
                    throw new FlightBookingSystemException("Customer ID not found.");
                }

            } else if (userType.equals("new")) {
                System.out.println("Please provide full details:");

                String name = InputUtils.readNonEmptyString(reader, "Customer Name: ");
                String phone = InputUtils.readPhoneNumber(reader, "Phone Number: ");
                int age = InputUtils.readAge(reader, "Age: ");
                String address = InputUtils.readNonEmptyString(reader, "Address: ");
                String country = InputUtils.readNonEmptyString(reader, "Country: ");
                boolean disabled = InputUtils.readBoolean(reader, "Is this customer disabled? (yes/no): ");
                String passportNumber = InputUtils.readPassportNumber(reader, "Passport Number: ");

                LocalDate passportExpiryDate;
                while (true) {
                    passportExpiryDate = InputUtils.readDate(reader, "Passport Expiry Date (YYYY-MM-DD): ");
                    if (passportExpiryDate.isAfter(LocalDate.now())) {
                        break;  // Valid expiry date
                    } else {
                        System.out.println(" Passport expiry date must be a future date. Please try again.");
                    }
                }

                String email = InputUtils.readEmail(reader, "Email Address: ");
                LocalDate dob = InputUtils.readDate(reader, "Date of Birth (YYYY-MM-DD): ");
                String gender = InputUtils.readString(reader, "Gender (M/F/Other): ");

                customer = system.addCustomer(name, phone, age, address, country,
                        passportNumber, passportExpiryDate, disabled, email, dob, gender);

                customerDataManager.storeData(system);

            } else {
                throw new FlightBookingSystemException("Invalid option. Please type 'new' or 'existing'.");
            }

            // Display available flights after filtering out past departures
            List<Flight> flights = system.getAllFlights();
            if (flights.isEmpty()) {
                System.out.println("No available flights.");
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            flights.removeIf(flight ->
                LocalDateTime.of(flight.getDepartureDate(), flight.getDepartureTime()).isBefore(now)
            );

            System.out.println("Available Flights:");
            System.out.printf("%-5s %-10s %-20s %-15s %-15s %-22s %-22s %-12s %-12s | %-25s\n",
                    "ID", "#Flight", "Airline", "Origin", "Destination",
                    "Departure", "Arrival", "Status", "Flight Type", "Prices (E/B/F)");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            for (Flight flight : flights) {
                System.out.println(flight.getDetailsShort());
            }

            System.out.print("Enter Flight ID to book: ");
            int flightId = Integer.parseInt(reader.readLine().trim());
            Flight selectedFlight = system.getFlightById(flightId);

            if (selectedFlight == null) {
                throw new FlightBookingSystemException("Invalid flight ID.");
            }

            // Select seat class with validation
            System.out.print("Enter Seat Class (ECONOMY/BUSINESS/FIRST): ");
            Booking.SeatClass seatClass = Booking.SeatClass.valueOf(reader.readLine().trim().toUpperCase());

            if (selectedFlight.getAvailableSeatsForClass(seatClass) <= 0) {
                throw new FlightBookingSystemException("No seats available in " + seatClass + " class.");
            }

            // Seat number input and availability check
            System.out.print("Enter Seat Number (e.g., 12A): ");
            String seatNumber = reader.readLine().trim().toUpperCase();

            if (!selectedFlight.isSeatAvailable(seatClass, seatNumber)) {
                throw new FlightBookingSystemException("Seat " + seatNumber + " is not available.");
            }

            // Pet information with validation loop
            boolean hasPet = false;
            String petType = "None";
            double petCharge = 0.0;
            while (true) {
                System.out.print("Is there a pet accompanying the customer? (yes/no): ");
                String petResponse = reader.readLine().trim().toLowerCase();
                if (petResponse.equals("yes")) {
                    hasPet = true;
                    while (true) {
                        System.out.print("Enter pet type (cat, dog, bird): ");
                        petType = reader.readLine().trim().toLowerCase();
                        if (petType.equals("cat") || petType.equals("dog") || petType.equals("bird")) {
                            petCharge = 15.0;
                            break;
                        } else {
                            System.out.println("Invalid pet type. Only cat, dog, or bird are allowed. Please try again.");
                        }
                    }
                    break;
                } else if (petResponse.equals("no")) {
                    hasPet = false;
                    petType = "None";
                    petCharge = 0.0;
                    break;
                } else {
                    System.out.println("Invalid response. Please enter 'yes' or 'no'.");
                }
            }

            // Calculate discounts based on age, disability, and manual input
            double discountPercent = 0.0;
            boolean manualDiscount = false;

            int age = customer.getAge();

            System.out.print("Apply manual discount? (yes/no): ");
            String manualDiscountAnswer = reader.readLine().trim().toLowerCase();

            if (manualDiscountAnswer.equals("yes")) {
                System.out.print("Enter discount percentage (0-100): ");
                discountPercent = Double.parseDouble(reader.readLine().trim());
                if (discountPercent < 0 || discountPercent > 100) {
                    throw new FlightBookingSystemException("Invalid discount percentage.");
                }
                manualDiscount = true;
            } else {
                if (age <= 12) {
                    discountPercent += 10.0;
                } else if (age >= 60) {
                    discountPercent += 15.0;
                }

                if (customer.isDisabled()) {
                    discountPercent += 5.0;
                }
            }

            // Calculate final price including pet charge
            double basePrice = selectedFlight.getPriceForClass(seatClass);
            double discountedPrice = basePrice * (1 - discountPercent / 100);
            double finalPrice = discountedPrice + petCharge;

            // Create and complete booking
            Booking booking = new Booking(customer, selectedFlight, system.getSystemDate(),
                    seatClass, finalPrice, seatNumber, discountPercent, manualDiscount, petType, petCharge);

            booking.completeBooking();

            // Update flight and customer with new booking and reserved seat
            selectedFlight.reserveSeat(seatClass, seatNumber);
            selectedFlight.addBooking(booking);
            customer.addBooking(booking);

            // Persist booking and customer data
            bookingDataManager.storeData(system);

            System.out.println("\n Booking successful!");
            System.out.println(booking.getBookingDetails());

        } catch (Exception e) {
            throw new FlightBookingSystemException("Booking failed: " + e.getMessage());
        }
    }
}
