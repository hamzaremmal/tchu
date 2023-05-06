package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import ch.epfl.tchu.Preconditions;

/**
 * Represents the ticket that allows trips in game.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public final class Ticket  implements Comparable<Ticket>{

	private final List<Trip> trips;
	private final String text;
	
	
	/**
	 * Builds ticket.
	 * @param trips (List) -trips supported by the ticket
	 * @throws IllegalArgumentException if trips is null or empty.
	 * @throws IllegalArgumentException if trips is null or empty.
	 */
	public Ticket(List<Trip> trips) {
		Preconditions.checkArgument(Objects.nonNull(trips) && trips.size() > 0);
		for(var i = 0 ; i < trips.size() ; i++)
			Preconditions.checkArgument(trips.get(i).from().name().equals(trips.get(0).from().name()));   
		this.trips = List.copyOf(trips);
		this.text = formatText();
	}
	
	
	/**
	 * Builds a ticket that allows a single trip from the first station to the second
	 * @param from (Station) - The departure Station.
	 * @param to (Station) -The arrivalStation.
	 * @param points (int) -points the trip is worth
	 */
	public Ticket(Station from, Station to, int points) { this(List.of(new Trip(from,to,points)));}
	
	/**
	 * Returns the maximal amount of points the ticket is worth.
	 * @param connectivity (StationConnectivity) -
	 * @return (int) -
	 */
	public int points(StationConnectivity connectivity) {
		List<Integer> points = new ArrayList<>();
		for(var i = 0 ; i < trips.size() ; i++)
			points.add(trips.get(i).points(connectivity));
		var x = 0;
		List<Integer> sanction = new ArrayList<>();
		for(var i = 0 ; i < points.size() ; i++) {
			if(points.get(i) > x) x = points.get(i);
			if(points.get(i) < 0) sanction.add(points.get(i));	
		}
		if(x == 0) { 
			x=sanction.get(0);
			for (var i = 1 ; i < sanction.size() ; i++)
				if(sanction.get(i) > x) x = sanction.get(i);
		}
		 return x;
	}
	
	/**
	 * Creates the textual representation of the ticket.
	 * @return (String) - The textual representation of a Ticket.
	 */
	public String formatText() {
		List<String> arrive= new ArrayList<>();
		List<Integer> points= new ArrayList<>();
		for(var i = 0 ; i < trips.size() ; i++) {
			var trip = trips.get(i);
			arrive.add(trip.to().name());
			points.add(trip.points());
		}
		return Ticket.textCompute(trips.get(0).from().name(), arrive, points);
	}
	
	/**
     * Get the textual representation of the ticket.
     * @return (String) - The textual representation of a Ticket.
     */
	public String text() {
	    return text;
	}
	
	/**
     * Computes the text of the ticket.
     * @param str (String) -Name of the first station
     * @param arrive (List<String>) - List of the names of the arrival stations
     * @param points (List<Integer>) -points given by each trip
     * @throws {@code IllegalArgumentException} if arrive.size() != points.size()
     * @return (String) -
     */
	private static String textCompute(String str,List<String> arrive,List<Integer> points) {
		Preconditions.checkArgument(arrive.size() == points.size());
		var s = new TreeSet<String>();
		for(var i = 0; i < arrive.size(); i++)
			s.add(arrive.get(i) + " (" + points.get(i) + ")");
		var solution = "";
		if(s.size() > 1) solution = str + " - " + "{" + String.join(", ", s) + "}";
		else  		     solution = str + " - " + s.first();
		return solution;
	}
	
	/**
	 * Compare the text by alphabetical order with the given Ticket.
	 * @param that (Ticket) - The ticket to compare to.
	 * @return (int) - {@code (-1) if this < that}  or {@code (0) if this == that} or {@code (1) if this > that}.
	 */
	@Override
	public int compareTo(Ticket that) {
		return this.text().compareTo(that.text());
	}
	
	/**
     * Get the textual representation of the ticket.
     * @return (String) - The textual representation of a Ticket.
     */
	@Override
	public String toString() {
	    return text();
	}
	

}