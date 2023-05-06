package ch.epfl.tchu.net;

/**
 * This class is an enumeration of all types of messages that the server will send to the client
 * @author Hamza REMMAL (310917)
 * @author Mehid ZIAZI (311475)
 */
public enum MessageId {
    /** ??? */
	INIT_PLAYERS,
    /** ??? */
	RECEIVE_INFO,
    /** ??? */
	UPDATE_STATE,
    /** ??? */
	SET_INITIAL_TICKETS,
    /** ??? */
	CHOOSE_INITIAL_TICKETS,
    /** ??? */
	NEXT_TURN,
    /** ??? */
	CHOOSE_TICKETS,
    /** ??? */
	DRAW_SLOT,
    /** ??? */
	ROUTE,
    /** ??? */
	CARDS,
    /** ??? */
	CHOOSE_ADDITIONAL_CARDS;
}
