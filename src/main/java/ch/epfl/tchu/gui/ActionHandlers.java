package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * This interface contains all the handlers interfaces used in this project
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
public interface ActionHandlers {
    
    /**
     * This interface represents the handler to be used when the player chooses to draw tickets
     * @author Hamza REMMAL (310917)
     * @author Mehdi ZIAZI (311475)
     */
    @FunctionalInterface
    public interface DrawTicketsHandler extends ActionHandlers{
        /**
         * communicates the kind of action performed (draw tickets) to the game thread
         */
         void onDrawTickets();
    }
    
    /**
     * This interface represents the handler to be used when the player chooses to draw cards
     * @author Hamza REMMAL (310917)
     * @author Mehdi ZIAZI (311475)
     */
    @FunctionalInterface
    public interface DrawCardHandler extends ActionHandlers{
        /**
         * communicates the kind of action performed (draw cards) to the game thread
         * @param slot (int) -the index of the face up card chosen or -1 if the deck was chosen
         */
        void onDrawCard(int slot);
    }
    
    /**
     * This interface represents the handler to be used when the player chooses to claim a route
     * @author Hamza REMMAL (310917)
     * @author Mehdi ZIAZI (311475)
     */
    @FunctionalInterface
    public interface ClaimRouteHandler extends ActionHandlers{
        /**
         * communicates  the kind of action performed (claim route), the route to claim and the cards used for that to the game thread
         * @param route (Route) - route to claim
         * @param cards (SortedBag) - cards used for that
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }
    
    /**
     * This interface represents the handler to be used when the player chooses tickets
     * @author Hamza REMMAL (310917)
     * @author Mehdi ZIAZI (311475)
     */
    @FunctionalInterface
    public interface ChooseTicketsHandler extends ActionHandlers{
        /**
         * communicates the chosen tickets to the game thread
         * @param tickets (SortedBag) - chosen tickets
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }
    
    /**
     * This interface represents the handler to be used when the player chooses cards
     * @author Hamza REMMAL (310917)
     * @author Mehdi ZIAZI (311475)
     */
    @FunctionalInterface
    public interface ChooseCardsHandler extends ActionHandlers{
        /**
         * communicates the chosen cards to the game thread
         * @param cards (SortedBag) - chosen cards
         */
        void onChooseCards(SortedBag<Card> cards);
    }

}


