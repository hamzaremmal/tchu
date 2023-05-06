package ch.epfl.tchu.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * This class represents the player proxy that will be used in the server and that
 * will communicate with the player in the client
 * @author Hamza REMMAL (310917)
 * @author Mehid ZIAZI (311475)
 */
public class RemotePlayerProxy implements Player{
    
    private final BufferedWriter writer;
    private final BufferedReader reader;

    /**
     * ???
     * @return ???
     */
    public String getName() {
        return read();
    }
    
    /**
	 * creates a player proxy that communicates with a player in the client program using a socket
	 * @param socket (Socket) - the socket used for the communication
     * @throws IOException ???
	 */
    public RemotePlayerProxy(Socket socket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.US_ASCII));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.US_ASCII));
    }
    /**
   	 * communicates to the player, via the socket, its own identity and the name of the other players
   	 * @param ownId (PlayerId ) - the identity of the player
   	 * @param playerNames (Map)- the name and identity of the other players.
   	 */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
       var idSerde = Serdes.PLAYER_ID_SERDE.serialize(ownId);
       var map = Serdes.LIST_STRING_SERDE.serialize(List.of(playerNames.get(PlayerId.PLAYER_1),
                                                    playerNames.get(PlayerId.PLAYER_2)));
       write(String.format("%s %s %s%n", MessageId.INIT_PLAYERS.name(),idSerde,map));
    }
    /**
   	 * communicates to the player, via the socket, a game information
   	 * @param info (String ) - the information
   	 */
    @Override
    public void receiveInfo(String info) {
    		var infoSerde = Serdes.STRING_SERDE.serialize(info);
    		write(String.format("%s %s%n",MessageId.RECEIVE_INFO.name(),infoSerde));
    	   
    }
    /**
   	 * communicates to the player, via the socket, the current game state and it's current state
   	 * used each time the game changes to update the player
   	 * @param newState (PublicGameState ) - the game state  after a change
   	 * @param ownState (PlayerState)- the  corresponding state of the player after the game changed 
   	 */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
    		var newStateSerde = Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState);
    		var ownStateSerde = Serdes.PLAYER_STATE_SERDE.serialize(ownState);
            write(String.format("%s %s %s%n",MessageId.UPDATE_STATE.name(),newStateSerde,ownStateSerde));
            }
    /**
   	 * communicates to the player, via the socket, the initial tickets to choose from 
   	 * used in the beginning of the game
     * @param tickets ???
   	 */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
    		var ticketsSerde= Serdes.BAG_TICKET_SERDE.serialize(tickets);
            write(String.format("%s %s%n",MessageId.SET_INITIAL_TICKETS.name(),ticketsSerde));
    }
    
    /**
   	 * returns the initial choice of tickets of the player in the client
   	 * used in the beginning of the game 
   	 * @return (SortedBag) tickets initially kept by the player
   	 */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        	write(String.format("%s%n",MessageId.CHOOSE_INITIAL_TICKETS.name()));
			var ticketsSerde = read();
			return Serdes.BAG_TICKET_SERDE.deserialize(ticketsSerde);
			}
    /**
   	 * returns the type of action the player in the client wants to perform in it's turn
   	 * @return (TurnKind) action chosen by the player
   	 */
    @Override
    public TurnKind nextTurn() {
        	write(String.format("%s%n",MessageId.NEXT_TURN.name()));
			var turnKindString = read();
			return Serdes.TURNKIND_SERDE.deserialize(turnKindString);  
    }
    /**
   	 * communicates to the player, via the socket, the tickets to choose from and returns the choice of the player 
   	 * @param options (SortedBag) the tickets to choose form
   	 * @return (SortedBag) tickets kept by the player
   	 */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
    	var optionsSerde = Serdes.BAG_TICKET_SERDE.serialize(options);
		write(String.format("%s %s%n",MessageId.CHOOSE_TICKETS.name() ,optionsSerde));
		var chooseTicketsSerde = read();
		SortedBag<Ticket> chooseTickets= Serdes.BAG_TICKET_SERDE.deserialize(chooseTicketsSerde);
		return chooseTickets;
    }
    /**
   	 *returns the position of the face up card the player wants to pick or -1 if the player chose the deck
   	 * @return (int)  position of the face up card or -1 for the deck
   	 */
    @Override
    public int drawSlot() { 
    		   write(String.format("%s%n",MessageId.DRAW_SLOT));
    			var slotSerde = read();
    			int slot = Serdes.INT_SERDE.deserialize(slotSerde);
    			return slot;  
    }
    /**
   	 * returns the route chosen by the player to (attempt to )claim
   	 * @return (Route) route chosen by the player
   	 */
    @Override
    public Route claimedRoute() {
    		write(String.format("%s%n",MessageId.ROUTE.name()));
 			var routeSerde = read();
 			var route = Serdes.ROUTE_SERDE.deserialize(routeSerde);
 			return route;
    }
    /**
   	 * returns the cards initially chosen by the player to (attempt to) claim a route 
   	 * @return (SortedBag) cards initially chosen by the player
   	 */
    @Override
    public SortedBag<Card> initialClaimCards() {
    		write(String.format("%s%n",MessageId.CARDS.name()));
 			var cardSerde =read();
 			var cards = Serdes.BAG_CARD_SERDE.deserialize(cardSerde);
 			return cards;
    }
    /**
   	 * communicates to the player, via the socket, the different possibilities of additional cards to play to claim a tunnel
   	 * returns the chosen cards of the player or an empty sorted bag if the player wants to abandon the claim
   	 * @param options (SortedBag) all the options to claim the tunnel
   	 * @return (SortedBag)  the choice of the player
   	 */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
    		var optionsSerde = Serdes.LIST_BAG_CARD_SERDE.serialize(options);
    		write(String.format("%s %s%n",MessageId.CHOOSE_ADDITIONAL_CARDS.name() ,optionsSerde));
 			var choosenCardsSerde = read();
 			var choosenCards= Serdes.BAG_CARD_SERDE.deserialize(choosenCardsSerde);
 			return choosenCards;
    }
    
    // ----------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------------------- WRITE AND READ METHODS ------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Write a value to the Stream.
     * @param requete (String) - Value to write to the stream.
     */
    private void write(String requete) {
        try {
            writer.write(requete);
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    /**
     * Read a value from the stream.
     * @return (String) - Value from the stream.
     */
    private String read() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
