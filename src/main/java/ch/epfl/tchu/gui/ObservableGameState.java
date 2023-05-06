package ch.epfl.tchu.gui;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class represents an observable game state that specifies a player 
 * @author Hamza REMMAL (310917)
 * @author Mehid ZIAZI (311475)
 */
public final class ObservableGameState {
    
    // --------------------------------------------
    // ----------- GENERAL ATTRIBUTES -------------
    // --------------------------------------------
    private final ObjectProperty<PublicGameState> gs = new SimpleObjectProperty<>();
    private final ObjectProperty<PlayerState>     ps = new SimpleObjectProperty<>();
    private final PlayerId id;
    /**
     * --------------------------------------------
     * ---------- GameState Properties ------------
     * --------------------------------------------
     */
    private final IntegerProperty rateTickets = new SimpleIntegerProperty();
    private final IntegerProperty rateCards  = new SimpleIntegerProperty();
    private final Map<Integer,ObjectProperty<Card>> faceUpCards = new HashMap<>(5);
    private final Map<Route, ObjectProperty<PlayerId>> routeMap = new HashMap<>();
    /**
     * --------------------------------------------
     * ------- PublicPlayerState Properties -------
     * --------------------------------------------
     */
    private final Map<PlayerId, IntegerProperty> ticketsCount = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> cardsCount   = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> carsCount    = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> claimPoints  = new EnumMap<>(PlayerId.class);
    /**
     * --------------------------------------------
     * ----------- PlayerState Properties ---------
     * --------------------------------------------
     */
    private final ObservableList<Ticket> listTickets    = FXCollections.observableArrayList();
    private final Map<Card, IntegerProperty> cards      = new EnumMap<>(Card.class);
    private final Map<Route, BooleanProperty> claimMap  = new HashMap<>();
    private final ObjectProperty<PlayerId> currentPlayer = new SimpleObjectProperty<PlayerId>();
    
    /**
     * Creates an instance of ObservableGameState
     * @param id (PlayerId) - The identity of the player.
     */
    public ObservableGameState(PlayerId id) {
        this.id = id;
        for(var card : Card.ALL) 
            cards.put(card, new SimpleIntegerProperty());
        for(var slot : Constants.FACE_UP_CARD_SLOTS)
            faceUpCards.put(slot,new SimpleObjectProperty<Card>());
        for(var route : ChMap.routes()) {
            claimMap.put(route, new SimpleBooleanProperty());
            routeMap.put(route, new SimpleObjectProperty<>());
            }
        for(var playerId : PlayerId.ALL) {
            ticketsCount.put(playerId,new SimpleIntegerProperty());
            cardsCount.put(playerId,new SimpleIntegerProperty());
            carsCount.put(playerId,new SimpleIntegerProperty());
            claimPoints.put(playerId,new SimpleIntegerProperty());
            }
    }
    
    /**
     * Updates the instance of ObservableGameState
     * @param gs (PublicGameState) - the new state of the game
     * @param ps (PlayerState) - the state of the player of the instance
     */
    public void setState(PublicGameState gs , PlayerState ps) {
        this.gs.set(gs);
        this.ps.set(ps);
        currentPlayer.set(gs.currentPlayerId());
        for(int slot : Constants.FACE_UP_CARD_SLOTS) {
            var card = gs.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(card);
        }
        for(var route : ChMap.routes()) {
           	if(routeMap.get(route).get()==null) {
           	    for(var playerId : PlayerId.ALL) {
           	      if (gs.playerState(playerId).routes().contains(route)) {
           	          routeMap.get(route).set(playerId);
           	          break;
           	      }   
           	    }
           	}
           	claimMap.get(route).set(this.canClaim(route));
        }
        rateTickets.set((gs.ticketsCount() * 100 )/ChMap.tickets().size());
        rateCards.set(gs.cardState().deckSize() * 100 /Constants.ALL_CARDS.size());
        PlayerId.ALL.forEach(playerId -> {
            ticketsCount.get(playerId).set(gs.playerState(playerId).ticketCount());
            cardsCount.get(playerId).set(gs.playerState(playerId).cardCount());
            carsCount.get(playerId).set(gs.playerState(playerId).carCount());
            claimPoints.get(playerId).set(gs.playerState(playerId).claimPoints());
            });
        for (var card : Card.ALL)
            cards.get(card).set(ps.cards().countOf(card));
       for (var ticket : ps.tickets()) 
           if(!listTickets.contains(ticket)) listTickets.add(ticket);
    }
    
    
     
    // ----------------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------- PROPERTIES METHODS -------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Returns a read only property containing the game state
     * @return (ObjectProperty) -the property containing the game state
     */
    public ReadOnlyObjectProperty<PublicGameState> gameState(){
        return gs;
    }
    
