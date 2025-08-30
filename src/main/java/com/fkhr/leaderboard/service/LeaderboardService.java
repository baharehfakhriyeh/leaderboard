package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.model.Player;

import java.util.List;

public interface LeaderboardService {
    List<Player> getTopNPlayers(int count);
}
