package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.IOException;

/**
 * Interface representing a data manager responsible for loading and storing
 * parts of the flight booking system data.
 * <p>
 * Implementations of this interface handle specific data domains such as flights,
 * customers, or bookings.
 * </p>
 */
public interface DataManager {
    
    /**
     * The separator string used to split and join data fields in storage files.
     */
    public static final String SEPARATOR = "::";
    
    /**
     * Loads data into the provided {@link FlightBookingSystem} instance.
     * 
     * @param fbs the flight booking system instance to populate with data
     * @throws IOException if an I/O error occurs during loading
     * @throws FlightBookingSystemException if the data is invalid or cannot be parsed
     */
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException;
    
    /**
     * Stores data from the provided {@link FlightBookingSystem} instance to persistent storage.
     * 
     * @param fbs the flight booking system instance containing data to save
     * @throws IOException if an I/O error occurs during storing
     */
    public void storeData(FlightBookingSystem fbs) throws IOException;
    
}
