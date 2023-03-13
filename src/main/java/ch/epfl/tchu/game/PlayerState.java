package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * The abstract representation of the state of a player.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * Build a new PlayerState object with the given parameters.
     * @param tickets (SortedBag<Ticket>) - Tickets of the player.
     * @param cards (SortedBag<Card>) - Cards of the player.
     * @param routes (List<Route>) - Routes claimed by the player.
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards   = cards;
    }

    /**
     * Build the initial playerState to start the game.
     * @param initialCards (SortedBag<Card>) - The initial drawn cards for the player.
     * @throws {@code IllegalArgumentException} if {@code initialCards.size() != Constants.INITIAL_CARDS_COUNT}.
     * @return (PlayerState) - the object to start the game.
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * Gets the player's tickets.
     * @return (SortedBag<Ticket>) - The player's tickets.
     */
    public SortedBag<Ticket> tickets() { return this.tickets;}

    /**
     * Add a list of tickets to the player's tickets.
     * @param newTickets (SortedBag<Ticket>) - The tickets to add.
     * @return (PlayerState) - A new PlayerState object with the added tickets.
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) { return new PlayerState(this.tickets.union(newTickets), this.cards, this.routes());}

    /**
     * Gets the player's cards.
     * @return (SortedBag<Card>) - The player's cards.
     */
    public SortedBag<Card> cards() { return this.cards;}

    /**
     * Add the given card to the player's cards.
     * @param card (Card) - the card to add
     * @return (PlayerState) - The new PlayerState object with the card added.
     */
    public PlayerState withAddedCard(Card card) { return new PlayerState(tickets, this.cards.union(SortedBag.of(card)),this.routes());}

    /**
     * Compute if with the current cards of the player, the player can claim the route or not.
     * @param route (Route) - the route to claim.
     * @return (boolean) - true iff the player can claim the given route.
     */
    public boolean canClaimRoute(Route route) { return (route.length() <= this.carCount() && !this.possibleClaimCards(route).isEmpty());}

    /**
     * Compute the possibilities to claim a given route.
     * @param route (Route) - Route to claim.
     * @throws {@code IllegalArgumentException} if  {@code this.carCount() < route.length()}
     * @return (List<SortedBag<Card>>) - Possible combinations to claim the route.
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(this.carCount() >= route.length());                                                                    
        var possibleSet = route.possibleClaimCards();
        List<SortedBag<Card>> list = new ArrayList<>();
        possibleSet.forEach(bag -> {
            if (this.cards.contains(bag))
                list.add(bag);
            });
        return list;
    }

    /**
     * Compute a list of all possible additional cards.
     * @param additionalCardsCount (int) - The additional cards.
     * @param initialCards (SortedBag<Card>) - The initail cards used to claim the route.
     * @param drawnCards (SortedBag<Card>) - The drawn cards.
     * @throws {@code IllegalArgumentException} if {@code drawnCards.size() != 3 } or  {@code additionalCardsCount < 1 || additionalCardsCount > 3} or {@code initialCards.isEmpty() || initialCards.toSet().size() >= 3}.
     * @return (List<SortedBag<Card>>) -
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() < Constants.ADDITIONAL_TUNNEL_CARDS );
        var cartesUtilisables = cards.difference(initialCards);
        var color = initialCards.get(0).color();
        var locomotiveAlone = Objects.isNull(color);
        List<SortedBag<Card>> list = new ArrayList<>();
        Set<SortedBag<Card>> possibilities;
        try {
            possibilities = cartesUtilisables.subsetsOfSize(additionalCardsCount);
        }catch(IllegalArgumentException e) {
            return List.of();
        }
        
        possibilities.forEach(carteBag -> {
             if (locomotiveAlone) {
                if (SortedBag.of(additionalCardsCount, Card.LOCOMOTIVE).contains(carteBag)) 
                    list.add(carteBag);
            } else {
                if (SortedBag.of(additionalCardsCount, Card.of(color), additionalCardsCount, Card.LOCOMOTIVE).contains(carteBag)) 
                    list.add(carteBag);       
            }
        });
        list.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        return list;
    }

    /**
     * Claims the given route.
     * @param route (Route) - The claimed route.
     * @param claimCards (SortedBag<Card>) - Cards used to claim the route.
     * @return (PlayerState) - The new PlayerState object with the route added to the player's claimed routes and the used cards removed.
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> list = new ArrayList<>(this.routes());
        list.add(route);
        return new PlayerState(this.tickets, this.cards.difference(claimCards), list);
    }

    /**
     * Compute the points won for the tickets.
     * @return (int) - the points won.
     */
    public int ticketPoints() {
        var point = 0;
        var indexMax = 0;
        for (var route : this.routes()) {
            if (route.station1().id() > indexMax) indexMax = route.station1().id();
            if (route.station2().id() > indexMax) indexMax = route.station2().id();
        }
        var sb = new StationPartition.Builder(indexMax + 1);
        this.routes().forEach(route -> sb.connect(route.station1(), route.station2()) );
        var partition = sb.build();
        for (var tk : tickets) 
            point += tk.points(partition);
        return point;
    }

    /**
     * Get the total points of the player.
     * @return (int) - the final count of points won by the player.
     */
    public int finalPoints() { return this.claimPoints() + this.ticketPoints();}
    
}
