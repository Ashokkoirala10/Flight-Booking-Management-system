package Testing;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Booking.SeatClass;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit test class for the {@link Customer} class.
 * <p>
 * This class tests core functionalities of the Customer object including
 * attribute access, booking management (add, remove, duplicate detection),
 * and detail formatting methods.
 * </p>
 * 
 * <p>
 * Each test ensures that the Customer behaves correctly when interacting with
 * bookings and flights under different scenarios.
 * </p>
 * 
 * @author Ashok
 */
public class CustomerTest {

    private Customer customer;
    private Flight flight;
    /**
     * Sets up a test {@link Customer} and {@link Flight} instance
     * before each test is executed.
     */
    @BeforeEach
    public void setup() {
        customer = new Customer(
                1,
                "John Doe",
                "1234567890",
                30,
                "123 Main St",
                "UK",
                "P12345678",
                LocalDate.of(2030, 5, 10),
                false,
                "john@example.com",
                LocalDate.of(1995, 5, 25),
                "Male"
        );

        flight = new Flight(
                101,
                "BA256",
                "British Airways",
                "London",
                "New York",
                LocalDate.of(2025, 7, 15),
                LocalTime.of(10, 30),
                LocalTime.of(14, 45),
                LocalDate.of(2025, 7, 15),
                true
        );
    }
    /**
     * Tests that all getter methods for {@link Customer} fields return
     * the correct values as initialized.
     */
    @Test
    public void testCustomerDetails() {
        assertEquals(1, customer.getId());
        assertEquals("John Doe", customer.getName());
        assertEquals("1234567890", customer.getPhone());
        assertEquals(30, customer.getAge());
        assertEquals("123 Main St", customer.getAddress());
        assertEquals("UK", customer.getCountry());
        assertEquals("P12345678", customer.getPassportNumber());
        assertEquals(LocalDate.of(2030, 5, 10), customer.getPassportExpiryDate());
        assertFalse(customer.isDisabled());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals(LocalDate.of(1995, 5, 25), customer.getDob());
        assertEquals("Male", customer.getGender());
    }
    /**
     * Tests that a booking can be successfully added to a customer.
     * Ensures the booking is stored and linked to the corresponding flight.
     *
     * @throws FlightBookingSystemException if adding the booking fails
     */
    @Test
    public void testAddBookingSuccessfully() throws FlightBookingSystemException {
        Booking booking = new Booking(
                customer,
                flight,
                LocalDate.now(),
                SeatClass.ECONOMY,
                300.0,
                "15B",
                0.0,
                false,
                null,
                0.0
        );

        customer.addBooking(booking);

        assertEquals(1, customer.getBookings().size());
        assertEquals(booking, customer.getBookings().get(0));
        assertTrue(customer.hasBooking(flight));
    }

    @Test
    public void testDuplicateBookingThrowsException() throws FlightBookingSystemException {
        Booking booking1 = new Booking(
                customer,
                flight,
                LocalDate.now(),
                SeatClass.ECONOMY,
                300.0,
                "15B",
                0.0,
                false,
                null,
                0.0
        );

        Booking booking2 = new Booking(
                customer,
                flight,
                LocalDate.now(),
                SeatClass.BUSINESS,
                500.0,
                "1A",
                0.0,
                false,
                null,
                0.0
        );

        customer.addBooking(booking1);

        FlightBookingSystemException exception = assertThrows(
                FlightBookingSystemException.class,
                () -> customer.addBooking(booking2)
        );

        assertTrue(exception.getMessage().toLowerCase().contains("already has a booking for this flight"));
    }
    /**
     * Tests that a booking can be removed from a customer and that
     * the customer no longer has that booking.
     *
     * @throws FlightBookingSystemException if booking operations fail
     */
    @Test
    public void testRemoveBooking() throws FlightBookingSystemException {
        Booking booking = new Booking(
                customer,
                flight,
                LocalDate.now(),
                SeatClass.ECONOMY,
                300.0,
                "22C",
                0.0,
                false,
                null,
                0.0
        );

        customer.addBooking(booking);
        assertEquals(1, customer.getBookings().size());

        customer.cancelBooking(booking);
        assertEquals(0, customer.getBookings().size());
        assertFalse(customer.hasBooking(flight));
    }
    /**
     * Tests the {@code hasBooking()} method to confirm whether a customer
     * has a booking for a specific flight.
     *
     * @throws FlightBookingSystemException if booking operations fail
     */
    @Test
    public void testHasBooking() throws FlightBookingSystemException {
        Booking booking = new Booking(
                customer,
                flight,
                LocalDate.now(),
                SeatClass.FIRST,
                1000.0,
                "1A",
                0.0,
                false,
                null,
                0.0
        );

        assertFalse(customer.hasBooking(flight));

        customer.addBooking(booking);

        assertTrue(customer.hasBooking(flight));
    }
    /**
     * Tests the output of {@code getDetailsShort()} and {@code getDetailsLong()}.
     * Ensures that key customer data is correctly included in both views.
     */
    @Test
    public void testDetailsShortAndLong() {
        String shortDetails = customer.getDetailsShort();
        String longDetails = customer.getDetailsLong();

        assertNotNull(shortDetails);
        assertNotNull(longDetails);

        assertTrue(shortDetails.contains("John Doe"));
        assertTrue(longDetails.contains("Passport"));
    }
}
