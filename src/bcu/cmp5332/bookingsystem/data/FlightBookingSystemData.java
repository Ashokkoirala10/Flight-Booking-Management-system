package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that manages the loading and storing of all flight booking system data.
 * <p>
 * This class maintains a static list of {@link DataManager} instances responsible
 * for handling different parts of the data (flights, customers, bookings).
 * It provides methods to load the entire system state from files and to persist
 * the current system state back to those files.
 * </p>
 */
public class FlightBookingSystemData {
    
    private static final List<DataManager> dataManagers = new ArrayList<>();
    
    // Static initializer block that registers all DataManager implementations.
    static {
        dataManagers.add(new FlightDataManager());
        dataManagers.add(new CustomerDataManager());
        dataManagers.add(new BookingDataManager());
    }
    
    /**
     * Loads the entire flight booking system data by delegating to all registered
     * data managers.
     * 
     * @return a populated {@link FlightBookingSystem} instance with loaded data
     * @throws FlightBookingSystemException if any data manager encounters an error
     * @throws IOException if there is an I/O problem reading data files
     */
    public static FlightBookingSystem load() throws FlightBookingSystemException, IOException {
        FlightBookingSystem fbs = new FlightBookingSystem();
        for (DataManager dm : dataManagers) {
            dm.loadData(fbs);
        }
        return fbs;
    }

    /**
     * Stores all data from the given {@link FlightBookingSystem} by delegating to all
     * registered data managers.
     * 
     * @param fbs the flight booking system instance containing data to save
     * @throws IOException if there is an I/O problem writing data files
     */
    public static void store(FlightBookingSystem fbs) throws IOException {
        for (DataManager dm : dataManagers) {
            dm.storeData(fbs);
        }
    }
}
