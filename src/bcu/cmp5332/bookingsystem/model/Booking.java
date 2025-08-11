package bcu.cmp5332.bookingsystem.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

/**
 * Represents a booking in the flight booking system.
 * <p>
 * A Booking contains information about the customer, flight, booking date,
 * seat class, price, seat number, discounts, pet information, and status.
 * It provides methods to manage the booking lifecycle including canceling
 * and completing a booking, as well as formatting booking details.
 * </p>
 * 
 * @author ashok
 */
public class Booking {

    /**
     * Status of a booking.
     */
    public enum Status { 
        ACTIVE, 
        CANCELLED, 
        COMPLETED 
    }

    /**
     * Seat class options.
     */
    public enum SeatClass { 
        ECONOMY, 
        BUSINESS, 
        FIRST 
    }

    private static int nextId = 1;
    private int bookingId;
    private Customer customer;
    private Flight flight;
    private LocalDate bookingDate;
    private Status status;
    private SeatClass seatClass;
    private double price;
    private String seatNumber;
    private double discountPercent;
    private final boolean manualDiscount;
    private String petType;
    private double petCharge;

    /**
     * Constructs a new Booking with the given parameters.
     * Automatically assigns a unique booking ID.
     *
     * @param customer the customer making the booking
     * @param flight the flight being booked
     * @param bookingDate the date when the booking was made
     * @param seatClass the seat class of the booking
     * @param price the price of the booking
     * @param seatNumber the seat number assigned
     * @param discountPercent discount percentage applied
     * @param manualDiscount true if discount is manually applied, false otherwise
     * @param petType type of pet included in the booking (if any)
     * @param petCharge additional charge for the pet
     */
    public Booking(Customer customer, Flight flight, LocalDate bookingDate,
                   SeatClass seatClass, double price, String seatNumber,
                   double discountPercent, boolean manualDiscount, String petType, double petCharge) {
        this.bookingId = nextId++;
        this.customer = customer;
        this.flight = flight;
        this.bookingDate = bookingDate;
        this.seatClass = seatClass;
        this.price = price;
        this.seatNumber = seatNumber;
        this.discountPercent = discountPercent;
        this.status = Status.ACTIVE;
        this.manualDiscount = manualDiscount;
        this.petType = petType;
        this.petCharge = petCharge;
    }

    /** Returns true if discount was manually applied, false otherwise. */
    public boolean isManualDiscount() {
        return manualDiscount;
    }

    /** Returns the type of pet included in the booking, or null if none. */
    public String getPetType() { 
        return petType; 
    }

    /** Returns the additional charge for the pet. */
    public double getPetCharge() { 
        return petCharge; 
    }

    /** Sets the booking ID (useful for loading existing bookings). */
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    /** Sets the pet type. */
    public void setPetType(String petType) { 
        this.petType = petType; 
    }

    /** Sets the pet charge. */
    public void setPetCharge(double petCharge) { 
        this.petCharge = petCharge; 
    }

    /** Sets the next booking ID to be assigned. */
    public static void setNextId(int nextId) {
        Booking.nextId = nextId;
    }

    /** Sets the booking status. */
    public void setStatus(Status status) {
        this.status = status;
    }

    /** Sets the customer for this booking. */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /** Sets the flight for this booking. */
    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    /** Sets the booking date. */
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    /** Sets the seat class for the booking. */
    public void setSeatClass(SeatClass seatClass) {
        this.seatClass = seatClass;
    }

    /** Sets the price of the booking. */
    public void setPrice(double price) {
        this.price = price;
    }

    /** Sets the seat number. */
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    /** Sets the discount percentage. */
    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    /** Returns the booking ID. */
    public int getBookingId() {
        return bookingId;
    }

    /** Returns the next booking ID to be assigned. */
    public static int getNextId() {
        return nextId;
    }

    /** Returns the customer associated with this booking. */
    public Customer getCustomer() {
        return customer;
    }

    /** Returns the flight associated with this booking. */
    public Flight getFlight() {
        return flight;
    }

