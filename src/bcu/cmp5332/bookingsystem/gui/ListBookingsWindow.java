package bcu.cmp5332.bookingsystem.gui;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * GUI window that displays a list of bookings in a table format.
 * Provides filtering options and ability to view detailed information of selected bookings.
 * 
 * <p>The window shows booking information such as booking ID, customer name,
 * flight details, departure date, seat class, price, and status.</p>
 * 
 * <p>Bookings can be filtered to show all bookings or only future bookings.</p>
 * 
 * @author 
 */
public class ListBookingsWindow extends JFrame {

    private final FlightBookingSystem flightBookingSystem;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;

    /**
     * Constructs the ListBookingsWindow with a reference to the FlightBookingSystem.
     * Initializes the GUI components and loads booking data.
     * 
     * @param flightBookingSystem the flight booking system containing customers and bookings
     */
    public ListBookingsWindow(FlightBookingSystem flightBookingSystem) {
        this.flightBookingSystem = flightBookingSystem;
        initialize();
        loadBookings();
    }

    /**
     * Initializes the GUI components, layout, and event handlers.
     * Sets up the table, filter combo box, and buttons.
     */
    private void initialize() {
        setTitle("📋 Bookings List");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Bookings", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(new Color(25, 25, 112));
        add(header, BorderLayout.NORTH);

        // Columns for bookings table
        String[] columns = {
            "ID", "Customer", "Flight", "Departure Date", "Class", "Price", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.getTableHeader().setReorderingAllowed(false);

        adjustColumnWidths();

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with filter combo + buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        filterComboBox = new JComboBox<>(new String[]{"All", "Future"});
        filterComboBox.setFont(new Font("Tahoma", Font.BOLD, 14));
        filterComboBox.setPreferredSize(new Dimension(120, 30));
        bottomPanel.add(filterComboBox);

        JButton refreshBtn = new JButton(" Refresh");
        styleButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadBookings());
        bottomPanel.add(refreshBtn);

        JButton detailsBtn = new JButton(" Show Details");
        styleButton(detailsBtn);
        detailsBtn.addActionListener(e -> showSelectedBookingDetails());
        bottomPanel.add(detailsBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Applies consistent styling to buttons used in the window.
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
     * Adjusts the preferred widths of columns in the bookings table for better UI layout.
     */
    private void adjustColumnWidths() {
        int[] widths = {40, 180, 250, 110, 80, 80, 100};
        for (int i = 0; i < widths.length; i++) {
            if (i < bookingsTable.getColumnModel().getColumnCount()) {
                bookingsTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
        }
    }

    /**
     * Loads bookings from the FlightBookingSystem and populates the table.
     * Applies filtering based on the selected filter (All or Future bookings).
     * Displays a message dialog if no bookings match the filter.
     */
    private void loadBookings() {
        tableModel.setRowCount(0);

        List<Booking> allBookings = getAllBookings(flightBookingSystem);
        String filter = ((String) filterComboBox.getSelectedItem()).toLowerCase();

        List<Booking> filteredBookings;

        LocalDate today = LocalDate.now();

        if ("future".equals(filter)) {
            filteredBookings = allBookings.stream()
                .filter(b -> {
                    Flight flight = b.getFlight();
                    return flight != null && flight.getDepartureDate() != null && flight.getDepartureDate().isAfter(today);
                })
                .collect(Collectors.toList());
        } else {
            filteredBookings = allBookings;
        }

        if (filteredBookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bookings found for the selected option.", "No Bookings", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Booking b : filteredBookings) {
            Customer customer = b.getCustomer();
            Flight flight = b.getFlight();
            String flightInfo = (flight != null)
                ? flight.getFlightNumber() + " (" + flight.getOrigin() + "→" + flight.getDestination() + ")"
                : "N/A";
            String depDate = (flight != null && flight.getDepartureDate() != null) ? flight.getDepartureDate().format(dateFormatter) : "N/A";

            tableModel.addRow(new Object[]{
                b.getBookingId(),
                customer != null ? customer.getName() : "N/A",
                flightInfo,
                depDate,
                b.getSeatClass().toString(),
                b.getPrice(),
                b.getStatus()
            });
        }
    }

    /**
     * Shows a dialog with detailed information about the currently selected booking.
     * If no booking is selected, shows a warning message.
     */
    private void showSelectedBookingDetails() {
        int selectedRow = bookingsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to see details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);

        Booking selectedBooking = null;
        for (Booking b : getAllBookings(flightBookingSystem)) {
            if (b.getBookingId() == bookingId) {
                selectedBooking = b;
                break;
            }
        }

        if (selectedBooking != null) {
            JTextArea detailsArea = new JTextArea(selectedBooking.getBookingDetails());
            detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            detailsArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(detailsArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Booking Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Booking not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Retrieves all bookings from all customers in the FlightBookingSystem.
     * 
     * @param flightBookingSystem the flight booking system containing customers and bookings
     * @return a list of all bookings from all customers
     */
    private List<Booking> getAllBookings(FlightBookingSystem flightBookingSystem) {
        List<Booking> allBookings = new ArrayList<>();
        for (Customer customer : flightBookingSystem.getAllCustomers()) {
            if (customer.getBookings() != null) {
                allBookings.addAll(customer.getBookings());
            }
        }
        return allBookings;
    }
}
