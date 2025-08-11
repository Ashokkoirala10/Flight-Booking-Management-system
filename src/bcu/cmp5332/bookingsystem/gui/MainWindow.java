package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import java.net.URL;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame {

    private final FlightBookingSystem fbs;

    public MainWindow() throws FlightBookingSystemException, IOException {
        super("Flight Booking System");
        this.fbs = FlightBookingSystemData.load();
        initialize();
    }

    public MainWindow(FlightBookingSystem fbs) {
        super("Flight Booking System");
        this.fbs = fbs;
        initialize();
    }

    private void initialize() {
        setTitle("✈️ Flight Booking System");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);  // Disable window resizing

        // Load image from the resources folder using file path
        Image backgroundImage = null;
        try {
            backgroundImage = ImageIO.read(new File("resources/plane.jpg"))
                    .getScaledInstance(900, 600, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not load background image from resources/plane.jpg", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImage));
        backgroundLabel.setLayout(new BorderLayout());

        // Gradient title panel
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                Color color1 = new Color(44, 62, 80);
                Color color2 = new Color(52, 152, 219);
                GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        titlePanel.setPreferredSize(new Dimension(getWidth(), 70));
        titlePanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("Flight Booking System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Central panel with slogan and buttons in flow layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);  // transparent to show background image
        centerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel sloganLabel = new JLabel("Your Journey Begins Here!", SwingConstants.CENTER);
        sloganLabel.setFont(new Font("Segoe UI", Font.ITALIC, 22));
        sloganLabel.setForeground(Color.WHITE);
        sloganLabel.setBorder(new EmptyBorder(0, 0, 25, 0));
        centerPanel.add(sloganLabel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonsPanel.setOpaque(false);

        Map<String, Color> groupColors = new HashMap<>();
        groupColors.put("List", new Color(41, 128, 185));
        groupColors.put("Show", new Color(39, 174, 96));
        groupColors.put("Add", new Color(243, 156, 18));
        groupColors.put("Update", new Color(142, 68, 173));
        groupColors.put("Remove", new Color(192, 57, 43));
        groupColors.put("Other", new Color(127, 140, 141));

        String[][] commandGroups = {
            {"List Flights", "List Customers", "List Bookings"},
            {"Show Bookings", "Show Flight", "Show Customer"},
            {"Add Flight", "Add Customer", "Add Booking", "Interactive Booking"},
            {"Update Customer", "Update Flight", "Edit Booking"},
            {"Remove Booking", "Remove Customer", "Remove Flight", "Cancel Booking", "Rebook"},
            {"Search Flight", "Save Data"}
        };

        for (int i = 0; i < commandGroups.length; i++) {
            String groupKey;
            switch(i) {
                case 0: groupKey = "List"; break;
                case 1: groupKey = "Show"; break;
                case 2: groupKey = "Add"; break;
                case 3: groupKey = "Update"; break;
                case 4: groupKey = "Remove"; break;
                default: groupKey = "Other"; break;
            }

            Color btnColor = groupColors.get(groupKey);
            for (String cmd : commandGroups[i]) {
                JButton btn = createNavButton(cmd, btnColor);
                btn.addActionListener(e -> {
                    try {
                        handleCommand(cmd);
                    } catch (FlightBookingSystemException e1) {
                        e1.printStackTrace();
                    }
                });
                buttonsPanel.add(btn);
            }
        }

        centerPanel.add(buttonsPanel, BorderLayout.CENTER);

        // Add components to background image
        backgroundLabel.add(titlePanel, BorderLayout.NORTH);
        backgroundLabel.add(centerPanel, BorderLayout.CENTER);

        // Set background as content pane
        setContentPane(backgroundLabel);

        setVisible(true);
    }

    private JButton createNavButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(160, 45));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void handleCommand(String command) throws FlightBookingSystemException {
        switch (command) {
            case "List Flights":
                SwingUtilities.invokeLater(() -> new ListFlightsWindow(fbs).setVisible(true));
                break;

            case "List Customers":
                SwingUtilities.invokeLater(() -> new ListCustomersWindow(fbs).setVisible(true));
                break;

            case "List Bookings":
                SwingUtilities.invokeLater(() -> new ListBookingsWindow(fbs).setVisible(true));
                break;

            case "Add Flight":
                new AddFlightWindow(fbs, this);
                break;

            case "Add Customer":
                new AddCustomerWindow(fbs, this);
                break;

            case "Add Booking":
                new AddBookingWindow(fbs);
                break;

            case "Interactive Booking":
                new InteractiveBookingWindow(fbs).setVisible(true);
                break;

            case "Update Flight":
                String flightInputId = JOptionPane.showInputDialog(this, "Enter Flight ID to update:");
                if (flightInputId != null && !flightInputId.trim().isEmpty()) {
                    try {
                        int flightId = Integer.parseInt(flightInputId.trim());
                        Flight flight = fbs.getFlightById(flightId);
                        if (flight != null) {
                            new UpdateFlightWindow(fbs, flight);
                        } else {
                            JOptionPane.showMessageDialog(this, "Flight not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid Flight ID entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;

            case "Show Bookings":
                new ShowBookingWindow(fbs);
                break;

            case "Show Flight":
                new ShowFlightWindow(fbs);
                break;

            case "Show Customer":
                new ShowCustomerWindow(fbs);
                break;

            case "Search Flight":
                new SearchFlightWindow(fbs);
                break;

            case "Update Customer":
                String inputId = JOptionPane.showInputDialog(this, "Enter Customer ID to update:");
                if (inputId != null && !inputId.trim().isEmpty()) {
                    try {
                        int customerId = Integer.parseInt(inputId.trim());
                        Customer customer = fbs.getCustomerById(customerId);
                        if (customer != null) {
                            new UpdateCustomerWindow(fbs, customer);
                        } else {
                            JOptionPane.showMessageDialog(this, "Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid ID entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;

            case "Remove Booking":
                new RemoveBookingWindow(fbs);
                break;

            case "Remove Customer":
                new RemoveCustomerWindow(fbs);
                break;
            case "Remove Flight":
                SwingUtilities.invokeLater(() -> {
                    JFrame removeFlightFrame = new JFrame("Remove Flight");
                    removeFlightFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    removeFlightFrame.setSize(400, 200);
                    removeFlightFrame.setLocationRelativeTo(null);
                    removeFlightFrame.setResizable(false);

                    RemoveFlightPanel removeFlightPanel = new RemoveFlightPanel(fbs);
                    removeFlightFrame.setContentPane(removeFlightPanel);

                    removeFlightFrame.setVisible(true);
                });
                break;

            case "Cancel Booking":
                new CancelBookingWindow(fbs);
                break;

            case "Rebook":
                new RebookWindow(fbs);
                break;

            case "Edit Booking":
                String bookingIdStr = JOptionPane.showInputDialog(null, "Enter Booking ID to edit:");
                if (bookingIdStr != null) {
                    try {
                        int bookingId = Integer.parseInt(bookingIdStr.trim());
                        new EditBookingWindow(fbs, bookingId);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid Booking ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;

            case "Save Data":
                try {
                    FlightBookingSystemData.store(fbs);
                    JOptionPane.showMessageDialog(this, "Data saved successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage());
                }
                break;

            default:
                JOptionPane.showMessageDialog(this, "Unknown command.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            new MainWindow();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load system: " + ex.getMessage());
        }
    }
}
