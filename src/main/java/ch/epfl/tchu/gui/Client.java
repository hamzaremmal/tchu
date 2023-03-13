package ch.epfl.tchu.gui;

import java.io.IOException;
import java.net.Socket;

import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.net.Call;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.stage.Stage;

/**
 * The Client Main Class
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
public class Client{

    
    /**
     * Launch The JavaFX Application.
     */
    public static void start(Stage primaryStage, String ... args) {
        try {
            var host = Constants.DEFAULT_HOST;
            var port = Constants.SERIAL_PORT;
            String name = Constants.DEFAULT_PLAYER_2_NAME;
            if(args.length != 0 && !args[0].isBlank()) {
                name = args[0];
            }
            else if(args.length == 3 && !args[1].isBlank() && !args[2].isBlank()) {
                port = Integer.valueOf(args[1]);
                host = args[2];
            }
            var comm = new Socket(host,port);
            while(!comm.isConnected()) {}
            var call = new Call(comm);
            var client = new RemotePlayerClient(new GraphicalPlayerAdapter(call) , host, port, name);
            new Thread(() -> client.run() ).start();
            call.startCall();
        } catch (IOException e) {}
    }
   
}
