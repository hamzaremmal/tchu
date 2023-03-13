package ch.epfl.tchu.game;

/**
 * An abstraction to verify if Stations are connected.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
@FunctionalInterface
public interface StationConnectivity {

    /**
     * Check if the given Stations are connected
     * @param s1 (Station) - The first station
     * @param s2 (Station) - The second Station
     * @return (boolean) - True if they are connected, otherwise false.
     */
    boolean connected(Station s1, Station s2);
}
