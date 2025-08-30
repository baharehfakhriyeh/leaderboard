package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;

import javax.management.InstanceNotFoundException;
import java.util.HashMap;

public interface PlayerService {
    Player create(CreatePlayerDto createPlayerDto);
    Player updateScore(UpdatePlayerScoreDto updatePlayerScoreDto) throws InstanceNotFoundException;
    HashMap<Long, Player> getPlayers();
    Player getPlayerById(long id) throws InstanceNotFoundException;
}
