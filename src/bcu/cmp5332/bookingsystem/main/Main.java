package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.*;

/**
 * The entry point of the Flight Booking System application.
 * <p>
 * This class initializes the system by loading existing flight booking data,
 * then enters a command processing loop where user input commands are parsed
 * and executed. The application continues to accept commands until the user
 * enters the "exit" command. Upon exit, the updated data is saved.
 * </p>
 */
public class Main {

    /**
     * Shared BufferedReader for reading input from the console.
     * Used throughout the application for user command input.
     */
    public static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Main method to launch the Flight Booking System.
     * <p>
     * It loads the existing flight booking data, displays an initial prompt,
     * and then continuously reads and processes commands entered by the user.
     * The loop terminates when the user enters the "exit" command.
     * Finally, it saves the current state of the flight booking system.
     * </p>
     *
     * @param args command-line arguments (not used)
     * @throws IOException                 if an input/output error occurs during reading input
     * @throws FlightBookingSystemException if a system-specific error occurs during command execution
     */
    public static void main(String[] args) throws IOException, FlightBookingSystemException {
        
        // Load the flight booking system data
        FlightBookingSystem fbs = FlightBookingSystemData.load();

        System.out.println("Flight Booking System");
        System.out.println("Enter 'help' to see a list of available commands.");
        
        // Main loop for reading commands
        while (true) {
            System.out.print("> ");
            String line = reader.readLine();  // Use the shared reader here
            if (line.equals("exit")) {
                break;  // Exit the loop if "exit" command is given
            }

            try {
                // Parse and execute the command
                Command command = CommandParser.parse(line, fbs);

                if (command != null) {
                    command.execute(fbs);
                }
            } catch (FlightBookingSystemException ex) {
                // Handle exceptions and print error message
                System.out.println(ex.getMessage());
            }
        }

        // Store the updated flight booking system data
        FlightBookingSystemData.store(fbs);

        // Exit the program
        System.exit(0);
    }
}
