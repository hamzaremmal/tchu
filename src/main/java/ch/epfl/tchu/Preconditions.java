package ch.epfl.tchu;

/**
 * Class to use to verify the Preconditions.
 * @author Hamza REMMAL (310917) -
 * @author Mehdi ZIAZI (311475)
 */
public final class Preconditions {
    private Preconditions() {}
    
    /**
     * To verify the argument.
     * @param shouldBeTrue (boolean) - The condition.
     * @throws (IllegalArgumentException) if the condition is false.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if(!shouldBeTrue) throw new IllegalArgumentException();
    }

}