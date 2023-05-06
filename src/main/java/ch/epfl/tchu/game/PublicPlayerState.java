package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;

import ch.epfl.tchu.Preconditions;

/**
 * An abstraction of the public part of the PlayerState.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475) 
 */
public class PublicPlayerState {
     
     private final int ticketCount;
     private final int cardCount;
     private final List<Route> routes;
     private final int carCount;
     private final int claimPoints;
     /**
      * Creates an instance of the class.
      * @param ticketCount (int) -number of tickets
      * @param cardCount (int) -number of cards
      * @param routes (List) -routes of the player
      * @throws IllegalArgumentException if the ticketCount or cardCount are negative numbers.
      * 
      */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        Preconditions.checkArgument(ticketCount >=0 && cardCount>=0);
        this.cardCount   = cardCount;
        this.ticketCount = ticketCount;
        this.routes      = List.copyOf(routes);  
        this.carCount    = Constants.INITIAL_CAR_COUNT - PublicPlayerState.computeLength(routes);
        this.claimPoints = PublicPlayerState.computePoints(routes);
     }
    
    /**
     * Get the number of tickets.
     * @return (int) - The number tickets 
     */
    public int ticketCount() { return this.ticketCount;}
    
    /**
     * Get the number of cards.
     * @return (int) - the number of cards
     */
    public int cardCount() { return this.cardCount;}
    
    /**
     * Get the routes of the player.
     * @return (List) - the list of routes
     */
    public List<Route> routes(){ return Collections.unmodifiableList(routes);}
    
    /**
     * Returns the number of cars
     * @return (int) - the number of cars
     */
    public int carCount() { return this.carCount;}
    
    /**
     * Get the number of points obtained when claiming the routes
     * @return (int) - the number of cards
     */
    public int claimPoints() { return this.claimPoints;}
    
    /**
     * Compute the points won by the claimed routes.
     * @param routes (List<Route>) - routes to use.
     * @return (int) - The points won with those routes.
     */
    private static int computePoints(List<Route> routes) {
        var points = 0;
        for (var route : routes) 
            points += route.claimPoints();      
        return points;   
    }
    
    /**
     * Compute the total length of all given routes.
     * @param routes (List<Route>) - List of routes.
     * @return (int) - The total length of all routes.
     */
    private static int computeLength(List<Route> routes) {
        var length = 0;
        for (var route : routes)
            length += route.length();
        return length;
    }
    
    
   
 }