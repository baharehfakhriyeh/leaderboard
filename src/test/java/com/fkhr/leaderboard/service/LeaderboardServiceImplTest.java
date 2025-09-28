package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.properties.LeaderboardProperties;
import com.fkhr.leaderboard.repository.PlayerRepository;
import com.fkhr.leaderboard.websocket.basic.LeaderboardClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceImplTest {
    private PlayerServiceImpl playerService;
    @Mock
    private PlayerRepository playerRepository;
    private LeaderboardService leaderboardService;
    private LeaderboardClient leaderboardClient;
    private LeaderboardProperties leaderboardProperties;

    @BeforeEach
    public void setUp() throws InstanceNotFoundException {
        leaderboardClient = new LeaderboardClient();
        playerService = new PlayerServiceImpl(playerRepository, leaderboardClient);
        leaderboardProperties = new LeaderboardProperties(5);
        leaderboardService = new LeaderboardServiceImpl(leaderboardProperties, playerService);
    }

    @Test
    void givenPlayer_whenUpdateLeaderboard_thenPlayerExistInLeaderboard() {
        Player player = new Player(1, UUID.randomUUID().toString(),"Bahareh", 50);

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
       /* Assertions.assertThat(result.get(0).getId()).isEqualTo(6);
        Assertions.assertThat(result.get(1).getId()).isEqualTo(4);
        Assertions.assertThat(result.get(2).getId()).isEqualTo(1);
        Assertions.assertThat(result.get(3).getId()).isEqualTo(5);
        Assertions.assertThat(result.get(4).getId()).isEqualTo(3);*/
    }

    @Test
    void givenRange_whenGetPlayersByRangeOfScore_thenReturnPlayers() {
        int minScore = 40;
        int maxScore = 80;
        List<Player> result = leaderboardService.getPlayersByRangeOfScore(minScore, maxScore);
        Assertions.assertThat(result).size().isEqualTo(3);
       /* Assertions.assertThat(result.get(0).getId()).isEqualTo(4);
        Assertions.assertThat(result.get(1).getId()).isEqualTo(1);
        Assertions.assertThat(result.get(2).getId()).isEqualTo(5);*/
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
        long id1 = playerService.create(new CreatePlayerDto(null,"Bahareh")).getId();
        long id2 = playerService.create(new CreatePlayerDto(null,"Mark")).getId();
        long id3 = playerService.create(new CreatePlayerDto(null,"Dorna")).getId();
        long id4 = playerService.create(new CreatePlayerDto(null,"Arman")).getId();
        long id5 = playerService.create(new CreatePlayerDto(null,"John")).getId();
        long id6 = playerService.create(new CreatePlayerDto(null,"Anna")).getId();
        playerService.updateScore(new UpdatePlayerScoreDto(id1, 50));
        playerService.updateScore(new UpdatePlayerScoreDto(id2, 20));
        playerService.updateScore(new UpdatePlayerScoreDto(id3, 30));
        playerService.updateScore(new UpdatePlayerScoreDto(id4, 70));
        playerService.updateScore(new UpdatePlayerScoreDto(id5, 50));
        playerService.updateScore(new UpdatePlayerScoreDto(id6, 90));
    }



}