package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.time.LocalDate;

/**
 * The {@code UpdateCustomer} command allows updating an existing customer's
 * personal details such as name, phone, passport info, etc., using their ID.
 * <p>
 * Only the fields that are not null (and valid) will be updated, which allows
 * for partial updates.
 * </p>
 */
public class UpdateCustomer implements Command {

    private final int customerId;
    private final String newName;
    private final String newPhone;
    private final Integer newAge;
    private final String newAddress;
    private final String newPassport;
    private final LocalDate newExpiryDate;
    private final String newEmail;
    private final LocalDate newDob;
    private final String newGender;
    private final Boolean newDisabled;

    /**
     * Constructs the command with the updated customer details.
     * Any field set to null will not be updated.
     *
     * @param customerId     ID of the customer to update
     * @param newName        new name (optional)
     * @param newPhone       new phone number (optional)
     * @param newAge         new age (optional)
     * @param newAddress     new address (optional)
     * @param newPassport    new passport number (optional)
     * @param newExpiryDate  new passport expiry date (optional)
     * @param newEmail       new email address (optional)
     * @param newDob         new date of birth (optional)
     * @param newGender      new gender (optional)
     * @param newDisabled    disability status (optional)
     */
    public UpdateCustomer(int customerId, String newName, String newPhone, Integer newAge,
                          String newAddress, String newPassport, LocalDate newExpiryDate,
                          String newEmail, LocalDate newDob, String newGender, Boolean newDisabled) {
        this.customerId = customerId;
        this.newName = newName;
        this.newPhone = newPhone;
        this.newAge = newAge;
        this.newAddress = newAddress;
        this.newPassport = newPassport;
        this.newExpiryDate = newExpiryDate;
        this.newEmail = newEmail;
        this.newDob = newDob;
        this.newGender = newGender;
        this.newDisabled = newDisabled;
    }

    /**
     * Executes the update of the customer's information in the booking system.
     * If the customer ID is not found, an exception is thrown.
     *
     * @param flightBookingSystem the booking system context
     * @throws FlightBookingSystemException if the customer does not exist
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerById(customerId);

        // Check if the customer exists
        if (customer == null) {
            throw new FlightBookingSystemException(" Customer with ID " + customerId + " not found.");
        }

        // Update fields only if the input is not null or blank (where applicable)
        if (newName != null && !newName.isBlank()) {
            customer.setName(newName);
        }

        if (newPhone != null && !newPhone.isBlank()) {
            customer.setPhone(newPhone);
        }

        if (newAge != null && newAge > 0) {
            customer.setAge(newAge);
        }

        if (newAddress != null && !newAddress.isBlank()) {
            customer.setAddress(newAddress);
        }

        if (newPassport != null && !newPassport.isBlank()) {
            customer.setPassportNumber(newPassport);
        }

        if (newExpiryDate != null) {
            customer.setPassportExpiryDate(newExpiryDate);
        }

        if (newEmail != null && !newEmail.isBlank()) {
            customer.setEmail(newEmail);
        }

        if (newDob != null) {
            customer.setDob(newDob);
        }

        if (newGender != null && !newGender.isBlank()) {
            customer.setGender(newGender);
        }

        if (newDisabled != null) {
            customer.setDisabled(newDisabled);
        }

        // Display updated customer information
        System.out.println("\n Customer updated successfully!");
        System.out.println("ID: " + customer.getId());
        System.out.println("Name: " + customer.getName());
        System.out.println("Phone: " + customer.getPhone());
        System.out.println("Age: " + customer.getAge());
        System.out.println("Address: " + customer.getAddress());
        System.out.println("Passport No: " + customer.getPassportNumber());
        System.out.println("Passport Expiry: " + customer.getPassportExpiryDate());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Date of Birth: " + customer.getDob());
        System.out.println("Gender: " + customer.getGender());
        System.out.println("Disabled: " + (customer.isDisabled() ? "Yes" : "No"));
    }
}
