package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;

import javax.management.InstanceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface PlayerService {
    Player create(CreatePlayerDto createPlayerDto);
    Player updateScore(UpdatePlayerScoreDto updatePlayerScoreDto) throws InstanceNotFoundException;
    Map<Long, Player> getPlayers();
    Player getPlayerById(long id) throws InstanceNotFoundException;
}
