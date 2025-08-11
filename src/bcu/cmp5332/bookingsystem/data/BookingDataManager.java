package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Manages the loading and storing of booking data from and to a text file.
 * This class handles the persistence of {@link Booking} objects, linking them
 * to both {@link Customer} and {@link Flight} instances during loading.
 * 
 * Bookings are stored in a delimited text file, and each booking line
 * contains relevant fields such as customer ID, flight ID, seat class, price,
 * status, and optional details like seat number, discounts, and pet-related data.
 * 
 * @author Ashok
 */
public class BookingDataManager implements DataManager {

    private final String SEPARATOR = "::";
    private final String RESOURCE = "./resources/data/bookings.txt";

    /**
     * Loads booking data from the configured text file and updates the system's customers and flights.
     *
     * @param fbs the flight booking system instance to load bookings into.
     * @throws IOException if the file cannot be read.
     * @throws FlightBookingSystemException if data is malformed or a referenced customer/flight is not found.
     */
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int lineIndex = 1;
            int maxBookingId = 0;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] properties = line.split(SEPARATOR, -1);

                try {
                    int bookingId = Integer.parseInt(properties[0]);
                    int customerId = Integer.parseInt(properties[1]);
                    int flightId = Integer.parseInt(properties[2]);
                    LocalDate bookingDate = LocalDate.parse(properties[3]);
                    Booking.SeatClass seatClass = Booking.SeatClass.valueOf(properties[4].toUpperCase());
                    double price = Double.parseDouble(properties[5]);
                    String statusStr = properties[6];
                    String seatNumber = properties.length > 7 ? properties[7] : null;
                    double discountPercent = properties.length > 8 ? Double.parseDouble(properties[8]) : 0.0;
                    boolean manualDiscount = properties.length > 9 && Boolean.parseBoolean(properties[9]);
                    String petType = properties.length > 10 ? properties[10] : null;
                    double petCharge = properties.length > 11 ? Double.parseDouble(properties[11]) : 0.0;

                    Customer customer = fbs.getCustomerById(customerId);
                    Flight flight = fbs.getFlightById(flightId);

                    Booking booking = new Booking(customer, flight, bookingDate, seatClass, price, seatNumber,
                            discountPercent, manualDiscount, petType, petCharge);
                    booking.setBookingId(bookingId);

                    Booking.Status status = Booking.Status.valueOf(statusStr);
                    booking.setStatus(status);

                    customer.addBooking(booking);

                    // Only add passenger and reserve seat if booking is ACTIVE or COMPLETED
                    if (status == Booking.Status.ACTIVE || status == Booking.Status.COMPLETED) {
                        flight.addPassenger(customer, seatClass);
                        flight.reserveSeat(seatClass, seatNumber);
                    }

                    if (bookingId > maxBookingId) {
                        maxBookingId = bookingId;
                    }

                } catch (Exception ex) {
                    throw new FlightBookingSystemException("Error parsing booking data on line " + lineIndex + ": " + ex.getMessage());
                }
                lineIndex++;
            }

            Booking.setNextId(maxBookingId + 1);
        }
    }


    /**
     * Saves all bookings from the system to the configured text file.
     *
     * @param fbs the flight booking system containing all customers and their bookings.
     * @throws IOException if the file cannot be written.
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            List<Booking> allBookings = new ArrayList<>();
            for (Customer customer : fbs.getAllCustomers()) {
                allBookings.addAll(customer.getBookings());
            }

            allBookings.sort(Comparator.comparingInt(Booking::getBookingId));

            for (Booking booking : allBookings) {
                out.print(booking.getBookingId() + SEPARATOR);
                out.print(booking.getCustomer().getId() + SEPARATOR);
                out.print(booking.getFlight().getId() + SEPARATOR);
                out.print(booking.getBookingDate() + SEPARATOR);
                out.print(booking.getSeatClass().name() + SEPARATOR);
                out.print(booking.getPrice() + SEPARATOR);
                out.print(booking.getStatus().name() + SEPARATOR);
                out.print(booking.getSeatNumber() + SEPARATOR);
                out.print(booking.getDiscountPercent() + SEPARATOR);
                out.print(booking.isManualDiscount() + SEPARATOR);
                out.print((booking.getPetType() != null ? booking.getPetType() : "") + SEPARATOR);
                out.println(booking.getPetCharge());
            }
        }
    }
}
