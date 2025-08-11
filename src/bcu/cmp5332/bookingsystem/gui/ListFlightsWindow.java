package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * GUI window that displays a list of upcoming flights in a table format.
 * Provides options to refresh the flight list and view detailed flight information including passenger details.
 * 
 * <p>The flight table shows columns including flight ID, number, airline, origin, destination,
 * departure and arrival times, status, type (domestic or international), and seat class prices.</p>
 */
public class ListFlightsWindow extends JFrame {

    private final FlightBookingSystem flightBookingSystem;
    private JTable flightsTable;
    private DefaultTableModel tableModel;

    /**
     * Constructs the ListFlightsWindow with the given FlightBookingSystem instance.
     * Initializes the GUI components and loads the upcoming flights list.
     * 
     * @param flightBookingSystem the flight booking system containing flights data
     */
    public ListFlightsWindow(FlightBookingSystem flightBookingSystem) {
        this.flightBookingSystem = flightBookingSystem;
        initialize();
        loadFlights();
    }

    /**
     * Initializes the GUI components such as table, buttons, and layout.
     * Sets fonts, colors, and registers event handlers.
     */
    private void initialize() {
        setTitle(" Upcoming Flights");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Upcoming Flights", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(new Color(25, 25, 112));
        add(header, BorderLayout.NORTH);

        String[] columns = {
            "ID", "#Flight", "Airline", "Origin", "Destination",
            "Departure", "Arrival", "Status", "Type", "Prices (E/B/F)"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        flightsTable = new JTable(tableModel);
        flightsTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightsTable.getTableHeader().setReorderingAllowed(false);

        // Adjust column widths for better readability
        adjustColumnWidths();

        JScrollPane scrollPane = new JScrollPane(flightsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton refreshBtn = new JButton(" Refresh");
        styleButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadFlights());
        buttonPanel.add(refreshBtn);

        JButton detailsBtn = new JButton(" Show Details");
        styleButton(detailsBtn);
        detailsBtn.addActionListener(e -> showSelectedFlightDetails());
        buttonPanel.add(detailsBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Applies consistent styling to the provided button.
     * 
     * @param button the JButton to style
     */
    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Tahoma", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(140, 30));
    }

    /**
     * Adjusts the preferred widths of the table columns to improve layout.
     */
    private void adjustColumnWidths() {
        int[] widths = {40, 80, 140, 100, 100, 140, 140, 80, 50, 150};

        for (int i = 0; i < widths.length; i++) {
            if (i < flightsTable.getColumnModel().getColumnCount()) {
                flightsTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
        }
    }

    /**
     * Loads the list of upcoming flights from the FlightBookingSystem into the table.
     * Filters out flights that have already departed.
     * Displays a message if no upcoming flights are found.
     */
    private void loadFlights() {
        tableModel.setRowCount(0);  // Clear existing rows

        List<Flight> flights = flightBookingSystem.getAllFlights();
        LocalDateTime now = LocalDateTime.now();

        // Remove flights that departed before the current time
        flights.removeIf(flight -> LocalDateTime.of(flight.getDepartureDate(), flight.getDepartureTime()).isBefore(now));

        if (flights.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No upcoming flights available.", "No Flights", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        for (Flight f : flights) {
            String depTime = f.getDepartureTime().format(timeFormatter);
            String arrTime = f.getArrivalTime().format(timeFormatter);
            String flightType = f.getInternational() ? "INTL" : "DOM";

            tableModel.addRow(new Object[]{
                f.getId(),
                f.getFlightNumber(),
                f.getAirlineName(),
                f.getOrigin(),
                f.getDestination(),
                f.getDepartureDate() + " " + depTime,
                f.getArrivalDate() + " " + arrTime,
                f.getStatus(),
                flightType,
                String.format("$%.2f / $%.2f / $%.2f", 
                    f.getDynamicPrice(Booking.SeatClass.ECONOMY),
                    f.getDynamicPrice(Booking.SeatClass.BUSINESS),
                    f.getDynamicPrice(Booking.SeatClass.FIRST))
            });
        }
    }

    /**
     * Shows detailed information about the selected flight, including passengers.
     * If no flight is selected, shows a warning.
     * If the flight cannot be found or an error occurs, shows an error dialog.
     */
    private void showSelectedFlightDetails() {
        int selectedRow = flightsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight to see details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int flightId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            Flight flight = flightBookingSystem.getFlightById(flightId);
            if (flight != null) {
                JTextArea detailsArea = new JTextArea(flight.getFlightDetailsWithPassengers());
                detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                detailsArea.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(detailsArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "Flight Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Flight not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
