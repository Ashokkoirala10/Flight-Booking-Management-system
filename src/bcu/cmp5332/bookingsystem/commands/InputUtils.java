package bcu.cmp5332.bookingsystem.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Utility class providing methods to safely read and validate user inputs from a BufferedReader.
 */
public class InputUtils {

    /**
     * Reads a non-empty trimmed string from the user after displaying a prompt.
     *
     * @param reader the BufferedReader to read input from
     * @param prompt the message to display to the user
     * @return the non-empty user input as a trimmed string
     * @throws IOException if an I/O error occurs
     */
	public static String readNonEmptyString(BufferedReader reader, String prompt) throws IOException {
	    while (true) {
	        System.out.print(prompt);
	        String input = reader.readLine().trim();

	        if (input.equalsIgnoreCase("back")) {
	            System.out.println("Going back...");
	            return null; // or break, or throw exception depending on your program flow
	        } else if (input.isEmpty()) {
	            System.out.println("Input cannot be empty. Please enter a valid value or press back to have null.");
	        } else {
	            return input;
	        }
	    }
	}


    /**
     * Reads a trimmed string from the user after displaying a prompt (can be empty).
     *
     * @param reader the BufferedReader to read input from
     * @param prompt the message to display to the user
     * @return the user input as a trimmed string
     * @throws IOException if an I/O error occurs
     */
    public static String readString(BufferedReader reader, String prompt) throws IOException {
        System.out.print(prompt);
        return reader.readLine().trim();
    }

    /**
     * Reads a positive integer value from the user, validating input and reprompting on errors.
     *
     * @param reader the BufferedReader to read input from
     * @param prompt the message to display to the user
     * @return the positive integer entered by the user
     * @throws IOException if an I/O error occurs
     */
    public static int readInt(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(reader.readLine().trim());
                if (value <= 0) {
                    System.out.println(" Value must be greater than 0.");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println(" Invalid number. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads a phone number consisting exactly of 10 digits.
     *
     * @param reader the BufferedReader to read input from
     * @param prompt the message to display to the user
     * @return the valid phone number string (exactly 10 digits)
     * @throws IOException if an I/O error occurs
     */
    public static String readPhoneNumber(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String input = reader.readLine().trim();
            if (!input.matches("\\d{10}")) {
                System.out.println(" Phone number must be exactly 10 digits.");
            } else {
                return input;
            }
        }
    }

    /**
     * Reads a passport number with a maximum length of 9 characters.
     *
     * @param reader the BufferedReader to read input from
     * @param prompt the message to display to the user
     * @return the valid passport number string
     * @throws IOException if an I/O error occurs
     */
    public static String readPassportNumber(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String input = reader.readLine().trim();
            if (input.length() > 9) {
                System.out.println("❌ Passport number must not exceed 9 characters.");
            } else {
                return input;
            }
        }
    }

    /**
     * Reads a date from the user in the format YYYY-MM-DD.
     *
     * @param reader the BufferedReader to read input from
     * @param prompt the message to display to the user
     * @return the LocalDate parsed from user input
     * @throws IOException if an I/O error occurs
     */
    public static LocalDate readDate(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(reader.readLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println(" Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    /**
     * Reads a boolean value from the user, accepting yes/no, y/n, true/false (case-insensitive).
     *
     * @param reader the BufferedReader to read input from
     * @param prompt the message to display to the user
     * @return true if user input is affirmative, false if negative
     */
    public static boolean readBoolean(BufferedReader reader, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = reader.readLine().trim().toLowerCase();

                if (input.equals("yes") || input.equals("y") || input.equals("true")) {
                    return true;
                } else if (input.equals("no") || input.equals("n") || input.equals("false")) {
                    return false;
                } else {
                    System.out.println(" Please enter 'yes' or 'no'.");
                }
            } catch (Exception e) {
                System.out.println(" Error reading input. Please try again.");
            }
        }
    }

    /**
     * Reads a valid age value (integer between 0 and 130) from the user.
     *
     * @param reader the BufferedReader to read input from
     * @param prompt the message to display to the user
     * @return the age as an integer
     * @throws IOException if an I/O error occurs
     */
    public static int readAge(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String input = reader.readLine().trim();

            if (input.isEmpty()) {
                System.out.println(" Age cannot be empty.");
                continue;
            }

            try {
                int age = Integer.parseInt(input);
                if (age <= 0 || age > 130) {
                    System.out.println(" Please enter a valid age between 0 and 130.");
                } else {
                    return age;
                }
            } catch (NumberFormatException e) {
                System.out.println(" Invalid number. Please enter a valid integer for age.");
            }
        }
    }

    /**
     * Reads an email address from the user, validating the format with a regex.
     *
     * @param reader the BufferedReader to read input from
     * @param prompt the message to display to the user
     * @return the valid email address string
     */
    public static String readEmail(BufferedReader reader, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = reader.readLine().trim();
                if (input.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                    return input;
                } else {
                    System.out.println(" Invalid email format.");
                }
            } catch (Exception e) {
                System.out.println(" Error reading input.");
            }
        }
    }
}
