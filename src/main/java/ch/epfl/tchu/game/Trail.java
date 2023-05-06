package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * This class gives an abstract representation of a Trail and compute the longest one with a given list of Routes.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 *
 */
public final class Trail {
    
    private final List<Station> listStations;
    private final List<Route> listRoutes;
    private final int length;
    
    
    private Trail(List<Station> listStations, List<Route> listRoutes){
        this.listStations = listStations;
        this.listRoutes = listRoutes;
        length = computelength();
    }

    /**
     * Build the longest Trail with a given list of Routes
     * @param routes (List) - The routes we will build the Trail with
     * @return (Trail) - One of the longest Trail composed by the routes
     */
    public static Trail longest(List<Route> routes) {
        if(routes.size() == 0) {
            return new Trail(new ArrayList<Station>(2),null); // null a voir quoi faire dedans
        } else {
            int longueurMax = 0;
            Trail cheminMax = null;
            List<Trail> cs = new ArrayList<>();
            for(Route road : routes) {
                cs.add(new Trail(List.of(road.stations().get(0),road.stations().get(1)), List.of(road)));
                cs.add(new Trail(List.of(road.stations().get(1),road.stations().get(0)), List.of(road)));
            }
            while(!cs.isEmpty()) {
                List<Trail> csp = new ArrayList<>();
                for(Trail chemin : cs) {
                  List<Route>  rs = new ArrayList<Route>();
                      for(Route roadN : routes) {
                          if(!chemin.listRoutes.contains(roadN) && roadN.stations().contains(chemin.listStations.get(chemin.listStations.size()-1))) {
                             rs.add(roadN);
                          } 
                      }
                      for(Route r : rs) {
                          List<Station> nvListStations = new ArrayList<Station>(chemin.listStations);
                          nvListStations.add(r.stationOpposite(chemin.listStations.get(chemin.listStations.size()-1)));
                          List<Route> nvListRoute = new ArrayList<Route>(chemin.listRoutes);
                          nvListRoute.add(r);
                          csp.add(new Trail(nvListStations,nvListRoute));
                      }
                }
                for(Trail c : cs) {
                    if(c.length() >= longueurMax) {
                        cheminMax = c;
                        longueurMax = c.length();
                    }
                } 
                cs = csp;  
            } 
            return cheminMax;
        }    
    }
    
    /**
     * Compute the length of the given Trail
     * @return (int) - The length of the Trail
     */
    public int computelength() {
        int l = 0;
        if(listRoutes == null) return l;
        for(Route r : listRoutes) {
            l += r.length();
       }
        return l;
    }
    
    /**
     * Return the length of the given Trail
     * @return (int) - The length of the Trail
     */
    public int length() {
        return length;
    }
    
    /**
     * Return the first station of the Trail ot null if the given Trail length is 0.
     * @return (Station) - The first station of the Trail ot null if the given Trail length is 0.
     */
    public Station station1() {
        if(this.length() == 0) {
            return null;
        } else {
            return listStations.get(0);
        }  
    }
    
    /**
     * Return the second station of the Trail ot null if the given Trail length is 0.
     * @return (Station) - The second station of the Trail ot null if the given Trail length is 0.
     */
    public Station station2() {
        if(this.length() == 0) {
            return null;
        } else {
            return listStations.get(listStations.size()-1);
        } 
    }
    
    /**
     * Build a String representation of the Trail composed by all the stations names seprarated by a " - " and the length at the end with parenthesis.
     * @return (String) - The String representation of a Trail.
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();
        for(Station s : listStations) {
            builder.append(s.name());
            if(listStations.indexOf(s) != listStations.size()-1) {
                builder.append(" -");
            }
            builder.append(" ");
        }
        builder.append("(" + this.length() + ")");
        return builder.toString();
    }
    
}
