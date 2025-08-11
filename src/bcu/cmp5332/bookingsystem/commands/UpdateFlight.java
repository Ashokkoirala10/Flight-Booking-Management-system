package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.time.LocalTime;

import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightStatus;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Booking;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The {@code UpdateFlight} class implements the {@link Command} interface and allows for updating
 * the details of an existing flight in the system.
 * <p>
 * Only non-null and valid parameters are applied, allowing partial updates to flight attributes.
 * The flight is persisted to the data store after successful update.
 * </p>
 * 
 * @author ashok 
 */
public class UpdateFlight implements Command {

    private final int flightId;
    private final String newFlightNumber;
    private final String newOrigin;
    private final String newDestination;
    private final LocalDate newDepartureDate;
    private final Integer newEconomyCapacity;
    private final Integer newBusinessCapacity;
    private final Integer newFirstCapacity;
    private final Double newEconomyPrice;
    private final Double newBusinessPrice;
    private final Double newFirstPrice;
    private final FlightStatus newStatus;
    private final String newAirlineName;
    private final Boolean isInternational;
    private final LocalDate newArrivalDate;
    private final LocalTime newArrivalTime;
    private final LocalTime newDepartureTime;


    /**
     * Constructs an {@code UpdateFlight} command with specified fields to update.
     * Fields left as {@code null} (or negative values for capacities/prices) will be ignored.
     *
     * @param flightId            the ID of the flight to update
     * @param newFlightNumber     the new flight number (optional)
     * @param newOrigin           the new origin location (optional)
     * @param newDestination      the new destination location (optional)
     * @param newDepartureDate    the new departure date (optional)
     * @param newEconomyCapacity  the new economy class seat capacity (optional)
     * @param newBusinessCapacity the new business class seat capacity (optional)
     * @param newFirstCapacity    the new first class seat capacity (optional)
     * @param newEconomyPrice     the new price for economy class (optional)
     * @param newBusinessPrice    the new price for business class (optional)
     * @param newFirstPrice       the new price for first class (optional)
     * @param newStatus           the new flight status (e.g., ON_TIME, DELAYED) (optional)
     * @param newAirlineName      the new name of the airline (optional)
     * @param isInternational     whether the flight is international or not (optional)
     */
    public UpdateFlight(int flightId,
                        String newFlightNumber,
                        String newOrigin,
                        String newDestination,
                        LocalDate newDepartureDate,
                        Integer newEconomyCapacity,
                        Integer newBusinessCapacity,
                        Integer newFirstCapacity,
                        Double newEconomyPrice,
                        Double newBusinessPrice,
                        Double newFirstPrice,
                        FlightStatus newStatus,
                        String newAirlineName,
                        Boolean isInternational, LocalDate newArrivalDate,
                        LocalTime newArrivalTime,LocalTime newDepartureTime
) {

        this.flightId = flightId;
        this.newFlightNumber = newFlightNumber;
        this.newOrigin = newOrigin;
        this.newDestination = newDestination;
        this.newDepartureDate = newDepartureDate;
        this.newEconomyCapacity = newEconomyCapacity;
        this.newBusinessCapacity = newBusinessCapacity;
        this.newFirstCapacity = newFirstCapacity;
        this.newEconomyPrice = newEconomyPrice;
        this.newBusinessPrice = newBusinessPrice;
        this.newFirstPrice = newFirstPrice;
        this.newStatus = newStatus;
        this.newAirlineName = newAirlineName;
        this.isInternational = isInternational;
        this.newArrivalDate = newArrivalDate;
        this.newArrivalTime = newArrivalTime;
        this.newDepartureTime = newDepartureTime;
        

    }

