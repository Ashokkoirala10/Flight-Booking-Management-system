package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.SearchFlight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;

/**
 * A GUI window that allows users to search for flights in the flight booking system.
 * <p>
 * Users can filter flights based on criteria like origin, destination, airline, flight status,
 * and departure/arrival dates. Matching results are displayed in a formatted dialog.
 */
public class SearchFlightWindow extends JFrame {
    
    /** The flight booking system instance to search within */
    private final FlightBookingSystem flightBookingSystem;

    private JTextField originField;
    private JTextField destinationField;
    private JTextField airlineField;
    private JTextField statusField;
    private JTextField startDateField;
    private JTextField endDateField;

    /**
     * Constructs and displays the SearchFlightWindow.
     *
     * @param flightBookingSystem the flight booking system to be queried
     */
    public SearchFlightWindow(FlightBookingSystem flightBookingSystem) {
        this.flightBookingSystem = flightBookingSystem;
        initialize();
    }

    /**
     * Initializes the GUI components and layout.
     */
    private void initialize() {
        setTitle("✈️ Search Flights");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panel.setBackground(new Color(240, 248, 255));

        JLabel header = new JLabel("Search Flights", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(25, 25, 112));
        add(header, BorderLayout.NORTH);

        originField = new JTextField();
        destinationField = new JTextField();
        airlineField = new JTextField();
        statusField = new JTextField();
        startDateField = new JTextField();
        endDateField = new JTextField();

        panel.add(new JLabel("Origin:"));
        panel.add(originField);

        panel.add(new JLabel("Destination:"));
        panel.add(destinationField);

        panel.add(new JLabel("Airline:"));
        panel.add(airlineField);

        panel.add(new JLabel("Status (Scheduled, Cancelled):"));
        panel.add(statusField);

        panel.add(new JLabel("Departure Start Date (YYYY-MM-DD):"));
        panel.add(startDateField);

        panel.add(new JLabel("Departure End Date (YYYY-MM-DD):"));
        panel.add(endDateField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(70, 130, 180));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        searchBtn.addActionListener(this::searchFlight);

        add(panel, BorderLayout.CENTER);
        add(searchBtn, BorderLayout.SOUTH);
        setVisible(true);
    }

    /**
     * Handles the search button click. Collects inputs, validates them, and triggers
     * the search command.
     *
     * @param e the event triggered by the button click
     */
    private void searchFlight(ActionEvent e) {
        try {
            String origin = originField.getText().trim();
            String destination = destinationField.getText().trim();
            String airline = airlineField.getText().trim();
            String statusText = statusField.getText().trim();
            String exactDepartureText = startDateField.getText().trim();
            String exactArrivalText = endDateField.getText().trim();

            if (origin.isEmpty() && destination.isEmpty() && airline.isEmpty() &&
                    statusText.isEmpty() && exactDepartureText.isEmpty() && exactArrivalText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter at least one field to perform a search.",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Convert empty strings to null
            if (origin.isEmpty()) origin = null;
            if (destination.isEmpty()) destination = null;
            if (airline.isEmpty()) airline = null;
            if (statusText.isEmpty()) statusText = null;

            LocalDate exactDeparture = exactDepartureText.isEmpty() ? null : LocalDate.parse(exactDepartureText);
            LocalDate exactArrival = exactArrivalText.isEmpty() ? null : LocalDate.parse(exactArrivalText);

            SearchFlight searchCommand = new SearchFlight(
                    origin, destination, exactDeparture, exactArrival, airline, statusText);

            SearchFlightHandler handler = new SearchFlightHandler(searchCommand, flightBookingSystem);
            handler.search();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * A helper class that performs the actual flight search logic using the
     * criteria provided in the {@link SearchFlight} command object.
     */
    class SearchFlightHandler {

        private final SearchFlight searchCommand;
        private final FlightBookingSystem flightBookingSystem;

        /**
         * Constructs a SearchFlightHandler.
         *
         * @param searchCommand the search command containing filter criteria
         * @param flightBookingSystem the system to search within
         */
        public SearchFlightHandler(SearchFlight searchCommand, FlightBookingSystem flightBookingSystem) {
            this.searchCommand = searchCommand;
            this.flightBookingSystem = flightBookingSystem;
        }

        /**
         * Performs the search using the specified criteria, filters matching flights,
         * and displays them in a scrollable dialog. Shows a message if no matches are found.
         *
         * @throws FlightBookingSystemException if an error occurs during the search
         */
        public void search() throws FlightBookingSystemException {
            List<Flight> allFlights = flightBookingSystem.getAllFlights();
            List<Flight> matched = new java.util.ArrayList<>();

            for (Flight flight : allFlights) {
                boolean match = true;

                if (searchCommand.origin != null &&
                        !flight.getOrigin().equalsIgnoreCase(searchCommand.origin))
                    match = false;

                if (searchCommand.destination != null &&
                        !flight.getDestination().equalsIgnoreCase(searchCommand.destination))
                    match = false;

                if (searchCommand.airline != null &&
                        !flight.getAirlineName().equalsIgnoreCase(searchCommand.airline))
                    match = false;

                if (searchCommand.status != null) {
                    FlightStatus flightStatus = FlightStatus.fromString(searchCommand.status);
                    if (!flight.getStatus().equals(flightStatus))
                        match = false;
                }

                if (searchCommand.exactDepartureDate != null &&
                        !flight.getDepartureDate().isEqual(searchCommand.exactDepartureDate))
                    match = false;

                if (searchCommand.exactArrivalDate != null &&
                        (flight.getArrivalDate() == null ||
                         !flight.getArrivalDate().isEqual(searchCommand.exactArrivalDate)))
                    match = false;

                if (match) matched.add(flight);
            }

            if (matched.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        " No matching flights found.",
                        "Results",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder result = new StringBuilder("\u2705 Matching Flights:\n\n");
                result.append(String.format("%-5s %-10s %-20s %-15s %-15s %-20s %-25s %-12s %-8s %-25s\n",
                        "ID", "Flight#", "Airline", "Origin", "Destination",
                        "Departure", "Arrival     ", "Status", "Type", "Fares (Eco/Bus/First)"));
                result.append("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");

                for (Flight f : matched) {
                    result.append(f.getDetailsShort()).append("\n\n");
                }

                JTextArea textArea = new JTextArea(result.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                JOptionPane.showMessageDialog(null, scrollPane,
                        "Flight Results", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
