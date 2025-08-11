package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.CancelBookingInMemory;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI window for cancelling an existing booking in the Flight Booking System.
 * <p>
 * This window allows the user to enter a customer ID and a flight ID, then attempts to
 * cancel the corresponding active booking in memory. It verifies inputs, handles errors,
 * and provides user feedback via dialog messages.
 * </p>
 * <p>
 * When a booking is cancelled, it updates the booking status to {@link Booking.Status#CANCELLED}
 * and removes the passenger from the flight seat allocation. The cancelled booking
 * is added to the in-memory cancelled bookings list.
 * </p>
 *
 * @see FlightBookingSystem
 * @see Booking
 * @see Customer
 * @see CancelBookingInMemory
 */
public class CancelBookingWindow extends JFrame implements ActionListener {

    private final FlightBookingSystem fbs;

    private final JTextField customerIdField = new JTextField();
    private final JTextField flightIdField = new JTextField();
    private final JButton cancelButton = new JButton("Cancel Booking");

    /**
     * Constructs the CancelBookingWindow with the given FlightBookingSystem instance.
     *
     * @param fbs the flight booking system managing customers, flights, and bookings
     */
    public CancelBookingWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;

        setTitle("Cancel Booking");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Header
        JLabel header = new JLabel("Cancel Booking");
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180));
        header.setPreferredSize(new Dimension(400, 50));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(customerIdField);
        formPanel.add(new JLabel("Flight ID:"));
        formPanel.add(flightIdField);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.add(cancelButton);
        cancelButton.addActionListener(this);

        // Layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Handles the action event triggered when the cancel button is pressed.
     * <p>
     * Parses and validates customer ID and flight ID input, checks if a valid active
     * booking exists, then cancels the booking by updating its status and removing
     * the passenger from the flight. Displays appropriate success or error dialogs.
     * </p>
     *
     * @param e the action event triggered by button click
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int customerId = parseInteger(customerIdField.getText().trim(), "Customer ID");
            int flightId = parseInteger(flightIdField.getText().trim(), "Flight ID");

            Customer customer = fbs.getCustomerById(customerId);
            if (customer == null) {
                throw new FlightBookingSystemException(" Customer ID " + customerId + " not found.");
            }

            Booking bookingToCancel = null;
            for (Booking booking : customer.getBookings()) {
                if (booking.getFlight().getId() == flightId &&
                        (booking.getStatus() == Booking.Status.ACTIVE || booking.getStatus() == Booking.Status.COMPLETED)) {
                    bookingToCancel = booking;
                    break;
                }
            }

            if (bookingToCancel == null) {
                throw new FlightBookingSystemException(" Active booking not found for customer on flight ID " + flightId + ".");
            }

            // Cancel booking in memory
            bookingToCancel.setStatus(Booking.Status.CANCELLED);
            bookingToCancel.getFlight().removePassenger(customer, bookingToCancel.getSeatClass());
            CancelBookingInMemory.cancelledBookingsMemory.add(bookingToCancel);

            JOptionPane.showMessageDialog(this,
                    " Booking cancelled in memory successfully.",
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
     * Parses a string into an integer and validates that it is not empty and is a valid integer.
     *
     * @param s         the string to parse
     * @param fieldName the name of the field, used in error messages
     * @return the parsed integer value
     * @throws IllegalArgumentException if the string is empty or cannot be parsed as an integer
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
