package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import ch.epfl.tchu.SortedBag;

/**
 * ???
 */
public final class PlayerAutomatique implements Bot{
    
    private PublicGameState gameState;
    private PlayerState playerState;
    private SortedBag<Ticket> initialTicketsDistribution ;
    private Route routeTryingToClaim;

    /**
     * ???
     */
    public PlayerAutomatique() {}

    /**
     * 
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {}

    /**
     * ------------------------------------ DONE -------------------------------------
     */
    @Override
    public void receiveInfo(String info) {}

    /**
     * ------------------------------------- DONE --------------------------------------
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        this.gameState = newState;
        this.playerState = ownState;
    }

    /**
     * ---------------------------------------- DONE --------------------------------------
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        initialTicketsDistribution = SortedBag.of(tickets);  
    }

    /**
     * ------------------------------------------ DONE ---------------------------------------
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        var initialTicketsChoosen = SortedBag.<Ticket>of();
        int k = 0;
        do {
             k = new Random().nextInt(5);
        }while(k < 3);
        
        for(int i = 0 ;i < k; i++) {
           initialTicketsChoosen = initialTicketsChoosen.union(SortedBag.of(initialTicketsDistribution.toList().get(i)));
        }
        return initialTicketsChoosen;
    }

    /**
     * ------------------------------------ DONE -------------------------------------------
     */
    @Override
    public TurnKind nextTurn() {
        Bot.waitLikeAHuman();
        List<Route> claimableRoute = new ArrayList<>();
        ChMap.routes().forEach(route -> {
            if(!gameState.claimedRoutes().contains(route) && playerState.canClaimRoute(route)) 
                claimableRoute.add(route);
        });
        if (playerState.cardCount() == 0 && gameState.canDrawCards())         return TurnKind.DRAW_CARDS;
        else if(!claimableRoute.isEmpty() && new Random().nextInt(2) == 1)    return TurnKind.CLAIM_ROUTE;
        else if(gameState.canDrawTickets() && new Random().nextInt(75) == 1 ) return TurnKind.DRAW_TICKETS;
        else                                                                  return TurnKind.DRAW_CARDS;
    }

    /**
     * ---------------------------------- DONE ------------------------------------
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        Bot.waitLikeAHuman();
        List<Ticket> list = options.toList();
        return SortedBag.of(list.subList(0, 3));
    }

    /**
     * ---------------------------- DONE ---------------------------------------
     */
    @Override
    public int drawSlot() {
        Bot.waitLikeAHuman();
        return new Random().nextInt(Constants.FACE_UP_CARD_SLOTS.size());
    }

    /**
     * --------------------------------- DONE -----------------------------------
     */
    @Override
    public Route claimedRoute() {
        Bot.waitLikeAHuman();
        List<Route> unclaimedRoutes = new ArrayList<>(ChMap.routes());
        unclaimedRoutes.removeAll(gameState.claimedRoutes());
        List<Route> canClaimRoute = new ArrayList<>();
        unclaimedRoutes.forEach(route -> {
            if(playerState.canClaimRoute(route)) 
                canClaimRoute.add(route);
        });
        List<Route> linkedWithClaimedRoutes = computeClaimabeRoutesWithLink(canClaimRoute);
        List<Route> linkedWithTickets = computeClaimabeRoutesWithLinkTickets(canClaimRoute);
        List<Route> linkedWithTicketsAndRoutes = computeLinkedWithBoth(linkedWithClaimedRoutes,linkedWithTickets) ;
       if(!linkedWithTicketsAndRoutes.isEmpty() && new Random().nextInt(4) == 1 ) 
           routeTryingToClaim  = linkedWithTicketsAndRoutes.get(new Random().nextInt(linkedWithTicketsAndRoutes.size()));
       else if(!linkedWithTickets.isEmpty()&& new Random().nextInt(4) == 1 )
           routeTryingToClaim  = linkedWithTickets.get(new Random().nextInt(linkedWithTickets.size()));
       else if (!linkedWithClaimedRoutes.isEmpty()&& new Random().nextInt(4) == 1 ) 
           routeTryingToClaim  = linkedWithClaimedRoutes.get(new Random().nextInt(linkedWithClaimedRoutes.size()));
       else routeTryingToClaim  = canClaimRoute.get(new Random().nextInt(canClaimRoute.size())) ;
        return routeTryingToClaim;
    }

