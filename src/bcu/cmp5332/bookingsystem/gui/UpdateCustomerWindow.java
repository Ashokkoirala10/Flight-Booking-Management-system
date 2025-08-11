package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.commands.UpdateCustomer;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.commands.Command;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The {@code UpdateCustomerWindow} class provides a GUI form for editing and updating
 * a customer's personal information in the Flight Booking System.
 * <p>
 * This window pre-fills the form fields with the customer's current details and allows
 * the user to update fields such as name, phone number, age, address, passport number,
 * passport expiry date, email, date of birth, gender, and disability status.
 * <p>
 * The data entered by the user is validated before updating the customer record via
 * the {@link UpdateCustomer} command.
 * 
 * @author Ashok
 */
public class UpdateCustomerWindow extends JFrame implements ActionListener {
    private final FlightBookingSystem flightBookingSystem;
    private final Customer customer;

    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField ageField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField passportField = new JTextField();
    private final JTextField passportExpiryField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField dobField = new JTextField();
    private final JComboBox<String> genderCombo = new JComboBox<>(new String[]{"", "Male", "Female", "Other"});
    private final JCheckBox disabledCheckBox = new JCheckBox("Disabled");

    private final JButton updateButton = new JButton("Update Customer");
    /**
     * Constructs the UpdateCustomerWindow.
     *
     * @param fbs the {@link FlightBookingSystem} used to apply the update
     * @param customer the {@link Customer} object to be edited
     */
    public UpdateCustomerWindow(FlightBookingSystem fbs, Customer customer) {
    	
        this.flightBookingSystem = fbs;
        this.customer = customer;


        setTitle("Update Customer");
        setSize(450, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JLabel header = new JLabel("Update Customer");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180));
        header.setPreferredSize(new Dimension(450, 40));

        JPanel formPanel = new JPanel(new GridLayout(11, 2, 10, 10));
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Pre-fill existing data
        nameField.setText(customer.getName());
        phoneField.setText(customer.getPhone());
        ageField.setText(String.valueOf(customer.getAge()));
        addressField.setText(customer.getAddress());
        passportField.setText(customer.getPassportNumber());
        passportExpiryField.setText(customer.getPassportExpiryDate().toString());
        emailField.setText(customer.getEmail());
        dobField.setText(customer.getDob().toString());
        genderCombo.setSelectedItem(customer.getGender());
        disabledCheckBox.setSelected(customer.isDisabled());

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Age:"));
        formPanel.add(ageField);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);

        formPanel.add(new JLabel("Passport Number:"));
        formPanel.add(passportField);

        formPanel.add(new JLabel("Passport Expiry (YYYY-MM-DD):"));
        formPanel.add(passportExpiryField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        formPanel.add(dobField);

        formPanel.add(new JLabel("Gender:"));
        formPanel.add(genderCombo);

        formPanel.add(new JLabel(""));
        formPanel.add(disabledCheckBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.add(updateButton);

        updateButton.addActionListener(this);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    /**
     * Handles the update button action.
     * <p>
     * Validates all input fields and then creates and executes an
     * {@link UpdateCustomer} command if all inputs are valid.
     * Displays error messages for invalid fields or system errors.
     *
     * @param e the ActionEvent triggered by the button click
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int id = customer.getId();

            // Check if customer exists (you can keep or remove this as getCustomerById throws exception)
            if (flightBookingSystem.getCustomerById(id) == null) {
                JOptionPane.showMessageDialog(this,
                    "Customer with ID " + id + " does not exist.",
                    "Customer Not Found",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }


            // Validate Name
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty.");
            }

            // Validate Phone (exactly 10 digits)
            String phone = phoneField.getText().trim();
            if (!phone.matches("\\d{10}")) {
                throw new IllegalArgumentException("Phone number must be exactly 10 digits.");
            }

            // Validate Age (integer between 0 and 130)
            int age;
            try {
                age = Integer.parseInt(ageField.getText().trim());
                if (age <= 0 || age > 130) {
                    throw new IllegalArgumentException("Age must be between 0 and 130.");
                }
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Age must be a valid integer.");
            } 

            // Address (optional, no validation, but trim)
            String address = addressField.getText().trim();

            // Validate Passport Number (max 9 characters)
            String passport = passportField.getText().trim();
            if (passport.length() > 9) {
                throw new IllegalArgumentException("Passport number must not exceed 9 characters.");
            }

            // Validate Passport Expiry Date (valid date format)
            LocalDate passportExpiry;
            try {
                passportExpiry = LocalDate.parse(passportExpiryField.getText().trim());
                // Optional: check if expiry date is not in the past
                if (passportExpiry.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("Passport expiry date cannot be in the past.");
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException("Passport expiry date must be in YYYY-MM-DD format.");
            }

            // Validate Email (simple regex)
            String email = emailField.getText().trim();
            if (!email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                throw new IllegalArgumentException("Invalid email format.");
            }

            // Validate Date of Birth (valid date format and before today)
            LocalDate dob;
            try {
                dob = LocalDate.parse(dobField.getText().trim());
                if (dob.isAfter(LocalDate.now())) {
                    throw new IllegalArgumentException("Date of Birth cannot be in the future.");
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException("Date of Birth must be in YYYY-MM-DD format.");
            }

            // Validate Gender (must be selected)
            String gender = genderCombo.getSelectedItem().toString();
            if (gender.isEmpty()) {
                throw new IllegalArgumentException("Please select a gender.");
            }

            // Disabled checkbox boolean
            boolean isDisabled = disabledCheckBox.isSelected();

            // Create and execute command
            Command cmd = new UpdateCustomer(id, name, phone, age, address, passport, passportExpiry,
                                             email, dob, gender, isDisabled);
            cmd.execute(flightBookingSystem);

            JOptionPane.showMessageDialog(this,
                "Customer updated successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (FlightBookingSystemException ex) {
            // Catch your custom exception explicitly and show a dialog
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Customer Not Found",
                JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                "Validation Error: " + ex.getMessage(),
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

}
