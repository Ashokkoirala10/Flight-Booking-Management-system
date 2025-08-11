package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Login window for the Flight Booking System.
 * <p>
 * Displays a simple login form with username and password fields.
 * On successful login, it opens the main application window.
 * The login credentials are currently hardcoded for demonstration purposes.
 * </p>
 */
public class LoginWindow extends JFrame {

    private final FlightBookingSystem fbs;

    /**
     * Constructs the LoginWindow.
     * Initializes the GUI components including background gradient, form fields, and login button.
     * Adds event handlers for login action and button hover effects.
     * 
     * @param fbs the FlightBookingSystem instance to be used by the main window upon successful login
     */
    public LoginWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;

        setTitle("✈️ Flight Booking System - Login");
        setSize(450, 300);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ==== Background Panel with Gradient ====
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(173, 216, 230);
                Color color2 = new Color(224, 255, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(backgroundPanel);

        // ==== Header ====
        JLabel header = new JLabel("Welcome to Flight Booking System", JLabel.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setForeground(new Color(33, 37, 41));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        backgroundPanel.add(header, BorderLayout.NORTH);

        // ==== Form Panel with wrapped label + field ====
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        // Username panel (label + field)
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.X_AXIS));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userPanel.add(userLabel);
        userPanel.add(Box.createRigidArea(new Dimension(5, 0))); // small horizontal gap

        JTextField userField = new JTextField();
        userField.setFont(userLabel.getFont());
        Dimension userFieldSize = new Dimension(160, userLabel.getFontMetrics(userLabel.getFont()).getHeight() + 6);
        userField.setPreferredSize(userFieldSize);
        userField.setMaximumSize(userFieldSize);
        userField.setMinimumSize(userFieldSize);
        userField.setMargin(new Insets(2, 8, 2, 4));
        userField.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        userPanel.add(userField);

        // Password panel (label + field)
        JPanel passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.X_AXIS));
        passPanel.setOpaque(false);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passPanel.add(passLabel);
        passPanel.add(Box.createRigidArea(new Dimension(5, 0))); // small horizontal gap

        JPasswordField passField = new JPasswordField();
        passField.setFont(passLabel.getFont());
        Dimension passFieldSize = new Dimension(160, passLabel.getFontMetrics(passLabel.getFont()).getHeight() + 6);
        passField.setPreferredSize(passFieldSize);
        passField.setMaximumSize(passFieldSize);
        passField.setMinimumSize(passFieldSize);
        passField.setMargin(new Insets(2, 4, 2, 4));
        passField.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        passPanel.add(passField);

        // Add to form panel with minimal spacing
        formPanel.add(userPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));  // smaller vertical gap
        formPanel.add(passPanel);

        backgroundPanel.add(formPanel, BorderLayout.CENTER);

        // ==== Login Button ====
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 36));
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 1, true));

        // Hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginButton.setBackground(new Color(0, 105, 230));
            }

            public void mouseExited(MouseEvent evt) {
                loginButton.setBackground(new Color(0, 123, 255));
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ==== Action Listener ====
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (validateCredentials(username, password)) {
                dispose();
                try {
                    new MainWindow(fbs);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to launch main window: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    /**
     * Validates the user credentials against hardcoded values.
     * <p>
     * In a real-world application, this should be replaced with proper authentication logic.
     * </p>
     * 
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return true if credentials are valid, false otherwise
     */
    private boolean validateCredentials(String username, String password) {
        return username.equals("staff") && password.equals("staff1");
    }
}
