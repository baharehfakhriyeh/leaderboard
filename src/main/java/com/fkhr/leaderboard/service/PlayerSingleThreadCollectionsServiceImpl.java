package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.HashMap;

@Service("player_single_thread")
public class PlayerSingleThreadCollectionsServiceImpl implements PlayerService {
    public static HashMap<Long, Player> players;

    static {
        players = new HashMap<>();
    }

    public PlayerSingleThreadCollectionsServiceImpl() {
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

    @Override
    public HashMap<Long, Player> getPlayers() {
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
