package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import ch.epfl.tchu.Preconditions;

/**
 * An abstraction of the public part of the GameState.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
public class PublicGameState {
    
    
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;
    
    /**
     * Construct a new PublicGameState object with the given parameters.
     * @param ticketsCount (int) - Number of available tickets.
     * @param cardState (PublicCardState) - The public part of the CardState of the game.
     * @param currentPlayerId (PlayerId) - The id of the current player.
     * @param playerState (Map<PlayerId, PublicPlayerState>) - The mapping between every id and its PublicPlayerState object.
     * @param lastPlayer (PlayerId) - The PlayerId of the last player.
     * @throws {@code IllegalArgumentException} if the number of tickets is negative or the size of playerState is not equal to 2.
     * @throws {@code NullPointerException} if the currentPlayerId or the cardState are null.
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == 2);
        this.ticketsCount    = ticketsCount;
        this.cardState       = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState     = Map.copyOf(playerState);
        this.lastPlayer      = lastPlayer;
    }
    
    /**
     * Gets the number of available tickets.
     * @return (int) - number of available tickets.
     */
    public int ticketsCount() { return this.ticketsCount;}

    /**
     * Check if the player can draw a ticket.
     * @return (boolean) - true iff the player can draw a ticket. 
     */
    public boolean canDrawTickets() { return this.ticketsCount > 0;}
    
    /**
     *  Get the public part of the CardState of the game.
     * @return (PublicCardState) - The public part of the CardState.
     */
    public PublicCardState cardState() { return this.cardState;}
    
    /**
     * Check if the player can draw a card.
     * @return (boolean) - true iff the player can draw a card.
     */
    public boolean canDrawCards() { return (this.cardState.deckSize() + this.cardState.discardsSize()) >= 5;}
    
    /**
     * Get the playerId of the current player.
     * @return (PlayerId) - The current player id.
     */
    public PlayerId currentPlayerId() { return this.currentPlayerId;}
    
    /**
     * Request the PublicPlayerState matching with the given playerId.
     * @param playerId (PlayerId) - The id of the requested playerState.
     * @return (PublicPlayerState) - The public part of the playerState matching with the playerId.
     */
    public PublicPlayerState playerState(PlayerId playerId) { return this.playerState.get(playerId);}
    
    /**
     * Request the PublicPlayerState matching with with the current player.
     * @return (PublicPlayerState) - The public part of the playerState matching with the current player.
     */
    public PublicPlayerState currentPlayerState() { return this.playerState(currentPlayerId);}
    
    /**
     * Gets the list of all claimed routes of the player.
     * @return (List<Route>) - List of all claimed routes.
     */
    public List<Route> claimedRoutes(){
        List<Route> allRoutes = new ArrayList<>();
        allRoutes.addAll(this.playerState(PlayerId.PLAYER_1).routes());
        allRoutes.addAll(this.playerState(PlayerId.PLAYER_2).routes());
        return allRoutes;
    }
    
    /**
     * Gets the last player of the game.
     * @return (PlayerId) - null if the last player is unknown.
     */
    public PlayerId lastPlayer() { return this.lastPlayer;}
}