package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * Representation of a route in the game.
 * A route links between two stations.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public final class Route {
    
  /**
   * Enum represents the different types of routes in tchu.
   * @author Hamza REMMAL (310917)
   * @author Mehdi ZIAZI (311475)
   */
	public enum Level{
        /** ??? */
		OVERGROUND ,
        /** ??? */
		UNDERGROUND;	
	}
	
	private final Station[] stations;
	private final String id; 
	private final Level level;
	private final int length;
	private final Color color;
	
	/**
	 * Builds a route with the given parameters.
	 * @param id (String) - id of the route
	 * @param station1 (Station) -The first Station.
	 * @param station2 (Station) -The second Station.
	 * @param length (int) - length of the route
	 * @param level (Level) -level of the route
	 * @param color (Color) - color of the route
	 * @throws IllegalArgumentException if the length are not in between {@code Constants.MIN_ROUTE_LENGTH} (inclusive) and {@code Constants.MAX_ROUTE_LENGTH} (inclusive), or the stations are the same.
	 * @throws NullPointerException if one of the stations are null
	 */
	public Route(String id, Station station1, Station station2, int length, Level level, Color color){
		Preconditions.checkArgument(!(station1.equals(station2)));
		Preconditions.checkArgument(length >= Constants.MIN_ROUTE_LENGTH && length <= Constants.MAX_ROUTE_LENGTH);
		this.stations = new Station[] {Objects.requireNonNull(station1),Objects.requireNonNull(station2)};
		this.id       = Objects.requireNonNull(id);
		this.level    = Objects.requireNonNull(level);
		this.color    = color;
		this.length   = length;	
	}

	/**
	 * Get the level of the route.
	 * @return (Level) - The level of the route.
	 */
	public Level level() { return level;}

	/**
	 * Get the length of the route.
	 * @return (int) - The length of the route.
	 */
	public int length() { return length;}

	/**
	 * Get the color of the route
	 * @return (Color) - The color of the route.
	 */
	public Color color() { return color;}

	/**
     * Get the id of the route.
     * @return (String) - The id of the route.
     */
	public String id() { return id;}
	
	/**
	 * Get a list of the two stations of the route
	 * @return (List) - List of the stations of the route.
	 */
	public List<Station> stations(){ return List.of(stations[0],stations[1]);}
	
	/**
     * Get the first station.
     * @return (Station) - The first station of the route.
     */
	public Station station1() { return this.stations[0];}
	
	/**
	 * Get the second station
	 * @return (Station) - The second station of the route
	 */
	public Station station2() { return this.stations[1];}
	
	/**
	 * Get the opposite station linked by the route.
	 * @param station (Station) - A station of the route.
	 * @throws IllegalArgumentException if the given station is not part of the route.
	 * @return (Station) - The opposite station.
	 */
	public Station stationOpposite(Station station) {
		Preconditions.checkArgument(station == this.stations[0] || station == this.stations[1]);
		return  station == this.stations[0] ? this.stations[1] : this.stations[0];
	}
	
	/**
	 * Get the points the route is worth when controlled by a player
	 * @return (int) - Points of the route
	 */
	public int claimPoints() { return Constants.ROUTE_CLAIM_POINTS.get(this.length);}
	
	/**
	 * Get all the combinations that can be played to control the route.
	 * @return (List) - all the combinations.
	 */
	public List<SortedBag<Card>> possibleClaimCards(){
        List<SortedBag<Card>> array = new ArrayList<>();
        switch(level) {
        case OVERGROUND:
            if(Objects.isNull(color)) {
                for (var i = 0; i < Color.COUNT; i++)
                   array.add(SortedBag.of(this.length, Card.of(Color.values()[i])));
               } else {
                   SortedBag<Card> bag = SortedBag.of(this.length, Card.of(this.color));
                   return List.of(bag);
                   }
            break;
        case UNDERGROUND :
            if(Objects.isNull(color)) {
                for (var i = 0; i <this.length; i++) {
                    for (var j = 0; j < Color.COUNT; j++) 
                        array.add(SortedBag.of(this.length-i,Card.of(Color.values()[j]),i,Card.LOCOMOTIVE));                       
                    }
                array.add(SortedBag.of(this.length, Card.LOCOMOTIVE));
                } else {
                    for (var i = 0; i <= this.length; i++) 
                        array.add(SortedBag.of(this.length-i,Card.of(this.color), i,Card.LOCOMOTIVE));               
                    }
            break;
            }
        return array;
   }
	
	/**
	 * Get the number of the card that can be played (from the drawn cards) 
	 * @param claimCards (SortedBag) - cards the player has chosen to play
	 * @param drawnCards (SortedBag) - cards drawn
	 * @throws IllegalArgumentException if {@code this.level() != Level.UNDERGROUND} or {@code drawnCards.size() != 3}.
	 * @return (int) - ???
	 */
	public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(this.level == Level.UNDERGROUND && drawnCards.size() == 3);
        var index = 0;
        for (var card : drawnCards) {
            if(claimCards.contains(card)) ++index;
            else if(card.equals(Card.LOCOMOTIVE)) ++index;
        }
        return index;
    }
	
}
