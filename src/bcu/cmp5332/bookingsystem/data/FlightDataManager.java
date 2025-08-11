package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Booking.SeatClass;
import bcu.cmp5332.bookingsystem.model.FlightStatus;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * Handles the loading and storing of flight data from and to a text file.
 * <p>
 * This class parses flight records from a file and creates corresponding
 * {@link Flight} objects, assigning them to the {@link FlightBookingSystem}.
 * It also writes all the flight information back to a file in a structured format.
 * </p>
 * 
 * The expected file format includes 17 fields per line, separated by the `::` delimiter:
 * ID, FlightNumber, Origin, Destination, DepartureDate, EconomyCapacity, BusinessCapacity, FirstClassCapacity,
 * EconomyPrice, BusinessPrice, FirstClassPrice, Status, DepartureTime, ArrivalTime, ArrivalDate,
 * AirlineName, IsInternational
 * 
 * @author Ashok
 */
public class FlightDataManager implements DataManager {

    private final String SEPARATOR = "::";
    private final String RESOURCE = "./resources/data/flights.txt";

    /**
     * Loads flight data from the file and populates the {@link FlightBookingSystem}.
     *
     * @param fbs the flight booking system to populate with flight data
     * @throws IOException if there is an issue reading the file
     * @throws FlightBookingSystemException if a line is malformed or data cannot be parsed
     */
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException, NumberFormatException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();

                if (line.endsWith(SEPARATOR)) {
                    line = line.substring(0, line.length() - SEPARATOR.length());
                }

                String[] properties = line.split(SEPARATOR, -1);

                if (properties.length != 17) {
                    throw new FlightBookingSystemException("Invalid line format on line " + line_idx);
                }

                try {
                    int id = Integer.parseInt(properties[0].trim());
                    String flightNumber = properties[1].trim();
                    String origin = properties[2].trim();
                    String destination = properties[3].trim();
                    String airlineName = properties[15].trim();
                    LocalDate departureDate = LocalDate.parse(properties[4].trim());

                    int economyCapacity = Integer.parseInt(properties[5].trim());
                    int businessCapacity = Integer.parseInt(properties[6].trim());
                    int firstClassCapacity = Integer.parseInt(properties[7].trim());

                    double economyPrice = Double.parseDouble(properties[8].trim());
                    double businessPrice = Double.parseDouble(properties[9].trim());
                    double firstPrice = Double.parseDouble(properties[10].trim());

                    FlightStatus status = FlightStatus.valueOf(properties[11].trim());

                    LocalTime departureTime = LocalTime.parse(properties[12].trim());
                    LocalTime arrivalTime = LocalTime.parse(properties[13].trim());
                    LocalDate arrivalDate = LocalDate.parse(properties[14].trim());

                    boolean isInternational = Boolean.parseBoolean(properties[16].trim());

                    Flight flight = new Flight(id, flightNumber, airlineName, origin, destination, departureDate,
                            departureTime, arrivalTime, arrivalDate, isInternational);

                    flight.setCapacityForClass(SeatClass.ECONOMY, economyCapacity);
                    flight.setCapacityForClass(SeatClass.BUSINESS, businessCapacity);
                    flight.setCapacityForClass(SeatClass.FIRST, firstClassCapacity);

                    flight.setPriceForClass(SeatClass.ECONOMY, economyPrice);
                    flight.setPriceForClass(SeatClass.BUSINESS, businessPrice);
                    flight.setPriceForClass(SeatClass.FIRST, firstPrice);

                    flight.setStatus(status);
                    fbs.addFlight(flight);

                } catch (IllegalArgumentException ex) {
                    throw new FlightBookingSystemException("Unable to parse flight data on line " + line_idx + ": " + ex);
                }

                line_idx++;
            }
        }
    }

    /**
     * Stores the flight data from the {@link FlightBookingSystem} to a text file.
     *
     * @param fbs the flight booking system containing all the flight records
     * @throws IOException if there is an issue writing the file
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Flight flight : fbs.getAllFlights()) {
                out.println(
                    flight.getId() + SEPARATOR +
                    flight.getFlightNumber() + SEPARATOR +
                    flight.getOrigin() + SEPARATOR +
                    flight.getDestination() + SEPARATOR +
                    flight.getDepartureDate() + SEPARATOR +
                    flight.getCapacityForClass(SeatClass.ECONOMY) + SEPARATOR +
                    flight.getCapacityForClass(SeatClass.BUSINESS) + SEPARATOR +
                    flight.getCapacityForClass(SeatClass.FIRST) + SEPARATOR +
                    flight.getPriceForClass(SeatClass.ECONOMY) + SEPARATOR +
                    flight.getPriceForClass(SeatClass.BUSINESS) + SEPARATOR +
                    flight.getPriceForClass(SeatClass.FIRST) + SEPARATOR +
                    flight.getStatus() + SEPARATOR +
                    flight.getDepartureTime() + SEPARATOR +
                    flight.getArrivalTime() + SEPARATOR +
                    flight.getArrivalDate() + SEPARATOR +
                    flight.getAirlineName() + SEPARATOR +
                    flight.getInternational()
                );
            }
        }
    }
}
