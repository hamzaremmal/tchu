package ch.epfl.tchu.game;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Interface to represent the Bots.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
public interface Bot extends Player{

    /**
     * ???
     */
    Random random = new Random();

    /**
     * ???
     */
    static void waitLikeAHuman() {
        int i = random.nextInt(1500);
        do {
            i = random.nextInt(1500);
        } while(750 > i);
        try {
            TimeUnit.MILLISECONDS.sleep(i);
        } catch (InterruptedException e) {}
    }
    
}
