package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Booking.SeatClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A GUI window to add a new flight to the FlightBookingSystem.
 * <p>
 * This window allows the user to input details about a flight including flight number,
 * airline name, origin, destination, departure and arrival dates and times,
 * seat capacities and prices for different classes, and whether the flight is international.
 * <p>
 * Upon submission, inputs are validated and if valid, the flight is added to the system.
 */
public class AddFlightWindow extends JFrame {

    /** The flight booking system model instance to add flights to. */
    private final FlightBookingSystem flightBookingSystem;

    /** The main window reference to update UI if necessary. */
    private final MainWindow mainWindow;

    // Input fields for flight details
    private JTextField flightNumberField;
    private JTextField airlineNameField;
    private JTextField originField;
    private JTextField destinationField;
    private JTextField departureDateField;
    private JTextField departureTimeField;
    private JTextField arrivalDateField;
    private JTextField arrivalTimeField;
    private JTextField economyCapacityField;
    private JTextField businessCapacityField;
    private JTextField firstClassCapacityField;
    private JTextField economyPriceField;
    private JTextField businessPriceField;
    private JTextField firstClassPriceField;
    private JCheckBox internationalCheckBox;

    /**
     * Constructs the AddFlightWindow.
     * <p>
     * Sets up the JFrame and all GUI components including text fields, labels, checkboxes, and buttons.
     * Binds the action listener to the add button to handle flight addition.
     *
     * @param fbs        the flight booking system instance to which flights will be added
     * @param mainWindow the main application window (for context or updates)
     */
    public AddFlightWindow(FlightBookingSystem fbs, MainWindow mainWindow) {
        this.flightBookingSystem = fbs;
        this.mainWindow = mainWindow;

        setTitle("Add New Flight");
        setSize(550, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Header title label configuration
        JLabel header = new JLabel("Add New Flight");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180)); // Steel blue
        header.setPreferredSize(new Dimension(550, 40));

        // Panel and layout setup
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 250, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add all input fields with labels
        int y = 0;
        flightNumberField = addField(panel, "Flight Number:", gbc, y++);
        airlineNameField = addField(panel, "Airline Name:", gbc, y++);
        originField = addField(panel, "Origin:", gbc, y++);
        destinationField = addField(panel, "Destination:", gbc, y++);
        departureDateField = addField(panel, "Departure Date (YYYY-MM-DD):", gbc, y++);
        departureTimeField = addField(panel, "Departure Time (HH:mm):", gbc, y++);
        arrivalDateField = addField(panel, "Arrival Date (YYYY-MM-DD):", gbc, y++);
        arrivalTimeField = addField(panel, "Arrival Time (HH:mm):", gbc, y++);
        economyCapacityField = addField(panel, "Economy Capacity:", gbc, y++);
        businessCapacityField = addField(panel, "Business Capacity:", gbc, y++);
        firstClassCapacityField = addField(panel, "First Class Capacity:", gbc, y++);
        economyPriceField = addField(panel, "Economy Price:", gbc, y++);
        businessPriceField = addField(panel, "Business Price:", gbc, y++);
        firstClassPriceField = addField(panel, "First Class Price:", gbc, y++);

