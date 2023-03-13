package ch.epfl.tchu.net;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * This class defines all the serdes that are going to be used for our project
 * @author Hamza REMMAL (310917)
 * @author Mehid ZIAZI (311475)
 */
public final class Serdes {
   
    /**
     *Creates a serde that can serialize and deserialize strings
     */
    public static final Serde<String> STRING_SERDE = Serde.of(s -> Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)) , 
                                                              s -> new String(Base64.getDecoder().decode(new String(s)),StandardCharsets.UTF_8));
	
    /**
     *Creates a serde that can serialize and deserialize integers
     */ 
    public static final Serde<Integer> INT_SERDE = Serde.of(i -> Integer.toString(i), s -> Integer.parseInt(s));
	
    /**
     *Creates a serde that can serialize and deserialize a Playerid
     */
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);
	
    /**
     *Creates a serde that can serialize and deserialize a Turnkind
     */
    public static final Serde<TurnKind> TURNKIND_SERDE = Serde.oneOf(TurnKind.ALL);
	
    /**
     *Creates a serde that can serialize and deserialize cards
     */
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);
	
    /**
     *Creates a serde that can serialize and deserialize routes
     */
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());
	
    /**
     *Creates a serde that can serialize and deserialize tickets
     */
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());
	
    /**
     *Creates a serde that can serialize and deserialize a list of strings
     */
    public static final Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE, ",");
	
    /**
     *Creates a serde that can serialize and deserialize a list of cards
     */
    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, ",");
	
    /**
     *Creates a serde that can serialize and deserialize a list of routes
     */
    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, ",");
	
    /**
     *Creates a serde that can serialize and deserialize a sorted bag of cards
     */
    public static final Serde<SortedBag<Card>> BAG_CARD_SERDE = Serde.bagOf(CARD_SERDE, ",");
	
    /**
     *Creates a serde that can serialize and deserialize  a sorted bag of tickets
     */
    public static final Serde<SortedBag<Ticket>> BAG_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, ",");
	
    /**
     *Creates a serde that can serialize and deserialize list of a sorted bag of cards
     */
    public static final Serde<List<SortedBag<Card>>> LIST_BAG_CARD_SERDE = Serde.listOf(BAG_CARD_SERDE, ";");	
	
    /**
     *Creates a serde that can serialize and deserialize a player state
     */
    public static final Serde<PlayerState> PLAYER_STATE_SERDE  = Serde.of(playerState -> String.format("%s;%s;%s" ,BAG_TICKET_SERDE.serialize(playerState.tickets()),
                                                                                                                   BAG_CARD_SERDE.serialize(playerState.cards()),
                                                                                                                   LIST_ROUTE_SERDE.serialize(playerState.routes())),
	                                                                      s -> { var list = s.split(Pattern.quote(";"),-1);
	                                                                             return new PlayerState(BAG_TICKET_SERDE.deserialize(list[0]),
	                                                                                                    BAG_CARD_SERDE.deserialize(list[1]),
	                                                                                                    LIST_ROUTE_SERDE.deserialize(list[2]));});
    
    /**
     *Creates a serde that can serialize and deserialize a public card state
     */
	public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(cardState -> String.format("%s;%s;%s", LIST_CARD_SERDE.serialize(cardState.faceUpCards()),
	                                                                                                                     INT_SERDE.serialize(cardState.deckSize()),
	                                                                                                                     INT_SERDE.serialize(cardState.discardsSize())), 
	                                                                              s -> { var list = s.split(Pattern.quote(";"));
	                                                                                     return new PublicCardState(LIST_CARD_SERDE.deserialize(list[0]),
	                                                                                                                INT_SERDE.deserialize(list[1]),
	                                                                                                                INT_SERDE.deserialize(list[2]));});
	       
	/**
     *Creates a serde that can serialize and deserialize a public player state 
     */ 
	public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = new Serde<>() {
				@Override
				public String serialize(PublicPlayerState playerState) { 
				    return String.format("%s;%s;%s", INT_SERDE.serialize(playerState.ticketCount()),
				                                     INT_SERDE.serialize(playerState.cardCount()),
				                                     LIST_ROUTE_SERDE.serialize(playerState.routes()));
				    }

				@Override
				public PublicPlayerState deserialize(String string) {
					var list = string.split(Pattern.quote(";"));
					List<Route> routes;
					try {
					 routes = LIST_ROUTE_SERDE.deserialize(list[2]);
					}
					catch(IndexOutOfBoundsException e) {
					 routes = LIST_ROUTE_SERDE.deserialize("");
					}
					return new PublicPlayerState(INT_SERDE.deserialize(list[0]),
					                             INT_SERDE.deserialize(list[1]),
					                             routes);
				}
	};
	
	/**
     *Creates a serde that can serialize and deserialize a public game state
     */
	public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = new Serde<>() {
				@Override
				public String serialize(PublicGameState game) {
					var builder = new StringBuilder();
					builder.append(INT_SERDE.serialize(game.ticketsCount()))
					       .append(":")
					       .append(PUBLIC_CARD_STATE_SERDE.serialize(game.cardState()))
					       .append(":")
					       .append(PLAYER_ID_SERDE.serialize(game.currentPlayerId()))
					       .append(":");
				    PlayerId.ALL.forEach(p -> {
			    	   builder.append(PUBLIC_PLAYER_STATE_SERDE.serialize(game.playerState(p)))
			    	          .append(":");
			       });
				    if(game.lastPlayer()!= null) builder.append(PLAYER_ID_SERDE.serialize(game.lastPlayer()));
					return builder.toString();
				}

				@Override
				public PublicGameState deserialize(String string) {
					var list = string.split(Pattern.quote(":"));
					List<PublicPlayerState> playerStates = new ArrayList<>();
					for(int i= 1 ; i <= PlayerId.COUNT;++i) {
						playerStates.add(PUBLIC_PLAYER_STATE_SERDE.deserialize(list[i+2]));
					}
					Map<PlayerId,PublicPlayerState> players = new EnumMap<>(PlayerId.class);
					for (var j = 0; j < playerStates.size(); j++) {
						players.put(PlayerId.values()[j], playerStates.get(j));
					}
					PlayerId lastPlayer;
					try {
						 lastPlayer = PLAYER_ID_SERDE.deserialize(list[3+PlayerId.COUNT]);
					}
					catch(IndexOutOfBoundsException exp) {
						lastPlayer = null;
					}
					return new PublicGameState(INT_SERDE.deserialize(list[0]), 
					                           PUBLIC_CARD_STATE_SERDE.deserialize(list[1]),
					                           PLAYER_ID_SERDE.deserialize(list[2]),
					                           players,
					                           lastPlayer);
				}
	};

}
