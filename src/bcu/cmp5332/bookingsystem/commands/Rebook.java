package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Rebook implements Command {

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Get Customer ID
            System.out.print("Enter Customer ID: ");
            int customerId = Integer.parseInt(reader.readLine());

            Customer customer = flightBookingSystem.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }

            // Read cancelled bookings from file
            List<String> allLines = new ArrayList<>();
            List<String> matchingCancelledLines = new ArrayList<>();
            File cancelledFile = new File("resources/data/cancelbooking.txt");
            if (!cancelledFile.exists()) {
                System.out.println("No cancelled bookings file found.");
                return;
            }

            BufferedReader fileReader = new BufferedReader(new FileReader(cancelledFile));
            String line;
            while ((line = fileReader.readLine()) != null) {
                allLines.add(line);
                if (line.contains("Customer ID: " + customerId)) {
                    matchingCancelledLines.add(line);
                }
            }
            fileReader.close();

            if (matchingCancelledLines.isEmpty()) {
                System.out.println("No cancelled bookings found for this customer.");
                return;
            }

            // Show matching lines
            System.out.println("\nCancelled bookings for customer " + customer.getName() + ":");
            int index = 1;
            for (String entry : matchingCancelledLines) {
                System.out.println(index + ". " + entry);
                index++;
            }

            // Ask user to choose one
            System.out.print("\nEnter number of cancelled booking to rebook: ");
            int choice = Integer.parseInt(reader.readLine());
            if (choice < 1 || choice > matchingCancelledLines.size()) {
                System.out.println("Invalid choice.");
                return;
            }

            String chosenLine = matchingCancelledLines.get(choice - 1);
            String[] parts = chosenLine.split(", ");
            int flightId = Integer.parseInt(parts[1].split(": ")[1]);
            Booking.SeatClass seatClass = Booking.SeatClass.valueOf(parts[2].split(": ")[1].toUpperCase());
            String seatNumber = parts[3].split(": ")[1];
            double oldPrice = Double.parseDouble(parts[4].split("\\$")[1]);
            int discountPercent = (int) Double.parseDouble(parts[5].split(": ")[1]);
            boolean manualDiscount = Boolean.parseBoolean(parts[6].split(": ")[1]);
            String petType = parts[7].split(": ")[1];
            double petCharge = Double.parseDouble(parts[8].split(": ")[1]);


            Flight flight = flightBookingSystem.getFlightById(flightId);
            if (flight == null) {
                System.out.println("Flight not found.");
                return;
            }

            // Check flight date
            LocalDate nowDate = flightBookingSystem.getSystemDate();
            LocalTime nowTime = LocalTime.now();
            if (flight.getDepartureDate().isBefore(nowDate) ||
                (flight.getDepartureDate().isEqual(nowDate) && flight.getDepartureTime().isBefore(nowTime))) {
                throw new FlightBookingSystemException("Cannot rebook a flight that has already departed.");
            }


            if (flight.getAvailableSeatsForClass(seatClass) <= 0) {
                throw new FlightBookingSystemException("No available seats in " + seatClass + " class.");
            }
            if (!flight.isSeatAvailable(seatClass, seatNumber)) {
                throw new FlightBookingSystemException("Seat " + seatNumber + " is already booked.");
            }

            double newPrice = oldPrice + 10.0;
            Booking newBooking = new Booking(
            	    customer,
            	    flight,
            	    flightBookingSystem.getSystemDate(),
            	    seatClass,
            	    newPrice,
            	    seatNumber,
            	    discountPercent,
            	    manualDiscount,
            	    petType,
            	    petCharge
            	);


            flight.reserveSeat(seatClass, seatNumber);
            flight.addBooking(newBooking);
            customer.addBooking(newBooking);

            // Update booking file
            updateBookingFile(flightBookingSystem, newBooking);

            // Remove rebooked line from cancelled_bookings.txt
            allLines.remove(chosenLine);
            BufferedWriter writer = new BufferedWriter(new FileWriter(cancelledFile));
            for (String ln : allLines) {
                writer.write(ln);
                writer.newLine();
            }
            writer.close();

            System.out.println("\nRebooking fee applied: $10.00");
            System.out.printf("Final Price: $%.2f\n", newPrice);
            System.out.println("Rebooking successful:");
            System.out.println(newBooking.getBookingDetails());

        } catch (IOException e) {
            throw new FlightBookingSystemException("Error reading input or file.");
        }
    }

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
