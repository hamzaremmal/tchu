package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.Call;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Class to use to create the Menu Node.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
public abstract class MenuViewCreator {
    
    /**
     *  Creates the menu bar
     * @param call ???
     * @param opponentName ???
     * @return ???
     */
    public static Node createMenuView(Call call, String opponentName) {
        var menuBar = new ToolBar();
        var mic = setGraphicNode(call.mute(),"micOn.png","micOff.png");
        mic.setOnMouseClicked(e -> call.switchMute());
        mic.setCursor(Cursor.HAND);
        var sound = setGraphicNode(call.sourdine(), "volOn.png", "volOff.png");
        sound.setCursor(Cursor.HAND);
        sound.setOnMouseClicked(e -> call.switchSourdine());
        menuBar.getItems().addAll(mic,new Separator(Orientation.VERTICAL),sound, new Separator(Orientation.VERTICAL));
        return menuBar;
    }

    /**
     * Creates the Graphic of the boutons.
     * @param property (ReadOnlyBooleanProperty) - The property to bind.
     * @param on (String) - The Path to the on image .
     * @param off (String) - The Path to the off image .
     * @return (Node) -The Button to use.
     */
    private static Node setGraphicNode(ReadOnlyBooleanProperty property, String on, String off) {
        var button = new Button();
        var stack = new StackPane();
        var micOn = new Rectangle(25, 25);
        micOn.visibleProperty().bind(property);
        micOn.setStyle("-fx-fill: url(\"" + on + "\")");
        var micOff = new Rectangle(25, 25);
        micOff.setStyle("-fx-fill: url(\"" + off + "\")");
        micOff.visibleProperty().bind(property.not());
        stack.getChildren().addAll(micOn,micOff);
        button.setGraphic(stack);
        return button;
    }
    

}
