package ch.epfl.tchu.gui;

import java.io.IOException;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The Client Main Class
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
public class ClientMain extends Application{

    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Launch The JavaFX Application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            var param = this.getParameters().getRaw();
            var host = Constants.DEFAULT_HOST;
            var port = Constants.SERIAL_PORT;
            if(param.size() == 2) {
                host = param.get(0);
                port = Integer.valueOf(param.get(1));
            }
            var client = new RemotePlayerClient(new GraphicalPlayerAdapter() , host, port);
            new Thread(() -> client.run() ).start();
        } catch (IOException e) {}
    }
   
}
