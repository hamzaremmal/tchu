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
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The Main class for the Server
 * @author Hamza REMMAL (310917)
 * @author Mehid ZIAZI (311475)
 */
public class ServerMain extends Application{
    
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Launch the JavaFX Application
     */
    @Override
    public void start(Stage v03) {
        var param = this.getParameters().getRaw();
        var namePlayer_1 = Constants.DEFAULT_PLAYER_1_NAME;
        var namePlayer_2 = Constants.DEFAULT_PLAYER_2_NAME;
        
        if(param.size() == 2) {
            namePlayer_1 = param.get(0);
            namePlayer_2 = param.get(1);
        }
        
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> playersName = new EnumMap<>(PlayerId.class);
        playersName.put(PlayerId.PLAYER_1, namePlayer_1);
        playersName.put(PlayerId.PLAYER_2, namePlayer_2);
        
        try(var server = new ServerSocket(Constants.SERIAL_PORT)){
            var socket = server.accept();
            var proxyPlayer = new RemotePlayerProxy(socket);
            var graphicalPlayer = new GraphicalPlayerAdapter();
            players.put(PlayerId.PLAYER_1, graphicalPlayer);
            players.put(PlayerId.PLAYER_2, proxyPlayer);
            new Thread(() -> Game.play(players, playersName, SortedBag.of(ChMap.tickets()), new Random())).start();
        } catch (IOException e) {}
    }
    
}
