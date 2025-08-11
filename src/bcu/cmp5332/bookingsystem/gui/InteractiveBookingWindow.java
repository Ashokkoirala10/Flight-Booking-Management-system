package bcu.cmp5332.bookingsystem.gui;

import javax.swing.*;
import javax.swing.border.*;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
/**
 * InteractiveBookingWindow is a GUI window implemented using Swing
 * that allows users to create new flight bookings interactively.
 * 
 * <p>
 * The window supports booking for new customers as well as existing customers.
 * It provides fields for customer details, flight selection, seat assignment,
 * pet information, and manual discount input.
 * </p>
 * 
 * <p>
 * Validations are performed on input fields including date parsing and seat availability.
 * The booking process includes calculating applicable discounts, reserving seats,
 * and persisting data through the FlightBookingSystem's data manager.
 * </p>
 * 
 * <p>
 * This class extends JFrame and manages its own UI components and event handling.
 * </p>
 * 
 * @author 
 * @version 1.0
 */

public class InteractiveBookingWindow extends JFrame {

    private final FlightBookingSystem system;

    // Customer Type
    private JRadioButton rbNewCustomer;
    private JRadioButton rbExistingCustomer;

    // Existing Customer Panel
    private JPanel existingCustomerPanel;
    private JTextField tfExistingCustomerId;

    // New Customer Panel
    private JPanel newCustomerPanel;
    private JTextField tfName, tfPhone, tfAge, tfAddress, tfCountry, tfPassport, tfPassportExpiry, tfEmail, tfDob, tfGender;
    private JCheckBox cbDisabled;

    // Flight & Booking
    private JComboBox<String> cbFlights;
    private JComboBox<String> cbSeatClass;
    private JTextField tfSeatNumber;
    private JCheckBox cbHasPet;
    private JComboBox<String> cbPetType;

    // Discount
    private JCheckBox cbManualDiscount;
    private JTextField tfDiscountPercent;

    private JButton btnBook;

    /**
     * Constructs the InteractiveBookingWindow with the given flight booking system.
     * Initializes the GUI components and event listeners.
     * 
     * @param system the FlightBookingSystem instance to interact with
     */

