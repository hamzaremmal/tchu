package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.SortedBag;



public class Automate implements Bot {
  
    private PublicGameState gameState;
    private PlayerState ownState;
    private Route routeTryingToClaim;
    private SortedBag<Ticket> givenTickets;
    private SortedBag<Card> initialClaimCards;
    private final Map<Card,Integer> myHand= new EnumMap<>(Card.class);
    
    

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {}
    
    	
    

    @Override
    public void receiveInfo(String info) {}
      
        
    @Override
    public String toString() {
        return "BOT : MEHDI";
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        this.ownState=ownState;
        this.gameState=newState;
        
      
        
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        this.givenTickets=tickets;
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
       SortedBag<Ticket> chosenTickets= bestTickets(givenTickets, Constants.IN_GAME_TICKETS_COUNT);
     
       return chosenTickets;
    
    }

    @Override
    public TurnKind nextTurn() {
        Bot.waitLikeAHuman();
    	updateMyHand();
       this.completeTickets();
       
        if(routeTryingToClaim==null) {
          return TurnKind.DRAW_CARDS;
        }
        initialClaimCards=this.hand();
        return TurnKind.CLAIM_ROUTE;
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        Bot.waitLikeAHuman();
        return bestTickets(options,1);
    }

    @Override
    public int drawSlot() {
        Bot.waitLikeAHuman();
    	int max =0;
    	Card wanted= null;
    	
    	for (Map.Entry<Card, Integer> element : myHand.entrySet()) {
    		if(element.getValue()>max) {
    			max =element.getValue();
    			wanted = element.getKey();
    		}
		}
    	
    	return gameState.cardState().faceUpCards().indexOf(wanted);
        
    }

    @Override
    public Route claimedRoute() {
        Bot.waitLikeAHuman();
        return this.routeTryingToClaim;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
      return this.initialClaimCards;
       
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        Bot.waitLikeAHuman();
    	for (SortedBag<Card> cards : options) {
			if(!cards.contains(Card.LOCOMOTIVE)) return cards;
		}
          
        
        return options.get(new Random().nextInt(options.size()));
    }
    private void focusOnRoutes() {
        List<Integer> points= new ArrayList<>();
        List<Route> routesNeutres= new ArrayList<>(ChMap.routes());
        HashMap<Integer,Route> map = new HashMap<>();
       routesNeutres.removeAll(gameState.claimedRoutes());
       routesNeutres.forEach(r->{
           if(this.ownState.canClaimRoute(r)) map.put(r.claimPoints(), r);
           points.add(r.claimPoints());
       });
       
   if(!map.isEmpty()) {
       Collections.sort(points,(e1,e2)-> e2.compareTo(e1));
       routeTryingToClaim=   map.get(points.get(0));
   }
       
   else routeTryingToClaim= null;
    }
   private SortedBag<Card> hand(){
       if(routeTryingToClaim== null) return null;
    	   for (SortedBag<Card> cards : ownState.possibleClaimCards(routeTryingToClaim)) {
			if(!cards.contains(Card.LOCOMOTIVE)) return cards;
		}
           return this.ownState.possibleClaimCards(routeTryingToClaim)
                   .get(new Random().nextInt( this.ownState.possibleClaimCards(routeTryingToClaim).size()));
       }
     
   
   private void longestRoute() {
       if(this.ownState.routes().isEmpty())  this.focusOnRoutes();
       HashMap<Integer,Route> map = new HashMap<>();
       List<Route> routesClaimable = new ArrayList<>();
       List<Route> routesNeutres= new ArrayList<>(ChMap.routes());
       List<Integer> points= new ArrayList<>();
      routesNeutres.removeAll(gameState.claimedRoutes());
      routesNeutres.forEach(r->{
          if(this.ownState.canClaimRoute(r)) routesClaimable.add(r);
      });
     routesNeutres.clear();
      routesClaimable.forEach(r->{
          ownState.routes().forEach(r1->{
              if(r1.station1().equals(r.station2())||r1.station2().equals(r.station1())) {
                  points.add(r.claimPoints());
                  map.put(r.claimPoints(), r);
              }
          });
          
      });
      
      if(map.isEmpty())  this.focusOnRoutes();
      else {
      Collections.sort(points,(e1,e2)-> e2.compareTo(e1));
      routeTryingToClaim= map.get(points.get(0));}
      
   }
   
   private static SortedBag<Ticket> bestTickets(SortedBag<Ticket> tk, int nombre) {
	   
  	   Map<Ticket, Integer> map = new HashMap<>();
  	 
  	  
  	   for (Ticket ticket : tk) {
  		
  		map.put(ticket, ticket.points((s1,s2)-> true));
  	}
  	   
  	
  	  List<Map.Entry<Ticket, Integer>> list = new LinkedList<>(map.entrySet());
  	  
  	  Collections.sort( list,(e1,e2)->{
  	  		  return e2.getValue().compareTo(e1.getValue());
  	  });
  	  List<Ticket> choice = new LinkedList<>();
  	if(nombre > list.size()) nombre = list.size();
  	for (Map.Entry<Ticket, Integer> entry : list.subList(0, nombre)) {
 		choice.add(entry.getKey());
 		
 	}
  	
  	
     return SortedBag.of(choice);
     }
    private void completeTickets() {
    	
    	
    	
    	List<Ticket> mostValuable= ownState.tickets().toList();
    	
    	Collections.sort( mostValuable,(t1,t2)->{
    		return  t2.points((s1,s2)-> true)-t1.points((s1,s2)-> true);
    	});
    	
    	for (Ticket ticket : mostValuable) {
    	List<String> goal = computeTicketGoal(ticket);
    	List<Route> available = new LinkedList<>(ChMap.routes());
    	available.removeAll(gameState.claimedRoutes());
    	 for (Route route : available) {
    		 for (String str : goal) {
				if(route.station1().name().equals(str)&&route.station2().name().equals(str)) {
					if(!gameState.claimedRoutes().contains(route)) {
						if(ownState.canClaimRoute(route)) routeTryingToClaim= route;
						return;
					
				}
			}
			
		}
    	
    }
    	}	
    	this.longestRoute();
    }
    private void updateMyHand() {
    	for(Card card : ownState.cards().toSet()) {
    		myHand.put(card, ownState.cards().countOf(card));
    	}
    }
    private List<String> computeTicketGoal(Ticket tk){
    	String[] tab = tk.text().replace("\s","").split("\\-|\\(|\\)|\\,|\\{|\\}");
        List<String> finalList= new ArrayList<>();
       List<String> secondList =new ArrayList<>();
      
        List<String> firstList= Arrays.asList(tab);
       for (String string : firstList) {
  		if(string.equals("")) continue;
  		secondList.add(string);
  		
       }
       secondList.forEach(e->{
      	 try {
      		 Integer.parseInt(e);
      		 
      	 }
      	 catch(Exception ex) {
    		 switch (e) {
    		
    		 case "LaChaux":{
    			 finalList.add("La Chaux-de-Fonds");
    			 break;
    		 }
    		 case "de":{
    			 break;
    		 }
    		 case "Fonds":{
    			 break;
    		 }
    		 default: finalList.add(e);
    		 				break;
    		 }

      	 		}
    
       	});
       
       return finalList ;
    }
}
   