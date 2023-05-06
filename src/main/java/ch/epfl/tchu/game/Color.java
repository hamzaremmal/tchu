package ch.epfl.tchu.game;

import java.util.List;

/**
 * Represent the color used in tchu.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public enum Color {
    /** ??? */
    BLACK,
    /** ??? */
    VIOLET,
    /** ??? */
    BLUE,
    /** ??? */
    GREEN,
    /** ??? */
    YELLOW,
    /** ??? */
    ORANGE,
    /** ??? */
    RED,
    /** ??? */
    WHITE,
    /** ??? */
    NEUTRAL;

    /**
     * List of all colors.
     */
    public static final List<Color> ALL = List.of(Color.values());
    
    /**
     * Number of different color.
     */
    public static final int COUNT = ALL.size() - 1;
}
