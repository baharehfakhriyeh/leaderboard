package com.fkhr.leaderboard.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "leaderboard")
public record LeaderboardProperties(int maxPlayers) {

}
