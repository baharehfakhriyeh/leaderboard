package com.fkhr.leaderboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.utils.CustomError;
import com.fkhr.leaderboard.utils.CustomException;
import com.fkhr.leaderboard.websocket.LeaderboardClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayerServiceImpl implements PlayerService {
    public static ConcurrentHashMap<Long, Player> players;
    private final LeaderboardClient leaderboardClient;
    static {
        players = new ConcurrentHashMap<>();
    }

    public PlayerServiceImpl(LeaderboardClient leaderboardClient) {
        this.leaderboardClient = leaderboardClient;
    }

    @Override
    public Player create(CreatePlayerDto createPlayerDto) {
        Player player = new Player();
        BeanUtils.copyProperties(createPlayerDto, player);
        players.put(player.getId(), player);
        return player;
    }

    @Override
    public Player updateScore(UpdatePlayerScoreDto updatePlayerScoreDto) throws InstanceNotFoundException {
        Player player = updatePlayerScore(updatePlayerScoreDto);
        updateScoreInLeaderboard(player);
        return player;
    }

    private Player updatePlayerScore(UpdatePlayerScoreDto updatePlayerScoreDto) throws InstanceNotFoundException {
        Player player = new Player();
        BeanUtils.copyProperties(updatePlayerScoreDto, player);
        if (players.containsKey(updatePlayerScoreDto.id())) {
            Player tempPlayer = players.get(player.getId());
            tempPlayer.setScore(player.getScore());
            players.put(tempPlayer.getId(), tempPlayer);
            return tempPlayer;
        } else {
            throw new InstanceNotFoundException();
        }
    }

    private void updateScoreInLeaderboard(Player player) {
        try {
            if (player == null) {
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            leaderboardClient.connect();
            if(leaderboardClient.isConnected()) {
                leaderboardClient.sendMessage(objectMapper.writeValueAsString(player));
            }
        }catch (Exception e){
            e.printStackTrace();
           // throw new CustomException(CustomError.LEADERBOARD_MAY_NOT_UPDATED, e);
        }
    }

    @Override
    public ConcurrentHashMap<Long, Player> getPlayers() {
        return players;
    }

    @Override
    public Player getPlayerById(long id) throws InstanceNotFoundException {
        if (players.containsKey(id))
            return players.get(id);
        else
            throw new InstanceNotFoundException();
    }


}
