package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Represents of a StationPartition
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public final class StationPartition implements StationConnectivity {

    private final int[] liens;

    private StationPartition(int[] liens) { this.liens = liens.clone();}
    
    /**
     * Check if the stations are connected
     * @param station1 (Station) -The first Station.
     * @param station2 (Station) -The second Station.
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        try {
            return liens[s1.id()] == liens[s2.id()] ? true : false;
        } catch (IndexOutOfBoundsException ex) {
            return s1.id() == s2.id() ? true : false;
        }
    }

    
    /**
     * 
     * @author Hamza REMMAL (310917)
     * @author Mehdi ZIAZI ()
     */
    public static final class Builder {
        private int[] partitions;

        /**
         * Creates a stationPartition Builder
         * @param length (int) - number of stations
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            this.partitions = new int[stationCount];
            for (var i = 0; i < partitions.length; i++) 
                partitions[i] = i;
        }
        
        /**
         * Connects the two stations
         * @param station1 (Station) -The first Station.
         * @param station2 (Station) -The second Station.
         */
        public Builder connect(Station s1, Station s2) {
            partitions[this.representative(s2.id())] = this.representative(s1.id());
            return this;
        }
           
        /**
         * Creates a new builder 
         * @param id (String) - id of the route
         * @param station1 (Station) -The first Station.
         * @param station2 (Station) -The second Station.
         * @param length (int) - length of the route
         * @param level (Level) -level of the route
         * @param color (Color) - color of the route
         */
        public StationPartition build() {
            var applatie = new int[partitions.length];
            for (var i = 0; i < partitions.length; i++) 
                applatie[i] = this.representative(i);
            return new StationPartition(applatie);
        }

        /**
         * Returns the id of the representative of the station
         * @param id (int) -id of the station
         * @return (int) -the id of the representative of the station
         */
        private int representative(int id) {
            var condition = true;
            var representative = partitions[id];
            while (condition) {
                if (partitions[representative] == representative)
                    condition = false;
                else
                    representative = partitions[representative];
            }
            return representative;
        }


    }

}