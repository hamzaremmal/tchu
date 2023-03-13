package ch.epfl.tchu.game;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;

/**
 * Represent the abstraction of a player in the game.
 * @author Hamza REMMAL (310917) -
 * @author Mehdi ZIAZI (311475)
 *
 */
public interface Player {
    
    /**
     * Represent the action a player can do in the game.
     * @author Hamza REMMAL (310917) -
     * @author Mehdi ZIAZI (311475)
     *
     */
    public enum TurnKind {
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;
        public static final List<TurnKind> ALL = List.of(TurnKind.values());
    }

    /**
     * 
     * @param ownId
     * @param playerNames
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);
    
    /**
     * 
     * @param info
     */
    void receiveInfo(String info);
    
    /**
     * 
     * @param newState
     * @param ownState
     */
    void updateState(PublicGameState newState, PlayerState ownState);
    
    /**
     * 
     * @param tickets
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);
    
    /**
     * 
     * @return
     */
    SortedBag<Ticket> chooseInitialTickets();
    
    /**
     * 
     * @return
     */
    TurnKind nextTurn();
    
    /**
     * 
     * @param options
     * @return
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);
    
    /**
     * 
     * @return
     */
    int drawSlot();
    
    /**
     * 
     * @return
     */
    Route claimedRoute();
    
    /**
     * 
     * @return
     */
    SortedBag<Card> initialClaimCards();
    
    /**
     * 
     * @param options
     * @return
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);
}
