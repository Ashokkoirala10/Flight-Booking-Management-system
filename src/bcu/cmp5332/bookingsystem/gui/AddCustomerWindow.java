package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * GUI window for adding a new customer to the flight booking system.
 * <p>
 * This window collects customer details including personal information,
 * contact info, passport details, disability status, and gender.
 * Input validation is performed on fields such as phone number, email,
 * age, and dates to ensure data integrity.
 * <p>
 * On successful addition, a confirmation dialog is shown and the window closes.
 * In case of input errors, appropriate error dialogs are displayed without closing the window.
 * 
 * @author 
 */
public class AddCustomerWindow extends JFrame {

    /**
     * The flight booking system instance where customers are managed.
     */
    private final FlightBookingSystem flightBookingSystem;

    /**
     * The main application window (caller) to which control returns after adding.
     */
    private final MainWindow mainWindow;

    // Input fields
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField ageField;
    private JTextField addressField;
    private JTextField countryField;
    private JTextField passportNumberField;
    private JTextField passportExpiryDateField;
    private JCheckBox disabledCheckBox;
    private JTextField emailField;
    private JTextField dobField;
    private JComboBox<String> genderBox;

    /**
     * Constructs the AddCustomerWindow and initializes all GUI components.
     * 
     * @param fbs The FlightBookingSystem instance to add the new customer.
     * @param mainWindow The main window to return control to after adding.
     */
    public AddCustomerWindow(FlightBookingSystem fbs, MainWindow mainWindow) {
        this.flightBookingSystem = fbs;
        this.mainWindow = mainWindow;

        setTitle("Add New Customer");
        setSize(550, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Header label configuration
        JLabel header = new JLabel("Add New Customer");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180)); // Steel blue
        header.setPreferredSize(new Dimension(550, 40));

        // Form panel with GridBagLayout for flexible positioning
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 250, 255)); // Light pastel background
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int y = 0;
        nameField = addField(panel, "Name:", gbc, y++);
        phoneField = addField(panel, "Phone:", gbc, y++);
        ageField = addField(panel, "Age:", gbc, y++);
        addressField = addField(panel, "Address:", gbc, y++);
        countryField = addField(panel, "Country:", gbc, y++);
        passportNumberField = addField(panel, "Passport Number:", gbc, y++);
        passportExpiryDateField = addField(panel, "Passport Expiry Date (YYYY-MM-DD):", gbc, y++);
        emailField = addField(panel, "Email:", gbc, y++);
        dobField = addField(panel, "Date of Birth (YYYY-MM-DD):", gbc, y++);

        // Gender label and combo box
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        panel.add(genderBox, gbc);
        y++;

        // Disability status label and checkbox
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel("Disability Status:"), gbc);
        gbc.gridx = 1;
        disabledCheckBox = new JCheckBox("Disabled");
        disabledCheckBox.setBackground(new Color(245, 250, 255));
        panel.add(disabledCheckBox, gbc);

        // Add Customer button
        JButton addButton = new JButton("Add Customer");
        addButton.setPreferredSize(new Dimension(140, 30));
        addButton.setBackground(new Color(100, 150, 255));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new AddCustomerListener());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.add(addButton);

        // Add components to frame layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Helper method to add a labeled text field to the panel with GridBagLayout.
     *
     * @param panel The JPanel to which the components are added.
     * @param label The text for the JLabel.
     * @param gbc The GridBagConstraints object for layout management.
     * @param y The vertical position (row) in the grid.
     * @return The created JTextField.
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
     * Private inner class that handles the action of clicking the "Add Customer" button.
     * Performs validation on the input fields and adds the customer to the system if valid.
     * Shows error dialogs for invalid input without closing the window.
     */
    private class AddCustomerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                String country = countryField.getText().trim();
                String passportNumber = passportNumberField.getText().trim();
                String email = emailField.getText().trim();
                String gender = (String) genderBox.getSelectedItem();

                // Check for empty required fields
                if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || country.isEmpty() ||
                        passportNumber.isEmpty() || email.isEmpty()) {
                    throw new IllegalArgumentException("All fields except 'Disabled' must be filled.");
                }

                // Validate phone number pattern
                if (!Pattern.matches("^\\+?[0-9\\-\\s]+$", phone)) {
                    throw new IllegalArgumentException("Invalid phone number format.");
                }

                // Validate email format
                if (!Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", email)) {
                    throw new IllegalArgumentException("Invalid email format.");
                }

                // Validate age range
                int age = Integer.parseInt(ageField.getText().trim());
                if (age <= 0 || age > 120) {
                    throw new IllegalArgumentException("Age must be between 1 and 120.");
                }

                // Validate passport expiry date
                LocalDate passportExpiryDate = LocalDate.parse(passportExpiryDateField.getText().trim());
                if (passportExpiryDate.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("Passport expiry must be in the future.");
                }

                // Validate date of birth
                LocalDate dob = LocalDate.parse(dobField.getText().trim());
                if (dob.isAfter(LocalDate.now().minusYears(1))) {
                    throw new IllegalArgumentException("Date of birth must be in the past.");
                }

                boolean disabled = disabledCheckBox.isSelected();

                // Add customer to the system
                Customer newCustomer = flightBookingSystem.addCustomer(
                        name, phone, age, address, country, passportNumber,
                        passportExpiryDate, disabled, email, dob, gender
                );

                // Show success message
                JOptionPane.showMessageDialog(AddCustomerWindow.this,
                        "Customer added: " + newCustomer.getName(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // Close the window after successful addition
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(AddCustomerWindow.this,
                        "Age must be a number.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(AddCustomerWindow.this,
                        "Invalid date format. Use YYYY-MM-DD.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(AddCustomerWindow.this,
                        "Error: " + ex.getMessage(),
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
