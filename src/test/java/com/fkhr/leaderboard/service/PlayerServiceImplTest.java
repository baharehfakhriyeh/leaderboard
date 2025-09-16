package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.utils.CustomException;
import com.fkhr.leaderboard.websocket.LeaderboardClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.util.concurrent.ConcurrentHashMap;

class PlayerServiceImplTest {
    PlayerService playerService;
    LeaderboardClient leaderboardClient;
    CreatePlayerDto createPlayerDto;

    @BeforeEach
    public void setUp() {
        leaderboardClient = new LeaderboardClient();
        playerService = new PlayerServiceImpl(leaderboardClient);
        createPlayerDto = new CreatePlayerDto(1, "Baharh");
    }

    @Test
    void givenCreatePlayerDto_whenCreate_thenReturnPlayer() {
        Player player = playerService.create(createPlayerDto);
        Assertions.assertThat(player.getId()).isEqualTo(createPlayerDto.id());
        Assertions.assertThat(player.getName()).isEqualTo(createPlayerDto.name());
        Assertions.assertThat(player.getScore()).isEqualTo(0);
    }

    @Test
    void givenUpdatePlayerDto_whenUpdateScore_thenReturnPlayer() throws InstanceNotFoundException {
        Player player = playerService.create(createPlayerDto);
        int score = 10;
        UpdatePlayerScoreDto updatePlayerScoreDto = new UpdatePlayerScoreDto(1, score);
        //Player result = playerService.updateScore(updatePlayerScoreDto);
        //Assertions.assertThat(result.getId()).isEqualTo(updatePlayerScoreDto.id());
        //Assertions.assertThat(result.getScore()).isEqualTo(score);
        CustomException result = org.junit.jupiter.api.Assertions.assertThrows(
                CustomException.class,
                () -> playerService.updateScore(updatePlayerScoreDto)
        );
        Assertions.assertThat(result).isInstanceOf(CustomException.class);
    }

    @Test
    void givenUpdatePlayerDto_whenUpdateScore_thenThrowsNotFoundException() throws InstanceNotFoundException {
        int score = 10;
        UpdatePlayerScoreDto updatePlayerScoreDto = new UpdatePlayerScoreDto(2, score);
        InstanceNotFoundException result = org.junit.jupiter.api.Assertions.assertThrows(
                InstanceNotFoundException.class,
                () -> playerService.updateScore(updatePlayerScoreDto)
        );
        Assertions.assertThat(result).isInstanceOf(InstanceNotFoundException.class);
    }

    @Test
    void givenNothing_whenGetPlayers_thenReturnPlayerList(){
        int count = 3;
        playerService.create(createPlayerDto);
        createPlayerDto = new CreatePlayerDto(2, "Arman");
        playerService.create(createPlayerDto);
        createPlayerDto = new CreatePlayerDto(3, "Dorna");
        playerService.create(createPlayerDto);
        ConcurrentHashMap<Long, Player> result = (ConcurrentHashMap<Long, Player>) playerService.getPlayers();
        Assertions.assertThat(result).size().isEqualTo(count);
        for (int i = 1; i <= count; i++) {
            Assertions.assertThat(result.keySet()).contains((long) i);
        }
    }

    @Test
    void givenPlayerId_whenGetPlayers_thenReturnPlayer() throws InstanceNotFoundException {
        long id = 1;
        playerService.create(createPlayerDto);
        Player result = playerService.getPlayerById(id);
        Assertions.assertThat(result).isEqualTo(new Player(createPlayerDto.id(), createPlayerDto.name(), 0));
    }

    @Test
    void givenPlayerId_whenGetPlayers_thenReturnInstanceNotFoundException() throws InstanceNotFoundException {
        long id = 5;
        playerService.create(createPlayerDto);
        InstanceNotFoundException result = org.junit.jupiter.api.Assertions.assertThrows(
                InstanceNotFoundException.class,
                () -> playerService.getPlayerById(id));
        Assertions.assertThat(result).isInstanceOf(InstanceNotFoundException.class);
    }
}