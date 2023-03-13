package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Gives an abstract representation of a Station in the game.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public final class Station {
    
    private final int id;
    private final String name;
    
    /**
     * Build a new Station object with the given parameters.
     * @param id (int) - The id of the Station.
     * @param name (String) - The name of the Station.
     * @throws {@code IllegalArgumentException} if the id is a negative number.
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id   = id;
        this.name = name;
    }

    /**
     * Get the id of the Station.
     * @return (int) - The id of the Station.
     */
    public int id() { return id;}
    
    /**
     * Get the name of the Station.
     * @return (String) - The name of the Station.
     */
    public String name() { return name;}
    
    /**
     * Get the name of the Station.
     * @return (String) - The name of the Station.
     */
    @Override
    public String toString() { return name;}
}
