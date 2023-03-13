package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Bot;
import ch.epfl.tchu.game.Player;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Creates the Launcher Frame.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
public abstract class LauncherViewCreator {

    /**
     * Creates and view the launcher frame.
     * @param primaryStage (Stage) - the primary stage.
     * @param automats (PlayerBot) - The bots to play with
     */
    public static void createLauncher(Stage primaryStage, Bot ... automats) {
        var vbox = new VBox();
        var anchorPane = new AnchorPane();
        vbox.getChildren().add(anchorPane);
        anchorPane.setPrefSize(640, 367);
        //
        var welcome = new Text("Welcome to tChu Game !");
        welcome.relocate(154,103);
        AnchorPane.setTopAnchor(welcome, 70d);
        welcome.setFont(Font.font(30));
        //
        var name = new Text("Your name");
        name.setTextOrigin(VPos.BASELINE);
        AnchorPane.setLeftAnchor(name, 155d);
        name.relocate(155,178);
        name.setFont(Font.font(15));
        //
        var fieldName = new TextField();
        AnchorPane.setRightAnchor(fieldName, 200d);
        fieldName.setPromptText("Player's name");
        fieldName.setPrefSize(149, 25);
        fieldName.relocate(293, 174);
        //
        var startOnline= new Button("Start an Online Game");
        AnchorPane.setLeftAnchor(startOnline, 123d);
        startOnline.setPrefSize(130, 25);
        startOnline.relocate(123, 225);
        //
        var joinOnline= new Button("Join an Online Game");
        AnchorPane.setLeftAnchor(joinOnline, 123d);
        joinOnline.setPrefSize(130, 25);
        joinOnline.relocate(123, 263);
        //
        var fieldHost = new TextField();
        fieldHost.setPromptText("host address");
        fieldHost.setPrefSize(150, 25);
        fieldHost.relocate(293, 263);
        //
        var fieldPort = new TextField();
        fieldPort.setPromptText("port");
        fieldPort.setPrefSize(60, 25);
        fieldPort.relocate(458, 263);
        //
        var automatGame = new Button("Play with Bots");
        AnchorPane.setLeftAnchor(automatGame, 123d);
        automatGame.setPrefSize(130, 25);
        automatGame.relocate(123, 299);
        //
        var choice = new ChoiceBox<Player>();
        choice.setPrefSize(150,25);
        choice.relocate(292, 299);
        choice.getItems().addAll(automats);
        automatGame.disableProperty().bind(choice.getSelectionModel().selectedItemProperty().isNull());
        //
       anchorPane.getChildren().addAll(welcome,name,fieldName,startOnline,joinOnline,fieldHost,fieldPort,automatGame,choice);
       
       startOnline.setOnAction(e -> {
           String[] args = {fieldName.getText()};
           primaryStage.hide();
           Server.start(primaryStage,args);
           
       });
       
       joinOnline.setOnAction(e -> {
           String[] args = {fieldName.getText(),fieldPort.getText(),fieldHost.getText()};
           primaryStage.hide();
           Client.start(primaryStage, args);
       });
       
       automatGame.setOnAction(e -> {
           String[] args = {fieldName.getText()};
           primaryStage.hide();
           AutomatGame.startAutoParty(choice.getSelectionModel().getSelectedItem(),args);
       });
       var scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        
    }
   
}
