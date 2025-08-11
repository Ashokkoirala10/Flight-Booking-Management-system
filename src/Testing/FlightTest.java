package Testing;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Flight;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for the {@link Flight} class.
 * <p>
 * This class verifies correct creation of Flight objects, proper
 * handling of attributes including nullable dates, seat availability,
 * pricing behavior, and summary details formatting.
 * </p>
 * <p>
 * The tests ensure Flight behaves as expected in a variety of
 * conditions relevant to the booking system.
 * </p>
 * 
 * @author Ashok
 */
public class FlightTest {
    /**
     * Tests creating a {@link Flight} object with valid parameters
     * and verifies that all getter methods return expected values.
     */
    @Test
    public void testFlightCreationAndGetters() {
        LocalDate depDate = LocalDate.of(2025, 10, 15);
        LocalTime depTime = LocalTime.of(10, 30);
        LocalTime arrTime = LocalTime.of(14, 45);
        LocalDate arrDate = LocalDate.of(2025, 10, 15);

        Flight flight = new Flight(
            101,
            "BA123",
            "British Airways",
            "London",
            "New York",
            depDate,
            depTime,
            arrTime,
            arrDate,
            true
        );

        assertEquals(101, flight.getId());
        assertEquals("BA123", flight.getFlightNumber());
        assertEquals("British Airways", flight.getAirlineName());
        assertEquals("London", flight.getOrigin());
        assertEquals("New York", flight.getDestination());
        assertEquals(depDate, flight.getDepartureDate());
        assertEquals(depTime, flight.getDepartureTime());
        assertEquals(arrTime, flight.getArrivalTime());
        assertEquals(arrDate, flight.getArrivalDate());
        assertTrue(flight.getInternational());
    }
    /**
     * Tests creation of a {@link Flight} object with null departure
     * and arrival dates and ensures the getters return null accordingly.
     */
    @Test
    public void testFlightWithNullDates() {
        Flight flight = new Flight(
            102,
            "AA456",
            "American Airlines",
            "Los Angeles",
            "Tokyo",
            null,
            LocalTime.of(8, 0),
            LocalTime.of(20, 0),
            null,
            true
        );

        assertNull(flight.getDepartureDate());
        assertNull(flight.getArrivalDate());
    }
    /**
     * Tests the initial seat availability for all seat classes
     * and the total available seats on the flight.
     */
    @Test
    public void testInitialSeatAvailability() {
        Flight flight = createSampleFlight();

        assertEquals(60, flight.getAvailableSeatsForClass(Booking.SeatClass.ECONOMY));
        assertEquals(25, flight.getAvailableSeatsForClass(Booking.SeatClass.BUSINESS));
        assertEquals(15, flight.getAvailableSeatsForClass(Booking.SeatClass.FIRST));
        assertEquals(100, flight.getAvailableSeats());
    }
    /**
     * Tests the base and dynamic pricing for a flight seat class.
     * Ensures the dynamic price is never less than the base price.
     */
    @Test
    public void testBaseAndDynamicPrice() {
        Flight flight = createSampleFlight();

        double baseEconomy = flight.getDynamicPrice(Booking.SeatClass.ECONOMY);
        double dynamicEconomy = flight.getDynamicPrice(Booking.SeatClass.ECONOMY);

        assertTrue(dynamicEconomy >= baseEconomy); // Dynamic price never less than base
        assertEquals(100.0, baseEconomy, 0.01);    // Default economy price
    }
    /**
     * Tests that the short details string returned by
     * {@link Flight#getDetailsShort()} is not null and contains
     * the flight number.
     */
    @Test
    public void testGetDetailsShortNotNull() {
        Flight flight = createSampleFlight();
        String details = flight.getDetailsShort();

        assertNotNull(details);
        assertTrue(details.contains("BA123"));
    }
    /**
     * Utility method to create a sample {@link Flight} instance
     * with predefined valid attributes for reuse in tests.
     *
     * @return a sample Flight instance
     */
    // Utility method to avoid repetition
    private Flight createSampleFlight() {
        return new Flight(
            200,
            "BA123",
            "British Airways",
            "London",
            "New York",
            LocalDate.now().plusDays(5),
            LocalTime.of(10, 30),
            LocalTime.of(14, 45),
            LocalDate.now().plusDays(5),
            true
        );
    }
}