    /** Returns the date the booking was made. */
    public LocalDate getBookingDate() {
        return bookingDate;
    }

    /** Returns the status of the booking. */
    public Status getStatus() {
        return status;
    }

    /** Returns the seat class of the booking. */
    public SeatClass getSeatClass() {
        return seatClass;
    }

    /** Returns the price of the booking. */
    public double getPrice() {
        return price;
    }

    /** Returns the seat number assigned to the booking. */
    public String getSeatNumber() {
        return seatNumber;
    }

    /** Returns the discount percentage applied to the booking. */
    public double getDiscountPercent() {
        return discountPercent;
    }

    /**
     * Marks this booking as completed.
     */
    public void completeBooking() {
        this.status = Status.COMPLETED;
    }

    /**
     * Cancels this booking and removes the passenger from the flight.
     * 
     * @throws FlightBookingSystemException if an error occurs when removing the passenger
     */
    public void cancel() throws FlightBookingSystemException {
        this.status = Status.CANCELLED;
        flight.removePassenger(customer, seatClass);  // existing passenger removal
        flight.releaseSeat(seatClass, seatNumber);    // new: release the seat number explicitly
    }


    /**
     * Returns a detailed string representation of the booking including customer,
     * flight, dates, times, seat details, price, status, discounts, and pet info.
     * 
     * @return formatted booking details string
     */
    public String getBookingDetails() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.ENGLISH);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        String formattedBookingDate = bookingDate.format(dateFormatter);
        String formattedDepartureDate = flight.getDepartureDate().format(dateFormatter);
        String formattedArrivalDate = flight.getArrivalDate() != null ? flight.getArrivalDate().format(dateFormatter) : "N/A";
        String formattedDepartureTime = flight.getDepartureTime().format(timeFormatter);
        String formattedArrivalTime = flight.getArrivalTime().format(timeFormatter);

        StringBuilder sb = new StringBuilder();

        sb.append("Booking ID: ").append(bookingId).append("\n");
        sb.append("Booking Date: ").append(formattedBookingDate).append("\n");

        sb.append("Customer Name: ").append(customer.getName()).append("\n");
        sb.append("Flight Number: ").append(flight.getFlightNumber()).append("\n");
        sb.append("Airline: ").append(flight.getAirlineName()).append("\n");
        sb.append("Route: ").append(flight.getOrigin()).append(" → ").append(flight.getDestination()).append("\n");

        sb.append("Flight Date: ").append(formattedDepartureDate).append("\n");
        sb.append("Departure Time: ").append(formattedDepartureTime).append("\n");
        sb.append("Arrival Date: ").append(formattedArrivalDate).append("\n");
        sb.append("Arrival Time: ").append(formattedArrivalTime).append("\n");

        sb.append("Seat Class: ").append(seatClass).append("\n");
        sb.append("Seat Number: ").append(seatNumber).append("\n");

        sb.append("Price: $").append(String.format("%.2f", price)).append("\n");

        sb.append("Booking Status: ").append(status.name()).append("\n");

        if (discountPercent > 0) {
            sb.append("Discount: ").append(discountPercent).append("% ");
            sb.append("(");
            if (manualDiscount) {
                sb.append("manual");
            } else {
                boolean ageDiscount = (customer.getAge() <= 12 || customer.getAge() >= 60);
                boolean disabilityDiscount = customer.isDisabled();

                if (ageDiscount) {
                    sb.append("age-based (").append(customer.getAge()).append(" years)");
                }
                if (disabilityDiscount) {
                    if (ageDiscount) sb.append(" and ");
                    sb.append("disability");
                }
            }
            sb.append(")\n");
        } else {
            sb.append("Discount: None\n");
        }

        if (petType != null && !petType.equalsIgnoreCase("None")) {
            sb.append("Pet Included: ").append(petType).append(" ($").append(String.format("%.2f", petCharge)).append(" extra)\n");
        } else {
            sb.append("Pet Included: None\n");
        }

        return sb.toString();
    }



}
