package Testing;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlightBookingSystemTest {

    private FlightBookingSystem system;
    private Customer customer;
    private Flight flight;
    private Booking booking;

    @BeforeEach
    public void setup() throws FlightBookingSystemException {
        system = new FlightBookingSystem();

        customer = new Customer(1, "Test User", "0000000000", 30, "Test Address", "Test Country",
                "P1234567", LocalDate.of(2030, 1, 1), false, "test@user.com",
                LocalDate.of(1995, 5, 20), "Other");

        flight = new Flight(
                101,
                "XY123",
                "TestAirline",         // airlineName
                "CityA",
                "CityB",
                LocalDate.now().plusDays(10),  // departureDate
                LocalTime.of(10, 30),          // departureTime
                LocalTime.of(12, 30),          // arrivalTime
                LocalDate.now().plusDays(10),  // arrivalDate
                false                          // isInternational
        );

        system.addCustomerDirect(customer);
        system.addFlight(flight);

        booking = new Booking(customer, flight, LocalDate.now(),
                Booking.SeatClass.ECONOMY, 300.00, "10A", 0.0,
                false, "None", 0.0);

        booking.setBookingId(1);
        customer.addBooking(booking);
        flight.addBooking(booking);

        // Properly add booking to system's internal list
        system.addBookingToSystem(booking);
    }

    @Test
    public void testAddAndGetFlight() throws FlightBookingSystemException {
        Flight f = system.getFlightById(101);
        assertNotNull(f);
        assertEquals("XY123", f.getFlightNumber());
    }

    @Test
    public void testAddAndGetCustomer() throws FlightBookingSystemException {
        Customer c = system.getCustomerById(1);
        assertNotNull(c);
        assertEquals("Test User", c.getName());
    }

    @Test
    public void testFindBookingById() throws FlightBookingSystemException {
        Booking b = system.findBookingById(1);
        assertNotNull(b);
        assertEquals(Booking.SeatClass.ECONOMY, b.getSeatClass());
        assertEquals("10A", b.getSeatNumber());
    }

    @Test
    public void testGetAllCustomers() {
        List<Customer> customers = system.getAllCustomers();
        assertTrue(customers.contains(customer));
    }

    @Test
    public void testGetAllFlights() {
        List<Flight> flights = system.getAllFlights();
        assertTrue(flights.contains(flight));
    }

    @Test
    public void testGetAllBookings() {
        List<Booking> bookings = system.getAllBookings();
        assertTrue(bookings.contains(booking));
    }

    @Test
    public void testCancelBooking() throws FlightBookingSystemException {
        system.cancelBooking(1);
        assertEquals(Booking.Status.CANCELLED, booking.getStatus());
    }

    @Test
    public void testBookingDiscounts() throws FlightBookingSystemException {
        // Add a senior disabled customer
        Customer senior = new Customer(2, "Senior", "1111111111", 65, "Address", "Country",
                "X999999", LocalDate.of(2032, 5, 1), true, "senior@test.com",
                LocalDate.of(1960, 1, 1), "Male");
        system.addCustomerDirect(senior);

        Booking discountedBooking = system.bookFlight(
                senior.getId(), flight.getId(), LocalDate.now(),
                Booking.SeatClass.BUSINESS, "Cat", 30.0
        );

        assertTrue(discountedBooking.getDiscountPercent() >= 15.0); // age + disability
        assertEquals("Cat", discountedBooking.getPetType());
    }
}
