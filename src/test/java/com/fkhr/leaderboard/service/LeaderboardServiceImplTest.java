package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.InstanceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardServiceImplTest {
    private PlayerServiceImpl playerService;
    private LeaderboardServiceImpl leaderboardService;

    @BeforeEach
    public void setUp() throws InstanceNotFoundException {
        playerService = new PlayerServiceImpl();
        leaderboardService = new LeaderboardServiceImpl(playerService);
        fillDataSet();
    }

    @Test
    public void givenCount_whenGetTopNPlayers_thenReturnTopNPlayerList() {
        int count = 3;
        List<Player> result = leaderboardService.getTopNPlayers(count);
        Assertions.assertThat(result.size()).isEqualTo(count);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(4);
        Assertions.assertThat(result.get(1).getId()).isEqualTo(1);
        Assertions.assertThat(result.get(2).getId()).isEqualTo(5);
    }

    private void fillDataSet() throws InstanceNotFoundException {
        playerService.create(new CreatePlayerDto(1, "Bahareh"));
        playerService.create(new CreatePlayerDto(2, "Mark"));
        playerService.create(new CreatePlayerDto(3, "Dorna"));
        playerService.create(new CreatePlayerDto(4, "Arman"));
        playerService.create(new CreatePlayerDto(5, "John"));
        playerService.updateScore(new UpdatePlayerScoreDto(1, 50));
        playerService.updateScore(new UpdatePlayerScoreDto(2, 20));
        playerService.updateScore(new UpdatePlayerScoreDto(3, 30));
        playerService.updateScore(new UpdatePlayerScoreDto(4, 70));
        playerService.updateScore(new UpdatePlayerScoreDto(5, 50));
    }
}