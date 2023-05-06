package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ch.epfl.tchu.Preconditions;

/**
 * Gives an abstract concept of a Trip in the game.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public final class Trip {
    
    private final Station from; 
    private final Station to;
    private final int points;
    
    /**
     * Build a Trip object with the given parameters
     * @param from (Station) - The departure Station.
     * @param to (Station) - The arrival Station.
     * @param points (int) - The Trip's points.
     * @throws IllegalArgumentException if {@code points <= 0}.
     * @throws NullPointerException if one of the stations is null.
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from   = Objects.requireNonNull(from);
        this.to     = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Return a List of Trip with all the possibilities with the given parameters.
     * @param from (List) - List of the departure Stations.
     * @param to (List) - List of the arrival Stations.
     * @param points (int) - The points of all the computed Trips
     * @throws IllegalArgumentException if the {@code points <= 0} or the lists in parameters are {@code null}.
     * @return (List) - List of all possible Trips.
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        Preconditions.checkArgument(points > 0 && Objects.nonNull(from) && Objects.nonNull(to));
        List<Trip> list = new ArrayList<>();
        from.forEach(stationFrom -> to.forEach(stationTo -> list.add(new Trip(stationFrom,stationTo,points))));
        return list;
    }
    
    /**
     * Get the departure Station of the given Trip.
     * @return (Station) - The departure Station of the given Trip.
     */
    public Station from() { return from;}
    
    /**
     * Get the arrival Station of the given Trip.
     * @return (Station) - The arrival Station of the given Trip.
     */
    public Station to() { return to;}
    
    /**
     * Get the points of a Trip.
     * @return (int) - Trip's points.
     */
    public int points() { return points;}
    
    /**
     * Get the points we get if the stations are connected in the game.
     * @param connectivity (StationConnectivity) - 
     * @return (int) - Number of points.
     */
    public int points(StationConnectivity connectivity) { return connectivity.connected(from,to) ? points : -points;}
    
}
