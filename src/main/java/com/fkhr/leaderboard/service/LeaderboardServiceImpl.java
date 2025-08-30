package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.model.Player;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.DelayQueue;

@Service
public class LeaderboardServiceImpl implements LeaderboardService{
    private static TreeMap<Integer, LinkedList<Player>> leaderboardPlayers;
    static {
        leaderboardPlayers = new TreeMap<>(Collections.reverseOrder());
    }

    private final PlayerService playerService;

    public LeaderboardServiceImpl(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public List<Player> getTopNPlayers(int count) {
        leaderboardPlayers.clear();
        HashMap<Long, Player> playersMap = playerService.getPlayers();
        playersMap.forEach(
                (k, v) -> {
                    if(leaderboardPlayers.containsKey(v.getScore())){
                        leaderboardPlayers.get(v.getScore()).add(v);
                    }
                    else {
                        leaderboardPlayers.put(v.getScore(), new LinkedList<>(List.of(v)));
                    }
                }
        );

        List<Player> playerList = new ArrayList<>();
        Map.Entry<Integer, LinkedList<Player>> playerEntry;
        for (int i = 0; i < count; i++) {
            playerEntry = leaderboardPlayers.pollFirstEntry();
            if(playerEntry.getValue().size() == 1){
                playerList.add(playerEntry.getValue().removeFirst());
            } else {
                playerList.add(playerEntry.getValue().removeFirst());
                leaderboardPlayers.put(playerEntry.getKey(), playerEntry.getValue());

            }
        }
        return playerList;
    }
}
