package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * An abstract representation of the game's cards.
 * This class contains private informations about the cards we use in the game, such as the deck and the discard.
 * It contains also methods, so we can can do a job with it.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public final class CardState extends PublicCardState {
    
    private final Deck<Card> deck;
    private final SortedBag<Card> discards;
    
    /**
     * Creates a new CardState object.
     * @param deck (Deck<Card>) - the deck of the game
     * @param defausse (SortedBag<Card>) - The discard of the game.
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discards) {
        super(faceUpCards, deck.size(), discards.size());
        this.deck     = deck;
        this.discards = discards;
    }

    /**
     * Create a new CradState object with the given Deck.
     * @param deck (Deck<Card>) - The deck for the game.
     * @return (CardState) - The cardState object.
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);
        List<Card> list = new ArrayList<>();
        for (var iterator = Constants.FACE_UP_CARD_SLOTS.iterator(); iterator.hasNext() ; iterator.next()) {
            list.add(deck.topCard());
            deck = deck.withoutTopCard();
        }
        return new CardState(list,deck, SortedBag.of());
    }
    
    /**
     * Remove one face up card in the given slot.
     * slot must be between 0 and {@code Constants.FACE_UP_CARDS_COUNT}.
     * @param slot (int) - The slot of the card to remove.
     * @throws {@code IllegalArgumentException} if the deck is empty.
     * @throws {@code IndexOutOfBoundsException} if the slot is not between 0 and {@code Constants.FACE_UP_CARDS_COUNT}.
     * @return (CardState) - The new CardState.
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!this.isDeckEmpty());
        List<Card> faceUpCards = new ArrayList<>(this.faceUpCards());
        faceUpCards.set(slot, this.topDeckCard());
        return new CardState(faceUpCards,this.deck.withoutTopCard(),this.discards);
    }
    
    /**
     * Returns the top card of the deck.
     * @throws {@code IllegalArgumentException} if the deck is empty.
     * @return (Card) - The top card.
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!this.isDeckEmpty());
        return deck.topCard();
    }
    
    /**
     * Returns a new cardState with the top card of the deck removed.
     * @throws {@code IllegalArgumentException} if the deck is empty.
     * @return (CardState) - A cardState with the top card removed.
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!this.deck.isEmpty());
        return new CardState(this.faceUpCards(),deck.withoutTopCard(),this.discards);
    }
    
    /**
     * Create a new deck with the discarded cards.
     * @param rng (Random) - 
     * @throws {@code IllegalArgumentException} if the deck is not empty.
     * @return (CardState) - CardState with new Deck.
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(this.deck.isEmpty());
        return new CardState(this.faceUpCards(),Deck.of(this.discards, rng),SortedBag.of());
    }
    
    /**
     * Add the additional cards given to the discarded cards.
     * @param additionalCards (SortedBag<Card>) - The cards to add.
     * @return (CardState) - CardState object with the added cards.
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalCards) { 
        return new CardState(this.faceUpCards(),this.deck, discards.union(additionalCards));
        }
}
