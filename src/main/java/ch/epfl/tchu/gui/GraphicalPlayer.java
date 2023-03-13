package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseTicketsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import ch.epfl.tchu.net.Call;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

/**
 * This class represents the graphical interface of a player
 * @author Hamza REMMAL (310917)
 * @author Mehid ZIAZI (311475)
 */
public class GraphicalPlayer {
    
    private final ObservableGameState gs;
    private final Stage mainStage;
    private final ObservableList<Text> info;
    private final ObjectProperty<DrawCardHandler>    gestionnaireCartes  = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawTicketsHandler> gestionnaireTickets = new SimpleObjectProperty<>();
    private final ObjectProperty<ClaimRouteHandler>  gestionnaireRoutes  = new SimpleObjectProperty<>();
     
    /**
     * Creates an instance of GraphicalPlayer
     * @param id (PlayerId) - the identity of the player
     * @param nameMap (Map<PlayerId, String>) - a map with the identities of all players and they're names
     */
     public GraphicalPlayer(PlayerId id, Map<PlayerId, String> nameMap, Call call) {
         if (!Platform.isFxApplicationThread()) 
             throw new AssertionError("This constructor need to be run on the JAVAFX Application Thread");
         this.gs = new ObservableGameState(id);
         this.info = FXCollections.observableArrayList(new Text(),new Text(),new Text(),new Text(),new Text());
         this.mainStage = createMainStage(id,nameMap,call);   
         }
     
     
     // ---------------------------------------------------------------------------------------------------------------------------
     // ---------------------------------------------------- GRAPHICAL METHODS ----------------------------------------------------
     // ---------------------------------------------------------------------------------------------------------------------------
     
     /**
      * Creates the main window with the view of the map, the player's hand, the deck and the information panel
      * @param id (PlayerId) -the identity of the player
      * @param nameMap (Map<PlayerId, String>) - a map with the identities of all players and they're names
      * @return (Stage) -The window that will be opened
      */
    private Stage createMainStage(PlayerId id, Map<PlayerId, String> nameMap, Call call) {
        if (!Platform.isFxApplicationThread())
            throw new AssertionError("This method need to be run on the JAVAFX Application Thread");
        var primaryStage = new Stage();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setTitle("tChu - " + nameMap.get(id));
        var mapView   = MapViewCreator.createMapView(gs, gestionnaireRoutes, this::chooseClaimCards);
        var handView  = DecksViewCreator.createHandView(gs);
        var cardsView = DecksViewCreator.createCardsView(gs, gestionnaireTickets, gestionnaireCartes);
        var infoView  = InfoViewCreator.createInfoView(id, nameMap, gs, info);  
        var menuView  = call != null ? MenuViewCreator.createMenuView(call,nameMap.get(id.next())) : null;
        var mainPane  = new BorderPane(mapView, menuView, cardsView, handView, infoView);
        primaryStage.setScene(new Scene(mainPane));
        if(call != null)
            mainPane.setOnKeyPressed(e -> {
                if(e.getCode().equals(KeyCode.M))
                    call.switchSourdine();
                else
                    e.consume();
                });
        primaryStage.show();
        return primaryStage;
        }
    
    /**
     * Creates the ticket window that will allow the player to make his choice
     * @param items (ObservableList<Ticket>) -tickets to choose from
     * @param chooseTicketsHandler (ChooseTicketsHandler) - the handler that communicates the player's choice to the game thread
     * @param minimumTickets (int) -the minimum tickets that the player can choose
     */
    private void createTicketGUI(ObservableList<Ticket> items , ChooseTicketsHandler chooseTicketsHandler, int minimumTickets) {
        if(!Platform.isFxApplicationThread()) 
            throw new AssertionError("This method need to be run on the JAVAFX Application Thread");
        var list = new ListView<>(items);
        var button = new Button();
        var stage = configStage(mainStage,
                                list,
                                button,
                                String.format(StringsFr.CHOOSE_TICKETS, minimumTickets,StringsFr.plural(minimumTickets)),
                                minimumTickets);
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        button.setOnAction(e -> {
            chooseTicketsHandler.onChooseTickets(SortedBag.of(list.getSelectionModel().getSelectedItems()));
            stage.hide();
            });
        }
     
