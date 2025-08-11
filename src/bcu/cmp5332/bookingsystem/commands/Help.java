package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to display the help message with all available commands.
 */
public class Help implements Command {

    /**
     * Executes the help command by printing the help message.
     *
     * @param flightBookingSystem the flight booking system (not used here)
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) {
        System.out.println(Command.HELP_MESSAGE);
    }
}
