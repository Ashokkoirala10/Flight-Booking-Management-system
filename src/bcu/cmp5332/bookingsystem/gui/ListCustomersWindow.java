package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * GUI window that displays a list of customers in a table format.
 * Provides buttons to refresh the customer list and show detailed information about a selected customer.
 * 
 * <p>The table includes columns for ID, name, phone, age, country, email, gender, and disability status.</p>
 * 
 * <p>Customer details can be viewed in a separate dialog by selecting a customer and clicking "Show Details".</p>
 * 
 */
public class ListCustomersWindow extends JFrame {

    private final FlightBookingSystem flightBookingSystem;
    private JTable customersTable;
    private DefaultTableModel tableModel;

    /**
     * Constructs the ListCustomersWindow with a reference to the FlightBookingSystem.
     * Initializes the GUI components and loads the list of customers.
     * 
     * @param flightBookingSystem the flight booking system containing the customer data
     */
    public ListCustomersWindow(FlightBookingSystem flightBookingSystem) {
        this.flightBookingSystem = flightBookingSystem;
        initialize();
        loadCustomers();
    }

    /**
     * Initializes the GUI components, including the table and buttons.
     * Sets layout, fonts, and event handlers.
     */
    private void initialize() {
        setTitle("👥 Customers List");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Customers", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(new Color(25, 25, 112));
        add(header, BorderLayout.NORTH);

        String[] columns = {
            "ID", "Name", "Phone", "Age", "Country", "Email", "Gender", "Disabled"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable cells
            }
        };

        customersTable = new JTable(tableModel);
        customersTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(customersTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton refreshBtn = new JButton(" Refresh");
        styleButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadCustomers());
        buttonPanel.add(refreshBtn);

        JButton detailsBtn = new JButton(" Show Details");
        styleButton(detailsBtn);
        detailsBtn.addActionListener(e -> showSelectedCustomerDetails());
        buttonPanel.add(detailsBtn);

        add(buttonPanel, BorderLayout.SOUTH);
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
     * Loads the list of customers from the FlightBookingSystem and populates the table.
     * If no customers are found, shows an information dialog.
     */
    private void loadCustomers() {
        tableModel.setRowCount(0); // clear existing rows

        List<Customer> customers = flightBookingSystem.getAllCustomers();

        if (customers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No customers found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getName(),
                c.getPhone(),
                c.getAge(),
                c.getCountry(),
                c.getEmail(),
                c.getGender(),
                c.isDisabled() ? "Yes" : "No"
            });
        }
    }

    /**
     * Shows a dialog with detailed information about the currently selected customer.
     * If no customer is selected, shows a warning message.
     */
    private void showSelectedCustomerDetails() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to see details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);

        Customer selectedCustomer = null;
        for (Customer c : flightBookingSystem.getAllCustomers()) {
            if (c.getId() == customerId) {
                selectedCustomer = c;
                break;
            }
        }

        if (selectedCustomer != null) {
            JTextArea detailsArea = new JTextArea(selectedCustomer.getDetailsLong());
            detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            detailsArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(detailsArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Customer Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
