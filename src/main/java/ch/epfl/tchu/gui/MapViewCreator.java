package ch.epfl.tchu.gui;

import java.util.List;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Class to use to create the Map Node.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
abstract class MapViewCreator {
    
    /**
     * Handler to use to choose cards
     * @author Hamza REMMAL (310917)
     * @author Mehdi ZIAZI (311475)
     *
     */
    @FunctionalInterface
    interface CardChooser {
        /**
         * CardChooser Handler
         * @param options (List<SortedBag<Card>>) - options to choose from
         * @param handler (ChooseCardsHandler) - The handler to use.
         */
        void chooseCards (List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }
    
    /**
     * Creates The map and returns the panel for use
     * @param gs (ObservableGameState) - The ObservableGameState to use
     * @param handler (ObjectProperty<ClaimRouteHandler>) - The handler
     * @param chooser (CardChooser) - The chooser
     * @return (Node) - The Node created.
     */
    public static Node createMapView(ObservableGameState gs, ObjectProperty<ClaimRouteHandler> handler, CardChooser chooser) {
        var carte = new Pane();
        carte.getStylesheets().addAll("map.css","colors.css");
        var fond = new ImageView();
        carte.getChildren().add(fond);
        for(Route routeMap : ChMap.routes()) {
        var route = new Group();
        route.disableProperty().bind(handler.isNull().or(gs.canClaimRoute(routeMap).not()));
        route.setOnMouseClicked(e -> {
            var possibleClaimCards = gs.possibleClaimCards(routeMap);
            var claimRouteH = handler.get();
            ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteH.onClaimRoute(routeMap, chosenCards);
            chooser.chooseCards(possibleClaimCards, chooseCardsH);
        });
        route.setId(routeMap.id());
        route.getStyleClass().addAll("route", routeMap.level().name(), routeMap.color() == null ? Color.NEUTRAL.name() : routeMap.color().name());
        gs.routeId(routeMap).addListener((o,ov,nv) -> route.getStyleClass().add(nv.name()));
        for(int i = 1 ; i <= routeMap.length() ; i++) {
            var caSe  = new Group();
            caSe.setId(routeMap.id() + "_" + i);
            var voie = new Rectangle();
            voie.setHeight(Constants.ROUTE_HEIGHT);
            voie.setWidth(Constants.ROUTE_WIDTH);
            voie.getStyleClass().addAll("track" ,"filled");
            var wagon = new Group(); 
            wagon.getStyleClass().add("car");
            var rec = new Rectangle();
            rec.setHeight(Constants.ROUTE_HEIGHT);
            rec.setWidth(Constants.ROUTE_WIDTH);
            rec.getStyleClass().add("filled");
            var cer1 = new Circle();
            var cer2 = new Circle();
            cer1.relocate(Constants.ROUTE_CIRCLE_1_X, Constants.ROUTE_CIRCLE_Y);
            cer2.relocate(Constants.ROUTE_CIRCLE_2_X, Constants.ROUTE_CIRCLE_Y);
            cer1.setRadius(Constants.ROUTE_CIRCLE_RADIUS);
            cer2.setRadius(Constants.ROUTE_CIRCLE_RADIUS);
            wagon.getChildren().addAll(rec,cer1,cer2); 
            caSe.getChildren().addAll(voie,wagon);
            route.getChildren().add(caSe);
        }
        carte.getChildren().add(route);
        }
        return carte;
    }
    
}