    public InteractiveBookingWindow(FlightBookingSystem system) {
        this.system = system;
        setTitle("Interactive Booking");
        setSize(850, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180)); // Steel Blue
        JLabel headerLabel = new JLabel("Interactive Flight Booking System");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel with scroll
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245)); // light gray
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Customer Type selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JPanel customerTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerTypePanel.setBackground(new Color(245, 245, 245));
        rbNewCustomer = new JRadioButton("New Customer");
        rbExistingCustomer = new JRadioButton("Existing Customer");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbNewCustomer);
        bg.add(rbExistingCustomer);
        customerTypePanel.add(rbNewCustomer);
        customerTypePanel.add(rbExistingCustomer);
        mainPanel.add(customerTypePanel, gbc);

        // Existing Customer Panel
        existingCustomerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        existingCustomerPanel.setBackground(new Color(245, 245, 245));
        existingCustomerPanel.setBorder(BorderFactory.createTitledBorder("Existing Customer Details"));
        existingCustomerPanel.setPreferredSize(new Dimension(800, 70));
        existingCustomerPanel.add(new JLabel("Customer ID:"));
        tfExistingCustomerId = new JTextField(10);
        existingCustomerPanel.add(tfExistingCustomerId);
        gbc.gridy++;
        mainPanel.add(existingCustomerPanel, gbc);

        // New Customer Panel (initially hidden)
        newCustomerPanel = new JPanel(new GridBagLayout());
        newCustomerPanel.setBorder(BorderFactory.createTitledBorder("New Customer Details"));
        newCustomerPanel.setBackground(new Color(245, 245, 245));
        gbc.gridy++;
        mainPanel.add(newCustomerPanel, gbc);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(4, 4, 4, 4);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        tfName = addLabeledTextField(newCustomerPanel, "Name:", 0, 0, gbc2);
        tfPhone = addLabeledTextField(newCustomerPanel, "Phone:", 0, 1, gbc2);
        tfAge = addLabeledTextField(newCustomerPanel, "Age:", 1, 0, gbc2);
        tfAddress = addLabeledTextField(newCustomerPanel, "Address:", 1, 1, gbc2);
        tfCountry = addLabeledTextField(newCustomerPanel, "Country:", 2, 0, gbc2);
        tfPassport = addLabeledTextField(newCustomerPanel, "Passport Number:", 2, 1, gbc2);
        tfPassportExpiry = addLabeledTextField(newCustomerPanel, "Passport Expiry (YYYY-MM-DD):", 3, 0, gbc2);
        tfEmail = addLabeledTextField(newCustomerPanel, "Email:", 3, 1, gbc2);
        tfDob = addLabeledTextField(newCustomerPanel, "DOB (YYYY-MM-DD):", 4, 0, gbc2);
        tfGender = addLabeledTextField(newCustomerPanel, "Gender (M/F/Other):", 4, 1, gbc2);

        cbDisabled = new JCheckBox("Disabled?");
        cbDisabled.setBackground(new Color(245, 245, 245));
        gbc2.gridx = 0;
        gbc2.gridy = 5;
        gbc2.gridwidth = 2;
        newCustomerPanel.add(cbDisabled, gbc2);

        // Flight selection
        gbc.gridy++;
        JPanel flightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        flightPanel.setBackground(new Color(245, 245, 245));
        flightPanel.setBorder(BorderFactory.createTitledBorder("Flight Details"));
        flightPanel.setPreferredSize(new Dimension(800, 70));

        flightPanel.add(new JLabel("Select Flight:"));
        cbFlights = new JComboBox<>();
        loadFlights();
        flightPanel.add(cbFlights);

        flightPanel.add(new JLabel("Seat Class:"));
        cbSeatClass = new JComboBox<>(new String[]{"ECONOMY", "BUSINESS", "FIRST"});
        flightPanel.add(cbSeatClass);

        flightPanel.add(new JLabel("Seat Number:"));
        tfSeatNumber = new JTextField(5);
        flightPanel.add(tfSeatNumber);

        gbc.gridy++;
        mainPanel.add(flightPanel, gbc);

        // Pet section
        gbc.gridy++;
        JPanel petPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        petPanel.setBackground(new Color(245, 245, 245));
        petPanel.setBorder(BorderFactory.createTitledBorder("Pet Information"));
        petPanel.setPreferredSize(new Dimension(800, 60));

        cbHasPet = new JCheckBox("Accompanying Pet?");
        petPanel.add(cbHasPet);

        petPanel.add(new JLabel("Pet Type:"));
        cbPetType = new JComboBox<>(new String[]{"", "cat", "dog", "bird"});
        cbPetType.setEnabled(false);
        petPanel.add(cbPetType);

        gbc.gridy++;
        mainPanel.add(petPanel, gbc);

        // Discount section
        gbc.gridy++;
        JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        discountPanel.setBackground(new Color(245, 245, 245));
        discountPanel.setBorder(BorderFactory.createTitledBorder("Discount Options"));
        discountPanel.setPreferredSize(new Dimension(800, 60));

        cbManualDiscount = new JCheckBox("Apply Manual Discount?");
        discountPanel.add(cbManualDiscount);

        discountPanel.add(new JLabel("Discount %:"));
        tfDiscountPercent = new JTextField(5);
        tfDiscountPercent.setEnabled(false);
        discountPanel.add(tfDiscountPercent);

        gbc.gridy++;
        mainPanel.add(discountPanel, gbc);



        // Book button
        // Add Book button in a centered panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnBook = new JButton("Book Flight");
        btnBook.setBackground(new Color(70, 130, 180));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 16));
        buttonPanel.add(btnBook);
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        mainPanel.add(buttonPanel, gbc);

        // Initial states
        existingCustomerPanel.setVisible(false);
        newCustomerPanel.setVisible(false);

        // Listeners
        rbNewCustomer.addActionListener(e -> toggleCustomerPanels());
        rbExistingCustomer.addActionListener(e -> toggleCustomerPanels());

        cbHasPet.addActionListener(e -> cbPetType.setEnabled(cbHasPet.isSelected()));
        cbManualDiscount.addActionListener(e -> tfDiscountPercent.setEnabled(cbManualDiscount.isSelected()));

        btnBook.addActionListener(e -> doBooking());

        // Default select
        rbNewCustomer.setSelected(true);
        toggleCustomerPanels();
    }
    /**
     * Helper method to add a JLabel and JTextField pair to a given panel
     * at a specified GridBagLayout coordinate.
     * 
     * @param panel the JPanel to add components to
     * @param label the label text for the field
     * @param x the grid x position (column)
     * @param y the grid y position (row)
     * @param gbc the GridBagConstraints used for layout
     * @return the created JTextField for user input
     */

    private JTextField addLabeledTextField(JPanel panel, String label, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x * 2;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        JTextField tf = new JTextField(15);
        gbc.gridx = x * 2 + 1;
        panel.add(tf, gbc);
        return tf;
    }

    private void toggleCustomerPanels() {
        boolean newCustomer = rbNewCustomer.isSelected();
        existingCustomerPanel.setVisible(!newCustomer);
        newCustomerPanel.setVisible(newCustomer);
    }

    private void loadFlights() {
        cbFlights.removeAllItems();
        for (Flight f : system.getAllFlights()) {
            if (!f.getDepartureDate().isBefore(LocalDate.now())) {
                cbFlights.addItem(f.getId() + ": " + f.getFlightNumber() + " (" + f.getOrigin() + " -> " + f.getDestination() + ")");
            }
        }
    }
    /**
     * Performs the booking process using data entered by the user.
     * Validates input, creates or fetches customer, calculates discounts,
     * reserves the seat, creates booking record, and persists data.
     * Displays booking confirmation or error messages in the output text area.
     */

    private void doBooking() {
        try {
            Customer customer;
            if (rbExistingCustomer.isSelected()) {
                String idStr = tfExistingCustomerId.getText().trim();
                if (idStr.isEmpty()) throw new Exception("Please enter existing Customer ID.");
                int id = Integer.parseInt(idStr);
                customer = system.getCustomerById(id);
                if (customer == null) throw new Exception("Customer ID not found.");
            } else {
                String name = tfName.getText().trim();
                String phone = tfPhone.getText().trim();
                String ageStr = tfAge.getText().trim();
                String address = tfAddress.getText().trim();
                String country = tfCountry.getText().trim();
                String passport = tfPassport.getText().trim();
                String passportExpiryStr = tfPassportExpiry.getText().trim();
                String email = tfEmail.getText().trim();
                String dobStr = tfDob.getText().trim();
                String gender = tfGender.getText().trim();
                boolean disabled = cbDisabled.isSelected();

                if (name.isEmpty() || phone.isEmpty() || ageStr.isEmpty() || address.isEmpty() ||
                    country.isEmpty() || passport.isEmpty() || passportExpiryStr.isEmpty() ||
                    email.isEmpty() || dobStr.isEmpty() || gender.isEmpty()) {
                    throw new Exception("Please fill all new customer fields.");
                }

                int age = Integer.parseInt(ageStr);
                if (age <= 0) throw new Exception("Age must be positive.");

                LocalDate passportExpiry = LocalDate.parse(passportExpiryStr);
                if (!passportExpiry.isAfter(LocalDate.now())) {
                    throw new Exception("Passport expiry date must be in the future.");
                }

                LocalDate dob = LocalDate.parse(dobStr);
                customer = system.addCustomer(name, phone, age, address, country, passport,
                        passportExpiry, disabled, email, dob, gender);
            }

            if (cbFlights.getSelectedIndex() == -1) throw new Exception("Please select a flight.");
            String flightSelected = (String) cbFlights.getSelectedItem();
            int flightId = Integer.parseInt(flightSelected.split(":")[0]);
            Flight flight = system.getFlightById(flightId);

            String seatClassStr = (String) cbSeatClass.getSelectedItem();
            Booking.SeatClass seatClass = Booking.SeatClass.valueOf(seatClassStr);
            if (flight.getAvailableSeatsForClass(seatClass) <= 0) {
                throw new Exception("No seats available in " + seatClass + " class.");
            }

            String seatNumber = tfSeatNumber.getText().trim().toUpperCase();
            if (seatNumber.isEmpty()) throw new Exception("Please enter seat number.");
            if (!flight.isSeatAvailable(seatClass, seatNumber)) {
                throw new Exception("Seat " + seatNumber + " is not available.");
            }

            boolean hasPet = cbHasPet.isSelected();
            String petType = "";
            double petCharge = 0;
            if (hasPet) {
                petType = (String) cbPetType.getSelectedItem();
                if (petType == null || petType.isEmpty()) throw new Exception("Please select a pet type.");
                petCharge = 15.0;
            }

            double discountPercent = 0;
            boolean manualDiscount = false;
            if (cbManualDiscount.isSelected()) {
                String discountStr = tfDiscountPercent.getText().trim();
                if (discountStr.isEmpty()) throw new Exception("Enter discount percentage.");
                discountPercent = Double.parseDouble(discountStr);
                if (discountPercent < 0 || discountPercent > 100)
                    throw new Exception("Discount must be between 0 and 100.");
                manualDiscount = true;
            } else {
                int age = customer.getAge();
                if (age <= 12) discountPercent += 10;
                else if (age >= 60) discountPercent += 15;
                if (customer.isDisabled()) discountPercent += 5;
            }

            double basePrice = flight.getPriceForClass(seatClass);
            double discountedPrice = basePrice * (1 - discountPercent / 100);
            double finalPrice = discountedPrice + petCharge;

            Booking booking = new Booking(customer, flight, system.getSystemDate(), seatClass, finalPrice,
                    seatNumber, discountPercent, manualDiscount, petType, petCharge);
            booking.completeBooking();

            flight.reserveSeat(seatClass, seatNumber);
            flight.addBooking(booking);
            customer.addBooking(booking);
            system.getDataManager().storeData(system);

            JOptionPane.showMessageDialog(this,
                    "Booking successful!\n\n" + booking.getBookingDetails(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Booking failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Main method for quick manual testing.
     * Creates an instance of FlightBookingSystem and opens the booking window.
     * 
     * @param args command line arguments (not used)
     */
    // For quick testing:

    public static void main(String[] args) {
        FlightBookingSystem system = new FlightBookingSystem();
        SwingUtilities.invokeLater(() -> {
            new InteractiveBookingWindow(system).setVisible(true);
        });
    }
}
