package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * An abstract class that to use to create all panels for the cards.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
abstract class DecksViewCreator {
    
    /**
     * Create the Hand Panel and returns it to use it.
     * @param gs (ObservableGameState) - The ObservableGameState to use.
     * @return (Node) - The Node ready to be used.
     */
    public static Node createHandView(ObservableGameState gs ) {
        var root = new HBox();
        root.getStylesheets().addAll("decks.css","colors.css");
        var billets = new ListView<Ticket>();
        billets.setId("tickets");
        var handPane = new HBox();
        handPane.setId("hand-pane");
        var map = gs.cards();
        for (var card : map.keySet()) {
            var pane = new StackPane();
            pane.visibleProperty().bind(Bindings.greaterThan(gs.cards().get(card), 0));
            pane.getStyleClass().addAll("card", card.color().name());
            var count = new Text();
            count.textProperty().bind(StringExpression.stringExpression(gs.cards().get(card)));
            count.getStyleClass().add("count");
            createCard(pane);
            pane.getChildren().add(count);
            handPane.getChildren().add(pane);  
        }
        billets.itemsProperty().bind(new SimpleListProperty<>(gs.listTickets()));
        root.getChildren().addAll(billets,handPane);
        return root;
    }
    
    /**
     * Create the Deck's panel and returns it to use it.
     * @param gs (ObservableGameState) - The ObservableGameState to use.
     * @param ticketsHandler (ObjectProperty<DrawTicketsHandler>) - The Handler to use to pick tickets from the deck.
     * @param cardsHandler (ObjectProperty<DrawCardHandler>) - The Handler to use to pick cards.
     * @return (Node) - The Node ready to be used.
     */
    public static Node createCardsView(ObservableGameState gs , ObjectProperty<DrawTicketsHandler> ticketsHandler, ObjectProperty<DrawCardHandler> cardsHandler ) {
        var root = new VBox();
        root.getStylesheets().addAll("decks.css","colors.css");
        root.setId("card-pane");
        var deckCards = new Button(StringsFr.CARDS);
        deckCards.getStyleClass().add("gauged");
        deckCards.disableProperty().bind(cardsHandler.isNull());
        var deckTickets = new Button(StringsFr.TICKETS);
        deckTickets.getStyleClass().add("gauged");
        deckTickets.disableProperty().bind(ticketsHandler.isNull());
        root.getChildren().add(deckTickets);
        for (var slot : Constants.FACE_UP_CARD_SLOTS) {
            var card = gs.faceUpCard(slot);
            var pan = new StackPane();
            pan.setOnMouseClicked(e -> cardsHandler.get().onDrawCard(slot));
            pan.disableProperty().bind(cardsHandler.isNull());
            pan.getStyleClass().add("card");
            if(card != null) {
                card.addListener((o,ov,nv) ->{   
                    if (ov != null) pan.getStyleClass().remove(ov.color().name());
                    pan.getStyleClass().add(nv.color().name());
                    });
                }
            createCard(pan);
            root.getChildren().add(pan);
        }
        root.getChildren().add(deckCards);
        configTicket(gs,deckTickets,ticketsHandler);
        configCard(gs,deckCards,cardsHandler);
        return root;
    }
    
    /**
     * Configures and sets the graphics for the button(Ticket)
     * @param gs (ObservableGameState) - The ObservableGameState to use.
     * @param button (Button) - The Button to custom.
     * @param handler (ObjectProperty<DrawTicketsHandler>) - The Handler to use to pick tickets from the deck.
     */
    private static void configTicket(ObservableGameState gs, Button button, ObjectProperty<DrawTicketsHandler> handlerProperty) {
        basicGraphics(button, gs.rateTickets());
        button.setOnAction(e -> handlerProperty.get().onDrawTickets());
    }
    
    /**
     * Configures and sets the graphics for the button(Ticket)
     * @param gs (ObservableGameState) - The ObservableGameState to use.
     * @param button (Button) - The Button to custom.
     * @param handler (ObjectProperty<DrawCardHandler>) - The Handler to use to pick cards.
     */
    private static void configCard(ObservableGameState gs,Button button, ObjectProperty<DrawCardHandler> handlerProperty) {
        basicGraphics(button , gs.rateCards());
        button.setOnAction(e -> handlerProperty.get().onDrawCard(Constants.DECK_SLOT));
    }
    
    /**
     * Creates the Graphical Structure of a card
     * @param pan (StackPane) - The StackPane of the Card
     */
    private static void createCard(StackPane pan) {
        var rec1 = new Rectangle();
        rec1.setHeight(Constants.CARD_REC_1_HEIGHT);
        rec1.setWidth(Constants.CARD_REC_1_WIDTH);
        rec1.getStyleClass().add("outside");
        var rec2 = new Rectangle();
        rec2.setHeight(Constants.CARD_REC_2_HEIGHT);
        rec2.setWidth(Constants.CARD_REC_2_WIDTH);
        rec2.getStyleClass().addAll("inside","filled");
        var rec3 = new Rectangle();
        rec3.setHeight(Constants.CARD_REC_3_HEIGHT);
        rec3.setWidth(Constants.CARD_REC_3_WIDTH);
        rec3.getStyleClass().add("train-image");
        pan.getChildren().addAll(rec1,rec2,rec3);
    }
    
    /**
     * Create the basics of the graphics in the button without the handler.
     * @param button (Button) - The Button to custom.
     * @param property (ReadOnlyIntegerProperty) - The property with the rate.
     */
    private static void basicGraphics(Button button, ReadOnlyIntegerProperty property) {
        var rec1 = new Rectangle();
        rec1.setHeight(Constants.BUTTON_GRAPHICS_HEIGHT);
        rec1.setWidth(Constants.BUTTON_GRAPHICS_WIDTH);
        rec1.getStyleClass().add("background");
        var rec2 = new Rectangle();
        rec2.setHeight(Constants.BUTTON_GRAPHICS_HEIGHT);
        rec2.widthProperty().bind(property.multiply(Constants.BUTTON_GRAPHICS_WIDTH).divide(100));
        rec2.getStyleClass().add("foreground");
        button.setGraphic(new Group(rec1,rec2));
    }

}
