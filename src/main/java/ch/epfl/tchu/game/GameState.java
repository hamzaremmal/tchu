package ch.epfl.tchu.game;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * Abstract representation of the state of the game.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
public final class GameState extends PublicGameState{

    private final Deck<Ticket> deckTickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;
    private boolean cc = false;
    
    /**
     * Build a new GameState object with the given parameters.
     * @param deckTickets (Deck<Ticket>) - A deck of the game's tickets.
     * @param cardState (CardState) - The acrdState of the game.
     * @param playerState (Map<PlayerId, PlayerState>) - Mapping between every playerId and its PlayerState object.
     * @param currentPlayerId (PlayerId) - The current player id.
     * @param lastPlayer (PlayerId) - The last player id.
     */
    private GameState(Deck<Ticket> deckTickets ,CardState cardState,Map<PlayerId, PlayerState> playerState,PlayerId currentPlayerId,PlayerId lastPlayer) {
        super(deckTickets.size(), cardState, currentPlayerId,Map.copyOf(playerState), lastPlayer);
        this.deckTickets = deckTickets;
        this.cardState   = cardState;
        this.playerState = Map.copyOf(playerState);
    }
    
    /**
     * Create the initial GameState for a tchu party.
     * @param tickets (SortedBag) - SortedBag of the tickets used for this game.
     * @param rng (Random) - Object to shuffle the cards.
     * @return (GameState) - The initial GameState of a tchu party with given parameters.
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
            // Tickets and Cards initialization
        Deck<Ticket> deckTickets = Deck.of(tickets, rng);
        Deck<Card> deckCards = Deck.of(Constants.ALL_CARDS, rng);
        Map<PlayerId, PlayerState> map = new EnumMap<>(PlayerId.class);
        for(var playerId : PlayerId.ALL) {
            var ps = PlayerState.initial(deckCards.topCards(Constants.INITIAL_CARDS_COUNT));
            deckCards = deckCards.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
            map.put(playerId, ps);
        }
        
        // First To Play
        var firstPlayer = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));
        return new GameState(deckTickets,CardState.of(deckCards),map,firstPlayer,null);
    }

    /**
     * Returns the private state of a given player. 
     * @param playerId (PlayerId) - The PlayerId of the requested player.
     * @return (PlayerState) - the current state of the given player.
     */
    @Override
    public PlayerState playerState(PlayerId playerId) { return this.playerState.get(playerId);}
    
    /**
     * Returns the private state of the current player. 
     * @return (PlayerState) - the current state of the current player.
     */
    @Override
    public PlayerState currentPlayerState() { return this.playerState.get(currentPlayerId());}
    
