package ch.epfl.tchu.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;
/**
 * Generates messages about the game
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public final  class Info {
  
    private final String player;
    /**
     * Creates an instance of Info
     * @param player(String)- name of the player
     * 
     */
    public Info(String player) {
        this.player = player;  
    }
    /**
     * returns the name of the card in French
     * @param card(Card) -  
     * @param count(int)- number of cards
     * @return (String) - name of the card
     */
    public static String cardName(Card card, int count) {
        var builder = new StringBuilder();
        String s ;
        switch(card) {
        case BLACK      : s = StringsFr.BLACK_CARD;
                        break;
        case BLUE       : s = StringsFr.BLUE_CARD;
                        break;
        case GREEN      : s = StringsFr.GREEN_CARD;
                        break;
        case LOCOMOTIVE : s = StringsFr.LOCOMOTIVE_CARD;
                        break;
        case ORANGE     : s = StringsFr.ORANGE_CARD;
                        break;
        case RED        : s = StringsFr.RED_CARD;
                        break;
        case VIOLET     : s = StringsFr.VIOLET_CARD;
                        break;
        case WHITE      : s = StringsFr.WHITE_CARD;
                        break;
        case YELLOW     : s = StringsFr.YELLOW_CARD;
                        break;
        // IN CASE WE ADD A NEW CARD IN THE ENUM AND WE FORGOT TO ADD IT IN THE SWITCH
        default         : throw new IllegalArgumentException("NEED TO ADD THE NEW CARD HERE."); 
        }
        return builder.append(s).append(StringsFr.plural(count)).toString();  
    }
   
    /**
     * Compute the name of the route.
     * @param route (Route) - The route to use.
     * @return (String) - The name of the route.
     */
    private static String routeName(Route route) {
        var builder= new StringBuilder();
       builder.append(route.station1().name())
              .append(StringsFr.EN_DASH_SEPARATOR)
              .append(route.station2().name());
       return builder.toString();        
    }
    
    /**
     * Compute the name of the content of the sortedbag.
     * @param list (SortedBag<Card>) - The bag of cards.
     * @return (String) - The name.
     */
  private static String sortedBagName(SortedBag<Card> list) {
        var builder = new StringBuilder();
        List<Integer> nombre= new LinkedList<>();
        List<Card> cartes = new LinkedList<>();
        for (var card : list.toSet()) {
            nombre.add(list.countOf(card));
            cartes.add(card);
        }
        ListIterator<Card> it = cartes.listIterator();
        ListIterator<Integer> it1 = nombre.listIterator();
        while(it.hasNext()) {
            var count = it1.next();
               builder.append(count)
                      .append(" ");
            var actuelle = it.next();
            builder.append(Info.cardName(actuelle, count));
            if(it.nextIndex()==cartes.size()-1) builder.append(StringsFr.AND_SEPARATOR);
            else if(it.hasNext()) builder.append(", ");
        }
        return builder.toString();
    }
  
    /**
     * Compute the name of the trail.
     * @param trail (Trail) - The trail to use.
     * @return (String) - The name of the Trail.
     */
   private static String trailName(Trail trail) {
       var builder = new StringBuilder();
       builder.append(trail.station1().name()).append(StringsFr.EN_DASH_SEPARATOR)
              .append(trail.station2().name());
    return builder.toString();
      
   }
   
   /**
    * returns a message that indicates that the players have finished with the same amount of points
    * @param playerNames (List)- list of players
    * @param points (int)-points scored
    * @return (String) - 
    */
   public static String draw(List<String> playerNames, int points) {
        var noms = String.join(StringsFr.AND_SEPARATOR, playerNames);
        return String.format(StringsFr.DRAW, noms,points);
    }
   
   /**
    * indicates that this player will be the first to play
    * @return (String) - message
    */
   public String willPlayFirst() { 
       return String.format(StringsFr.WILL_PLAY_FIRST, this.player);
       }
   
   /** indicates that the player kept a number of tickets
    * @param count(int)- the number of tickets
    * @return (String) - the message
    */
   public String keptTickets(int count) {
       return String.format(StringsFr.KEPT_N_TICKETS,this.player,count,StringsFr.plural(count));
       }
   
   /** indicates that it's the player's turn to play
    * @return (String) - the message
    */
    public String canPlay() { 
        return String.format(StringsFr.CAN_PLAY, this.player);
        }
    /** indicates that the player drew a number of tickets
     * @param count(int)- the number of tickets
     * @return (String) - the message
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, this.player,count,StringsFr.plural(count));
        }
    
    /** indicates that the player drew a blind card.
     * @return (String) - the message
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD,this.player);
        }
    
    /** indicates that the player drew a visible card
     * @param card(Card)- the card drawn
     * @return (String) - the message
     */
    public String drewVisibleCard(Card card) { 
        return String.format(StringsFr.DREW_VISIBLE_CARD,this.player,Info.cardName(card,1)  );
        }
    
    /** indicates that the player claimed a route using a set of cards
     * @param route (Route)- the route claimed
     * @param cards (SortedBag)-cards played
     * @return (String) - the message
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) { 
        return String.format(StringsFr.CLAIMED_ROUTE, this.player,Info.routeName(route),Info.sortedBagName(cards));
        }
    /** indicates that the player attempted to claim a tunnel using a set of cards
     * @param route(Route)- the tunnel aimed at
     * @param initialCards (SortedBag)-cards played
     * @return (String) - the message
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) { 
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, this.player,Info.routeName(route),Info.sortedBagName(initialCards));
        }
    
    /** indicates that the player drew additional cards specifying it cost 
     * @param drawnCards (SortedBag)- cards drawn
     * @param additionalCost -points that the drawn cards are worth
     * @return (String) - the message
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        var builder = new StringBuilder();
        builder.append(String.format(StringsFr.ADDITIONAL_CARDS_ARE, Info.sortedBagName(drawnCards)));
         if(additionalCost==0) builder.append(StringsFr.NO_ADDITIONAL_COST);
         else builder.append(String.format(StringsFr.SOME_ADDITIONAL_COST,additionalCost,StringsFr.plural(additionalCost)));
         return builder.toString();
    }
    
    /** indicates that the player didn't (couldn't) claim a route 
     * @param route(Route)- the route 
     * @return (String) - the message
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE,this.player,Info.routeName(route));
        }
    
    /** indicates that the player is on his last turn
     * @param carCount(int)-  number of cars the player has
     * @return (String) - the message
     */
    public String  lastTurnBegins(int carCount) { 
        return String.format(StringsFr.LAST_TURN_BEGINS,this.player,carCount,StringsFr.plural(carCount));
        }
    
    /** indicates that the player gets 10 bonus points for the longest trail
     * @param longestTrail (Trail)- longest trail
     * @return (String) - the message
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, this.player,Info.trailName(longestTrail));
        }
    
    /** indicates that the player won the game
     * @param points(int)- player's points
     * @param loserPoints(int)-the opponent points
     * @return (String) - the message
     */
    public String won(int points, int loserPoints) { 
        return String.format(StringsFr.WINS, this.player,points,StringsFr.plural(points),loserPoints,StringsFr.plural(loserPoints));
        }
 
}
