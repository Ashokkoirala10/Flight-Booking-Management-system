package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.*;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Handles loading and storing customer data to and from a file.
 * <p>
 * The customer data is persisted in a plain text file, where each customer's
 * attributes are stored on a single line using a predefined separator. This
 * class ensures that {@link Customer} objects are correctly reconstructed
 * and added to the {@link FlightBookingSystem}.
 * </p>
 * 
 * @author Ashok
 */
public class CustomerDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/customers.txt";
    private static final String SEPARATOR = "::";

    /**
     * Loads customer data from a text file and adds them to the flight booking system.
     *
     * @param fbs the {@link FlightBookingSystem} instance to populate with customer data
     * @throws IOException if an I/O error occurs while reading the file
     * @throws FlightBookingSystemException if the file format is invalid or a parsing error occurs
     */
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int lineIndex = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] properties = line.split(SEPARATOR, -1);

                if (properties.length < 12) {
                    throw new FlightBookingSystemException("Invalid customer data format on line " + lineIndex);
                }

                try {
                    int id = Integer.parseInt(properties[0]);
                    String name = properties[1];
                    String phone = properties[2];
                    int age = Integer.parseInt(properties[3]);
                    String address = properties[4];
                    String country = properties[5];
                    String passportNumber = properties[6];
                    LocalDate passportExpiryDate = LocalDate.parse(properties[7]);
                    boolean disabled = Boolean.parseBoolean(properties[8]);
                    String email = properties[9].isEmpty() ? "" : properties[9];
                    LocalDate dob = properties[10].isEmpty() ? LocalDate.of(1900, 1, 1) : LocalDate.parse(properties[10]);
                    String gender = properties.length >= 12 ? properties[11] : "Unspecified";

                    Customer customer = new Customer(id, name, phone, age, address, country,
                            passportNumber, passportExpiryDate, disabled, email, dob, gender);
                    customer.setDisabled(disabled);

                    // Directly add customer to system without triggering ID increment
                    fbs.addCustomerDirect(customer);

                } catch (Exception ex) {
                    throw new FlightBookingSystemException("Error parsing customer on line " + lineIndex + ": " + ex.getMessage());
                }

                lineIndex++;
            }
        }
    }

    /**
     * Stores customer data from the flight booking system into a text file.
     *
     * @param fbs the {@link FlightBookingSystem} containing all customer data
     * @throws IOException if an I/O error occurs while writing the file
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Customer customer : fbs.getAllCustomers()) {
                out.println(customer.getId() + SEPARATOR +
                            customer.getName() + SEPARATOR +
                            customer.getPhone() + SEPARATOR +
                            customer.getAge() + SEPARATOR +
                            customer.getAddress() + SEPARATOR +
                            customer.getCountry() + SEPARATOR +
                            customer.getPassportNumber() + SEPARATOR +
                            customer.getPassportExpiryDate() + SEPARATOR +
                            customer.isDisabled() + SEPARATOR +
                            customer.getEmail() + SEPARATOR +
                            customer.getDob() + SEPARATOR +
                            customer.getGender());
            }
        }
    }
}