    /**
     * 
     * @param count (int) - The number of tickets we ask for.
     * @throws IllegalArgumentException if count is not between 0 (inclusive) and the size of the tickets deck. (inclusive).
     * @return (SortedBag) - A SortedBag of the asked tickets.
     */
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count >= 0 && count <= this.ticketsCount());
        return  this.deckTickets.topCards(count);
    }
    
    /**
     * Create a new GameState without the given number of top tickets.
     * @param count (int) - The number of tickets we ask to remove.
     * @return (GameState) - A new GameState object with the count top tickets removed.
     */
    public GameState withoutTopTickets(int count) {
        var newDeck = this.deckTickets.withoutTopCards(count);
        return new GameState(newDeck, this.cardState, this.playerState, super.currentPlayerId(),super.lastPlayer());
    }
    
    /**
     * Get the top card of the game's deck.
     * @return (Card) - The deck top card.
     */
    public Card topCard() {
        Preconditions.checkArgument(!this.cardState.isDeckEmpty());
        return this.cardState.topDeckCard();
    }
    
    /**
     * Get the new GameState after getting the top deck card.
     * @return (GameState) -  A new GameState object with the top card of the deck removed.
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!this.cardState.isDeckEmpty());
        return new GameState(this.deckTickets, this.cardState.withoutTopDeckCard(), this.playerState, super.currentPlayerId(),super.lastPlayer());
    }
    
    /**
     * Get the new GameState with the given cards to the discard.
     * @param discardedCards ???
     * @return (GameState) -  A new GameState object with the given cards added to the discard.
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        var carte = this.cardState.withMoreDiscardedCards(discardedCards);
        return new GameState(this.deckTickets, carte, playerState, super.currentPlayerId(), super.lastPlayer());
    }
    
    /**
     * Create a new deck with the discarded cards if the deck is empty.
     * @param rng (Random) - Object to shuffle the cards.
     * @return (GameState) -  A new GameState object with the recreated deck.
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        var cs = cardState.isDeckEmpty() ? cardState.withDeckRecreatedFromDiscards(rng) : cardState;
        return new GameState(this.deckTickets, cs, playerState, currentPlayerId(), lastPlayer());
    }
    
    /**
     * Get the GameState with the chosen tickets added to the player given.
     * @param playerId (PlayerId)  - the id of the player to add to.
     * @param chosenTickets (SortedBag) - the tickets to add.
     * @throws IllegalArgumentException ???
     * @return (GameState) -  A new GameState object with the given tickets added to the player.
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) { 
        Preconditions.checkArgument(playerState.get(playerId).tickets().isEmpty());
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(playerState);
        var ps = playerState.get(playerId).withAddedTickets(chosenTickets);
        playerStateMap.replace(playerId, ps);
        return new GameState(this.deckTickets, this.cardState, playerStateMap, currentPlayerId(), lastPlayer());
    }
    
    /**
     * Choose cards from the drawn cards
     * @param drawnTickets (SortedBag) - Cards to choose from.
     * @param chosenTickets (SortedBag) - Cards chosen.
     * @throws IllegalArgumentException if the drawnCards doesn't contains the chosenCards.
     * @return (GameState) -  A new GameState object with the chosenCards added.
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(playerState);
        Deck<Ticket> deck = deckTickets.withoutTopCards(drawnTickets.size());
        var ps = playerState.get(currentPlayerId()).withAddedTickets(chosenTickets);
        playerStateMap.replace(currentPlayerId(), ps);
        return new GameState(deck, this.cardState, playerStateMap, currentPlayerId(), lastPlayer());
    }
    
    /**
     * Get the GameState with the visible card in the given slot added to the current player cards.
     * @param slot (int) - the slot of the chosen card.
     * @throws IllegalArgumentException if the player can't pick a card.
     * @return (GameState) - A new GameState object with the given slot card added to the current player's cards.
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(playerState);
        var card = cardState.faceUpCard(slot);
        var stateWithoutCard = cardState.withDrawnFaceUpCard(slot);
        var state = playerState.get(currentPlayerId()).withAddedCard(card);
        playerStateMap.replace(currentPlayerId(), state);
        return new GameState(this.deckTickets, stateWithoutCard, playerStateMap, currentPlayerId(), lastPlayer());
    }
    
    /**
     * Get the GameState with the top deck card added to the current player cards.
     * @throws IllegalArgumentException if the player can't pick a card.
     * @return (GameState) - A new GameState object with the top card added to the current player's cards.
     */
    public GameState withBlindlyDrawnCard() {
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(playerState);
        var topCard = cardState.topDeckCard();
        var stateWithoutTopCard = cardState.withoutTopDeckCard();
        var state = playerState.get(currentPlayerId()).withAddedCard(topCard);
        playerStateMap.replace(currentPlayerId(), state);
        return new GameState(this.deckTickets, stateWithoutTopCard, playerStateMap, currentPlayerId(), lastPlayer());
    }
    
    /**
     * Claims the given route to the current player.
     * @param route (Route) - The claimed Route.
     * @param cards (SortedBag) - Cards used to claim the route.
     * @return (GameState) - A new GameState object with the given tickets added to the player.
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(playerState);
        var ps = playerState.get(currentPlayerId()).withClaimedRoute(route, cards);
        playerStateMap.replace(currentPlayerId(), ps);
        return new GameState(this.deckTickets, cardState.withMoreDiscardedCards(cards) , playerStateMap, currentPlayerId(), lastPlayer());
    }
    
    /**
     * Check if the last turn begins.
     * @return (boolean) - true iff we just knew the last player.
     */
    public boolean lastTurnBegins() { 
        return (Objects.isNull(this.lastPlayer())) && (playerState.get(this.currentPlayerId()).carCount() <= 2);
        }
    
    /**
     * Get the GameState for the next turn.
     * @return (GameState) - A new GameState object with the modifications for the next turn.
     */
    public GameState forNextTurn() {
        var lastPlayer = this.lastTurnBegins() ? this.currentPlayerId() : this.lastPlayer();
        PlayerId id;
        if(cc) {
            if(lastPlayer() == this.currentPlayerId()) id = null;
            else id = this.currentPlayerId().next();
        }else {
            id = this.currentPlayerId().next();
            if (lastPlayer() == this.currentPlayerId()) cc = true;
        }
        return new GameState(this.deckTickets, this.cardState, this.playerState, id, lastPlayer);
    }
    
}
