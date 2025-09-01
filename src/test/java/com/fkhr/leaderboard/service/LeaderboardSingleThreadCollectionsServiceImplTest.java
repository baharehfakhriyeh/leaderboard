package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.InstanceNotFoundException;

import java.util.List;

class LeaderboardSingleThreadCollectionsServiceImplTest {
    private PlayerSingleThreadCollectionsServiceImpl playerService;
    private LeaderboardService leaderboardService;

    @BeforeEach
    public void setUp() throws InstanceNotFoundException {
        playerService = new PlayerSingleThreadCollectionsServiceImpl();
        leaderboardService = new LeaderboardSingleThreadCollectionsServiceImpl(playerService);
        //fillDataSet();
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