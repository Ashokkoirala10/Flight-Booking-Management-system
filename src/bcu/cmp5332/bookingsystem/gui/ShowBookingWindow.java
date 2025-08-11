package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
/**
 * The {@code ShowBookingWindow} class provides a GUI window that allows
 * the user to search for and display all bookings associated with a given
 * customer by entering the customer's ID.
 * <p>
 * This window is part of the graphical user interface for the
 * Flight Booking System and interacts with the {@link FlightBookingSystem}
 * to retrieve booking details.
 * </p>
 * 
 * @author Ashok
 */
public class ShowBookingWindow extends JFrame {

    private final FlightBookingSystem flightBookingSystem;
    private JTextField customerIdField;

    public ShowBookingWindow(FlightBookingSystem flightBookingSystem) {
        this.flightBookingSystem = flightBookingSystem;
        initialize();
    }
    /**
     * Initializes the GUI components for the window, including labels,
     * input fields, and the search button.
     */
    private void initialize() {
        setTitle(" Show Bookings");
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Header
        JLabel header = new JLabel(" Show Customer Bookings", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(new Color(25, 25, 112));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // Main panel with compact layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Input row
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(240, 248, 255));

        JLabel idLabel = new JLabel("Customer ID:");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        customerIdField = new JTextField(8);  // Compact width
        customerIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        customerIdField.setPreferredSize(new Dimension(100, 28));

        inputPanel.add(idLabel);
        inputPanel.add(customerIdField);

        mainPanel.add(inputPanel);

        // Search Button
        JButton showBtn = new JButton("Search ");
        showBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        showBtn.setBackground(new Color(70, 130, 180));
        showBtn.setForeground(Color.WHITE);
        showBtn.setFocusPainted(false);
        showBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        showBtn.setPreferredSize(new Dimension(120, 35));
        showBtn.addActionListener(this::showBookings);

        mainPanel.add(showBtn);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }
    /**
     * Event handler that is triggered when the user clicks the "Search" button.
     * It attempts to retrieve and display the bookings for the customer whose ID
     * is entered in the text field.
     *
     * @param e the action event triggered by clicking the button
     */
    private void showBookings(ActionEvent e) {
        try {
            String input = customerIdField.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a customer ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int customerId = Integer.parseInt(input);
            Customer customer = flightBookingSystem.getCustomerById(customerId);

            if (customer == null) {
                JOptionPane.showMessageDialog(this, "Customer not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder result = new StringBuilder("📄 Bookings for: " + customer.getName() + "\n\n");
            List<Booking> bookings = customer.getBookings();

            if (bookings.isEmpty()) {
                result.append("⚠ No bookings found.");
            } else {
                for (Booking booking : bookings) {
                    result.append(booking.getBookingDetails()).append("\n\n");
                }
            }

            JTextArea textArea = new JTextArea(result.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Booking Details", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Customer ID must be a number.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
