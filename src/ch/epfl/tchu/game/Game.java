package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

/**
 * Abstract class that represent the tchu game.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
public final class Game {

    private static final Map<PlayerId, Info> info = new EnumMap<>(PlayerId.class);
    private static  GameState gs;
    
    private Game() {}
    
    /**
     * The method to call to play the game.
     * @param players (Map<PlayerId, Player>) - Mapping between every player and its PlayerId.
     * @param playerNames (Map<PlayerId, String>) - Mapping between every playerId and the player's name.
     * @param tickets (SortedBag<Ticket>) - Tickets to use in the game.
     * @param rng (Random) - Object to shuffle the cards.
     * @throws {@code IllegalArgumentException} if {@code playerNames.size() != 2 } or {@code players.size() != 2 }.
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(playerNames.size() == PlayerId.ALL.size());
        Preconditions.checkArgument(players.size() == PlayerId.ALL.size());
        gs = GameState.initial(tickets, rng);
        players.forEach((playerId, player) -> player.initPlayers(playerId, playerNames));
        updatePlayers(players, gs);
        PlayerId.ALL.forEach(playerId -> info.put(playerId, new Info(playerNames.get(playerId))));
        informePlayers(players, info.get(gs.currentPlayerId()).willPlayFirst());
        updatePlayers(players, gs);
        players.forEach((playerId, player) -> {
            player.setInitialTicketChoice(gs.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gs = gs.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            updatePlayers(players, gs);
        });
        updatePlayers(players, gs);
        players.forEach((playerId, player) -> {
            var ticketsBag = player.chooseInitialTickets();
            gs = gs.withInitiallyChosenTickets(playerId, ticketsBag);
            updatePlayers(players, gs);
            informePlayers(players, info.get(playerId).keptTickets(ticketsBag.size()));
        });

        // -------- BOUCLE DE JEU --------
        while(gs.lastPlayer() == null)
            gs = lap(players, rng);      
        
        //--------------- LAST TURN -----------------
        informePlayers(players, info.get(gs.currentPlayerId().next()).lastTurnBegins(gs.playerState(gs.currentPlayerId().next()).carCount()));
        PlayerId.ALL.forEach(id -> gs = lap(players, rng) );
        updatePlayers(players, gs);
        
        // ----------------------------- POINTS ANALYSIS -----------------
        Map<PlayerId,EndGameState> listEndGame = new EnumMap<>(PlayerId.class);
        players.keySet().forEach(playerId -> {
            var points = gs.playerState(playerId).finalPoints();
            Trail longest = Trail.longest(gs.playerState(playerId).routes());
            listEndGame.put(playerId, new EndGameState(playerId,longest, points));
            });
        var idLongestTrail = EndGameState.computeLongestTrail(listEndGame);
        if(idLongestTrail.size() == 1) {
            informePlayers(players, info.get(idLongestTrail.get(0).playerId).getsLongestTrailBonus(idLongestTrail.get(0).longest));
        }else {
            informePlayers(players, info.get(idLongestTrail.get(0).playerId).getsLongestTrailBonus(idLongestTrail.get(0).longest));    
            informePlayers(players, info.get(idLongestTrail.get(1).playerId).getsLongestTrailBonus(idLongestTrail.get(1).longest));
        }
        var idWinner = EndGameState.computeWinner(listEndGame);
        if(idWinner.size() == 1) {
            var pointsWinner = listEndGame.get(idWinner.get(0).playerId).points;
            var pointsLoser = listEndGame.get(idWinner.get(0).playerId.next()).points;
            informePlayers(players, info.get(idWinner.get(0).playerId).won(pointsWinner,pointsLoser));
        }else {
            var pointsWinner = listEndGame.get(idWinner.get(0).playerId).points;
            List<String> names = new ArrayList<>();
            for(var id : playerNames.keySet())
                names.add(playerNames.get(id));
            informePlayers(players, Info.draw(names,pointsWinner));
            }
        } 
    
    /**
     * Represent one turn in the game.
     * @param players (Map<PlayerId, Player>) - Mapping between every player and its PlayerId.
     * @param rng (Random) - Object to shuffle the cards.
     * @return (GameState) - The GameState for the next turn
     */
    private static GameState lap(Map<PlayerId, Player> players,  Random rng) {
        informePlayers(players, info.get(gs.currentPlayerId()).canPlay());
        updatePlayers(players, gs);
        var tk = players.get(gs.currentPlayerId()).nextTurn();
        switch(tk) {
        case DRAW_TICKETS :
            informePlayers(players, info.get(gs.currentPlayerId()).drewTickets(Constants.IN_GAME_TICKETS_COUNT));
            var drawnTickets = gs.topTickets(Constants.IN_GAME_TICKETS_COUNT);
            var chosenTickets = players.get(gs.currentPlayerId()).chooseTickets(drawnTickets);      
            gs = gs.withChosenAdditionalTickets(drawnTickets, chosenTickets);
            updatePlayers(players, gs);
            informePlayers(players, info.get(gs.currentPlayerId()).keptTickets(chosenTickets.size()));  
            break;
        case DRAW_CARDS :
            for (int i = 0 ; i < 2; i++) {
                gs = gs.withCardsDeckRecreatedIfNeeded(rng);
                updatePlayers(players, gs);
                var slot = players.get(gs.currentPlayerId()).drawSlot();
            if(slot == Constants.DECK_SLOT) {
                gs = gs.withBlindlyDrawnCard();
                updatePlayers(players, gs);
                informePlayers(players, info.get(gs.currentPlayerId()).drewBlindCard());
            }else {
                var card = gs.cardState().faceUpCard(slot);
                gs = gs.withDrawnFaceUpCard(slot);
                updatePlayers(players, gs);
                informePlayers(players, info.get(gs.currentPlayerId()).drewVisibleCard(card)); 
                }
            gs = gs.withCardsDeckRecreatedIfNeeded(rng);
            updatePlayers(players, gs);
            }
            break;
        case CLAIM_ROUTE:
            var route = players.get(gs.currentPlayerId()).claimedRoute();
            var playedCards = players.get(gs.currentPlayerId()).initialClaimCards();
            switch(route.level()) {
            case UNDERGROUND:
                informePlayers(players,info.get(gs.currentPlayerId()).attemptsTunnelClaim(route, playedCards) );
                var drawnCards = SortedBag.<Card>of();
                for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                    gs = gs.withCardsDeckRecreatedIfNeeded(rng);
                    drawnCards = drawnCards.union(SortedBag.of(gs.topCard()));
                    gs = gs.withoutTopCard(); 
                    }
                gs = gs.withMoreDiscardedCards(drawnCards);
                updatePlayers(players, gs);
                int taxe = route.additionalClaimCardsCount(playedCards, drawnCards);
                informePlayers(players, info.get(gs.currentPlayerId()).drewAdditionalCards(drawnCards, taxe));
                if(taxe == 0) {
                    gs = gs.withClaimedRoute(route, playedCards);
                    updatePlayers(players, gs);
                    informePlayers(players,info.get(gs.currentPlayerId()).claimedRoute(route, playedCards) );
                    } else if(taxe >= 1) {
                        if(gs.playerState(gs.currentPlayerId()).possibleAdditionalCards(taxe, playedCards).isEmpty()) {
                            informePlayers(players, info.get(gs.currentPlayerId()).didNotClaimRoute(route));
                            } else {
                                var chosenAdditionnalCards = players.get(gs.currentPlayerId())
                                                                    .chooseAdditionalCards(gs.playerState(gs.currentPlayerId())
                                                                    .possibleAdditionalCards(taxe, playedCards));
                                if(chosenAdditionnalCards.isEmpty()) {
                                    informePlayers(players, info.get(gs.currentPlayerId()).didNotClaimRoute(route));
                                    } else {
                                        informePlayers(players,info.get(gs.currentPlayerId()).claimedRoute(route, playedCards.union(chosenAdditionnalCards)));
                                        gs = gs.withClaimedRoute(route, playedCards.union(chosenAdditionnalCards));
                                        updatePlayers(players, gs);
                                        }              
                                }
                        }
                break;
            case OVERGROUND:
                gs = gs.withClaimedRoute(route, playedCards);
                informePlayers(players,info.get(gs.currentPlayerId()).claimedRoute(route, playedCards));
                break;
            }
            break;
            }
        return gs.forNextTurn();
        }
    
    /**
     * Informe each player in the game.
     * @param map (Map<PlayerId, Player>) - Mapping between every player and its PlayerId.
     * @param information (String) - Information to send to every player.
     */
    private static void informePlayers(Map<PlayerId, Player> map, String information) { 
        map.forEach((playerId, player) -> player.receiveInfo(information));
        }
    
    /**
     * Update each player GameState.
     * @param map (Map<PlayerId, Player>) - Mapping between every player and its PlayerId.
     * @param gameState (GameState) - gameState to update for every player.
     */
    private static void updatePlayers(Map<PlayerId, Player> map, GameState gameState) { 
        map.forEach((playerId, player) -> player.updateState(gameState, gameState.playerState(playerId)));
        }
    
    /**
     * Class to represent the longest Trail and the total points of each player at the end of the game.
     * @author Hamza REMMAL (310917)
     * @author Mehdi ZIAZI (311475)
     *
     */
    private static class EndGameState {
        
        private final PlayerId playerId;
        private final Trail longest;
        private final int points;
        
        private EndGameState(PlayerId playerId ,Trail longest, int points) {
            this.playerId = playerId;
            this.longest = longest;
            this.points  = points;
        }

        /**
         * Compute the winner(s) of the game.
         * @param mapEndGame (Map<PlayerId, EndGameState>) - 
         * @return (List<EndGameState>) - List of the winners.
         */
        private static List<EndGameState> computeWinner(Map<PlayerId, EndGameState> mapEndGame) {
            Map<PlayerId, EndGameState> list = new HashMap<>(mapEndGame);
            List<EndGameState> listWinner = new ArrayList<>();
            int pointsMax = Integer.MIN_VALUE;
            for(PlayerId playerId : list.keySet()) {
                    if(list.get(playerId).points > pointsMax) {
                        listWinner.clear();
                        listWinner.add(list.get(playerId));
                        pointsMax = list.get(playerId).points;
                    }else if(list.get(playerId).points == pointsMax) {
                        listWinner.add(list.get(playerId));
                    }
                }
            return listWinner;
        }

        /**
         * Compute the longest Trail for each player and returns a list of the longest Trails in the game.
         * @param mapEndGame (Map<PlayerId, EndGameState>) - Mapping between every playerId and its corresponding EndGameState.
         * @return (List<EndGameState>) - List of the longest trail.
         */
        private static List<EndGameState> computeLongestTrail(Map<PlayerId,EndGameState> listEndGame) {
            Map<PlayerId, EndGameState> list = new HashMap<>(listEndGame);
            List<EndGameState> listWinner = new ArrayList<>();
            int longestSize = 0;
            for(PlayerId playerId : list.keySet()) {
                if(list.get(playerId).longest.length() > longestSize) {
                    listWinner.clear();
                    listWinner.add((new EndGameState(playerId, list.get(playerId).longest , list.get(playerId).points + Constants.LONGEST_TRAIL_BONUS_POINTS)));
                    longestSize = list.get(playerId).longest.length();
                }else if(list.get(playerId).longest.length() == longestSize) {
                    listWinner.add(new EndGameState(playerId, list.get(playerId).longest, list.get(playerId).points + Constants.LONGEST_TRAIL_BONUS_POINTS));
                }
            }
            return listWinner;
        }
        
    }
    
}