    /**
     * --------------------------------- DONE ---------------------------------
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        List<SortedBag<Card>> list = playerState.possibleClaimCards(routeTryingToClaim);
        return list.get(new Random().nextInt(list.size()));
    }

    /**
     * ----------------------------------- DONE -------------------------------------
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        Bot.waitLikeAHuman();
        SortedBag<Card> choosenCards = SortedBag.of();
        int i = new Random().nextInt(options.size()) ;
        try {
            choosenCards = options.get(i);
        }catch(IndexOutOfBoundsException e) {
            choosenCards = SortedBag.<Card>of();
        }
        return choosenCards;
    }
    
    public String toString() {
        return "BOT : HAMZA";
    }
    
    private List<Route> computeClaimabeRoutesWithLink(List<Route> canClaimRoute) {
        Map<Route,Integer> linkedWithClaimedRoutes = new HashMap<>();
        List<Route> list = new ArrayList<>();
        playerState.routes().forEach(route -> {
            canClaimRoute.forEach(tryToClaimRoute -> {
                if(route.station1().equals(tryToClaimRoute.station1()) || 
                   route.station1().equals(tryToClaimRoute.station2()) ||
                   route.station2().equals(tryToClaimRoute.station1()) ||
                   route.station2().equals(tryToClaimRoute.station2()))
                    linkedWithClaimedRoutes.put(tryToClaimRoute, linkedWithClaimedRoutes.getOrDefault(tryToClaimRoute, 0) +1);
                });
        });
        int valueMax = 0;
        for(Route route : linkedWithClaimedRoutes.keySet()) {
            int value = linkedWithClaimedRoutes.get(route);
            if(value > valueMax) {
                list.clear();
                list.add(route);
                valueMax = value;
            }else if(value == valueMax) {
                list.add(route);
            }
        }
        return list;
    }
    
    private List<Route> computeClaimabeRoutesWithLinkTickets(List<Route> canClaimRoute) {
        Map<Route,Integer> mapLinked = new HashMap<>();
        List<Route> list = new ArrayList<>();
        playerState.tickets().forEach(ticket -> {
            canClaimRoute.forEach(tryToClaimRoute -> {
                if(ticket.text().indexOf(tryToClaimRoute.station1().name()) != -1 || 
                   ticket.text().indexOf(tryToClaimRoute.station2().name()) != -1) 
                    mapLinked.put(tryToClaimRoute, mapLinked.getOrDefault(tryToClaimRoute, 0)+1);
                });
            });
        int valueMax = 0;
        for(Route route : mapLinked.keySet()) {
            int value = mapLinked.get(route);
            if(value > valueMax) {
                list.clear();
                list.add(route);
                valueMax = value;
            }else if(value == valueMax) {
                list.add(route);
            }
        }
        return list;
        }
    
    private List<Route> computeLinkedWithBoth(List<Route> linkedWithRoutes ,List<Route> linkedWithTickets){
        Map<Route,Integer> mapLinked = new HashMap<>();
        List<Route> list = new ArrayList<>();
        linkedWithRoutes.forEach(route ->{
            linkedWithTickets.forEach(routeTicket ->{
                if(route == routeTicket) mapLinked.put(routeTicket, mapLinked.getOrDefault(route, 0)+1);
            });
        });
        int valueMax = 0;
        for(Route route : mapLinked.keySet()) {
            int value = mapLinked.get(route);
            if(value > valueMax) {
                list.clear();
                list.add(route);
                valueMax = value;
            }else if(value == valueMax) {
                list.add(route);
            }
        }
        return list;
    }
    

}
