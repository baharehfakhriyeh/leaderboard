package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Map;

public interface PlayerService {
    Player create(CreatePlayerDto createPlayerDto);
    Player updateScore(UpdatePlayerScoreDto updatePlayerScoreDto);
    List<Player> getPlayers();
    List<Player> getPlayers(int page, int size);
    List<Player> getNTopScorePlayers(int count);
    Player getPlayerById(long id);
}