    /**
     * Executes the update flight command. It applies all valid non-null parameters to the target flight.
     * <p>
     * If the flight ID does not exist or the new departure date is invalid, an exception is thrown.
     * After updating, the flight data is saved to persistent storage.
     * </p>
     *
     * @param flightBookingSystem the flight booking system instance to operate on
     * @throws FlightBookingSystemException if the flight is not found or if saving data fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {

        Flight flight = flightBookingSystem.getFlightById(flightId);
        if (flight == null) {
            throw new FlightBookingSystemException("Flight with ID " + flightId + " not found.");
        }

        LocalDate updatedDepartureDate = (newDepartureDate != null) ? newDepartureDate : flight.getDepartureDate();
        LocalTime updatedDepartureTime = (newDepartureTime != null) ? newDepartureTime : flight.getDepartureTime();

        LocalDate updatedArrivalDate = (newArrivalDate != null) ? newArrivalDate : flight.getArrivalDate();
        LocalTime updatedArrivalTime = (newArrivalTime != null) ? newArrivalTime : flight.getArrivalTime();

        LocalDateTime now = LocalDateTime.now();

        // Validate Departure Date (must not be in the past)
        LocalDateTime departureDateTime = LocalDateTime.of(updatedDepartureDate, updatedDepartureTime != null ? updatedDepartureTime : LocalTime.MIDNIGHT);
        if (departureDateTime.isBefore(now)) {
            throw new FlightBookingSystemException("Departure date and time cannot be in the past.");
        }

        // Validate Arrival Date and Time (must be after departure and in future)
        if (updatedArrivalDate != null && updatedArrivalTime != null) {
            LocalDateTime arrivalDateTime = LocalDateTime.of(updatedArrivalDate, updatedArrivalTime);
            if (arrivalDateTime.isBefore(now)) {
                throw new FlightBookingSystemException("Arrival date and time cannot be in the past.");
            }
            if (!arrivalDateTime.isAfter(departureDateTime)) {
                throw new FlightBookingSystemException("Arrival date and time must be after departure date and time.");
            }
        }

        // Now update fields only if valid values are provided
        if (newFlightNumber != null && !newFlightNumber.trim().isEmpty()) {
            flight.setFlightNumber(newFlightNumber.trim());
        }

        if (newOrigin != null && !newOrigin.trim().isEmpty()) {
            flight.setOrigin(newOrigin.trim());
        }

        if (newDestination != null && !newDestination.trim().isEmpty()) {
            flight.setDestination(newDestination.trim());
        }

        if (newDepartureDate != null) {
            flight.setDepartureDate(newDepartureDate);
        }
        if (newDepartureTime != null) {
            flight.setDepartureTime(newDepartureTime);
        }

        if (newEconomyCapacity != null && newEconomyCapacity >= 0) {
            flight.setCapacityForClass(Booking.SeatClass.ECONOMY, newEconomyCapacity);
        }

        if (newBusinessCapacity != null && newBusinessCapacity >= 0) {
            flight.setCapacityForClass(Booking.SeatClass.BUSINESS, newBusinessCapacity);
        }

        if (newFirstCapacity != null && newFirstCapacity >= 0) {
            flight.setCapacityForClass(Booking.SeatClass.FIRST, newFirstCapacity);
        }

        if (newEconomyPrice != null && newEconomyPrice >= 0) {
            flight.setPriceForClass(Booking.SeatClass.ECONOMY, newEconomyPrice);
        }

        if (newBusinessPrice != null && newBusinessPrice >= 0) {
            flight.setPriceForClass(Booking.SeatClass.BUSINESS, newBusinessPrice);
        }

        if (newFirstPrice != null && newFirstPrice >= 0) {
            flight.setPriceForClass(Booking.SeatClass.FIRST, newFirstPrice);
        }

        if (newStatus != null) {
            flight.setStatus(newStatus);
        }

        if (newAirlineName != null && !newAirlineName.trim().isEmpty()) {
            flight.setAirlineName(newAirlineName.trim());
        }

        if (isInternational != null) {
            flight.setInternational(isInternational);
        }

        if (newArrivalDate != null) {
            flight.setArrivalDate(newArrivalDate);
        }

        if (newArrivalTime != null) {
            flight.setArrivalTime(newArrivalTime);
        }

        // Save changes to persistent storage
        try {
            flightBookingSystem.getDataManager().storeData(flightBookingSystem);
        } catch (IOException e) {
            throw new FlightBookingSystemException("Failed to save flight data: " + e.getMessage());
        }

        System.out.println("\nFlight updated successfully:\n" + flight.getDetailsLong());
    }

}
