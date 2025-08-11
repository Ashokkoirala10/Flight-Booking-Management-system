package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI window for editing an existing booking in the Flight Booking System.
 * <p>
 * This window allows the user to modify details of a booking such as booking date,
 * flight, status, seat class, seat number, price, and pet details. It performs validation,
 * updates the booking in memory, and provides user feedback via dialog messages and status label.
 * </p>
 * <p>
 * When changes are saved, an update fee is added to the booking price. Pet charges are
 * handled appropriately if the user indicates a pet is accompanying.
 * </p>
 *
 * @see FlightBookingSystem
 * @see Booking
 * @see Customer
 */
public class EditBookingWindow extends JFrame implements ActionListener {

    private final FlightBookingSystem flightBookingSystem;
    private final int bookingId;
    private Booking booking;

    private final JTextField seatNumberField = new JTextField();
    private final JTextField bookingDateField = new JTextField();
    private final JComboBox<String> statusCombo = new JComboBox<>(new String[]{"ACTIVE", "CANCELLED", "COMPLETED"});
    private final JComboBox<String> seatClassCombo = new JComboBox<>(new String[]{"ECONOMY", "BUSINESS", "FIRST"});
    private final JTextField priceField = new JTextField();
    private final JCheckBox petCheckBox = new JCheckBox("Pet Accompanying");
    private final JComboBox<String> petTypeCombo = new JComboBox<>(new String[]{"", "cat", "dog", "bird"});
    private final JTextField newFlightIdField = new JTextField();
    private final JButton saveButton = new JButton("Save Changes");
    private final JLabel statusLabel = new JLabel(" ");

    /**
     * Constructs the EditBookingWindow for the given booking ID within the specified flight booking system.
     * <p>
     * If the booking ID does not correspond to an existing booking, an error message is shown
     * and the window closes.
     * </p>
     *
     * @param flightBookingSystem the flight booking system managing flights and bookings
     * @param bookingId           the unique identifier of the booking to edit
     */
    public EditBookingWindow(FlightBookingSystem flightBookingSystem, int bookingId) {
        this.flightBookingSystem = flightBookingSystem;
        this.bookingId = bookingId;

        booking = findBookingById(flightBookingSystem, bookingId);
        if (booking == null) {
            JOptionPane.showMessageDialog(this, "Booking not found.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setTitle("Edit Booking");
        setSize(450, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Header styling and form initialization omitted for brevity

        // Setup form fields with booking data, add listeners and layout components

        setVisible(true);
    }

    /**
     * Handles the action event triggered when the "Save Changes" button is pressed.
     * <p>
     * Validates and updates booking fields with user input, including booking date,
     * flight, status, seat class, seat number, price, and pet details. Adds an update fee
     * to the booking price. Displays success or error messages accordingly.
     * </p>
     *
     * @param e the action event triggered by the save button click
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // Parse and update booking date if provided
            if (!bookingDateField.getText().trim().isEmpty()) {
                booking.setBookingDate(java.time.LocalDate.parse(bookingDateField.getText().trim()));
            }

            // Update flight if new valid flight ID provided
            String newFlightId = newFlightIdField.getText().trim();
            if (!newFlightId.isEmpty()) {
                Flight newFlight = flightBookingSystem.getFlightById(Integer.parseInt(newFlightId));
                if (newFlight != null) {
                    booking.setFlight(newFlight);
                } else {
                    statusLabel.setText("Invalid Flight ID.");
                    statusLabel.setForeground(Color.RED);
                    return;
                }
            }

            // Update other booking fields from user input
            booking.setStatus(Booking.Status.valueOf(statusCombo.getSelectedItem().toString()));
            booking.setSeatClass(Booking.SeatClass.valueOf(seatClassCombo.getSelectedItem().toString()));
            booking.setSeatNumber(seatNumberField.getText().trim());

            if (!priceField.getText().trim().isEmpty()) {
                booking.setPrice(Double.parseDouble(priceField.getText().trim()));
            }

            // Handle pet details and pet charge adjustments
            if (petCheckBox.isSelected()) {
                String petType = petTypeCombo.getSelectedItem().toString().toLowerCase();
                if (petType.equals("cat") || petType.equals("dog") || petType.equals("bird")) {
                    booking.setPetType(petType);
                    if (booking.getPetCharge() == 0.0) {
                        booking.setPetCharge(15.0);
                        booking.setPrice(booking.getPrice() + 15.0);
                    }
                } else {
                    statusLabel.setText("Invalid pet type.");
                    statusLabel.setForeground(Color.RED);
                    return;
                }
            } else {
                if (booking.getPetCharge() > 0.0) {
                    booking.setPrice(booking.getPrice() - booking.getPetCharge());
                    booking.setPetCharge(0.0);
                    booking.setPetType("");
                }
            }

            // Apply update fee
            final double updateFee = 10.0;
            booking.setPrice(booking.getPrice() + updateFee);

            JOptionPane.showMessageDialog(this,
                    String.format("Booking updated successfully.\n$%.2f update fee added.", updateFee),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            statusLabel.setForeground(new Color(0, 128, 0));
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    /**
     * Searches for a booking by its ID in the given flight booking system.
     *
     * @param system the flight booking system to search in
     * @param id     the booking ID to find
     * @return the Booking object if found; otherwise, {@code null}
     */
    private Booking findBookingById(FlightBookingSystem system, int id) {
        for (Customer customer : system.getAllCustomers()) {
            for (Booking b : customer.getBookings()) {
                if (b.getBookingId() == id) {
                    return b;
                }
            }
        }
        return null;
    }
}
