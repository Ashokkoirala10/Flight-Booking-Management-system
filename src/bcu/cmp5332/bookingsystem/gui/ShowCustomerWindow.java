package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
/**
 * The {@code ShowCustomerWindow} class creates a GUI window that allows
 * the user to search for and view details of a specific customer by entering
 * their Customer ID.
 * <p>
 * This window is part of the GUI for the Flight Booking System and
 * interacts with the {@link FlightBookingSystem} to retrieve customer
 * information.
 * </p>
 * 
 * <p>The information displayed includes the customer's profile as well as
 * their associated bookings (if any).</p>
 * 
 * @author Ashok
 */
public class ShowCustomerWindow extends JFrame {

    private final FlightBookingSystem flightBookingSystem;
    private JTextField customerIdField;
    /**
     * Constructs the {@code ShowCustomerWindow} with a reference to the flight booking system.
     *
     * @param flightBookingSystem the {@link FlightBookingSystem} to be used for retrieving customer data
     */
    public ShowCustomerWindow(FlightBookingSystem flightBookingSystem) {
        this.flightBookingSystem = flightBookingSystem;
        initialize();
    }
    /**
     * Initializes the GUI components including the header, input field,
     * search button, and layout settings.
     */
    private void initialize() {
        setTitle("Show Customer");
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Header
        JLabel header = new JLabel(" Find Customer Details", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(new Color(25, 25, 112));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Form row
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(240, 248, 255));

        JLabel label = new JLabel("Customer ID:");
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        customerIdField = new JTextField(8); // small box, suitable for ID
        customerIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        customerIdField.setPreferredSize(new Dimension(100, 28)); // smaller height and width

        inputPanel.add(label);
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
        showBtn.addActionListener(this::showCustomer);

        mainPanel.add(showBtn);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }
    /**
     * Event handler that is called when the "Search" button is clicked.
     * It attempts to retrieve and display the details of a customer based
     * on the entered ID.
     *
     * @param e the {@link ActionEvent} triggered by the button click
     */
    private void showCustomer(ActionEvent e) {
        try {
            String idText = customerIdField.getText().trim();

            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a Customer ID.",
                        "Input Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int customerId = Integer.parseInt(idText);
            Customer customer = flightBookingSystem.getCustomerById(customerId);

            if (customer == null) {
                JOptionPane.showMessageDialog(this,
                        "Customer ID not found.",
                        "Not Found",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Show customer long details (with bookings)
            JTextArea resultArea = new JTextArea(customer.getDetailsLong());
            resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            resultArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(resultArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Customer Info", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Customer ID. Please enter a valid number.",
                    "Format Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
