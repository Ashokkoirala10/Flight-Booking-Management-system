package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Represents a command that can be executed within the flight booking system.
 * <p>
 * Each command performs a specific operation on the {@link FlightBookingSystem}
 * when executed.
 * </p>
 * <p>
 * Contains a static help message listing all available commands and their usage.
 * </p>
 */
public interface Command {

    /**
     * A help message listing all available commands and their descriptions.
     */
    public static final String HELP_MESSAGE = "Commands:\n"
            + "\tlistflights                               	print all flights\n"
            + "\tlistcustomers                             	print all customers\n"
            + "\tlistbookings                              	show all bookings in the system\n"
            + "\tshowbookings [customer id]                	show all bookings for a customer\n"
            + "\tshowflight [flight id]                    	show flight details\n"
            + "\tshowcustomer [customer id]                	show customer details\n"
            + "\tsearchflight                              	search flights \n"
            + "\taddflight                                 	add a new flight\n"
            + "\tupdateflight                              	Update existing flight\n"
            + "\tremoveflight [flight id]                  	remove existing flight\n"
            + "\taddcustomer                               	add a new customer\n"
            + "\tupdatecustomer                            	update customer information\n"
            + "\tremovecustomer [customer id]              	remove a customer and all their bookings\n"
            + "\taddbooking [customer id] [flight id]      	add a new booking (Economy)\n"
            + "\taddbooking [customer id][flight id][Class]	add a new booking with own class (Business, First)\n"
            + "\tinteractivebooking                        	interactively add a booking (Economy, Business, First)\n"
            + "\tremovebooking [customer id] [flight id]   	cancel a booking\n"
            + "\teditbooking [booking id]                  	update a booking\n"
            + "\tcancelBooking                            	cancel a booking in memory only (no file update)\n"
            + "\trebook                                  	rebook a cancelled booking with fare increase\n"
            + "\tloadgui                                   	loads the GUI version of the app\n"
            + "\thelp                                      	prints this help message\n"
            + "\texit                                      	exits the program";

    /**
     * Executes the command on the provided flight booking system.
     *
     * @param flightBookingSystem the flight booking system instance on which the command operates
     * @throws FlightBookingSystemException if an error occurs during command execution
     */
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException;
}
