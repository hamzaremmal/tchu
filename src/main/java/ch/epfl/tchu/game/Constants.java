package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;

/**
 * ???
 */
public final class Constants {
    private Constants() {}
    
    // ----------------------------------------------------------------------------------------------------------------------------
    // --------------------------------------------------- GRAPHICS CONSTANTS -----------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------------------------

    /**
     * The height of the first rectangle int he cards stack.
     */
    public static final int CARD_REC_1_HEIGHT = 90;
    
    /**
     * The width of the first rectangle int he cards stack.
     */
    public static final int CARD_REC_1_WIDTH  = 60;
    
    /**
     * The height of the second rectangle int he cards stack.
     */
    public static final int CARD_REC_2_HEIGHT = 70;
    
    /**
     * The width of the second rectangle int he cards stack.
     */
    public static final int CARD_REC_2_WIDTH  = 40;
    
    /**
     * The height of the third rectangle int he cards stack.
     */
    public static final int CARD_REC_3_HEIGHT = 70;
    
    /**
     * The width of the third rectangle int he cards stack.
     */
    public static final int CARD_REC_3_WIDTH  = 40;
    
    /**
     * The height of the buttons.
     */
    public static final int BUTTON_GRAPHICS_HEIGHT = 5;
    
    /**
     * The width of the buttons.
     */
    public static final int BUTTON_GRAPHICS_WIDTH = 50;
    
    /**
     * The radius of the circles in info.
     */
    public static final int INFO_CIRCLE_RADIUS = 5;
    
    /**
     * The radius of the circles in the roads.
     */
    public static final int ROUTE_CIRCLE_RADIUS = 3;
    
    /**
     * The X coodinate for the first circle.
     */
    public static final int ROUTE_CIRCLE_1_X = 12;
    
    /**
     * The X coodinate for the second circle.
     */
    public static final int ROUTE_CIRCLE_2_X = 24;
    
    /**
     * The Y coodinate for the circles..
     */
    public static final int ROUTE_CIRCLE_Y = 6;
    
    /**
     * The route height.
     */
    public static final int ROUTE_HEIGHT = 12;
    
    /**
     * The route width.
     */
    public static final int ROUTE_WIDTH = 36;
    
    
    
    
    
    
    
    // ----------------------------------------------------------------------------------------------------------------------------
    // ----------------------------------------------- COMMUNICATION CONSTANTS ----------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------------------------
    
    /**
     * The default port to use in the game
     */
    public static final int SERIAL_PORT = 5108;
    
    /**
     * The default host to use in the game
     */
    public static final String DEFAULT_HOST = "localhost";   
    
    /**
     * 
     */
    public static final int BYTES_AUDIO = 5000;
    
    // ----------------------------------------------------------------------------------------------------------------------------
    // ----------------------------------------------------- GAME CONSTANTS -------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------------------------
    
    /**
     * The default name to use for the PLAYER_1
     */
    public static final String DEFAULT_PLAYER_1_NAME = "Ada";  
    
    /**
     * The default name to use for the PLAYER_2
     */
    public static final String DEFAULT_PLAYER_2_NAME = "Charles";   
    
    /**
     * Nombre de cartes wagon de chaque couleur.
     */
    public static final int CAR_CARDS_COUNT = 12;

    /**
     * Nombre de cartes locomotive.
     */
    public static final int LOCOMOTIVE_CARDS_COUNT = 14;

    /**
     * Nombre total de cartes wagon/locomotive.
     */
    public static final int TOTAL_CARDS_COUNT = LOCOMOTIVE_CARDS_COUNT + CAR_CARDS_COUNT * Color.COUNT;

    /**
     * Ensemble de toutes les cartes (110 au total).
     */
    public static final SortedBag<Card> ALL_CARDS = computeAllCards();

    private static SortedBag<Card> computeAllCards() {
        var cardsBuilder = new SortedBag.Builder<Card>();
        cardsBuilder.add(LOCOMOTIVE_CARDS_COUNT, Card.LOCOMOTIVE);
        for (Card card : Card.CARS)
            cardsBuilder.add(CAR_CARDS_COUNT, card);
       assert cardsBuilder.size() == TOTAL_CARDS_COUNT;
        return cardsBuilder.build();
    }

    /**
     * Numéro d'emplacement fictif désignant la pioche de cartes.
     */
    public static final int DECK_SLOT = -1;

    /**
     * Liste de tous les numéros d'emplacements de cartes face visible.
     */
    public static final List<Integer> FACE_UP_CARD_SLOTS = List.of(0, 1, 2, 3, 4);

    /**
     * Nombre d'emplacements pour les cartes face visible.
     */
    public static final int FACE_UP_CARDS_COUNT = FACE_UP_CARD_SLOTS.size();

    /**
     * Nombre de billets distribués à chaque joueur en début de partie.
     */
    public static final int INITIAL_TICKETS_COUNT = 5;

    /**
     * Nombre de cartes distribuées à chaque joueur en début de partie.
     */
    public static final int INITIAL_CARDS_COUNT = 4;

    /**
     * Nombre de wagons dont dispose chaque joueur en début de partie.
     */
    public static final int INITIAL_CAR_COUNT = 40;

    /**
     * Nombre de billets tirés à la fois en cours de partie.
     */
    public static final int IN_GAME_TICKETS_COUNT = 3;

    /**
     * Nombre maximum de billets qu'un joueur peut défausser lors d'un tirage.
     */
    public static final int DISCARDABLE_TICKETS_COUNT = 2;

    /**
     * Nombre de cartes à tirer lors de la construction d'un tunnel.
     */
    public static final int ADDITIONAL_TUNNEL_CARDS = 3;

    /**
     * Nombre de points obtenus pour la construction de routes de longueur 1 à 6.
     * (L'élément à l'index i correspond à une longueur de route i. Une valeur
     * invalide est placée à l'index 0, car les routes de longueur 0 n'existent pas).
     */
    public static final List<Integer> ROUTE_CLAIM_POINTS = List.of(Integer.MIN_VALUE, 1, 2, 4, 7, 10, 15);

    /**
     * Longueur minimum d'une route.
     */
    public static final int MIN_ROUTE_LENGTH = 1;

    /**
     * Longueur maximum d'une route.
     */
    public static final int MAX_ROUTE_LENGTH = ROUTE_CLAIM_POINTS.size() - 1;

    /**
     * Nombre de points bonus obtenus par le(s) joueur(s) disposant du plus long chemin.
     */
    public static final int LONGEST_TRAIL_BONUS_POINTS = 10;
}
