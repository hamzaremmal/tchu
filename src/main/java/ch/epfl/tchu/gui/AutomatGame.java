package ch.epfl.tchu.gui;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;

/**
 * ???
 */
public class AutomatGame {

    /**
     * ???
     * @param player - ???
     * @param args - ???
     */
    public static void startAutoParty(Player player,String ...args) {       
        var name = Constants.DEFAULT_PLAYER_1_NAME;
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> playersName = new EnumMap<>(PlayerId.class);
        if(args.length == 1 && !args[0].isBlank())
            name = args[0];        
        var graphicalPlayer = new GraphicalPlayerAdapter(null);
        playersName.put(PlayerId.PLAYER_1, name);
        playersName.put(PlayerId.PLAYER_2, player.toString());
        players.put(PlayerId.PLAYER_1, graphicalPlayer);
        players.put(PlayerId.PLAYER_2, player);
        new Thread(() -> Game.play(players, playersName, SortedBag.of(ChMap.tickets()), new Random())).start();
    }
}
