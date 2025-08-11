package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.RemoveCustomer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A GUI window that allows an administrator to remove a customer from the flight booking system.
 * <p>
 * The user must enter a valid customer ID, which will be used to remove the corresponding customer
 * along with all of their associated bookings from the system.
 */
public class RemoveCustomerWindow extends JFrame implements ActionListener {

    /** The flight booking system instance this window interacts with */
    private final FlightBookingSystem fbs;

    /** Text field for entering the customer ID */
    private final JTextField customerIdField = new JTextField();

    /** Button to trigger customer removal */
    private final JButton removeButton = new JButton("Remove Customer");

    /**
     * Constructs and displays the RemoveCustomerWindow.
     *
     * @param fbs the flight booking system instance
     */
    public RemoveCustomerWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;

        setTitle("Remove Customer");
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Header label
        JLabel header = new JLabel("Remove Customer");
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180)); // Steel blue
        header.setPreferredSize(new Dimension(400, 50));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(customerIdField);
        formPanel.add(new JLabel("")); // Spacer
        formPanel.add(new JLabel("")); // Spacer

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255));
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
     * Handles the button click event. Parses the input and attempts to remove the customer
     * and their associated bookings from the flight booking system.
     *
     * @param e the action event triggered by the button
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int customerId = parseInteger(customerIdField.getText().trim(), "Customer ID");

            RemoveCustomer cmd = new RemoveCustomer(customerId);
            cmd.execute(fbs);

            JOptionPane.showMessageDialog(this,
                    "Customer and all associated bookings removed successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            this.dispose();

        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper method to safely parse a string as an integer.
     *
     * @param s the input string
     * @param fieldName the name of the field (used in error messages)
     * @return the parsed integer
     * @throws IllegalArgumentException if the input is empty or not a valid integer
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
