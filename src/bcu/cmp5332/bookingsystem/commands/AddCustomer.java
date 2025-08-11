package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.time.LocalDate;

public class AddCustomer implements Command {

    
    /**
     * The customer's full name.
     */
    private final String name;

    /**
     * The customer's phone number.
     */
    private final String phone;

    /**
     * The customer's age.
     */
    private final int age;

    /**
     * The customer's residential address.
     */
    private final String address;

    /**
     * The customer's country of residence.
     */
    private final String country;

    /**
     * The customer's passport number.
     */
    private final String passportNumber;

    /**
     * The expiry date of the customer's passport.
     */
    private final LocalDate passportExpiryDate;

    /**
     * Indicates whether the customer has a disability.
     */
    private final boolean disabled;

    /**
     * The customer's email address.
     */
    private final String email;

    /**
     * The customer's date of birth.
     */
    private final LocalDate dob;

    /**
     * The customer's gender.
     */
    private final String gender;

    /**
     * Constructs an AddCustomer command with the specified customer details.
     *
     * @param name the customer's full name
     * @param phone the customer's phone number
     * @param age the customer's age
     * @param address the customer's residential address
     * @param country the customer's country of residence
     * @param passportNumber the customer's passport number
     * @param passportExpiryDate the expiry date of the customer's passport
     * @param disabled whether the customer has a disability
     * @param email the customer's email address
     * @param dob the customer's date of birth
     * @param gender the customer's gender
     */
    public AddCustomer(String name, String phone, int age, String address,
                       String country, String passportNumber, LocalDate passportExpiryDate,
                       boolean disabled, String email, LocalDate dob, String gender) {
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

    /**
     * Executes the command to add a new customer to the flight booking system.
     * <p>
     * This method adds the customer with the provided details to the system and
     * prints a confirmation message including the customer's name and assigned ID.
     * </p>
     *
     * @param flightBookingSystem the flight booking system instance
     * @throws FlightBookingSystemException if the customer cannot be added due to invalid data or system errors
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer newCustomer = flightBookingSystem.addCustomer(
                name, phone, age, address, country, passportNumber, passportExpiryDate,
                disabled, email, dob, gender
        );

        System.out.println("Customer added: " + newCustomer.getName() + " (Id: " + newCustomer.getId() + ")");
    }
}
