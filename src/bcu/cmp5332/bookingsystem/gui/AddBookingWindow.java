package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddBooking;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI window that allows users to add a new booking to the flight booking system.
 * The window collects input such as customer ID, flight ID, seat class, seat number,
 * discount, and optional pet details, then executes the AddBooking command.
 * 
 * <p>Handles input validation and provides feedback via a status label.</p>
 * 
 * @author 
 */
public class AddBookingWindow extends JFrame implements ActionListener {

    /**
     * Reference to the FlightBookingSystem model for managing bookings.
     */
    private final FlightBookingSystem flightBookingSystem;

    // Form input fields
    private final JTextField customerIdField = new JTextField();
    private final JTextField flightIdField = new JTextField();
    private final JComboBox<String> seatClassCombo = new JComboBox<>(new String[]{"ECONOMY", "BUSINESS", "FIRST"});
    private final JTextField seatNumberField = new JTextField();
    private final JTextField discountField = new JTextField();
    private final JCheckBox petCheckBox = new JCheckBox("Pet Accompanying");
    private final JComboBox<String> petTypeCombo = new JComboBox<>(new String[]{"", "cat", "dog", "bird"});
    private final JButton addButton = new JButton("Add Booking");
    private final JLabel statusLabel = new JLabel(" ");

    /**
     * Constructs the AddBookingWindow and initializes the GUI components.
     * 
     * @param flightBookingSystem The flight booking system instance to which bookings will be added.
     */
    public AddBookingWindow(FlightBookingSystem flightBookingSystem) {
        this.flightBookingSystem = flightBookingSystem;

        setTitle("Add Booking");
        setSize(450, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Header label configuration
        JLabel header = new JLabel("Add Booking");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180)); // Steel blue
        header.setPreferredSize(new Dimension(450, 40));

        // Form panel setup with labels and input fields
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(customerIdField);

        formPanel.add(new JLabel("Flight ID:"));
        formPanel.add(flightIdField);

        formPanel.add(new JLabel("Seat Class:"));
        formPanel.add(seatClassCombo);

        formPanel.add(new JLabel("Seat Number (e.g., 12A):"));
        formPanel.add(seatNumberField);

        formPanel.add(new JLabel("Manual Discount (%):"));
        formPanel.add(discountField);

        formPanel.add(petCheckBox);
        formPanel.add(petTypeCombo);

        formPanel.add(new JLabel("")); // Placeholder for alignment
        formPanel.add(statusLabel);     // Status message label

        // Button panel at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.add(addButton);

        // Enable/disable pet type combo box based on checkbox selection
        petTypeCombo.setEnabled(false);
        petCheckBox.addActionListener(e -> petTypeCombo.setEnabled(petCheckBox.isSelected()));

        // Add action listener to Add Booking button
        addButton.addActionListener(this);

        // Setup window layout and add components
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);      // Header at top
        getContentPane().add(formPanel, BorderLayout.CENTER);  // Form center
        getContentPane().add(buttonPanel, BorderLayout.SOUTH); // Button bottom

        setVisible(true);
    }

    /**
     * Invoked when the "Add Booking" button is clicked.
     * Collects and validates input data, constructs an AddBooking command,
     * and executes it on the flight booking system.
     * Displays success or error messages in the status label.
     * 
     * @param e The action event triggered by button click.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            int flightId = Integer.parseInt(flightIdField.getText().trim());
            Booking.SeatClass seatClass = Booking.SeatClass.valueOf(seatClassCombo.getSelectedItem().toString());

            AddBooking addBookingCmd = new AddBooking(customerId, flightId, seatClass);
            Command command = addBookingCmd;

            // Simulate console inputs required by AddBooking command
            System.setIn(new java.io.ByteArrayInputStream((
                    seatNumberField.getText().trim() + "\n" +
                    discountField.getText().trim() + "\n" +
                    (petCheckBox.isSelected() ? "yes\n" + petTypeCombo.getSelectedItem().toString() + "\n" : "no\n")
            ).getBytes()));

            command.execute(flightBookingSystem);

            statusLabel.setText("Booking added successfully.");
            statusLabel.setForeground(new Color(0, 128, 0)); // Green for success
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED); // Red for errors
        }
    }
}
