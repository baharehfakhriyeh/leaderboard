package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.properties.LeaderboardProperties;
import com.fkhr.leaderboard.utils.CustomError;
import com.fkhr.leaderboard.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {
    private LeaderboardProperties leaderboardProperties;
    private static ConcurrentSkipListMap<Integer, LinkedList<Player>> leaderboardPlayers;

    static {
        leaderboardPlayers = new ConcurrentSkipListMap<>(Collections.reverseOrder());
    }

    private final PlayerService playerService;

    public LeaderboardServiceImpl(LeaderboardProperties leaderboardProperties, PlayerService playerService) throws InstanceNotFoundException {
        this.leaderboardProperties = leaderboardProperties;
        this.playerService = playerService;
        setLeaderboardPlayers(leaderboardProperties.maxPlayers());
    }


    @Override
    public void updateLeaderboard(Player player) {
        if (player == null) {
            throw new InvalidParameterException();
        }
        removePlayerById(player.getId());
        LinkedList<Player> players = leaderboardPlayers.getOrDefault(player.getScore(), null);
        if (players == null) {
            players = new LinkedList<>();
            players.addLast(player);
            leaderboardPlayers.put(player.getScore(), players);
        }
        else {
            Iterator<Player> playerIterator = players.iterator();
            Player tempPlayer;
            boolean existPlayer = false;
            while (playerIterator.hasNext()) {
                tempPlayer = playerIterator.next();
                if (tempPlayer.getId() == player.getId()) {
                    tempPlayer.setScore(player.getScore());
                    existPlayer = true;
                    break;
                }
            }
            if(!existPlayer){
                players.add(player);
            }
        }
    }

    @Override
    public List<Player> getTopNPlayers(int count) {
        List<Player> playerList = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(count);
        Iterator<LinkedList<Player>> leaderboardIt = leaderboardPlayers.values().iterator();
        while (leaderboardIt.hasNext()) {
            Iterator<Player> playersIterator = leaderboardIt.next().iterator();
            while (playersIterator.hasNext()) {
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
        ConcurrentNavigableMap<Integer, LinkedList<Player>> subMapLeaderboard =
                leaderboardPlayers.subMap(maxScore, true, minScore, true);
        List<Player> players = new ArrayList<>();
        subMapLeaderboard.values().forEach(v -> players.addAll(v));
        return players;
    }

    @Override
    public Optional<Player> getPlayerScoreById(long id) {
        Optional<Player> player = leaderboardPlayers.values().stream().flatMap(item -> item.stream())
                .filter(p -> p.getId() == id).findFirst();
        if(!player.isPresent()){
            throw new CustomException(CustomError.PLAYER_NOT_EXIST_IN_LEADERBOARD);
        }
        return player;
    }

    @Override
    public void removePlayerById(long id) {
        Iterator<Map.Entry<Integer, LinkedList<Player>>> iterator = leaderboardPlayers.entrySet().iterator();
        boolean result = false;
        while (iterator.hasNext()){
            Map.Entry<Integer, LinkedList<Player>> entry = iterator.next();
            result =  entry.getValue().removeIf(player -> player.getId() == id);
            if(result && entry.getValue().isEmpty()){
                leaderboardPlayers.remove(entry.getKey());
                return;
            }
        }
    }

    private synchronized void setLeaderboardPlayers(int count) {
        if (leaderboardPlayers.isEmpty()) {
            List<Player> playersList = playerService.getNTopScorePlayers(count);
            playersList.forEach(
                    (player) -> {
                        if (leaderboardPlayers.containsKey(player.getScore())) {
                            leaderboardPlayers.get(player.getScore()).add(player);
                        } else {
                            leaderboardPlayers.put(player.getScore(), new LinkedList<>(List.of(player)));
                        }
                    }
            );
        }
    }
}
