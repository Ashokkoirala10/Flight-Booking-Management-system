package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.CancelBookings;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI window that allows users to remove a booking by providing a customer ID and flight ID.
 * <p>
 * The window contains input fields for Customer ID and Flight ID, and a button to remove the booking.
 * Upon clicking the button, it attempts to cancel the booking in the {@link FlightBookingSystem}.
 * <p>
 * Displays success or error dialogs depending on the result.
 */
public class RemoveBookingWindow extends JFrame implements ActionListener {

    /** Reference to the flight booking system instance */
    private final FlightBookingSystem fbs;

    /** Text field for entering customer ID */
    private final JTextField customerIdField = new JTextField();

    /** Text field for entering flight ID */
    private final JTextField flightIdField = new JTextField();

    /** Button to trigger booking removal */
    private final JButton removeButton = new JButton("Remove Booking");

    /**
     * Constructs a RemoveBookingWindow for the given flight booking system.
     *
     * @param fbs the flight booking system instance to operate on
     */
    public RemoveBookingWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;

        setTitle("Remove Booking");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Header
        JLabel header = new JLabel("Remove Booking");
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180)); // Steel blue
        header.setPreferredSize(new Dimension(400, 50));

        // Form panel (Grid 3 rows x 2 columns)
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(240, 248, 255)); // same light bg color
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(customerIdField);

        formPanel.add(new JLabel("Flight ID:"));
        formPanel.add(flightIdField);

        // Empty labels for spacing to keep grid 3x2
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255)); // same as UpdateFlightWindow
        buttonPanel.add(removeButton);

        removeButton.addActionListener(this);

        // Layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Handles the click event of the remove button.
     * Attempts to parse input, execute the cancellation command, and show appropriate messages.
     *
     * @param e the action event triggered by the remove button
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int customerId = parseInteger(customerIdField.getText().trim(), "Customer ID");
            int flightId = parseInteger(flightIdField.getText().trim(), "Flight ID");

            CancelBookings cancelCommand = new CancelBookings(customerId, flightId);
            cancelCommand.execute(fbs);

            JOptionPane.showMessageDialog(this,
                    "Booking removed successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Optionally close window after successful removal
            this.dispose();

        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Parses a string to an integer.
     *
     * @param s         the string to parse
     * @param fieldName the name of the field (for error messages)
     * @return the parsed integer
     * @throws IllegalArgumentException if the input is null, empty, or not a valid integer
     */
    private int parseInteger(String s, String fieldName) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid integer.");
        }
    }
}
