package bcu.cmp5332.bookingsystem.gui;



import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A JPanel for removing a flight from the FlightBookingSystem by flight ID.
 * Displays a text field to enter the flight ID, a button to remove the flight,
 * and uses dialog boxes to show error or success messages.
 */
public class RemoveFlightPanel extends JPanel {

    private final FlightBookingSystem flightBookingSystem;
    private final JTextField flightIdField = new JTextField(10); // Set preferred width

    /**
     * Constructs the RemoveFlightPanel with the given FlightBookingSystem.
     *
     * @param flightBookingSystem the flight booking system instance to operate on
     */
    public RemoveFlightPanel(FlightBookingSystem flightBookingSystem) {
        this.flightBookingSystem = flightBookingSystem;

        setLayout(new BorderLayout());

        // Header label
        JLabel header = new JLabel("Remove Flight");
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180)); // Steel blue
        header.setPreferredSize(new Dimension(400, 50));
        add(header, BorderLayout.NORTH);

        // Panel with FlowLayout for Flight ID label + text field horizontally
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(240, 248, 255)); // Light background
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel label = new JLabel("Flight ID:");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(label);

        flightIdField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(flightIdField);

        add(inputPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255)); // Light blueish background
        JButton removeButton = new JButton("Remove Flight");
        removeButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(removeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button action
        removeButton.addActionListener(e -> removeFlight());
    }

    /**
     * Attempts to remove the flight corresponding to the entered flight ID.
     * Shows popup dialogs for errors or success messages.
     */
    private void removeFlight() {
        String input = flightIdField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Flight ID.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int flightId = Integer.parseInt(input);
            Flight flight = flightBookingSystem.getFlightById(flightId);
            if (flight == null) {
                JOptionPane.showMessageDialog(this, "Flight with ID " + flightId + " not found.", "Flight Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            flightBookingSystem.removeFlight(flight);
            JOptionPane.showMessageDialog(this, "Flight with ID " + flightId + " has been removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            flightIdField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Flight ID. Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}