    /**
     * Creates the card window that will allow the player to make his choice
     * @param items (ObservableList<SortedBag<Card>>) - the sorted bag of cards to choose from
     * @param chooseCardsHandler (ChooseCardsHandler) - the handler that communicates the player's choice to the game thread
     * @param textFlow (String) - String to be written in the text flow
     * @param size (int) - minimum number of element chosen
     */
     private  void createCardsGUI(ObservableList<SortedBag<Card>> items, ChooseCardsHandler chooseCardsHandler, String textFlow, int size) {
          if(!Platform.isFxApplicationThread()) 
              throw new AssertionError("This method need to be run on the JAVAFX Application Thread");
          var list = new ListView<>(items);
          var button = new Button();
          var stage = configStage(mainStage,
                                  list,
                                  button,
                                  textFlow,
                                  size);
          list.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
          list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
          button.setOnAction(e -> {
              var choice = list.getSelectionModel().getSelectedItem();
              chooseCardsHandler.onChooseCards(Objects.isNull(choice) ? SortedBag.of() : choice);
              stage.hide();  
              });
          }
 
     /**
     * Configure the pop-up window's stage.
     * @param mainStage (Stage) - The owner Stage.
     * @param box (VBox) - The box to use
     * @return (Stage) - The Stage ready to be used
     */
    private static Stage configStage(Stage mainStage,ListView<?> list, Button button,String text, int minimum) {
        var stage = new Stage(StageStyle.UTILITY);
        var box = new VBox();
        var flow = new TextFlow(new Text(text));
        stage.setTitle(StringsFr.TICKETS_CHOICE);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(e -> e.consume());
        var scene = new Scene(box);
        stage.setScene(scene);
        button.disableProperty().bind(Bindings.greaterThan(minimum,Bindings.size(list.getSelectionModel().getSelectedItems())));
        button.setText(StringsFr.CHOOSE);
        scene.getStylesheets().add("chooser.css");
        box.getChildren().addAll(flow,list,button);
        stage.show();
        return stage;
    }
    
    
     // ---------------------------------------------------------------------------------------------------------------------------
     // ------------------------------------------------- INTERACTION METHODS -----------------------------------------------------
     // ---------------------------------------------------------------------------------------------------------------------------
     
     
     /**
      * actualizes the info view of the graphical interface
      * @param info (String) - the new info that needs to be shown 
      */
    public void receiveInfo(String info) {
        if (!Platform.isFxApplicationThread()) 
            throw new AssertionError("This method need to be run on the JAVAFX Application Thread");
        for(int i = 0; i < 4 ; i++) {
            this.info.get(i).setText(this.info.get(i+1).getText());;
        }
        this.info.get(4).setText(info);
    }
    
    /**
     * Updates the graphical interface
     * @param newState (PublicGameState) - the new state of the game
     * @param ownState (PlayerState) - the state of the player 
     */
    public void updateState(PublicGameState newState, PlayerState ownState) {
        if (!Platform.isFxApplicationThread()) 
            throw new AssertionError("This method need to be run on the JAVAFX Application Thread");
        gs.setState(newState, ownState); 
    }
    
    /**
     * Creates a window that allows the player to choose a sorted bag of cards from a list of options
     * @param options (List<SortedBag<Card>>) - the list of options to choose from
     * @param chooseCardsHandler (ChooseCardsHandler) - the handler that communicates the choice of the player to the game thread
     */
  public void chooseAdditionalCards(List<SortedBag<Card>> options, ChooseCardsHandler chooseCardsHandler) {
      if (!Platform.isFxApplicationThread()) 
          throw new AssertionError("This method need to be run on the JAVAFX Application Thread");
      this.createCardsGUI(FXCollections.observableArrayList(options),chooseCardsHandler,StringsFr.CHOOSE_ADDITIONAL_CARDS,0);
    }
  
  /**
   * In the beginning of each turn, fills the property of the handler corresponding to each action 
   * the player can perform or null if the player can't execute it
   * @param drawTicketsHandler (DrawTicketsHandler) - the handler that communicates the kind of action performed(draw tickets) by the player to the game thread
   * @param drawCardHandler (DrawCardHandler) -the handler that communicates  the kind of action performed (draw cards) and the index to draw from to the game thread
   * @param claimRouteHanlder (ClaimRouteHandler) -the handler that communicates  the kind of action performed (claim route), the route that the player wants to claim and the cards used for that to the game thread
   */
    public void startTurn(DrawTicketsHandler drawTicketsHandler, DrawCardHandler drawCardHandler, ClaimRouteHandler claimRouteHanlder) {
        if (!Platform.isFxApplicationThread())
            throw new AssertionError("This method need to be run on the JAVAFX Application Thread");
    	if(gs.canDrawTickets()) 
    	    this.gestionnaireTickets.set(() -> {
    	        drawTicketsHandler.onDrawTickets();
    	        resetProperties();
    	        });
    	else 
    	    this.gestionnaireTickets.set(null);
    	if(gs.canDrawCards()) 
    	    this.gestionnaireCartes.set(slot -> {
    	        drawCardHandler.onDrawCard(slot);
    	        resetProperties();
    	        });
    	else 
    	    this.gestionnaireCartes.set(null);
    	this.gestionnaireRoutes.set((route,cards) -> {
    	    claimRouteHanlder.onClaimRoute(route, cards);	
    		resetProperties();
    	});	
    }
    
