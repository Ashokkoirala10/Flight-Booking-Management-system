package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.gui.LoginWindow;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import javax.swing.*;

/**
 * Command implementation to launch the graphical user interface (GUI) of the flight booking system.
 * <p>
 * When executed, this command initializes and displays the login window.
 * </p>
 */
public class LoadGUI implements Command {

    /**
     * Executes the command by creating and displaying the login GUI window.
     *
     * @param flightBookingSystem the flight booking system instance to be passed to the GUI
     * @throws FlightBookingSystemException not thrown by this implementation but declared by interface
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        SwingUtilities.invokeLater(() -> {
            new LoginWindow(flightBookingSystem).setVisible(true);
        });
    }
}
