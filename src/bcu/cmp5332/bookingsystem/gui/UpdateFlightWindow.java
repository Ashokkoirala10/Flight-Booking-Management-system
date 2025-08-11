package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.UpdateFlight;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
/**
 * GUI window for updating flight details in the Flight Booking System.
 * <p>
 * This window presents a form populated with the current details of the selected flight.
 * The user can modify any values and update the flight record.
 * </p>
 *
 * @author ashok
 */
public class UpdateFlightWindow extends JFrame implements ActionListener {

    private final FlightBookingSystem fbs;
    private final Flight flight;

    private final JTextField flightNumberField = new JTextField();
    private final JTextField originField = new JTextField();
    private final JTextField destinationField = new JTextField();
    private final JTextField departureDateField = new JTextField(); // Format: YYYY-MM-DD
    private final JTextField departureTimeField = new JTextField(); // Format: HH:mm
    private final JTextField arrivalDateField = new JTextField();   // Format: YYYY-MM-DD
    private final JTextField arrivalTimeField = new JTextField();   // Format: HH:mm
    private final JTextField economyCapacityField = new JTextField();
    private final JTextField businessCapacityField = new JTextField();
    private final JTextField firstCapacityField = new JTextField();
    private final JTextField economyPriceField = new JTextField();
    private final JTextField businessPriceField = new JTextField();
    private final JTextField firstPriceField = new JTextField();
    private final JComboBox<String> statusCombo = new JComboBox<>(new String[]{"SCHEDULED", "CANCELLED", "DELAYED", "COMPLETED"});
    private final JTextField airlineNameField = new JTextField();
    private final JCheckBox internationalCheckBox = new JCheckBox("International Flight");

    private final JButton updateButton = new JButton("Update Flight");

    public UpdateFlightWindow(FlightBookingSystem fbs, Flight flight) {
        this.fbs = fbs;
        this.flight = flight;

        setTitle("Update Flight");
        setSize(500, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Header
        JLabel header = new JLabel("Update Flight");
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180)); // Steel blue
        header.setPreferredSize(new Dimension(500, 50));

        // Form panel (Grid 16 rows x 2 columns)
        JPanel formPanel = new JPanel(new GridLayout(16, 2, 10, 10));
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Add labels and fields
        formPanel.add(new JLabel("Flight Number:"));
        formPanel.add(flightNumberField);

