package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Automate;
import ch.epfl.tchu.game.PlayerAutomatique;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * This Class is the Entry Point of our Game
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
public class Launcher extends  Application{
    
    /**
     * Main method.
     * @param args (String []) - argument.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start the JavaFX Application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);
        LauncherViewCreator.createLauncher(primaryStage, new PlayerAutomatique(), new Automate());
    }

}
