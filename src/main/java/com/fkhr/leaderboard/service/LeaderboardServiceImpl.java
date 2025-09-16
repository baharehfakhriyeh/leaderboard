package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.utils.CustomError;
import com.fkhr.leaderboard.utils.CustomException;
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
    private static ConcurrentSkipListMap<Integer, LinkedList<Player>> leaderboardPlayers;

    static {
        leaderboardPlayers = new ConcurrentSkipListMap<>(Collections.reverseOrder());
    }

    private final PlayerService playerService;

    public LeaderboardServiceImpl(PlayerService playerService) throws InstanceNotFoundException {
        this.playerService = playerService;
        this.fillDataSet();
        setLeaderboardPlayers();
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

    private synchronized void setLeaderboardPlayers() {
        if (leaderboardPlayers.isEmpty()) {
            ConcurrentHashMap<Long, Player> playersMap = (ConcurrentHashMap<Long, Player>) playerService.getPlayers();
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
     *
     * @throws InstanceNotFoundException
     */
    public synchronized void fillDataSet() throws InstanceNotFoundException {
        List<Player> players = playerService.getPlayers().values().stream().toList();
        if (players.isEmpty()) {
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
