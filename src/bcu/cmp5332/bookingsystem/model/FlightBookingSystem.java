package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.data.BookingDataManager;
import bcu.cmp5332.bookingsystem.data.DataManager;
import bcu.cmp5332.bookingsystem.data.FlightDataManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Core class representing the flight booking system.
 * Manages customers, flights, and bookings.
 */
public class FlightBookingSystem {

    private final Map<Integer, Customer> customers = new HashMap<>();
    private final Map<Integer, Flight> flights = new HashMap<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final Map<String, Customer> customersByPassport = new HashMap<>();

    private int nextCustomerId = 1;
    private int nextFlightId = 1;
    private int nextBookingId = 1;

    /**
     * Adds a new customer with an auto-generated ID.
     *
     * @param name  the customer's name
     * @param phone the customer's phone number
     * @return the newly created Customer object
     */
    public Customer addCustomer(String name, String phone) {
        Customer customer = new Customer(nextCustomerId++, name, phone);
        customers.put(customer.getId(), customer);
        return customer;
    }

    /**
     * Adds a new customer with a specified ID (used for loading existing data).
     *
     * @param id    the customer ID
     * @param name  the customer's name
     * @param phone the customer's phone number
     * @return the newly created Customer object
     */
    public Customer addCustomer(int id, String name, String phone) {
        Customer customer = new Customer(id, name, phone);
        customers.put(customer.getId(), customer);
        if (id >= nextCustomerId) {
            nextCustomerId = id + 1; // Update next available customer ID
        }
        return customer;
    }

    /**
     * Adds a new customer with extended details including passport and personal info.
     *
     * @param name                the customer's name
     * @param phone               the customer's phone number
     * @param age                 the customer's age
     * @param address             the customer's address
     * @param country             the customer's country
     * @param passportNumber      the customer's passport number
     * @param passportExpiryDate  the expiry date of the passport
     * @param disabled            whether the customer is disabled
     * @param email               the customer's email
     * @param dob                 the customer's date of birth
     * @param gender              the customer's gender
     * @return the newly created Customer object
     */
    public Customer addCustomer(String name, String phone, int age, String address, String country,
            String passportNumber, LocalDate passportExpiryDate,
            boolean disabled, String email, LocalDate dob, String gender) {
        Customer customer = new Customer(nextCustomerId++, name, phone, age, address, country,
                passportNumber, passportExpiryDate, disabled, email, dob, gender);
        customers.put(customer.getId(), customer);
        customersByPassport.put(passportNumber.toUpperCase(), customer);
        return customer;
    }

    /**
     * Adds a customer directly to the system, ensuring no duplicates.
     *
     * @param customer the customer to add
     * @throws FlightBookingSystemException if the customer ID or passport already exists
     */
    public void addCustomerDirect(Customer customer) throws FlightBookingSystemException {
        int id = customer.getId();

        if (customers.containsKey(id)) {
            throw new FlightBookingSystemException("Customer with ID " + id + " already exists.");
        }

        if (customersByPassport.containsKey(customer.getPassportNumber().toUpperCase())) {
            throw new FlightBookingSystemException("Customer with passport " + customer.getPassportNumber() + " already exists.");
        }

        customers.put(id, customer);
        customersByPassport.put(customer.getPassportNumber().toUpperCase(), customer);

        // Ensure nextCustomerId is always greater than the highest used ID
        if (id >= nextCustomerId) {
            nextCustomerId = id + 1;
        }
    }

