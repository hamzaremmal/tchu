package ch.epfl.tchu.gui;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import javax.sound.sampled.AudioSystem;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseTicketsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import ch.epfl.tchu.net.Call;
import javafx.application.Platform;

/**
 * This class represent a Player object how interact with the GraphicalPlayer
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public class GraphicalPlayerAdapter implements Player{

    private GraphicalPlayer gp;
    private final ArrayBlockingQueue<Route> queueRoute;
    private final ArrayBlockingQueue<TurnKind> queueTK;
    private final ArrayBlockingQueue<Integer> queueINT;
    private final ArrayBlockingQueue<SortedBag<Ticket>> queueSBT;
    private final ArrayBlockingQueue<SortedBag<Card>> queueSBC;
    private final Call call;
    
    /**
     * Creates the GraphicalPlayerAdapter Object
     * @param call ???
     */
     public GraphicalPlayerAdapter(Call call) {
        queueRoute = new ArrayBlockingQueue<>(2);
        queueINT   = new ArrayBlockingQueue<>(2);
        queueTK    = new ArrayBlockingQueue<>(2);
        queueSBT   = new ArrayBlockingQueue<>(2);
        queueSBC   = new ArrayBlockingQueue<>(2);
        this.call  = call;
    }
     
     
     private static void playSound() {
         try {
             var audioStream = AudioSystem.getAudioInputStream(new File("res/sound.wav"));
             var clip = AudioSystem.getClip();
             clip.open(audioStream);
             clip.start();
             audioStream.close();
         } catch(Exception e) {}
     }
     
     
    // ----------------------------------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------- HANDLERS -------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------------------------
    
     /**
      * The Handler to use to pick the first card.
      */
    private DrawCardHandler startTurnCardHandler = slot -> {
        writeToTheQueueTK(TurnKind.DRAW_CARDS);
        writeToTheQueueINT(slot);
    };
    
    /**
     * The Handler to use to pick the second Card.
     */
    private DrawCardHandler drawCardHandler = slot -> {
        writeToTheQueueINT(slot);
    };
    
    /**
     * The Handler to use to claim a Route.
     */
    private ClaimRouteHandler startTurnRouteHandler = (route , cards) -> {
        writeToTheQueueTK(TurnKind.CLAIM_ROUTE);
        writeToTheQueueRoute(route);
        writeToTheQueueSBC(cards);
    };
     
    /**        
     * The Handler to use to draw a ticket.
     */
    private DrawTicketsHandler ticketsHandler = () -> {
        writeToTheQueueTK(TurnKind.DRAW_TICKETS);
    };
    
    /**
     * The Handler to use to choose a ticket.
     */
    private ChooseTicketsHandler chooseTicketsHandler = tickets -> {
        writeToTheQueueSBT(tickets);
    };
    
    /**
     * The Handler to use to choose a card.
     */
    private ChooseCardsHandler chooseCardsHandler = cards -> {
        writeToTheQueueSBC(cards);
    };
    
   
    
    // ----------------------------------------------------------------------------------------------------------------------------
    // --------------------------------------------------------- PLAYER'S METHODS -------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------------------------
    
    
    /**
     * Initialize the Player.
     * @param id (PlayerId) - The id of this Player.
     * @param playerNames (Map) - The Map with every Player name.
     */
    @Override
    public void initPlayers(PlayerId id, Map<PlayerId, String> playerNames) {
        Platform.runLater(() -> gp = new GraphicalPlayer(id, playerNames,call));
    }

    /**
     * Transfer the information in the parameters to the Graphical Player to display it.
     * @param info (String) - Information to display
     */
    @Override
    public void receiveInfo(String info) {
        Platform.runLater(() -> gp.receiveInfo(info));
    }

    /**
     * Updates the Player to set new information in the frame.
     * @param publicGameState (PublicGameState) - The current PublicGameState object of the game
     * @param playerState (PlayerState) - The current PlayerState object of the game
     */
    @Override
    public void updateState(PublicGameState publicGameState, PlayerState playerState) {
        Platform.runLater(() -> gp.updateState(publicGameState, playerState));   
    }

    /**
     * Configures the Player to display the pop-up window with the tickets to choose from.
     * @param tickets (SortedBag) - The initial tickets to choose from.
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        Platform.runLater(() -> gp.setInitialTicketChoice(tickets, chooseTicketsHandler));
    }

    /**
     * Gives the tickets the player decided to keep.
     * @return (SortedBag) - The chosen tickets.
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return readFromTheQueueSBT();
    }

    /**
     * Gives The Kind of Turn the Player decided to perform.
     * @return (TurnKind) - The TurnKind the player chose to perform.
     */
    @Override
    public TurnKind nextTurn() {
        playSound();
        Platform.runLater(() -> gp.startTurn(ticketsHandler, startTurnCardHandler, startTurnRouteHandler));
        return readFromTheQueueTK();  
    }

    /**
     * Gives the Player tickets to choose from and returns the chosen tickets.
     * @param options (SortedBag) - The tickets to choose from.
     * @return (SortedBag) - The chosen tickets.
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        Platform.runLater(() -> gp.chooseTickets(options, chooseTicketsHandler));
        return readFromTheQueueSBT();
    }

    /**
     * Gives the slot the Player decided to pick from.
     * @return (int) - The slot to draw from.
     */
    @Override
    public int drawSlot() {
        Platform.runLater(() -> gp.drawCard(drawCardHandler));
        return readFromTheQueueINT();
    }

    /**
     * Gives the Route the Player decided to claim.
     * @return (Route) - The route to claim.
     */
    @Override
    public Route claimedRoute() {
        return readFromTheQueueRoute();
    }

    /**
     * Gives the initial cards the player decided to claim with.
     * @return (SortedBag) - The cards to claim with.
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return readFromTheQueueSBC();
    }

    /**
     * Gives the additional cards to pick from and returns the SortedBag to use.
     * @param options (List) - The options to pick from.
     * @return (SortedBag) - The chosen SortedBag.
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        Platform.runLater(() -> gp.chooseAdditionalCards(options, chooseCardsHandler));
        return readFromTheQueueSBC();
    }
    
    // ----------------------------------------------------------------------------------------------------------------------------
    // --------------------------------------------------- QUEUE METHODS ----------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Read Value from the Queue.
     * @return (int) -The value from the Queue.
     */
    private int readFromTheQueueINT() {
        try {
            return queueINT.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("cannot read from the queue");
        }
    }
    
    /**
     * Read Value from the Queue.
     * @return (TurnKind) -The value from the Queue.
     */
    private TurnKind readFromTheQueueTK() {
        try {
            return queueTK.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("cannot read from the queue");
        }
    }
    
    /**
     * Read Value from the Queue.
     * @return (SortedBag<Ticket>) -The value from the Queue.
     */
    private SortedBag<Ticket> readFromTheQueueSBT() {
        try {
            return queueSBT.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("cannot read from the queue");
        }
    }
    
    /**
     * Read Value from the Queue.
     * @return (Route) -The value from the Queue.
     */
    private Route readFromTheQueueRoute() {
        try {
            return queueRoute.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("cannot read from the queue");
        }
    }
    
    /**
     * Read Value from the Queue.
     * @return (SortedBag<Card>) -The value from the Queue.
     */
    private SortedBag<Card> readFromTheQueueSBC() {
        try {
            return queueSBC.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("cannot read from the queue");
        }
    }
    
    /**
     * Write a value to the queue.
     * @param obj (int) - Value to Write to the Queue.
     */
    private void writeToTheQueueINT(int obj) {
        try {
            queueINT.put(obj);
        } catch (InterruptedException e) {
            throw new RuntimeException("cannot write the queue");
        }
    }
    
    /**
     * Write a value to the queue.
     * @param obj (TurnKind) - Value to Write to the Queue.
     */
    private void writeToTheQueueTK(TurnKind obj) {
        try {
            queueTK.put(obj);
        } catch (InterruptedException e) {
            throw new RuntimeException("cannot write the queue");
        }
    }
    
    /**
     * Write a value to the queue.
     * @param obj (Route) - Value to Write to the Queue.
     */
    private void writeToTheQueueRoute(Route obj) {
        try {
            queueRoute.put(obj);
        } catch (InterruptedException e) {
            throw new RuntimeException("cannot write the queue");
        }
    }
    
    /**
     * Write a value to the queue.
     * @param obj (SortedBag<Card>) - Value to Write to the Queue.
     */
    private void writeToTheQueueSBC(SortedBag<Card> obj) {
        try {
            queueSBC.put(obj);
        } catch (InterruptedException e) {
            throw new RuntimeException("cannot write the queue");
        }
    }
    
    /**
     * Write a value to the queue.
     * @param obj (SortedBag<Ticket>) - Value to Write to the Queue.
     */
    private void writeToTheQueueSBT(SortedBag<Ticket> obj) {
        try {
            queueSBT.put(obj);
        } catch (InterruptedException e) {
            throw new RuntimeException("cannot write the queue");
        }
    }

}
