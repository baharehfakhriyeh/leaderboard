package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.websocket.LeaderboardClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.InstanceNotFoundException;
import java.util.List;

class LeaderboardServiceImplTest {
    private PlayerServiceImpl playerService;
    private LeaderboardService leaderboardService;
    private LeaderboardClient leaderboardClient;

    @BeforeEach
    public void setUp() throws InstanceNotFoundException {
        leaderboardClient = new LeaderboardClient();
        playerService = new PlayerServiceImpl(leaderboardClient);
        leaderboardService = new LeaderboardServiceImpl(playerService);
        //fillDataSet();
    }

    @Test
    void givenPlayer_whenUpdateLeaderboard_thenPlayerExistInLeaderboard() {
        Player player = new Player(1, "Bahareh", 50);

        leaderboardService.updateLeaderboard(player);

        Player result = leaderboardService.getPlayerScoreById(player.getId()).orElse(null);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(player);
    }

    @Test
    public void givenCount_whenGetTopNPlayers_thenReturnTopNPlayerList() {
        int count = 10;
        List<Player> result = leaderboardService.getTopNPlayers(count);

        int playerListSize = playerService.getPlayers().size();
        if(playerListSize < count){
            count = playerListSize;
        }
        Assertions.assertThat(result.size()).isEqualTo(count);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(6);
        Assertions.assertThat(result.get(1).getId()).isEqualTo(4);
        Assertions.assertThat(result.get(2).getId()).isEqualTo(1);
        Assertions.assertThat(result.get(3).getId()).isEqualTo(5);
        Assertions.assertThat(result.get(4).getId()).isEqualTo(3);
    }

    @Test
    void givenRange_whenGetPlayersByRangeOfScore_thenReturnPlayers() {
        int minScore = 40;
        int maxScore = 80;
        List<Player> result = leaderboardService.getPlayersByRangeOfScore(minScore, maxScore);
        Assertions.assertThat(result).size().isEqualTo(3);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(4);
        Assertions.assertThat(result.get(1).getId()).isEqualTo(1);
        Assertions.assertThat(result.get(2).getId()).isEqualTo(5);
    }

    @Test
    void givenId_whenGetPlayerScoreById_thenReturnPlayer() {
        long id = 3;
        Player player = leaderboardService.getPlayerScoreById(id).orElse(null);
        Assertions.assertThat(player).isNotNull();
        Assertions.assertThat(player.getId()).isEqualTo(id);
        System.out.println(player);
    }

    @Test
    void givenId_whenGetPlayerScoreById_thenReturnNull() {
        long id = 30;
        Player player = leaderboardService.getPlayerScoreById(id).orElse(null);
        Assertions.assertThat(player).isNull();
    }

    private void fillDataSet() throws InstanceNotFoundException {
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