package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.*;
import bcu.cmp5332.bookingsystem.commands.InputUtils;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Parses command lines entered by the user and creates corresponding Command objects
 * to perform operations on the FlightBookingSystem.
 */
public class CommandParser {

    /**
     * Parses a command line string and returns the corresponding Command object.
     * Supports multiple commands like help, cancelbooking, rebook, searchflight, addflight, etc.
     * For some commands, it prompts the user interactively for additional input.
     *
     * @param line the command line input string to parse
     * @param system the FlightBookingSystem instance on which commands operate
     * @return the Command object corresponding to the parsed input
     * @throws IOException if an input/output error occurs during reading user input
     * @throws FlightBookingSystemException if the command is invalid or cannot be processed
     */
	

    public static Command parse(String line,FlightBookingSystem system) throws IOException, FlightBookingSystemException {
        try {
            line = line.trim();
            String[] parts = line.split(" ", 4); 
            String cmd = parts[0].toLowerCase();

            
            if (cmd.equals("help")) {
                return new Help();
            }
            if (cmd.equals("cancelbooking")) {
               
                return new CancelBookingInMemory();
            }
            if (cmd.equals("rebook")) {
                return new Rebook(); 
            }


             if (cmd.equals("searchflight")) {
                Scanner scanner = new Scanner(System.in);

                System.out.print("Enter origin (or press Enter to skip): ");
                String origin = scanner.nextLine().trim();
                if (origin.isEmpty()) origin = null;

                System.out.print("Enter destination (or press Enter to skip): ");
                String destination = scanner.nextLine().trim();
                if (destination.isEmpty()) destination = null;

                System.out.print("Enter departure (YYYY-MM-DD) (or press Enter to skip): ");
                String depStartStr = scanner.nextLine().trim();
                LocalDate depStart = depStartStr.isEmpty() ? null : LocalDate.parse(depStartStr);

                System.out.print("Enter arrival (YYYY-MM-DD) (or press Enter to skip): ");
                String depEndStr = scanner.nextLine().trim();
                LocalDate depEnd = depEndStr.isEmpty() ? null : LocalDate.parse(depEndStr);

                System.out.print("Enter airline (or press Enter to skip): ");
                String airline = scanner.nextLine().trim();
                if (airline.isEmpty()) airline = null;

                System.out.print("Enter status (or press Enter to skip): ");
                String status = scanner.nextLine().trim();
                if (status.isEmpty()) status = null;

                return new SearchFlight(origin, destination, depStart, depEnd, airline, status);
            }


            if (cmd.equals("addflight")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                String flightNumber = InputUtils.readNonEmptyString(reader, "Flight Number: ");
                String airlineName = InputUtils.readNonEmptyString(reader, "Airline Name: ");
                String origin = InputUtils.readNonEmptyString(reader, "Origin: ");
                String destination = InputUtils.readNonEmptyString(reader, "Destination: ");


                LocalDate departureDate = null;
                while (true) {
                    departureDate = parseDateWithAttempts(reader, "Departure Date (YYYY-MM-DD): ");
                    if (departureDate.isBefore(LocalDate.now())) {
                        System.out.println("Departure date cannot be in the past. Please enter a valid future date.");
                    } else {
                        break;
                    }
                }
                LocalTime departureTime = null;
                while (true) {
                    System.out.print("Departure Time (HH:mm): ");
                    try {
                        departureTime = LocalTime.parse(reader.readLine(), DateTimeFormatter.ofPattern("HH:mm"));
                        break;
                    } catch (Exception e) {
                        System.out.println("Invalid time format. Please use HH:mm (e.g., 14:30).");
                    }
                }
                LocalDate arrivalDate = null;
                while (true) {
                    arrivalDate = parseDateWithAttempts(reader, "Arrival Date (YYYY-MM-DD): ");
                    if (arrivalDate.isBefore(departureDate)) {
                        System.out.println("Arrival date cannot be before departure date. Please enter a valid date.");
                    } else {
                        break;
                    }
                }
                LocalTime arrivalTime = null;
                while (true) {
                    System.out.print("Arrival Time (HH:mm): ");
                    try {
                        arrivalTime = LocalTime.parse(reader.readLine(), DateTimeFormatter.ofPattern("HH:mm"));
                        break;
                    } catch (Exception e) {
                        System.out.println("Invalid time format. Please use HH:mm (e.g., 18:45).");
                    }
                }



                int economyCapacity = 0;
                while (true) {
                    System.out.print("Economy Capacity: ");
                    try {
                        economyCapacity = Integer.parseInt(reader.readLine());
                        if (economyCapacity < 0) throw new NumberFormatException();
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a non-negative whole number.");
                    }
                }

                int businessCapacity = 0;
                while (true) {
                    System.out.print("Business Capacity: ");
                    try {
                        businessCapacity = Integer.parseInt(reader.readLine());
                        if (businessCapacity < 0) throw new NumberFormatException();
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a non-negative whole number.");
                    }
                }

                int firstClassCapacity = 0;
                while (true) {
                    System.out.print("First Class Capacity: ");
                    try {
                        firstClassCapacity = Integer.parseInt(reader.readLine());
                        if (firstClassCapacity < 0) throw new NumberFormatException();
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a non-negative whole number.");
                    }
                }

                double economyPrice = 0;
                while (true) {
                    System.out.print("Economy Price: ");
                    try {
                        economyPrice = Double.parseDouble(reader.readLine());
                        if (economyPrice < 0) throw new NumberFormatException();
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a non-negative number.");
                    }
                }

                double businessPrice = 0;
                while (true) {
                    System.out.print("Business Price: ");
                    try {
                        businessPrice = Double.parseDouble(reader.readLine());
                        if (businessPrice < 0) throw new NumberFormatException();
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a non-negative number.");
                    }
                }

                double firstClassPrice = 0;
                while (true) {
                    System.out.print("First Class Price: ");
                    try {
                        firstClassPrice = Double.parseDouble(reader.readLine());
                        if (firstClassPrice < 0) throw new NumberFormatException();
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a non-negative number.");
                    }
                }
                boolean isInternational = false;
                while (true) {
                    System.out.print("Is the flight International? (yes/no): ");
                    String input = reader.readLine().trim().toLowerCase();
                    if (input.equals("yes")) {
                        isInternational = true;
                        break;
                    } else if (input.equals("no")) {
                        isInternational = false;
                        break;
                    } else {
                        System.out.println("Please answer 'yes' or 'no'.");
                    }
                }

                


                return new AddFlight(
                	    flightNumber, airlineName, origin, destination, departureDate,
                	    economyCapacity, businessCapacity, firstClassCapacity,
                	    economyPrice, businessPrice, firstClassPrice,
                	    departureTime, arrivalTime, arrivalDate,
                	    isInternational
                	);

            }
            else if (cmd.equals("updateflight")) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                
                List<Flight> flights = system.getAllFlights();
                LocalDateTime now = LocalDateTime.now();

                // Filter out past flights
                flights.removeIf(flight ->
                    LocalDateTime.of(flight.getDepartureDate(), flight.getDepartureTime()).isBefore(now)
                );
                if (flights.isEmpty()) {
                    System.out.println("No flights available.");
                    return null;
                }

                System.out.println("Available Flights:");
                System.out.printf("%-5s %-10s %-20s %-15s %-15s %-22s %-22s %-12s %-12s | %-25s\n",
                        "ID", "#Flight", "Airline", "Origin", "Destination",
                        "Departure", "Arrival", "Status", "Flight Type", "Prices (E/B/F)");
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                for (Flight flight : flights) {
                    System.out.println(flight.getDetailsShort());
                }

                System.out.print("Enter Flight ID to update: ");
                int flightId = Integer.parseInt(reader.readLine());

                String input;
                String flightNumber = null;
                String origin = null;
                String destination = null;
                LocalDate departureDate = null;
                Integer economyCapacity = null;
                Integer businessCapacity = null;
                Integer firstClassCapacity = null;
                Double economyPrice = null;
                Double businessPrice = null;
                Double firstClassPrice = null;
                FlightStatus status = null;
                String airlineName = null;
                Boolean isInternational = null;
                LocalDate arrivalDate = null;
                LocalTime arrivalTime = null;
                LocalTime departureTime = null;


                // Flight Number
                System.out.print("Update Flight Number? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    System.out.print("New Flight Number: ");
                    flightNumber = reader.readLine();
                }

                // Origin
                System.out.print("Update Origin? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    System.out.print("New Origin: ");
                    origin = reader.readLine();
                }

                // Destination
                System.out.print("Update Destination? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    System.out.print("New Destination: ");
                    destination = reader.readLine();
                }

             // Departure Date
                System.out.print("Update Departure Date? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    departureDate = CommandParser.parseDateWithAttempts(reader, "New Departure Date (YYYY-MM-DD): ");
                }
                System.out.print("Update Departure Time? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    departureTime = CommandParser.parseTimeWithAttempts(reader, "New Departure Time (HH:MM): ");
                }

                // Arrival Date
                System.out.print("Update Arrival Date? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    arrivalDate = CommandParser.parseDateWithAttempts(reader, "New Arrival Date (YYYY-MM-DD): ");
                }

                // Arrival Time
                System.out.print("Update Arrival Time? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    arrivalTime = CommandParser.parseTimeWithAttempts(reader, "New Arrival Time (HH:MM): ");
                }
         
                // Economy Capacity
                System.out.print("Update Economy Capacity? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    economyCapacity = safeParseInt(reader, "New Economy Capacity: ");
                }

                // Business Capacity
                System.out.print("Update Business Capacity? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    businessCapacity = safeParseInt(reader, "New Business Capacity: ");
                }

                // First Class Capacity
                System.out.print("Update First Class Capacity? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    firstClassCapacity = safeParseInt(reader, "New First Class Capacity: ");
                }

                // Economy Price
                System.out.print("Update Economy Price? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    economyPrice = safeParseDouble(reader, "New Economy Price: ");
                }

                // Business Price
                System.out.print("Update Business Price? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    businessPrice = safeParseDouble(reader, "New Business Price: ");
                }

                // First Class Price
                System.out.print("Update First Class Price? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    firstClassPrice = safeParseDouble(reader, "New First Class Price: ");
                }

                // Flight Status
                System.out.print("Update Flight Status? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    System.out.print("Enter Status (SCHEDULED, CANCELLED, DELAYED, COMPLETED): ");
                    try {
                        status = FlightStatus.valueOf(reader.readLine().trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid flight status. Update skipped.");
                    }
                }

                // Airline Name
                System.out.print("Update Airline Name? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    System.out.print("New Airline Name: ");
                    airlineName = reader.readLine();
                }

                // 🔁 International/Domestic
                System.out.print("Update Flight Type (International/Domestic)? (y/n): ");
                input = reader.readLine().trim();
                if (input.equalsIgnoreCase("y")) {
                    System.out.print("Is the flight international? (yes/no): ");
                    String typeInput = reader.readLine().trim().toLowerCase();
                    if (typeInput.equals("yes")) {
                        isInternational = true;
                    } else if (typeInput.equals("no")) {
                        isInternational = false;
                    } else {
                        System.out.println("Invalid input for flight type. Skipping update.");
                    }
                }

                return new UpdateFlight(flightId, flightNumber, origin, destination, departureDate,
                        economyCapacity, businessCapacity, firstClassCapacity,
                        economyPrice, businessPrice, firstClassPrice,
                        status, airlineName, isInternational,arrivalDate, arrivalTime,departureTime);  
            }




            else if (cmd.equals("addcustomer")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                
                String name = InputUtils.readNonEmptyString(reader, "Customer Name: ");
                String phone = InputUtils.readPhoneNumber(reader, "Phone Number: ");
                int age = InputUtils.readAge(reader, "Age: ");
                String address = InputUtils.readNonEmptyString(reader, "Address: ");
                String country = InputUtils.readNonEmptyString(reader, "Country: ");
                boolean disabled = InputUtils.readBoolean(reader, "Is this customer disabled? (yes/no): ");
                String passportNumber = InputUtils.readPassportNumber(reader, "Passport Number: ");
                LocalDate passportExpiryDate;
                while (true) {
                    passportExpiryDate = InputUtils.readDate(reader, "Passport Expiry Date (YYYY-MM-DD): ");
                    if (passportExpiryDate.isAfter(LocalDate.now())) {
                        break;  // Valid expiry date
                    } else {
                        System.out.println(" Passport expiry date must be a future date. Please try again.");
                    }
                }

                String email = InputUtils.readEmail(reader, "Email Address: ");
                LocalDate dob = InputUtils.readDate(reader, "Date of Birth (YYYY-MM-DD): ");
                String gender = InputUtils.readString(reader, "Gender (M/F/Other): ");

                return new AddCustomer(name, phone, age, address, country, passportNumber, passportExpiryDate, disabled,email, dob, gender);

            }
            else if (cmd.equals("loadgui")) {
                return new LoadGUI();

            } else if (cmd.equals("exit")) {
                System.exit(0);

            // Handle commands with no arguments or a single argument
            } else if (parts.length == 1) {
                
                if (cmd.equals("listflights")) {
                    return new ListFlights();
                } else if (cmd.equals("listcustomers")) {
                    return new ListCustomers();
                } else if (cmd.equals("listbookings")) {
                    return new ListBookings(); }
                else if (cmd.equals("interactivebooking")) {
                        return new InteractiveBookings();
                    }else if (cmd.equals("updatecustomer")) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                        // 1. Show all customers before asking for ID
                        System.out.printf("%-5s %-20s %-15s %-5s %-15s %-25s %-10s %-10s\n",
                            "ID", "Name", "Phone", "Age", "Country", "Email", "Gender", "Disabled");
                        System.out.println("-----------------------------------------------------------------------------------------------------");
                        for (Customer c : system.getAllCustomers()) {
                            System.out.println(c.getDetailsShort());
                        }

                        System.out.print("\nEnter Customer ID to update (press Enter to cancel): ");
                        String idInput = reader.readLine().trim();

                        if (idInput.isEmpty()) {
                            System.out.println(" Update cancelled.");
                            return null;
                        }

                        int customerId;
                        try {
                            customerId = Integer.parseInt(idInput);
                        } catch (NumberFormatException e) {
                            System.out.println(" Invalid number. Update cancelled.");
                            return null;
                        }


                        Customer customer = system.getCustomerById(customerId);
                        if (customer == null) {
                            throw new FlightBookingSystemException("Customer not found.");
                        }

                        // Prompt fields with current values
                        String newName = promptUpdateField(reader, "Name", customer.getName());
                        String newPhone = customer.getPhone();
                        while (true) {
                            System.out.printf("New Phone [%s]: ", newPhone);
                            String input = reader.readLine().trim();
                            if (input.isEmpty()) {
                                // Keep old value
                                break;
                            } else if (!input.matches("\\d{10}")) {
                                System.out.println(" Phone number must be exactly 10 digits.");
                            } else {
                                newPhone = input;
                                break;
                            }
                        }

                        Integer newAge = promptUpdateIntField(reader, "Age", customer.getAge());
                        String newAddress = promptUpdateField(reader, "Address", customer.getAddress());
                        String newPassport = promptUpdateField(reader, "Passport Number", customer.getPassportNumber());
                        LocalDate newPassportExpiry = promptUpdatePassportExpiryField(reader, "Passport Expiry", customer.getPassportExpiryDate());
                        String newEmail = promptUpdateField(reader, "Email", customer.getEmail());
                        LocalDate newDob = promptUpdateDateField(reader, "Date of Birth", customer.getDob());
                        String newGender = promptUpdateField(reader, "Gender", customer.getGender());

                        Boolean newDisabled = null;
                        System.out.print("Update Disabled status? (y/n): ");
                        if (reader.readLine().trim().equalsIgnoreCase("y")) {
                            System.out.print("Is the customer disabled? (yes/no): ");
                            String input = reader.readLine().trim().toLowerCase();
                            newDisabled = input.equals("yes") || input.equals("y");
                        }

                        return new UpdateCustomer(customerId, newName, newPhone, newAge, newAddress,
                                                  newPassport, newPassportExpiry, newEmail, newDob, newGender, newDisabled);
                    }

            }

            // Handle commands with two arguments
            else if (parts.length == 2) {
                int id = Integer.parseInt(parts[1]);

                if (cmd.equals("showflight")) {
                    return new ShowFlight(id);
                } else if (cmd.equals("showcustomer")) {
                    return new ShowCustomer(id);
                } else if (cmd.equals("editbooking")) {  
                    return new EditBookings(id);
                } else if (cmd.equals("showbookings")) {
                    return new ShowBookings(id);
                } else if (cmd.equals("removecustomer")) {
                    return new RemoveCustomer(id);  
                } else if (cmd.equals("removeflight")) {  // Add this new case
                return new RemoveFlight(id);
            }

            }

            if (cmd.equals("addbooking") && parts.length == 4) {
                int customerId = Integer.parseInt(parts[1]);
                int flightId = Integer.parseInt(parts[2]);
                Booking.SeatClass seatClass = Booking.SeatClass.valueOf(parts[3].toUpperCase());
                return new AddBooking(customerId, flightId, seatClass);
            }

            // Handle addbooking with default ECONOMY class (3 arguments)
            else if (cmd.equals("addbooking") && parts.length == 3) {
                int customerId = Integer.parseInt(parts[1]);
                int flightId = Integer.parseInt(parts[2]);
                return new AddBooking(customerId, flightId, Booking.SeatClass.ECONOMY);
            }

            // Handle cancelbooking
            else if (cmd.equals("removebooking") && parts.length == 3) {
                int customerId = Integer.parseInt(parts[1]);
                int flightId = Integer.parseInt(parts[2]);
                return new CancelBookings(customerId, flightId);
            }


        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Invalid number format in command.");
        }

