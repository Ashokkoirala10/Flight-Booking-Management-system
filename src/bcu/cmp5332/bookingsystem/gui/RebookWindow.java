package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * The {@code RebookWindow} class provides a graphical user interface (GUI)
 * for rebooking flights in the Flight Booking System.
 * <p>
 * Users can input customer ID, flight ID, seat class, seat number, discount,
 * and pet details to create a new booking on an existing flight.
 * The window validates inputs, calculates pricing (including discounts and pet charges),
 * and updates the system data accordingly.
 * </p>
 * <p>
 * This class extends {@link JFrame} and implements {@link ActionListener}
 * to handle the rebooking action when the "Rebook" button is clicked.
 * </p>
 * 
 * @author 
 * @version 1.0
 */
public class RebookWindow extends JFrame implements ActionListener {

    /** The flight booking system instance used to access flights, customers, and bookings. */
    private final FlightBookingSystem fbs;

    /** Text field for entering the customer ID. */
    private final JTextField customerIdField = new JTextField();

    /** Text field for entering the flight ID. */
    private final JTextField flightIdField = new JTextField();

    /** Combo box for selecting the seat class (ECONOMY, BUSINESS, FIRST). */
    private final JComboBox<String> seatClassBox = new JComboBox<>(new String[]{"ECONOMY", "BUSINESS", "FIRST"});

    /** Text field for entering the seat number. */
    private final JTextField seatNumberField = new JTextField();

    /** Text field for entering a manual discount percentage. */
    private final JTextField discountField = new JTextField();

    /** Checkbox to indicate if the customer is bringing a pet. */
    private final JCheckBox petCheckbox = new JCheckBox("Bringing Pet");

    /** Combo box to select the pet type (cat, dog, bird, or empty). */
    private final JComboBox<String> petTypeBox = new JComboBox<>(new String[]{"", "cat", "dog", "bird"});

    /** Button to trigger the rebooking action. */
    private final JButton rebookButton = new JButton("Rebook");

    /**
     * Constructs the RebookWindow GUI for the given flight booking system instance.
     * Initializes all components, sets layout, and displays the window.
     * 
     * @param fbs the {@link FlightBookingSystem} instance to interact with system data
     */
    public RebookWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;

        setTitle("Rebook Flight");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Header label setup
        JLabel header = new JLabel("Rebook Flight");
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180));
        header.setPreferredSize(new Dimension(400, 50));

        // Form panel setup
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(customerIdField);

        formPanel.add(new JLabel("Flight ID:"));
        formPanel.add(flightIdField);

        formPanel.add(new JLabel("Seat Class:"));
        formPanel.add(seatClassBox);

        formPanel.add(new JLabel("Seat Number:"));
        formPanel.add(seatNumberField);

        formPanel.add(new JLabel("Manual Discount %:"));
        formPanel.add(discountField);

        formPanel.add(new JLabel("Pet Accompanying?"));
        formPanel.add(petCheckbox);

        formPanel.add(new JLabel("Pet Type (cat/dog/bird):"));
        formPanel.add(petTypeBox);

        // Button panel setup
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.add(rebookButton);
        rebookButton.addActionListener(this);

        // Layout setup
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Handles the action event when the rebook button is clicked.
     * <p>
     * Validates input data, checks flight and customer availability,
     * calculates pricing including discounts and pet charges,
     * creates a new booking, updates flight and customer data,
     * persists the booking to a file, and provides user feedback.
     * </p>
     * 
     * @param e the {@link ActionEvent} triggered by clicking the rebook button
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            int flightId = Integer.parseInt(flightIdField.getText().trim());
            Booking.SeatClass seatClass = Booking.SeatClass.valueOf(seatClassBox.getSelectedItem().toString());
            String seatNumber = seatNumberField.getText().trim().toUpperCase();

            boolean hasPet = petCheckbox.isSelected();
            String petType = petTypeBox.getSelectedItem().toString().toLowerCase();
            double petCharge = (hasPet && (petType.equals("cat") || petType.equals("dog") || petType.equals("bird"))) ? 15.0 : 0.0;

            Customer customer = fbs.getCustomerById(customerId);
            if (customer == null) throw new FlightBookingSystemException(" Customer not found.");

            Flight flight = fbs.getFlightById(flightId);
            if (flight == null) throw new FlightBookingSystemException(" Flight not found.");

            LocalDate nowDate = fbs.getSystemDate();
            LocalTime nowTime = LocalTime.now();
            if (flight.getDepartureDate().isBefore(nowDate) ||
                (flight.getDepartureDate().isEqual(nowDate) && flight.getDepartureTime().isBefore(nowTime))) {
                throw new FlightBookingSystemException(" Flight has already departed.");
            }

            for (Booking b : customer.getBookings()) {
                if (b.getFlight().getId() == flightId) {
                    throw new FlightBookingSystemException(" Customer already has a booking for this flight.");
                }
            }

            if (flight.getAvailableSeatsForClass(seatClass) <= 0) {
                throw new FlightBookingSystemException(" No seats available in selected class.");
            }

            if (!flight.isSeatAvailable(seatClass, seatNumber)) {
                throw new FlightBookingSystemException(" Seat not available.");
            }

            double basePrice = flight.getPriceForClass(seatClass);
            String discountInput = discountField.getText().trim();
            double discountPercent = 0.0;
            boolean manual = false;

            if (discountInput.isEmpty()) {
                int age = customer.getAge();
                if (age <= 12) discountPercent += 10.0;
                else if (age >= 60) discountPercent += 15.0;
                if (customer.isDisabled()) discountPercent += 5.0;
            } else {
                manual = true;
                try {
                    discountPercent = Double.parseDouble(discountInput);
                    if (discountPercent < 0 || discountPercent > 100) {
                        throw new FlightBookingSystemException(" Discount must be 0–100.");
                    }
                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException(" Invalid discount input.");
                }
            }

            double discounted = basePrice * (1 - discountPercent / 100);
            double finalPrice = discounted + petCharge + 10.0;

            Booking booking = new Booking(customer, flight, nowDate, seatClass, finalPrice,
                    seatNumber, discountPercent, manual, petType, petCharge);
            booking.completeBooking();

            flight.reserveSeat(seatClass, seatNumber);
            flight.addBooking(booking);
            customer.addBooking(booking);

            // Persist booking to file
            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                    new java.io.FileWriter("./resources/data/bookings.txt", true))) {
                String line = booking.getBookingId() + "::" +
                              customer.getId() + "::" +
                              flight.getId() + "::" +
                              booking.getBookingDate() + "::" +
                              seatClass + "::" +
                              finalPrice + "::" +
                              booking.getStatus() + "::" +
                              seatNumber + "\n";
                writer.write(line);
            }

            StringBuilder msg = new StringBuilder();
            msg.append("✅ Booking rebooked successfully.\n\n");

            msg.append(String.format("Base Price: $%.2f\n", basePrice));

            if (discountPercent > 0) {
                msg.append(String.format("Discount Applied: %.1f%%\n", discountPercent));
                msg.append(String.format("Price After Discount: $%.2f\n", discounted));
            } else {
                msg.append("No Discount Applied.\n");
                msg.append(String.format("Price After Discount: $%.2f\n", discounted));
            }

            if (petCharge > 0) {
                msg.append(String.format("Pet Charge: $%.2f\n", petCharge));
            }
            msg.append("Rebooking Fee: $10.00\n");
            msg.append(String.format("Final Price: $%.2f", finalPrice));

            JOptionPane.showMessageDialog(this,
                    msg.toString(),
                    "Rebooking Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            this.dispose();

        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, " Please enter valid numeric values.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
