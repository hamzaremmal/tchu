package ch.epfl.tchu.gui;

import java.util.Map;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Class to use to create the Information Node.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
abstract class InfoViewCreator {
    
    /**
     * Creates the Information Node.
     * @param id (PlayerId) - The playerId of the Player.
     * @param nameMap (Map<PlayerId,String>) - The map with the Player's names.
     * @param gs (ObservableGameState) - The ObservableGameState to use.
     * @param gameInfo (ObservableList<Text>) - The information to print on the screen.
     * @return (Node) - The Information Node created.
     */
    public static final Node createInfoView(PlayerId id, Map<PlayerId,String> nameMap, ObservableGameState gs, ObservableList<Text> gameInfo) {
        var root = new VBox();
        var playerStats = new VBox();
        playerStats.setId("player-stats");
        root.getStylesheets().addAll("info.css", "colors.css");
        for(PlayerId playerId : PlayerId.ALL) {
            var stat = new TextFlow();
            stat.getStyleClass().add(playerId.name());
            var circle = new Circle();
            circle.getStyleClass().add("filled");
            circle.setRadius(Constants.INFO_CIRCLE_RADIUS);
            var text = new Text();
            text.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS,
                    nameMap.get(playerId),
                    gs.ticketsCount(playerId),
                    gs.cardsCount(playerId),
                    gs.carsCount(playerId),
                    gs.claimPoints(playerId)));
            stat.getChildren().addAll(circle, text);
            playerStats.getChildren().add(stat);
        }
        root.getChildren().addAll(playerStats, new Separator(Orientation.HORIZONTAL));
        var messages = new TextFlow();
        messages.setId("game-info");
        Bindings.bindContent(messages.getChildren(), gameInfo);
        root.getChildren().add(messages);
        return root;
    }

}
