package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import bcu.cmp5332.bookingsystem.model.Booking.SeatClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Flight {

    private int id;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDate departureDate;
    private final Map<Booking.SeatClass, Integer> seatCapacities;
    private final Map<Booking.SeatClass, Integer> seatsBooked;
    private final Map<Booking.SeatClass, Double> pricing;
    private final Set<Customer> passengers;
    private Map<Booking.SeatClass, Set<String>> bookedSeats = new HashMap<>();
    private List<Booking> bookings = new ArrayList<>();  // Add a list to store bookings
    private FlightStatus status = FlightStatus.SCHEDULED;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private LocalDate arrivalDate;
    private String airlineName;
    private boolean isInternational;




    public Flight(int id, String flightNumber, String airlineName, String origin, String destination,
            LocalDate departureDate, LocalTime departureTime, LocalTime arrivalTime, LocalDate arrivalDate,
            boolean isInternational) {
		  this.id = id;
		  this.flightNumber = flightNumber;
		  this.airlineName = airlineName;
		  this.origin = origin;
		  this.destination = destination;
		  this.departureDate = departureDate;
		  this.departureTime = departureTime;
		  this.arrivalTime = arrivalTime;
		  this.arrivalDate = arrivalDate;
		  this.isInternational = isInternational;

		  seatCapacities = new HashMap<>();
		  seatsBooked = new HashMap<>();
		  passengers = new HashSet<>();
		  pricing = new HashMap<>();
		
		  seatCapacities.put(Booking.SeatClass.ECONOMY, 60);
		  seatCapacities.put(Booking.SeatClass.BUSINESS, 25);
		  seatCapacities.put(Booking.SeatClass.FIRST, 15);
		
		  seatsBooked.put(Booking.SeatClass.ECONOMY, 0);
		  seatsBooked.put(Booking.SeatClass.BUSINESS, 0);
		  seatsBooked.put(Booking.SeatClass.FIRST, 0);
		
		  pricing.put(Booking.SeatClass.ECONOMY, 100.0);
		  pricing.put(Booking.SeatClass.BUSINESS, 250.0);
		  pricing.put(Booking.SeatClass.FIRST, 500.0);
		}

    public void setInternational(boolean isInternational) {
        this.isInternational = isInternational;
    }
    public boolean getInternational() {
    	return isInternational;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }



    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    public void setAirlineName(String name) {
        this.airlineName = name;
    }



    public int getId() {
        return id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }


    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public int getCapacityForClass(Booking.SeatClass seatClass) {
        return seatCapacities.getOrDefault(seatClass, 0);
    }

    public int getAvailableSeatsForClass(Booking.SeatClass seatClass) {
        return seatCapacities.getOrDefault(seatClass, 0) - seatsBooked.getOrDefault(seatClass, 0);
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }


    // New method to return the total available seats across all classes
    public int getAvailableSeats() {
        int totalAvailableSeats = 0;
        for (Booking.SeatClass seatClass : Booking.SeatClass.values()) {
            totalAvailableSeats += getAvailableSeatsForClass(seatClass);
        }
        return totalAvailableSeats;
    }

    public void addPassenger(Customer passenger, Booking.SeatClass seatClass) throws FlightBookingSystemException {
        if (seatsBooked.get(seatClass) >= seatCapacities.get(seatClass)) {
            throw new FlightBookingSystemException("No available seats in " + seatClass + " class.");
        }
        passengers.add(passenger);
        seatsBooked.put(seatClass, seatsBooked.get(seatClass) + 1);
    }

    public void removePassenger(Customer passenger, Booking.SeatClass seatClass) {
        passengers.remove(passenger);
        seatsBooked.put(seatClass, seatsBooked.get(seatClass) - 1);
    }

    public double getPriceForClass(Booking.SeatClass seatClass) {
        return pricing.getOrDefault(seatClass, 0.0);
    }

    public String getDetailsShort() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        String depTime = departureTime.format(timeFormatter);
        String arrTime = arrivalTime.format(timeFormatter);
        String flightType = isInternational ? "INTL" : "DOM";

        return String.format(
            "%-5s %-10s %-20s %-15s %-15s %-22s %-22s %-12s %-12s | $%.2f / $%.2f / $%.2f",
            "[" + id + "]", flightNumber, airlineName, origin, destination,
            departureDate + " " + depTime,
            arrivalDate + " " + arrTime,
            status, flightType,
            getDynamicPrice(Booking.SeatClass.ECONOMY),
            getDynamicPrice(Booking.SeatClass.BUSINESS),
            getDynamicPrice(Booking.SeatClass.FIRST)
        );
    }









    public void setCapacityForClass(Booking.SeatClass seatClass, int capacity) {
        seatCapacities.put(seatClass, capacity);
    }

    public void setPriceForClass(Booking.SeatClass seatClass, double price) throws FlightBookingSystemException {
        if (pricing.containsKey(seatClass)) {
            pricing.put(seatClass, price);
        } else {
            throw new FlightBookingSystemException("Invalid seat class.");
        }
    }

    // Check if seat is available for the given class and seat number
    public boolean isSeatAvailable(SeatClass seatClass, String seatNumber) {
        return !bookedSeats.getOrDefault(seatClass, new HashSet<>()).contains(seatNumber);
    }

    // Reserve the seat by adding it to the set of booked seats
    public void reserveSeat(SeatClass seatClass, String seatNumber) {
        bookedSeats.computeIfAbsent(seatClass, k -> new HashSet<>()).add(seatNumber);
    }

    // Add booking and track the booked seat
    public void addBooking(Booking booking) {
        bookings.add(booking);  // Add booking to flight's list
        seatsBooked.put(booking.getSeatClass(), seatsBooked.get(booking.getSeatClass()) + 1);  // Increment seat count

        // Track the booked seat number for the specific class
        if (booking.getSeatNumber() != null && !booking.getSeatNumber().isEmpty()) {
            bookedSeats.computeIfAbsent(booking.getSeatClass(), k -> new HashSet<>()).add(booking.getSeatNumber());
        }
        passengers.add(booking.getCustomer());
    }
    public List<Booking> getBookings() {
        return bookings;
    }
    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public String getDetailsLong() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.ENGLISH);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        String formattedDate = departureDate.format(dateFormatter);
        String formattedDepartureTime = departureTime.format(timeFormatter);
        String formattedArrivalTime = arrivalTime.format(timeFormatter);
        String formattedArrivalDate = arrivalDate != null ? arrivalDate.format(dateFormatter) : "N/A";
        String flightType = isInternational ? "International" : "Domestic";

        return new StringBuilder()
            .append("Flight #").append(id).append(" - ").append(flightNumber)
            .append(" (").append(airlineName).append(")")
            .append(" [").append(flightType).append("]")
            .append("\nFrom: ").append(origin).append(" to ").append(destination)
            .append("\nDeparture: ").append(formattedDate).append(" at ").append(formattedDepartureTime)
            .append("\nArrival: ").append(formattedArrivalDate).append(" at ").append(formattedArrivalTime)
            .append("\nStatus: ").append(status)
            .append("\nFlight Type: ").append(flightType)
            .append("\nSeats Available: ").append(getAvailableSeats())
            .append("\nSeat Capacities: Economy = ").append(seatCapacities.get(Booking.SeatClass.ECONOMY))
            .append(", Business = ").append(seatCapacities.get(Booking.SeatClass.BUSINESS))
            .append(", First = ").append(seatCapacities.get(Booking.SeatClass.FIRST))
            .append("\nSeat Prices: Economy = $").append(String.format("%.2f", getPriceForClass(Booking.SeatClass.ECONOMY)))
            .append(", Business = $").append(String.format("%.2f", getPriceForClass(Booking.SeatClass.BUSINESS)))
            .append(", First = $").append(String.format("%.2f", getPriceForClass(Booking.SeatClass.FIRST)))
            .toString();
    }






    public String getFlightDetailsWithPassengers() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        String flightType = isInternational ? "International" : "Domestic";

        StringBuilder details = new StringBuilder();
        details.append("Flight #").append(id).append(" - ").append(flightNumber)
               .append(" (").append(airlineName).append(")")
               .append(" [").append(flightType).append("]")
               .append(" from ").append(origin).append(" to ").append(destination).append("\n")
               .append("Departure: ").append(departureDate).append(" at ").append(departureTime.format(timeFormatter)).append("\n")
               .append("Arrival: ").append(arrivalDate).append(" at ").append(arrivalTime.format(timeFormatter)).append("\n")
               .append("Status: ").append(status).append("\n")
               .append("Flight Type: ").append(flightType).append("\n")
               .append("Seats Available: ").append(getAvailableSeats()).append("\n")
               .append("Seat Capacities: Economy=").append(seatCapacities.get(Booking.SeatClass.ECONOMY))
               .append(", Business=").append(seatCapacities.get(Booking.SeatClass.BUSINESS))
               .append(", First=").append(seatCapacities.get(Booking.SeatClass.FIRST)).append("\n");

        if (passengers.isEmpty()) {
            details.append("\nPassengers:\nNo passengers booked.\n");
        } else {
            details.append("\nPassengers:\n");
            details.append(String.format(
                "%-5s %-20s %-15s %-5s %-15s %-25s %-10s %-10s\n",
                "ID", "Name", "Phone", "Age", "Country", "Email", "Gender", "Disabled"
            ));
            details.append("------------------------------------------------------------------------------------------------------------\n");

            for (Customer passenger : passengers) {
                details.append(passenger.getDetailsShort()).append("\n");
            }
        }

        return details.toString();
    }




    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        seatsBooked.put(booking.getSeatClass(), seatsBooked.get(booking.getSeatClass()) - 1);

        // Remove the seat number from booked seats
        if (booking.getSeatNumber() != null && !booking.getSeatNumber().isEmpty()) {
            bookedSeats.getOrDefault(booking.getSeatClass(), new HashSet<>()).remove(booking.getSeatNumber());
        }
    }
	public String getAirlineName() {
		// TODO Auto-generated method stub
		return airlineName;
	}


    // Calculate price adjusted based on days to departure
	public double getDynamicPrice(Booking.SeatClass seatClass) {
	    LocalDateTime now = LocalDateTime.now();
	    LocalDateTime depDateTime = LocalDateTime.of(this.getDepartureDate(), this.getDepartureTime());
	    long minutesUntilDeparture = ChronoUnit.MINUTES.between(now, depDateTime);

	    double basePrice = this.getPriceForClass(seatClass);
	    double increasePercent = 0.0;

	    if (minutesUntilDeparture <= 0) {
	        // Flight is now or past, max increase
	        increasePercent = 0.25;
	    } else if (minutesUntilDeparture <= 24 * 60) {
	        // Within 1 day
	        increasePercent = 0.20;
	    } else if (minutesUntilDeparture <= 3 * 24 * 60) {
	        // Within 3 days
	        increasePercent = 0.10;
	    }

	    return basePrice * (1 + increasePercent);
	}

	public double getBasePrice(SeatClass seatClass) {
	    return pricing.getOrDefault(seatClass, 0.0);
	}

	public void releaseSeat(SeatClass seatClass, String seatNumber) {
	    Set<String> seats = bookedSeats.get(seatClass);
	    if (seats != null && seats.remove(seatNumber)) {
	        seatsBooked.put(seatClass, seatsBooked.get(seatClass) - 1);
	    }
	}






}