    /**
     * Creates a window that will allow the player to choose a ticket from a sorted bag of options
     * @param options (SortedBag<Ticket>) - the options to choose from
     * @param chooseTicketsHandler (ChooseTicketsHandler) -the handler that communicates the choice of the player to the game thread
     */
    public void chooseTickets(SortedBag<Ticket> options, ChooseTicketsHandler chooseTicketsHandler) {
        if (!Platform.isFxApplicationThread()) 
            throw new AssertionError("This method need to be run on the JAVAFX Application Thread");
    	this.createTicketGUI(FXCollections.observableArrayList(options.toList()),chooseTicketsHandler,1);
    }
    
    /**
     * Creates a window that will allow the player to choose the initial tickets from a sorted bag of options
     * @param options (SortedBag<Ticket>) - the options to choose from
     * @param chooseTicketsHandler (ChooseTicketsHandler) -the handler that communicates the choice of the player to the game thread
     */
    public void setInitialTicketChoice(SortedBag<Ticket> tickets, ChooseTicketsHandler chooseTicketsHandler) {
        if (!Platform.isFxApplicationThread()) 
            throw new AssertionError("This method need to be run on the JAVAFX Application Thread");
        this.createTicketGUI(FXCollections.observableArrayList(tickets.toList()),chooseTicketsHandler,3);
    }
    
    /**
     * Creates a window that will allow the player to choose a sorted bag of cards to
     * attempt to claim a route from a list of options
     * @param options (List<SortedBag<Card>>) - the list of sorted bag of cards to choose form
     * @param chooseCardHandler (ChooseCardsHandler) -the handler that communicates the choice of the player to the game thread
     */
    public void chooseClaimCards(List<SortedBag<Card>> options,ChooseCardsHandler chooseCardHandler) {
        if (!Platform.isFxApplicationThread()) 
            throw new AssertionError("This method need to be run on the JAVAFX Application Thread");
    	this.createCardsGUI(FXCollections.observableArrayList(options), chooseCardHandler, StringsFr.CHOOSE_CARDS,1);
    }
    
    /**
     * Fills the property containing a DrawCardHandler
     * @param drawCardHandler (DrawCardHandler) -the handler that communicates the kind of action (draw card) the player wants to perform and the chosen index to choose from to the game thread
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        if (!Platform.isFxApplicationThread()) 
            throw new AssertionError("This method need to be run on the JAVAFX Application Thread");
    	this.gestionnaireCartes.set(slot -> {
    	    drawCardHandler.onDrawCard(slot);
    		resetProperties();	
    	});

    }
    
    /**
     * Reset all the handlers properties
     */
    private void resetProperties() {
    	gestionnaireCartes.set(null);
    	gestionnaireRoutes.set(null);
    	gestionnaireTickets.set(null);
    }
    
    /**
     * Convert the SortedBag to String
     * @author Hamza REMMAL (310917)
     * @author Mehdi ZIAZI (311475)
     */
    private final class CardBagStringConverter extends StringConverter<SortedBag<Card>>{

        /** Answers a string that contains a concise, human-readable  description of a SortedBag of cards
         * @param object (SortedBag<Card>) - SortedBag of cards described
         * @return (String) - description returned
         */
		@Override
		public String toString(SortedBag<Card> object) {
			var builder = new StringBuilder();
			for (var card : object.toSet()) {
			    builder.append(object.countOf(card)).append(" ")
				.append(Info.cardName(card,object.countOf(card)))
				.append(" ");
			   	}
			return builder.toString();
		}

		/**
		 * Always throws the exception.
		 * @throws UnsupportedOperationException
		 * @param string (String) -
		 * @return (SortedBag<Card>) -
		 */
		@Override
		public SortedBag<Card> fromString(String string) {
			throw new UnsupportedOperationException();
		}
    }
   
}
