package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.model.Player;

import java.util.List;
import java.util.Optional;

public interface LeaderboardService {
    List<Player> getTopNPlayers(int count);
    List<Player> getPlayersByRangeOfScore(int minScore, int maxScore);
    void updateLeaderboard(Player player);
    Optional<Player> getPlayerScoreById(long id);
    void removePlayerById(long id);
}
