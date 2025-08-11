package bcu.cmp5332.bookingsystem.model;

/**
 * Enum representing the possible statuses of a flight.
 */
public enum FlightStatus {
    /** Flight is scheduled and expected to depart on time. */
    SCHEDULED,
    
    /** Flight departure has been delayed. */
    DELAYED,
    
    /** Flight has been cancelled. */
    CANCELLED,
    
    /** Flight has been completed. */
    COMPLETED;

    /**
     * Converts a string to the corresponding FlightStatus enum value,
     * ignoring case differences.
     *
     * @param status the string representation of the flight status
     * @return the matching FlightStatus, or null if no match found
     */
    public static FlightStatus fromString(String status) {
        for (FlightStatus fs : FlightStatus.values()) {
            if (fs.name().equalsIgnoreCase(status)) {
                return fs;
            }
        }
        return null; // or throw new IllegalArgumentException("Invalid FlightStatus: " + status);
    }

    /**
     * Compares this FlightStatus with a string, ignoring case differences.
     *
     * @param status the string to compare with
     * @return true if the string matches this status ignoring case, false otherwise
     */
    public boolean equalsIgnoreCase(String status) {
        return this.name().equalsIgnoreCase(status);
    }
}
