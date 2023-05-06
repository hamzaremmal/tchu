package ch.epfl.tchu.gui;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.Call;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The Main class for the Server
 * @author Hamza REMMAL (310917)
 * @author Mehid ZIAZI (311475)
 */
public class Server {

    /** ??? */
    private static final Alert alert = createWaitingFrame();
    /** ??? */
    private static String namePlayer_1;
    /** ??? */
    private static String namePlayer_2;

    /**
     * Launch the JavaFX Application
     * @param primaryStage - ???
     * @param args - ???
     */
    public static void start(Stage primaryStage,String ... args) {
        namePlayer_1 = Constants.DEFAULT_PLAYER_1_NAME;
        namePlayer_2 = Constants.DEFAULT_PLAYER_2_NAME;
        
        if(args.length == 1 && !args[0].isBlank())
            namePlayer_1 = args[0];
        
        
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> playersName = new EnumMap<>(PlayerId.class);
        new Thread(() -> {
        try(var server = new ServerSocket(Constants.SERIAL_PORT)){
           Platform.runLater(() -> alert.show() ) ;
            var communication = server.accept();
            var proxy = server.accept();
            Platform.runLater(() -> alert.hide());
            var proxyPlayer = new RemotePlayerProxy(proxy);
            var call = new Call(communication);
            var graphicalPlayer = new GraphicalPlayerAdapter(call);
            var name = proxyPlayer.getName();
            if(!name.isBlank()) namePlayer_2 = name.trim();
            playersName.put(PlayerId.PLAYER_1, namePlayer_1);
            playersName.put(PlayerId.PLAYER_2, namePlayer_2);
            players.put(PlayerId.PLAYER_1, graphicalPlayer);
            players.put(PlayerId.PLAYER_2, proxyPlayer);
            new Thread(() -> Game.play(players, playersName, SortedBag.of(ChMap.tickets()), new Random())).start();
            call.startCall();
            } catch (IOException e) {}
            }).start();
    }
    
    /**
     * Creates the frame when waiting for the other player.
     * @return (Alert) -The waitingto connect frame.
     */
    private static Alert createWaitingFrame() {
        var alert = new Alert(AlertType.INFORMATION, "WAITING FOR THE OTHER PLAYER",ButtonType.CANCEL);
        alert.initStyle(StageStyle.UNDECORATED);
        var progressIndicator = new ProgressIndicator(-1.0);
        alert.setGraphic(progressIndicator);
        alert.setHeaderText(null);
        return alert;
    }
    
}
