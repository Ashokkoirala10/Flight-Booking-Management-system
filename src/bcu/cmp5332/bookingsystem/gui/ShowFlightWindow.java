package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
/**
 * The {@code ShowFlightWindow} class creates a GUI window that allows the user
 * to view detailed information about a flight, including its passengers, by
 * entering the Flight ID.
 *
 * <p>This window is part of the Flight Booking System GUI and uses a reference
 * to the main {@link FlightBookingSystem} to access flight data.</p>
 * 
 * <p>Upon entering a valid flight ID, the user is shown all details related
 * to the flight, including destination, date, available seats, and passenger
 * bookings.</p>
 * 
 * @author Ashok
 */
public class ShowFlightWindow extends JFrame {

    private final FlightBookingSystem flightBookingSystem;
    private JTextField flightIdField;
    /**
     * Constructs a new {@code ShowFlightWindow} instance using the provided
     * {@link FlightBookingSystem}.
     *
     * @param flightBookingSystem the flight booking system instance to use for retrieving flight data
     */
    public ShowFlightWindow(FlightBookingSystem flightBookingSystem) {
        this.flightBookingSystem = flightBookingSystem;
        initialize();
    }
    /**
     * Initializes the GUI window, setting up the layout, input fields,
     * labels, and search button.
     */
    private void initialize() {
        setTitle(" Show Flight Details");
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Header Label
        JLabel header = new JLabel(" Show Flight Details", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(new Color(25, 25, 112));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // Main panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Input panel for flight ID
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(240, 248, 255));

        JLabel idLabel = new JLabel("Flight ID:");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        flightIdField = new JTextField(8);
        flightIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        flightIdField.setPreferredSize(new Dimension(100, 28));

        inputPanel.add(idLabel);
        inputPanel.add(flightIdField);

        mainPanel.add(inputPanel);

        // Search button
        JButton showBtn = new JButton("Search ");
        showBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        showBtn.setBackground(new Color(70, 130, 180));
        showBtn.setForeground(Color.WHITE);
        showBtn.setFocusPainted(false);
        showBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        showBtn.setPreferredSize(new Dimension(120, 35));
        showBtn.addActionListener(this::showFlightDetails);

        mainPanel.add(showBtn);

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }
    /**
     * Displays the flight details when the "Search" button is clicked.
     * Validates the input, retrieves the flight from the system, and shows
     * a dialog with the flight details.
     *
     * @param e the {@link ActionEvent} triggered by the search button click
     */
    private void showFlightDetails(ActionEvent e) {
        try {
            String input = flightIdField.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Flight ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int flightId = Integer.parseInt(input);
            Flight flight = flightBookingSystem.getFlightById(flightId);

            if (flight == null) {
                JOptionPane.showMessageDialog(this, "Flight not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String flightDetails = flight.getFlightDetailsWithPassengers();

            JTextArea textArea = new JTextArea(flightDetails);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(550, 350));

            JOptionPane.showMessageDialog(this, scrollPane, "Flight Details", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Flight ID must be a valid number.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