    /**
     * Returns the identity of the player
     * @return (PlayerId) - id of the player
     */
    public PlayerId id() {
        return id;
    }
    /**
     * Returns a read only property containing the player state
     * @return (PlayerId) - the property containing the player state
     */
    public ReadOnlyObjectProperty<PlayerState> playerState(){
        return ps;
    }
    /**
     *  Returns a read only property containing the chosen face up card
     * @param slot (int) - index of the chosen face up card
     * @return (ReadOnlyObjectProperty)-the property containing the face up card
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }
    
    /**
     * Returns a read only property containing the identity of the owner of a route
     * or null if no player claimed it yet
     * @param route (Route)-chosen route
     * @return (ReadOnlyObjectProperty)-the property containing the id
     */
    public ReadOnlyObjectProperty<PlayerId> routeId(Route route) {
        return routeMap.get(route);
        }
    
    /**
     * Returns a read only property containing true if the player specified by this instance 
     * owns the route and false otherwise
     * @param route(Route)-chosen route
     * @return (ReadOnlyBooleanProperty)-the boolean property
     */
    public ReadOnlyBooleanProperty canClaimRoute(Route route) {
        return claimMap.get(route);
        }
    
    /**
     * @return(ReadOnlyIntegerProperty)-a read only property containing the percentage of the tickets remaining
     */
    public ReadOnlyIntegerProperty rateTickets() {
        return rateTickets;
        }
    
    /**
     * @return(ReadOnlyIntegerProperty- a read only property containing the percentage of the cards remaining in the deck
     */
    public ReadOnlyIntegerProperty rateCards() {
        return rateCards;
        }
    
    /**
     * returns  a read only property containing the number of tickets of a player
     * @param id(PlayerId)-the identity of the chosen player
     * @return (ReadOnlyIntegerProperty)-the corresponding property
     */
    public ReadOnlyIntegerProperty ticketsCount(PlayerId id) {
        return ticketsCount.get(id);
        }
    
    /**
     * returns  a read only property containing the number of cards of a player
     * @param id(PlayerId)-the identity of the chosen player
     * @return (ReadOnlyIntegerProperty)-the corresponding property
     */
    public ReadOnlyIntegerProperty cardsCount(PlayerId id) {
        return cardsCount.get(id);
        }
    
    /**
     * returns  a read only property containing the number of cars of a player
     * @param id(PlayerId)-the identity of the chosen player
     * @return (ReadOnlyIntegerProperty)-the corresponding property
     */
    public ReadOnlyIntegerProperty carsCount(PlayerId id) {
        return carsCount.get(id);
        }
    
    /**
     * returns  a read only property containing the claim points of a player
     * @param id(PlayerId)-the identity of the chosen player
     * @return (ReadOnlyIntegerProperty)-the corresponding property
     */
    public ReadOnlyIntegerProperty claimPoints(PlayerId id) {
        return claimPoints.get(id);
        }
    
    /**
     * returns true iff the player specified by this instance can draw tickets and else otherwise 
     * @return -(boolean)
     */
    public boolean canDrawTickets() { 
        return gs.get().canDrawTickets();
        }
    
    /**
     * returns true iff the player specified by this instance can draw cards and else otherwise 
     * @return - (boolean)
     */
    public boolean canDrawCards() { 
        return gs.get().canDrawCards();
        }
    
    
    /**
     * returns the possibilities to claim a given route.
     * @param route (Route) - Route to claim.
     * @return (List) - Possible combinations to claim the route.
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) { 
        return Collections.unmodifiableList(ps.get().possibleClaimCards(route));
        }
    
    /**
     * returns  an observable list containing the tickets of the player specified by this instance
     * @return (ObservableList)-the observable list containing all the tickets
     */
    public ObservableList<Ticket> listTickets() {
        return FXCollections.unmodifiableObservableList(this.listTickets);
        }
    
    /**
     * returns  a map containing the card and the number of it of the player specified by this instance
     * @return (Map)-the map that for each card assigns an integer property
     */
    public  Map<Card, ReadOnlyIntegerProperty> cards() { 
        return Collections.unmodifiableMap(cards);
        }
    
    /**
     * returns the identity of the current player
     * @return -(PlayerId) id of the current player
     */
    public ReadOnlyObjectProperty<PlayerId> currentPlayerId() { 
        return currentPlayer;
        }
    
    /**
     * returns true iff the player specified by this instance can claim the chosen route and false otherwise
     * @param route (Route) -chosen route
     * @return (boolean) -
     */
    private boolean canClaim(Route route) {
    	for (var element : routeMap.keySet())
			if(route.stations().equals(element.stations()) && routeMap.get(element).get() != null) return false;		
    	return this.gs.get().currentPlayerId()== id && ps.get().canClaimRoute(route);
    }

}

