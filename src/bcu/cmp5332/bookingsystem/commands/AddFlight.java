package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Booking.SeatClass;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Command to add a new flight to the flight booking system.
 * <p>
 * This command creates a flight with all relevant details such as flight number,
 * airline name, route, schedule, seating capacities, prices, and international status.
 * </p>
 */
public class AddFlight implements Command {

    /**
     * The flight number identifier (e.g., "BA256").
     */
    private final String flightNumber;

    /**
     * The name of the airline operating the flight.
     */
    private final String airlineName;

    /**
     * The origin airport/location of the flight.
     */
    private final String origin;

    /**
     * The destination airport/location of the flight.
     */
    private final String destination;

    /**
     * The scheduled departure date of the flight.
     */
    private final LocalDate departureDate;

    /**
     * The scheduled departure time of the flight.
     */
    private final LocalTime departureTime;

    /**
     * The scheduled arrival time of the flight.
     */
    private final LocalTime arrivalTime;

    /**
     * The scheduled arrival date of the flight.
     */
    private final LocalDate arrivalDate;

    /**
     * The seating capacity in economy class.
     */
    private final int economyCapacity;

    /**
     * The seating capacity in business class.
     */
    private final int businessCapacity;

    /**
     * The seating capacity in first class.
     */
    private final int firstClassCapacity;

    /**
     * The ticket price for economy class.
     */
    private final double economyPrice;

    /**
     * The ticket price for business class.
     */
    private final double businessPrice;

    /**
     * The ticket price for first class.
     */
    private final double firstClassPrice;

    /**
     * Indicates whether the flight is international (true) or domestic (false).
     */
    private final boolean isInternational;

    /**
     * Constructs an AddFlight command with all necessary flight details.
     *
     * @param flightNumber the flight number (e.g., "BA256")
     * @param airlineName the airline operating the flight
     * @param origin the flight origin location/airport
     * @param destination the flight destination location/airport
     * @param departureDate the scheduled departure date
     * @param economyCapacity the economy class seating capacity
     * @param businessCapacity the business class seating capacity
     * @param firstClassCapacity the first class seating capacity
     * @param economyPrice the ticket price for economy class
     * @param businessPrice the ticket price for business class
     * @param firstClassPrice the ticket price for first class
     * @param departureTime the scheduled departure time
     * @param arrivalTime the scheduled arrival time
     * @param arrivalDate the scheduled arrival date
     * @param isInternational true if flight is international, false otherwise
     */
    public AddFlight(String flightNumber, String airlineName, String origin, String destination, LocalDate departureDate,
                     int economyCapacity, int businessCapacity, int firstClassCapacity,
                     double economyPrice, double businessPrice, double firstClassPrice,
                     LocalTime departureTime, LocalTime arrivalTime, LocalDate arrivalDate,
                     boolean isInternational) {
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.arrivalDate = arrivalDate;
        this.economyCapacity = economyCapacity;
        this.businessCapacity = businessCapacity;
        this.firstClassCapacity = firstClassCapacity;
        this.economyPrice = economyPrice;
        this.businessPrice = businessPrice;
        this.firstClassPrice = firstClassPrice;
        this.isInternational = isInternational;
    }

    /**
     * Executes the command to add the flight to the flight booking system.
     * <p>
     * This method creates a new flight with the provided details, sets seating capacities and prices,
     * and prints a confirmation message with the flight's internal ID and airline name.
     * </p>
     *
     * @param flightBookingSystem the flight booking system instance
     * @throws FlightBookingSystemException if adding the flight fails due to invalid data or system errors
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Flight flight = flightBookingSystem.addFlight(
                flightNumber, airlineName, origin, destination, departureDate, departureTime, arrivalTime, arrivalDate, isInternational
        );

        flight.setCapacityForClass(SeatClass.ECONOMY, economyCapacity);
        flight.setCapacityForClass(SeatClass.BUSINESS, businessCapacity);
        flight.setCapacityForClass(SeatClass.FIRST, firstClassCapacity);

        flight.setPriceForClass(SeatClass.ECONOMY, economyPrice);
        flight.setPriceForClass(SeatClass.BUSINESS, businessPrice);
        flight.setPriceForClass(SeatClass.FIRST, firstClassPrice);

        System.out.println("Flight #" + flight.getId() + " (" + airlineName + ") added.");
    }
}

