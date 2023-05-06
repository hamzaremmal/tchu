package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;
import ch.epfl.tchu.Preconditions;

/**
 * Gives an abstract representation of the public part of the CardState.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public class PublicCardState {
    
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;
    
    /**
     * Construct a new object with the given parameters.
     * @param faceUpCards (List) - List of the face up cards.
     * @param deckSize (int) - The size of the deck.
     * @param discardsSize (int) -  The size of the discards.
     * @throws IllegalArgumentException if {@code faceUpCards.size() != Constants.FACE_UP_CARDS_COUNT} or {@code deckSize < 0} or {@code discardsSize < 0};
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT && deckSize >= 0 && discardsSize >= 0);
        this.faceUpCards  = List.copyOf(faceUpCards);
        this.deckSize     = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * Get the list of all face up cards.
     * @return (List) - List of the face up cards.
     */
    public List<Card> faceUpCards(){ return faceUpCards;}
    
    /**
     * Returns the face up card in the given slot.
     * @param slot (int) - The slot of the card.
     * @throws IndexOutOfBoundsException if slot is not between 0 and {@code Constants.FACE_UP_CARDS_COUNT} (exclusive).
     * @return (Card) - The card in the given slot.
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }
    
    /**
     * Get the size of the deck.
     * @return (int) - The size pof the deck.
     */
    public int deckSize() { return deckSize;}
    
    /**
     * Check if the deck is empty.
     * @return (boolean) - True if the deck is empty.
     */
    public boolean isDeckEmpty() { return deckSize == 0;}
    
    /**
     * Get the size of the discards.
     * @return (int) - The size of the discards.
     */
    public int discardsSize() { return discardsSize;}
}
