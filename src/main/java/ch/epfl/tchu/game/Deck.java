package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
/**
 * Represents the concept of deck used in game.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public final class Deck<C extends Comparable<C>>{

    private final List<C> cards;
    
   
    private Deck(List<C> list) { this.cards = new ArrayList<>(list);}
   
    /**
     * Creates an instance of Deck
     * @param <C> specialization of the deck.
     * @param cards cards within the deck
     * @param rng   random to shuffle the deck
     * @return a shuffled deck
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        var deck = new Deck<C>(cards.toList());
        Collections.shuffle(deck.cards,rng);
        return deck;
    }

    /** Returns the size of the deck
     * @return (int) - size of the deck.
     */
    public int size() { return this.cards.size();}
    
    /** Returns if the deck is empty.
     * @return (boolean) - true iff the deck is empty ({@code this.size() == 0}).
     */
    public boolean isEmpty() { return this.cards.isEmpty();}
    
    /** Returns the card in the top
     * @throws {@code IllegalArgumentException} if the deck is empty.
     * @return card in the top
     */
    public C topCard() {
        Preconditions.checkArgument(!this.isEmpty());
        return this.cards.get(this.cards.size()-1);   
    }
    
    /** returns the deck without the top card.
     * @throws {@code IllegalArgumentException} if the deck is empty.
     * @return the deck without the top card.
     */
    public Deck<C> withoutTopCard(){
       return withoutTopCards(1); 
    }
    
    /**
     * Returns a SortedBag of the {@code count} top cards of the deck
     * @param count(int)- number of cards.
     * @throws {@code IllegalArgumentException} if count is not between 0 (inclusive) & {@code this.size()} (inclusive).
     * @return (SortedBag<C>)- SortedBag of cards.
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(count >= 0 && count<=this.size());
        var builder = new SortedBag.Builder<C>();
        var deck = new Deck<C>(cards);
        for (var i = 0; i < count; i++) {
            builder.add(deck.topCard());
            deck = deck.withoutTopCard();
        }
        return builder.build();
    }
    
    /** 
     * Returns the deck without the {@code count} top cards of the deck.
     * @param count(int)- number of cards 
     * @throws {@code IllegalArgumentException} if count is not between 0 (inclusive) & {@code this.size()} (inclusive).
     * @return (Deck) - Deck without the removed number of top cards.
     */
    public Deck<C> withoutTopCards(int count){
       Preconditions.checkArgument(count >= 0 && count <= this.size());
       List<C> list = cards.subList(0, this.size() - count);
       return new Deck<>(list);
    }
  }
