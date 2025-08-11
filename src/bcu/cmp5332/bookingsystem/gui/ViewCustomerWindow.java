package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.*;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
/**
 * GUI window for viewing all customers registered in the Flight Booking System.
 * <p>
 * This window displays a read-only scrollable text area listing all customers and their details,
 * including name, phone number, age, country, passport information, and expiry date.
 * </p>
 *
 * <p>
 * It is intended to be launched from the {@link MainWindow} and is centered on that parent window.
 * </p>
 * 
 * @author Ashok
 */
public class ViewCustomerWindow extends JFrame {
    /**
     * Constructs a new ViewCustomerWindow to display a list of all customers in the system.
     *
     * @param mainWindow The main application window (used to center this window).
     * @param fbs        The flight booking system containing customer data.
     */
    public ViewCustomerWindow(MainWindow mainWindow, FlightBookingSystem fbs) {
        setTitle("View Customers");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(mainWindow); // centers on main window

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        StringBuilder sb = new StringBuilder();
        for (Customer customer : fbs.getAllCustomers()) {
            sb.append("ID: ").append(customer.getId()).append("\n");
            sb.append("Name: ").append(customer.getName()).append("\n");
            sb.append("Phone: ").append(customer.getPhone()).append("\n");
            sb.append("Age: ").append(customer.getAge()).append("\n");
            sb.append("Country: ").append(customer.getCountry()).append("\n");
            sb.append("Passport: ").append(customer.getPassportNumber()).append("\n");
            sb.append("Expiry: ").append(customer.getPassportExpiryDate()).append("\n");
            sb.append("----------------------------------------\n");
        }

        textArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        setVisible(true);
    }
}