        throw new FlightBookingSystemException("Invalid command.");
    }
    /**
     * Utility method to parse a LocalDate from the input with retry attempts.
     *
     * @param reader the BufferedReader for input
     * @param prompt the prompt to display to the user
     * @return the parsed LocalDate
     * @throws IOException if an input error occurs
     */

    private static LocalDate parseDateWithAttempts(BufferedReader br, int attempts, String prompt) throws IOException, FlightBookingSystemException {
        if (attempts < 1) {
            throw new IllegalArgumentException("Number of attempts should be higher than 0");
        }
        while (attempts > 0) {
            attempts--;
            System.out.print(prompt);
            try {
                LocalDate date = LocalDate.parse(br.readLine());
                return date;
            } catch (DateTimeParseException dtpe) {
                System.out.println("Date must be in YYYY-MM-DD format. " + attempts + " attempts remaining...");
            }
        }
        throw new FlightBookingSystemException("Incorrect date provided. Cannot proceed.");
    }

    private static LocalDate parseDateWithAttempts(BufferedReader br, String prompt) throws IOException, FlightBookingSystemException {
        return parseDateWithAttempts(br, 3, prompt);
    }
    public static LocalTime parseTimeWithAttempts(BufferedReader br, String prompt) throws IOException, FlightBookingSystemException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < 3; i++) {
            System.out.print(prompt);
            String input = br.readLine().trim();
            try {
                return LocalTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please use HH:MM (e.g., 14:30). Attempts left: " + (2 - i));
            }
        }

        throw new FlightBookingSystemException("Failed to parse time after 3 attempts.");
    }

    private static Integer safeParseInt(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(reader.readLine());
                if (value < 0) throw new NumberFormatException();
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a non-negative whole number.");
            }
        }
    }

    private static Double safeParseDouble(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(reader.readLine());
                if (value < 0) throw new NumberFormatException();
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a non-negative number.");
            }
        }
    }
    private static String promptUpdateField(BufferedReader reader, String label, String currentValue) throws IOException {
        System.out.print("New " + label + " (Current: " + currentValue + ") or press Enter to keep: ");
        String input = reader.readLine();
        return input.isBlank() ? null : input;
    }

    private static Integer promptUpdateIntField(BufferedReader reader, String label, int currentValue) throws IOException {
        System.out.print("New " + label + " (Current: " + currentValue + ") or press Enter to keep: ");
        String input = reader.readLine();
        return input.isBlank() ? null : Integer.parseInt(input);
    }

    private static LocalDate promptUpdateDateField(BufferedReader reader, String label, LocalDate currentValue) throws IOException {
        System.out.print("New " + label + " (Current: " + currentValue + ", format YYYY-MM-DD) or press Enter to keep: ");
        String input = reader.readLine();
        return input.isBlank() ? null : LocalDate.parse(input);
    }
    private static LocalDate promptUpdatePassportExpiryField(BufferedReader reader, String label, LocalDate currentValue) throws IOException {
        while (true) {
            System.out.print("New " + label + " (Current: " + currentValue + ", format YYYY-MM-DD) or press Enter to keep: ");
            String input = reader.readLine().trim();

            if (input.isEmpty()) {
                return null; // keep current value
            }

            try {
                LocalDate newDate = LocalDate.parse(input);
                if (newDate.isAfter(LocalDate.now())) {
                    return newDate;
                } else {
                    System.out.println(" Passport expiry date must be in the future. Please try again.");
                }
            } catch (Exception e) {
                System.out.println(" Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

   

}