        formPanel.add(new JLabel("Origin:"));
        formPanel.add(originField);

        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destinationField);

        formPanel.add(new JLabel("Departure Date (YYYY-MM-DD):"));
        formPanel.add(departureDateField);

        formPanel.add(new JLabel("Departure Time (HH:mm):"));
        formPanel.add(departureTimeField);

        formPanel.add(new JLabel("Arrival Date (YYYY-MM-DD):"));
        formPanel.add(arrivalDateField);

        formPanel.add(new JLabel("Arrival Time (HH:mm):"));
        formPanel.add(arrivalTimeField);

        formPanel.add(new JLabel("Economy Capacity:"));
        formPanel.add(economyCapacityField);

        formPanel.add(new JLabel("Business Capacity:"));
        formPanel.add(businessCapacityField);

        formPanel.add(new JLabel("First Class Capacity:"));
        formPanel.add(firstCapacityField);

        formPanel.add(new JLabel("Economy Price:"));
        formPanel.add(economyPriceField);

        formPanel.add(new JLabel("Business Price:"));
        formPanel.add(businessPriceField);

        formPanel.add(new JLabel("First Class Price:"));
        formPanel.add(firstPriceField);

        formPanel.add(new JLabel("Flight Status:"));
        formPanel.add(statusCombo);

        formPanel.add(new JLabel("Airline Name:"));
        formPanel.add(airlineNameField);

        formPanel.add(new JLabel("Flight Type:"));
        formPanel.add(internationalCheckBox);

        // Bottom button panel (button centered)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.add(updateButton);

        // Fill fields with current flight data
        populateFields();

        updateButton.addActionListener(this);

        // Layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void populateFields() {
        flightNumberField.setText(flight.getFlightNumber());
        originField.setText(flight.getOrigin());
        destinationField.setText(flight.getDestination());
        departureDateField.setText(flight.getDepartureDate().toString());
        if (flight.getDepartureTime() != null) {
            departureTimeField.setText(flight.getDepartureTime().toString());
        } else {
            departureTimeField.setText("");
        }
        if (flight.getArrivalDate() != null) {
            arrivalDateField.setText(flight.getArrivalDate().toString());
        } else {
            arrivalDateField.setText("");
        }
        if (flight.getArrivalTime() != null) {
            arrivalTimeField.setText(flight.getArrivalTime().toString());
        } else {
            arrivalTimeField.setText("");
        }
        economyCapacityField.setText(String.valueOf(flight.getCapacityForClass(Booking.SeatClass.ECONOMY)));
        businessCapacityField.setText(String.valueOf(flight.getCapacityForClass(Booking.SeatClass.BUSINESS)));
        firstCapacityField.setText(String.valueOf(flight.getCapacityForClass(Booking.SeatClass.FIRST)));
        economyPriceField.setText(String.format("%.2f", flight.getPriceForClass(Booking.SeatClass.ECONOMY)));
        businessPriceField.setText(String.format("%.2f", flight.getPriceForClass(Booking.SeatClass.BUSINESS)));
        firstPriceField.setText(String.format("%.2f", flight.getPriceForClass(Booking.SeatClass.FIRST)));
        statusCombo.setSelectedItem(flight.getStatus().name());
        airlineNameField.setText(flight.getAirlineName());
        internationalCheckBox.setSelected(flight.getInternational());
    }
    /**
     * Handles the action event triggered when the "Update Flight" button is clicked.
     * <p>
     * Gathers form input, validates and converts it to appropriate types, and then
     * creates and executes an {@link UpdateFlight} command to update the flight details.
     * </p>
     *
     * Shows a success or error message based on the outcome.
     *
     * @param e the event triggered by clicking the button
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // Parse inputs (nullable if blank)
            String newFlightNumber = emptyToNull(flightNumberField.getText());
            String newOrigin = emptyToNull(originField.getText());
            String newDestination = emptyToNull(destinationField.getText());
            LocalDate newDepartureDate = null;
            if (!departureDateField.getText().trim().isEmpty()) {
                newDepartureDate = LocalDate.parse(departureDateField.getText().trim());
            }
            LocalTime newDepartureTime = null;
            if (!departureTimeField.getText().trim().isEmpty()) {
                newDepartureTime = LocalTime.parse(departureTimeField.getText().trim());
            }
            LocalDate newArrivalDate = null;
            if (!arrivalDateField.getText().trim().isEmpty()) {
                newArrivalDate = LocalDate.parse(arrivalDateField.getText().trim());
            }
            LocalTime newArrivalTime = null;
            if (!arrivalTimeField.getText().trim().isEmpty()) {
                newArrivalTime = LocalTime.parse(arrivalTimeField.getText().trim());
            }

            Integer newEconomyCapacity = parseIntegerOrNull(economyCapacityField.getText());
            Integer newBusinessCapacity = parseIntegerOrNull(businessCapacityField.getText());
            Integer newFirstCapacity = parseIntegerOrNull(firstCapacityField.getText());
            Double newEconomyPrice = parseDoubleOrNull(economyPriceField.getText());
            Double newBusinessPrice = parseDoubleOrNull(businessPriceField.getText());
            Double newFirstPrice = parseDoubleOrNull(firstPriceField.getText());
            FlightStatus newStatus = FlightStatus.valueOf(statusCombo.getSelectedItem().toString());
            String newAirlineName = emptyToNull(airlineNameField.getText());
            Boolean newIsInternational = internationalCheckBox.isSelected();

            UpdateFlight updateFlightCmd = new UpdateFlight(
                flight.getId(),
                newFlightNumber,
                newOrigin,
                newDestination,
                newDepartureDate,
                newEconomyCapacity,
                newBusinessCapacity,
                newFirstCapacity,
                newEconomyPrice,
                newBusinessPrice,
                newFirstPrice,
                newStatus,
                newAirlineName,
                newIsInternational,
                newArrivalDate,
                newArrivalTime,
                newDepartureTime
            );

            updateFlightCmd.execute(fbs);

            JOptionPane.showMessageDialog(this,
                "Flight updated successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private Integer parseIntegerOrNull(String s) {
        try {
            return s == null || s.trim().isEmpty() ? null : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + s);
        }
    }
    /**
     * Attempts to parse a string into a Double, or returns null if the string is blank.
     *
     * @param s The string to parse.
     * @return Parsed Double value or null.
     * @throws IllegalArgumentException if the string is not a valid decimal number.
     */
    private Double parseDoubleOrNull(String s) {
        try {
            return s == null || s.trim().isEmpty() ? null : Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid decimal number: " + s);
        }
    }
}
