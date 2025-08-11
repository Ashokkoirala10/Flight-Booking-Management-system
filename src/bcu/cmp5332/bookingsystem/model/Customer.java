package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the flight booking system.
 * <p>
 * A Customer has personal details such as name, phone, age, address,
 * passport information, disability status, email, date of birth, gender,
 * and a list of bookings.
 * </p>
 * <p>
 * This class provides methods to add and cancel bookings, retrieve customer
 * details in both short and long formats, and check if a customer has a
 * booking on a specific flight.
 * </p>
 * 
 * @author ashok
 */
public class Customer {

    private int id;
    private String name;
    private String phone;

    // Additional personal information
    private int age;
    private String address;
    private String country;
    private String passportNumber;
    private LocalDate passportExpiryDate;
    private boolean disabled = false;
    private String email;
    private LocalDate dob;
    private String gender;

    private final List<Booking> bookings = new ArrayList<>();

    /**
     * Constructs a Customer with basic details.
     * 
     * @param id the customer ID
     * @param name the customer's name
     * @param phone the customer's phone number
     */
    public Customer(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    /**
     * Constructs a Customer with full details.
     * 
     * @param id the customer ID
     * @param name the customer's name
     * @param phone the customer's phone number
     * @param age the customer's age
     * @param address the customer's address
     * @param country the customer's country
     * @param passportNumber the customer's passport number
     * @param passportExpiryDate the expiry date of the passport
     * @param disabled true if the customer has a disability, false otherwise
     * @param email the customer's email address
     * @param dob the customer's date of birth
     * @param gender the customer's gender
     */
    public Customer(int id, String name, String phone, int age, String address,
                    String country, String passportNumber, LocalDate passportExpiryDate,
                    boolean disabled, String email, LocalDate dob, String gender) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.address = address;
        this.country = country;
        this.passportNumber = passportNumber;
        this.passportExpiryDate = passportExpiryDate;
        this.disabled = disabled;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
    }

    // Getters and setters

    /**
     * Returns whether the customer has a disability.
     * 
     * @return true if disabled, false otherwise
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets whether the customer has a disability.
     * 
     * @param disabled true if disabled, false otherwise
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Returns the customer's email address.
     * 
     * @return the email address
     */
    public String getEmail() { 
        return email; 
    }

    /**
     * Sets the customer's email address.
     * 
     * @param email the email address to set
     */
    public void setEmail(String email) { 
        this.email = email; 
    }

    /**
     * Returns the customer's date of birth.
     * 
     * @return the date of birth
     */
    public LocalDate getDob() { 
        return dob; 
    }

    /**
     * Sets the customer's date of birth.
     * 
     * @param dob the date of birth to set
     */
    public void setDob(LocalDate dob) { 
        this.dob = dob; 
    }

    /**
     * Returns the customer's gender.
     * 
     * @return the gender
     */
    public String getGender() { 
        return gender; 
    }

    /**
     * Sets the customer's gender.
     * 
     * @param gender the gender to set
     */
    public void setGender(String gender) { 
        this.gender = gender; 
    }

    /**
     * Returns the customer ID.
     * 
     * @return the customer ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the customer ID.
     * 
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the customer's name.
     * 
     * @return the customer's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer's name.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the customer's phone number.
     * 
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the customer's phone number.
     * 
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the customer's age.
     * 
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the customer's age.
     * 
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Returns the customer's address.
     * 
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the customer's address.
     * 
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the customer's country.
     * 
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the customer's country.
     * 
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Returns the customer's passport number.
     * 
     * @return the passport number
     */
    public String getPassportNumber() {
        return passportNumber;
    }

    /**
     * Sets the customer's passport number.
     * 
     * @param passportNumber the passport number to set
     */
    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    /**
     * Returns the expiry date of the customer's passport.
     * 
     * @return the passport expiry date
     */
    public LocalDate getPassportExpiryDate() {
        return passportExpiryDate;
    }

    /**
     * Sets the expiry date of the customer's passport.
     * 
     * @param passportExpiryDate the expiry date to set
     */
    public void setPassportExpiryDate(LocalDate passportExpiryDate) {
        this.passportExpiryDate = passportExpiryDate;
    }

    /**
     * Returns the list of bookings associated with this customer.
     * 
     * @return the list of bookings
     */
    public List<Booking> getBookings() {
        return bookings;
    }

    /**
     * Adds a booking to this customer.
     * <p>
     * Throws a FlightBookingSystemException if the customer already has a booking
     * for the same flight.
     * </p>
     * 
     * @param booking the booking to add
     * @throws FlightBookingSystemException if a booking for the flight already exists
     */
    public void addBooking(Booking booking) throws FlightBookingSystemException {
        for (Booking b : bookings) {
            if (b.getFlight().equals(booking.getFlight())) {
                throw new FlightBookingSystemException("Customer already has a booking for this flight.");
            }
        }
        bookings.add(booking);
    }

    /**
     * Cancels a booking for this customer.
     * <p>
     * Throws a FlightBookingSystemException if the booking is not found.
     * </p>
     * 
     * @param booking the booking to cancel
     * @throws FlightBookingSystemException if the booking is not found
     */
    public void cancelBooking(Booking booking) throws FlightBookingSystemException {
        if (!bookings.contains(booking)) {
            throw new FlightBookingSystemException("Booking not found for this customer.");
        }
        booking.cancel();
        bookings.remove(booking);
    }

    /**
     * Returns a short formatted string with key customer details.
     * 
     * @return a formatted string summarizing customer details
     */
    public String getDetailsShort() {
        return String.format(
            "%-5s %-20s %-15s %-5s %-15s %-25s %-10s %-10s",
            "[" + id + "]", name, phone, age, country, email, gender, disabled ? "Yes" : "No"
        );
    }

    /**
     * Returns a detailed formatted string with all customer information and bookings.
     * 
     * @return detailed customer information as a string
     */
    public String getDetailsLong() {
        StringBuilder details = new StringBuilder();
        details.append("\nCustomer ID: ").append(id).append("\n")
               .append("Name: ").append(name).append("\n")
               .append("Phone: ").append(phone).append("\n")
               .append("Age: ").append(age).append("\n")
               .append("Address: ").append(address).append("\n")
               .append("Country: ").append(country).append("\n")
               .append("Disabled: ").append(disabled ? "Yes" : "No").append("\n")
               .append("Email: ").append(email).append("\n")
               .append("DOB: ").append(dob).append("\n")
               .append("Gender: ").append(gender).append("\n")
               .append("Passport Number: ").append(passportNumber).append("\n")
               .append("Passport Expiry Date: ").append(passportExpiryDate).append("\n")
               .append("----------------------------------------------------------------\n");

        if (!bookings.isEmpty()) {
            details.append("Bookings:\n");
            for (Booking booking : bookings) {
                details.append(booking.getBookingDetails()).append("\n");
            }
        } else {
            details.append("No bookings yet.\n");
        }

        return details.toString();
    }

    /**
     * Checks if the customer has a booking for the specified flight.
     * 
     * @param flight the flight to check bookings for
     * @return true if a booking exists for the flight, false otherwise
     */
    public boolean hasBooking(Flight flight) {
        for (Booking b : bookings) {
            if (b.getFlight().equals(flight)) {
                return true;
            }
        }
        return false;
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }


}
