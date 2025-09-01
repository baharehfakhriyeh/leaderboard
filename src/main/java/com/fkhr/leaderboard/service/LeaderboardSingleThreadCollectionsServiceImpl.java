package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service("leaderboard_single_thread")
public class LeaderboardSingleThreadCollectionsServiceImpl implements LeaderboardService{
    private static TreeMap<Integer, LinkedList<Player>> leaderboardPlayers;
    static {
        leaderboardPlayers = new TreeMap<>(Collections.reverseOrder());

    }

    private final PlayerService playerService;

    public LeaderboardSingleThreadCollectionsServiceImpl(@Qualifier("player_single_thread") PlayerService playerService) throws InstanceNotFoundException {
        this.playerService = playerService;
        this.fillDataSet();
    }

    @Override
    public synchronized List<Player> getTopNPlayers(int count) {
        List<Player> playerList = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(count);
        Iterator<LinkedList<Player>> leaderboardIt = leaderboardPlayers.values().iterator();
        while (leaderboardIt.hasNext()) {
            Iterator<Player> playersIterator = leaderboardIt.next().iterator();
            while (playersIterator.hasNext()){
                playerList.add(playersIterator.next());
                counter.decrementAndGet();
                if (counter.intValue() <= 0) {
                    playersIterator.forEachRemaining(item -> {
                    });
                    break;
                }
            }
            if (counter.intValue() <= 0) {
                leaderboardIt.forEachRemaining(item -> {
                });
                break;
            }
        }
        return playerList;
    }

    @Override
    public List<Player> getPlayersByRangeOfScore(int minScore, int maxScore) {
        setLeaderboardPlayers();
        NavigableMap<Integer, LinkedList<Player>> subMapLeaderboard =
                leaderboardPlayers.subMap(maxScore, true, minScore, true);
        List<Player> players = new ArrayList<>();
        subMapLeaderboard.values().forEach(v->players.addAll(v));
        return players;
    }

    private synchronized void setLeaderboardPlayers(){
        if (leaderboardPlayers.isEmpty()) {
            HashMap<Long, Player> playersMap = (HashMap<Long, Player>) playerService.getPlayers();
            playersMap.forEach(
                    (k, v) -> {
                        if (leaderboardPlayers.containsKey(v.getScore())) {
                            leaderboardPlayers.get(v.getScore()).add(v);
                        } else {
                            leaderboardPlayers.put(v.getScore(), new LinkedList<>(List.of(v)));
                        }
                    }
            );
        }

    }

    /**
     * Initialisation. //todo: remove if it became a real project.
     * @throws InstanceNotFoundException
     */
    public synchronized void fillDataSet() throws InstanceNotFoundException {
        List<Player> players = playerService.getPlayers().values().stream().toList();
        if(players.isEmpty()) {
            playerService.create(new CreatePlayerDto(1, "Bahareh"));
            playerService.create(new CreatePlayerDto(2, "Mark"));
            playerService.create(new CreatePlayerDto(3, "Dorna"));
            playerService.create(new CreatePlayerDto(4, "Arman"));
            playerService.create(new CreatePlayerDto(5, "John"));
            playerService.create(new CreatePlayerDto(6, "Anna"));
            playerService.updateScore(new UpdatePlayerScoreDto(1, 50));
            playerService.updateScore(new UpdatePlayerScoreDto(2, 20));
            playerService.updateScore(new UpdatePlayerScoreDto(3, 30));
            playerService.updateScore(new UpdatePlayerScoreDto(4, 70));
            playerService.updateScore(new UpdatePlayerScoreDto(5, 50));
            playerService.updateScore(new UpdatePlayerScoreDto(6, 90));
        }
    }
}