        // International checkbox
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("International Flight:"), gbc);
        gbc.gridx = 1;
        internationalCheckBox = new JCheckBox("Yes");
        internationalCheckBox.setBackground(new Color(245, 250, 255));
        panel.add(internationalCheckBox, gbc);

        // Add button configuration
        JButton addButton = new JButton("Add Flight");
        addButton.setPreferredSize(new Dimension(120, 30));
        addButton.setBackground(new Color(100, 150, 255));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new AddFlightListener());

        // Button panel setup
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.add(addButton);

        // Layout container setup
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Helper method to add a label and text field to the panel at the specified grid y position.
     *
     * @param panel the panel to add components to
     * @param label the label text
     * @param gbc   the GridBagConstraints object for layout positioning
     * @param y     the row number to place the components
     * @return the created JTextField component
     */
    private JTextField addField(JPanel panel, String label, GridBagConstraints gbc, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JTextField textField = new JTextField(20);
        panel.add(textField, gbc);
        return textField;
    }

    /**
     * Action listener class for handling the add flight button click event.
     * <p>
     * Validates input fields, parses and converts text inputs to appropriate data types,
     * adds the new flight to the system if validation passes, or shows error dialogs otherwise.
     */
    private class AddFlightListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                String flightNumber = flightNumberField.getText().trim();
                String airlineName = airlineNameField.getText().trim();
                String origin = originField.getText().trim();
                String destination = destinationField.getText().trim();

                LocalDate departureDate = LocalDate.parse(departureDateField.getText().trim());
                LocalTime departureTime = LocalTime.parse(departureTimeField.getText().trim(), timeFormatter);
                LocalDateTime departureDateTime = LocalDateTime.of(departureDate, departureTime);

                if (departureDateTime.isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Departure must be in the future.");
                }

                LocalDate arrivalDate = LocalDate.parse(arrivalDateField.getText().trim());
                LocalTime arrivalTime = LocalTime.parse(arrivalTimeField.getText().trim(), timeFormatter);
                LocalDateTime arrivalDateTime = LocalDateTime.of(arrivalDate, arrivalTime);

                if (!arrivalDateTime.isAfter(departureDateTime)) {
                    throw new IllegalArgumentException("Arrival must be after departure.");
                }

                int economyCapacity = parsePositiveInt(economyCapacityField.getText(), "Economy Capacity");
                int businessCapacity = parsePositiveInt(businessCapacityField.getText(), "Business Capacity");
                int firstClassCapacity = parsePositiveInt(firstClassCapacityField.getText(), "First Class Capacity");

                double economyPrice = parsePositiveDouble(economyPriceField.getText(), "Economy Price");
                double businessPrice = parsePositiveDouble(businessPriceField.getText(), "Business Price");
                double firstClassPrice = parsePositiveDouble(firstClassPriceField.getText(), "First Class Price");

                boolean isInternational = internationalCheckBox.isSelected();

                // Add flight to the booking system
                Flight flight = flightBookingSystem.addFlight(
                        flightNumber, airlineName, origin, destination,
                        departureDate, departureTime, arrivalTime, arrivalDate,
                        isInternational
                );

                // Set capacities and prices for different seat classes
                flight.setCapacityForClass(SeatClass.ECONOMY, economyCapacity);
                flight.setCapacityForClass(SeatClass.BUSINESS, businessCapacity);
                flight.setCapacityForClass(SeatClass.FIRST, firstClassCapacity);

                flight.setPriceForClass(SeatClass.ECONOMY, economyPrice);
                flight.setPriceForClass(SeatClass.BUSINESS, businessPrice);
                flight.setPriceForClass(SeatClass.FIRST, firstClassPrice);

                JOptionPane.showMessageDialog(AddFlightWindow.this, "Flight added successfully.");

                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(AddFlightWindow.this,
                        "Error: " + ex.getMessage(),
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * Parses the input text as a non-negative integer.
         *
         * @param text      the text to parse
         * @param fieldName the name of the field (used in error messages)
         * @return the parsed integer value
         * @throws IllegalArgumentException if the value is negative or invalid
         */
        private int parsePositiveInt(String text, String fieldName) {
            int value = Integer.parseInt(text.trim());
            if (value < 0) throw new IllegalArgumentException(fieldName + " must be non-negative.");
            return value;
        }

        /**
         * Parses the input text as a non-negative double.
         *
         * @param text      the text to parse
         * @param fieldName the name of the field (used in error messages)
         * @return the parsed double value
         * @throws IllegalArgumentException if the value is negative or invalid
         */
        private double parsePositiveDouble(String text, String fieldName) {
            double value = Double.parseDouble(text.trim());
            if (value < 0) throw new IllegalArgumentException(fieldName + " must be non-negative.");
            return value;
        }
    }
}