    /**
     * Adds a new flight with auto-generated ID.
     *
     * @param flightNumber   flight number
     * @param airlineName    airline name
     * @param origin         origin airport/location
     * @param destination    destination airport/location
     * @param departureDate  date of departure
     * @param departureTime  time of departure
     * @param arrivalTime    time of arrival
     * @param arrivalDate    date of arrival
     * @param isInternational whether flight is international
     * @return the newly created Flight object
     */
    public Flight addFlight(String flightNumber, String airlineName, String origin, String destination,
            LocalDate departureDate, LocalTime departureTime,
            LocalTime arrivalTime, LocalDate arrivalDate,
            boolean isInternational) {
        Flight flight = new Flight(nextFlightId++, flightNumber, airlineName, origin, destination,
                departureDate, departureTime, arrivalTime, arrivalDate,
                isInternational);
        flights.put(flight.getId(), flight);
        return flight;
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param id the customer ID
     * @return the Customer object
     * @throws FlightBookingSystemException if no customer with the given ID exists
     */
    public Customer getCustomerById(int id) throws FlightBookingSystemException {
        Customer customer = customers.get(id);
        if (customer == null) {
            throw new FlightBookingSystemException("Customer with ID " + id + " not found.");
        }
        return customer;
    }

    /**
     * Retrieves a flight by its ID.
     *
     * @param id the flight ID
     * @return the Flight object
     * @throws FlightBookingSystemException if no flight with the given ID exists
     */
    public Flight getFlightById(int id) throws FlightBookingSystemException {
        Flight flight = flights.get(id);
        if (flight == null) {
            throw new FlightBookingSystemException("Flight with ID " + id + " not found.");
        }
        return flight;
    }

    /**
     * Books a flight for a customer.
     *
     * @param customerId  the customer's ID
     * @param flightId    the flight's ID
     * @param bookingDate the date of booking
     * @param seatClass   the seat class
     * @param petType     type of pet (if any)
     * @param petCharge   additional charge for pet
     * @return the created Booking object
     * @throws FlightBookingSystemException if customer or flight not found or booking fails
     */
    public Booking bookFlight(int customerId, int flightId, LocalDate bookingDate,
            Booking.SeatClass seatClass, String petType, double petCharge)
            throws FlightBookingSystemException {

        Customer customer = getCustomerById(customerId);
        Flight flight = getFlightById(flightId);

        int age = customer.getAge();
        double discountPercent = 0.0;

        if (age <= 12) discountPercent += 10.0;
        else if (age >= 60) discountPercent += 15.0;

        if (customer.isDisabled()) discountPercent += 5.0;

        double basePrice = flight.getPriceForClass(seatClass);
        double discountedPrice = basePrice * (1 - discountPercent / 100);
        boolean manualDiscount = false;

        Booking booking = new Booking(
                customer,
                flight,
                bookingDate,
                seatClass,
                discountedPrice + petCharge,  // Add pet charge to price
                null,                         // seatNumber (or pass actual seat if you have it)
                discountPercent,
                manualDiscount,
                petType,
                petCharge
        );

        booking.setBookingId(nextBookingId++);

        customer.addBooking(booking);
        flight.addPassenger(customer, seatClass);
        flight.addBooking(booking);

        return booking;
    }

    /**
     * Cancels a booking by its ID.
     *
     * @param bookingId the booking ID
     * @throws FlightBookingSystemException if booking not found or not active
     */
    public void cancelBooking(int bookingId) throws FlightBookingSystemException {
        Booking booking = findBookingById(bookingId);
        if (booking.getStatus() != Booking.Status.ACTIVE) {
            throw new FlightBookingSystemException("Only active bookings can be cancelled.");
        }
        booking.getCustomer().cancelBooking(booking);
    }

    /**
     * Finds a booking by its ID.
     *
     * @param bookingId the booking ID
     * @return the Booking object
     * @throws FlightBookingSystemException if no booking with given ID exists
     */
    public Booking findBookingById(int bookingId) throws FlightBookingSystemException {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        throw new FlightBookingSystemException("Booking with ID " + bookingId + " not found.");
    }

    /**
     * Returns a list of all customers in the system.
     *
     * @return list of customers
     */
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    /**
     * Returns a list of all flights in the system.
     *
     * @return list of flights
     */
    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights.values());
    }

    /**
     * Returns a list of all bookings in the system.
     *
     * @return list of bookings
     */
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    /**
     * Returns all bookings for a specific customer.
     *
     * @param customerId the customer's ID
     * @return list of bookings for the customer
     * @throws FlightBookingSystemException if customer not found
     */
    public List<Booking> getBookingsForCustomer(int customerId) throws FlightBookingSystemException {
        Customer customer = getCustomerById(customerId);
        return customer.getBookings();
    }

    /**
     * Displays a summary of the current system state.
     */
    public void displaySystemSummary() {
        System.out.println("System contains:");
        System.out.println(customers.size() + " customers");
        System.out.println(flights.size() + " flights");
        System.out.println(bookings.size() + " bookings");
    }

    /**
     * Returns the current system date.
     *
     * @return the current date
     */
    public LocalDate getSystemDate() {
        return LocalDate.now();
    }

    /**
     * Adds a flight directly to the system (e.g., for loading existing flights).
     *
     * @param flight the flight to add
     * @return the added flight
     */
    public Flight addFlight(Flight flight) {
        flights.put(flight.getId(), flight);
        if (flight.getId() >= nextFlightId) {
            nextFlightId = flight.getId() + 1;
        }
        return flight;
    }

    private DataManager dataManager;

    /**
     * Default constructor initializing the data manager.
     */
    public FlightBookingSystem() {
        // Initialize your data manager (e.g., BookingDataManager)
        this.dataManager = new BookingDataManager();  // Use the appropriate DataManager implementation
    }

    /**
     * Returns the current data manager used by the system.
     *
     * @return the DataManager instance
     */
    public DataManager getDataManager() {
        return this.dataManager;
    }

    /**
     * Retrieves a customer by their passport number.
     *
     * @param passportNumber the passport number
     * @return the Customer object, or null if not found
     */
    public Customer getCustomerByPassportNumber(String passportNumber) {
        return customersByPassport.get(passportNumber.toUpperCase()); // assuming passport numbers are case-insensitive
    }

    /**
     * Returns the next available customer ID.
     *
     * @return next customer ID
     */
    public int getNextCustomerId() {
        return nextCustomerId;
    }

    /**
     * Returns the next available flight ID.
     *
     * @return next flight ID
     */
    public int getNextFlightId() {
        return nextFlightId;
    }

    /**
     * Removes a customer from the internal customer map.
     *
     * @param customer the customer to remove
     */
    public void removeCustomer(Customer customer) {
        customers.remove(customer.getId());
    }

    /**
     * Removes a booking from the internal bookings list.
     *
     * @param booking the booking to remove
     */
    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }

    /**
     * Cancels all bookings for a given customer ID.
     *
     * @param customerId the customer ID
     */
    public void cancelAllBookingsForCustomer(int customerId) {
        bookings.removeIf(booking -> booking.getCustomer().getId() == customerId);
    }

    /**
     * Adds a booking to the system's internal booking list.
     *
     * @param booking the booking to add
     */
    public void addBookingToSystem(Booking booking) {
        bookings.add(booking);
    }

    public void removeFlight(Flight flight) {
        if (flight == null) {
            return;
        }
        
        // Remove flight from flights map
        flights.remove(flight.getId());
        
        // Remove all bookings associated with this flight from the system bookings list
        bookings.removeIf(booking -> booking.getFlight().equals(flight));
        
        // Remove flight from each customer's booking list and the flight's passenger list
        // We'll iterate over all customers and remove bookings related to this flight
        for (Customer customer : customers.values()) {
            // Remove bookings of this flight from customer's bookings
            customer.getBookings().removeIf(booking -> booking.getFlight().equals(flight));
        }
        
    }


}
