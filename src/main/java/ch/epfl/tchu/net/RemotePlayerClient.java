package ch.epfl.tchu.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.regex.Pattern;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;

/**
 * This class represents the client that will be used as a middleman between the player
 * and the server located in a different machine
 * In this class we will use the http vocabular to describe the protocol (request)
 * @author Hamza REMMAL (310917)
 * @author Mehid ZIAZI (311475)
 */
public class RemotePlayerClient {
    
    private final Player player;
    private final Socket socket;
    private final  BufferedReader reader;
    private final BufferedWriter writer;
    
    /**
	 * creates a remote player client that communicates with a player proxy in the server program using a socket
	 * and acts as the middleman with a player instance
	 * @param socket (Socket) - the socket used for the communication 
	 * @param name (String)- named host
	 * @param port (int)- port of the connection
	 */
    public RemotePlayerClient(Player player, String name, int port) throws IOException{
        this.player = player;
        this.socket = new Socket(name,port);
         reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.US_ASCII));
         writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.US_ASCII)); 					 
    }
    
    /**
     *loops while the game is still on and waits for a request from the player proxy  
     * once an instruction is received, calls the appropriated method of it's player 
     * and sends the necessary information (if needed) to the player proxy that runs in the server program
     */
    public void run() {	
        while(true) {
            var request = read();
            if (request == null ) break;
            var requestParts = request.split(Pattern.quote(" "));
            switch(MessageId.valueOf(requestParts[0])) {
                case INIT_PLAYERS : 
                    initPlayers(requestParts);
                    break;
                case CARDS : 
                    cards(requestParts);
                    break;
                case CHOOSE_ADDITIONAL_CARDS :
                    chooseAdditionalCards(requestParts);
                    break;
                case CHOOSE_INITIAL_TICKETS : 
                    chooseInitialTickets(requestParts);
                    break;
                case CHOOSE_TICKETS :
                    chooseTickets(requestParts);
                    break;
                case DRAW_SLOT : 
                    drawSlot(requestParts);
                    break;
                case NEXT_TURN : 
                    nextTurn(requestParts);
                    break;
                case RECEIVE_INFO :    
                    receiveInfo(requestParts);
                    break;
                case ROUTE :   
                    route(requestParts);
                    break;
                case SET_INITIAL_TICKETS : 
                    setInitialTickets(requestParts);
                    break;
                case UPDATE_STATE : 
                    updateState(requestParts);
                    break;
                    // IN CASE WE ADDED A NEW MESSAGEID AND FORGOT TO ADDED HERE IN THE SWITCH
                default : throw new IllegalArgumentException("NOT A VALID REQUEST");
            }
        }
    }
    
    /**
     * method for the UPDATE_STATE request
     * @param requestParts (String[]) - request parts to use.
     */
    private void updateState(String[] requestParts) {
        assert(requestParts[0].equals( MessageId.UPDATE_STATE.name()));
        var gs = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(requestParts[1]);
        var ps = Serdes.PLAYER_STATE_SERDE.deserialize(requestParts[2]);
        player.updateState(gs, ps);
    }

    /**
     * method for the SET_INITIAL_TICKETS request
     * @param requestParts (String[]) - request parts to use.
     */
    private void setInitialTickets(String[] requestParts) {
    	assert(requestParts[0].equals( MessageId.SET_INITIAL_TICKETS.name()));
        var initialTickets= Serdes.BAG_TICKET_SERDE.deserialize(requestParts[1]);
        player.setInitialTicketChoice(initialTickets);  
    }

    /**
     * method for the ROUTE request
     * @param requestParts (String[]) - request parts to use.
     */
    private void route(String[] requestParts) {
    	assert(requestParts[0].equals( MessageId.ROUTE.name()));
    	var routeSerde= Serdes.ROUTE_SERDE.serialize(player.claimedRoute());
    	write(String.format("%s\n", routeSerde));
    }

    /**
     * method for the RECEIVE_INFO request
     * @param requestParts (String[]) - request parts to use.
     */
    private void receiveInfo(String[] requestParts) {
    	assert(requestParts[0].equals( MessageId.RECEIVE_INFO.name()));
		player.receiveInfo(Serdes.STRING_SERDE.deserialize(requestParts[1]));
		
    }
    
    /**
     * method for the NEXT_TURN request
     * @param requestParts (String[]) - request parts to use.
     */
    private void nextTurn(String[] requestParts) {
        assert(requestParts[0].equals( MessageId.NEXT_TURN.name()));
		write(String.format("%s\n", Serdes.TURNKIND_SERDE.serialize(player.nextTurn())));
    }

    /**
     * method for the DRAW_SLOT request
     * @param requestParts (String[]) - request parts to use.
     */
    private void drawSlot(String[] requestParts) {
    	assert(requestParts[0].equals( MessageId.DRAW_SLOT.name()));
		write(String.format("%s\n", Serdes.INT_SERDE.serialize(player.drawSlot())));
    }
    
    /**
     * method for the CHOOSE_TICKETS request
     * @param requestParts (String[]) - request parts to use.
     */
    private void chooseTickets(String[] requestParts) {
    	assert(requestParts[0].equals( MessageId.CHOOSE_TICKETS.name()));
		var choosenTickets = player.chooseTickets(Serdes.BAG_TICKET_SERDE.deserialize(requestParts[1]));
		write(String.format("%s\n", Serdes.BAG_TICKET_SERDE.serialize(choosenTickets)));
    }
   
    /**
     * method for the CHOOSE_INITIAL_TICKETS request
     * @param requestParts (String[]) - request parts to use.
     */
    private void chooseInitialTickets(String[] requestParts) {
    	assert(requestParts[0].equals( MessageId.CHOOSE_INITIAL_TICKETS.name()));
    	var chooseTicketSerde = Serdes.BAG_TICKET_SERDE.serialize(player.chooseInitialTickets());
    	write(String.format("%s\n", chooseTicketSerde));
    }

    /**
     * method for the INIT_PLAYERS request
     * @param requestParts (String[]) - request parts to use.
     */
    private void initPlayers(String[] requestParts) {
    	assert(requestParts[0].equals( MessageId.INIT_PLAYERS.name()));
        var listNames = Serdes.LIST_STRING_SERDE.deserialize(requestParts[2]);
        EnumMap<PlayerId, String> mapPlayers = new EnumMap<>(PlayerId.class);
        mapPlayers.put(PlayerId.PLAYER_1, listNames.get(0));
        mapPlayers.put(PlayerId.PLAYER_2, listNames.get(1));
        player.initPlayers(Serdes.PLAYER_ID_SERDE.deserialize(requestParts[1]), mapPlayers);
    }
    
    /**
     * method for the CARDS request
     * @param requestParts (String[]) - request parts to use.
     */
    private void cards(String[] requestParts) {
    	assert(requestParts[0].equals( MessageId.CARDS.name()));
    	String cardsSerde = Serdes.BAG_CARD_SERDE.serialize(player.initialClaimCards());
    	write(String.format("%s\n", cardsSerde));
    }
    
    /**
     * method for the CHOOSE_ADDITIONAL_CARDS request
     * @param requestParts (String[]) - request parts to use.
     */
    private void chooseAdditionalCards(String[] requestParts) {
    	assert(requestParts[0].equals( MessageId.CHOOSE_ADDITIONAL_CARDS.name()));
    	var options= Serdes.LIST_BAG_CARD_SERDE.deserialize(requestParts[1]);
    	var choosenCards= Serdes.BAG_CARD_SERDE.serialize(player.chooseAdditionalCards(options));
    	write(String.format( "%s\n",choosenCards));
    }
    
    // ----------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------------------- WRITE AND READ METHODS ------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Write a value to the Stream.
     * @param request (String) - Value to write to the stream.
     */
    private void write(String request) {
        try {
            writer.write(request);
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
