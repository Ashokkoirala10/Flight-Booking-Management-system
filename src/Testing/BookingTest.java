package Testing;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Booking.SeatClass;
import bcu.cmp5332.bookingsystem.model.Booking.Status;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit test class for the {@link Booking} class.
 * <p>
 * This test class verifies the correct creation of a Booking object and ensures
 * all related getter methods return expected values. It also validates the formatting
 * and presence of key information in the booking details string.
 * </p>
 * 
 * <p>
 * The tests are based on mock {@link Customer} and {@link Flight} objects created
 * in the {@code setup()} method.
 * </p>
 * 
 * @author Ashok
 */
public class BookingTest {

    private Customer customer;
    private Flight flight;
    /**
     * Initializes test data before each test method is run.
     * <p>
     * Creates a sample customer and flight for use in booking creation.
     * </p>
     */
    @BeforeEach
    public void setup() {
        customer = new Customer(
                1,
                "Alice",
                "0987654321",
                25,
                "456 Main Street",
                "USA",
                "X12345678",
                LocalDate.of(2028, 3, 15),
                false,
                "alice@example.com",
                LocalDate.of(2000, 1, 1),
                "Female"
        );

        flight = new Flight(
                101,
                "DL789",
                "Delta Airlines",
                "Atlanta",
                "Paris",
                LocalDate.of(2025, 6, 10),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                LocalDate.of(2025, 6, 11),
                true
        );
    }
    /**
     * Tests the creation of a {@link Booking} object and verifies that all
     * properties and details are correctly stored and returned.
     * <p>
     * Assertions are made on:
     * <ul>
     *     <li>Customer and flight linkage</li>
     *     <li>Seat class, seat number, and price</li>
     *     <li>Status and pet details</li>
     *     <li>Correct content in booking details string</li>
     * </ul>
     * </p>
     */
    @Test
    public void testBookingCreationAndDetails() {
        Booking booking = new Booking(
                customer,
                flight,
                LocalDate.of(2025, 5, 25),
                SeatClass.BUSINESS,
                1200.50,
                "12A",
                10.0,
                false,
                "Dog",
                50.00
        );

        assertEquals(customer, booking.getCustomer());
        assertEquals(flight, booking.getFlight());
        assertEquals(SeatClass.BUSINESS, booking.getSeatClass());
        assertEquals("12A", booking.getSeatNumber());
        assertEquals(1200.50, booking.getPrice(), 0.001);   
        assertEquals(Status.ACTIVE, booking.getStatus());
        assertEquals("Dog", booking.getPetType());
        assertEquals(50.00, booking.getPetCharge(), 0.001);
        assertFalse(booking.isManualDiscount());

        String details = booking.getBookingDetails();
        assertNotNull(details);
        assertTrue(details.contains("Alice"));
        assertTrue(details.contains("DL789"));
        assertTrue(details.contains("Delta Airlines"));
        assertTrue(details.contains("BUSINESS")); 
        assertTrue(details.contains("Dog"));
        assertTrue(details.contains("Booking ID"));
        assertTrue(details.contains("Seat Number: 12A"));
        assertTrue(details.contains("Discount: 10.0%"));
    }
}