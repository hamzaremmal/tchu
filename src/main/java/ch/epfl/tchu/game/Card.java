package ch.epfl.tchu.game;

import java.util.List;

/**
 * Represents the cards used in game.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public enum Card {
    
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(Color.NEUTRAL);
    
    private final Color color;
    
    /**
     * A list of all cards.
     */
    public static final  List<Card> ALL = List.of(Card.values());
    
    /**
     * List of all cars.
     */
    public static final List<Card> CARS = ALL.subList(BLACK.ordinal(), LOCOMOTIVE.ordinal());
    
    /**
     * Number of cards.
     */
    public static final int COUNT = ALL.size();
    
    private Card(Color color) { this.color = color;}
    
    /**
     * Returns the card that has the parameter's color
     * @param color(Color) - color of the card
     * @return (Card) - card that has the same color
     */
    public static Card of(Color color) {
        switch(color) {
            case BLACK  : return Card.BLACK;
            case VIOLET : return Card.VIOLET;
            case BLUE   : return Card.BLUE;
            case GREEN  : return Card.GREEN;
            case YELLOW : return Card.YELLOW;
            case ORANGE : return Card.ORANGE;
            case RED    : return Card.RED;
            case WHITE  : return Card.WHITE;
            case NEUTRAL: return Card.LOCOMOTIVE;
            default: throw new IllegalArgumentException("This Color doesn't correspond to any of the cards in this enum.");
        }
    }
    
    /**
     * returns the color of the card
     * @return (Color) - color of the card
     */
    public Color color() { return this.color;}
   
}