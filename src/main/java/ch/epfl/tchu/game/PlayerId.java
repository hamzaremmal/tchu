package ch.epfl.tchu.game;

import java.util.List;

/**
 * Represent the id of every player.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (310917)
 *
 */
public enum PlayerId {
    /** ??? */
    PLAYER_1,
    /** ??? */
    PLAYER_2;
    
    /**
     * List of all playerId.
     */
    public static final List<PlayerId> ALL = List.of(PlayerId.values());
    
    /**
     * The number of ids in the game.
     */
    public static final int COUNT = ALL.size();
    
    /**
     * 
     * @return (PlayerId) - The next player.
     */
    public PlayerId next() { return this == PLAYER_1 ? PLAYER_2 : PLAYER_1;}
}
