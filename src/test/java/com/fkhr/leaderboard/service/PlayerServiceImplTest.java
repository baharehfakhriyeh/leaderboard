package com.fkhr.leaderboard.service;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.kafka.KafkaProducer;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.repository.PlayerRepository;
import com.fkhr.leaderboard.utils.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompSession;

import javax.management.InstanceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PlayerServiceImplTest {
    PlayerServiceImpl playerService;
    @Mock
    PlayerRepository playerRepository;
    @Mock
    StompSession session;
    @Mock
    KafkaProducer kafkaProducer;
    CreatePlayerDto createPlayerDto;
    Player player;

    @BeforeEach
    public void setUp() {
        playerService = new PlayerServiceImpl(playerRepository, kafkaProducer);
        createPlayerDto = new CreatePlayerDto(null,"Baharh");
        player = new Player(1, null, "Bahareh", 0);
    }

    @Test
    void givenCreatePlayerDto_whenCreate_thenReturnPlayer() {
        Player assumedPlayer = new Player(1, null, createPlayerDto.name(), 0);
        BDDMockito.given(playerService.create(createPlayerDto)).willReturn(assumedPlayer);
        Player player = playerService.create(createPlayerDto);
        Assertions.assertThat(player.getId()).isNotNull();
        Assertions.assertThat(player.getName()).isEqualTo(createPlayerDto.name());
        Assertions.assertThat(player.getScore()).isEqualTo(0);
    }

    @Test
    void givenUpdatePlayerDto_whenUpdateScore_thenReturnPlayer() {
        int score = 10;
        BDDMockito.given(playerRepository.findById(player.getId())).willReturn(Optional.of(player));
        UpdatePlayerScoreDto updatePlayerScoreDto = new UpdatePlayerScoreDto(player.getId(), score);
        BDDMockito.given(playerRepository.updatePlayerById(updatePlayerScoreDto.id(), updatePlayerScoreDto.score())).willReturn(1);
        Player result = playerService.updateScore(updatePlayerScoreDto);
        Assertions.assertThat(result.getId()).isEqualTo(updatePlayerScoreDto.id());
        Assertions.assertThat(result.getScore()).isEqualTo(score);
        /*CustomException result = org.junit.jupiter.api.Assertions.assertThrows(
                CustomException.class,
                () -> playerService.updateScore(updatePlayerScoreDto)
        );
        Assertions.assertThat(result).isInstanceOf(CustomException.class);*/
    }

    @Test
    void givenUpdatePlayerDto_whenUpdateScore_thenThrowsNotFoundException() throws InstanceNotFoundException {
        int score = 10;
        UpdatePlayerScoreDto updatePlayerScoreDto = new UpdatePlayerScoreDto(2, score);
        BDDMockito.given(playerRepository.findById(updatePlayerScoreDto.id())).willReturn(Optional.empty());
        CustomException result = org.junit.jupiter.api.Assertions.assertThrows(
                CustomException.class,
                () -> playerService.updateScore(updatePlayerScoreDto)
        );
        Assertions.assertThat(result).isInstanceOf(CustomException.class);
    }

    @Test
    void givenNothing_whenGetPlayers_thenReturnPlayerList(){
        int count = 3;
        List<Player> players = new ArrayList<>();
        players.add(playerService.create(createPlayerDto));
        createPlayerDto = new CreatePlayerDto(null,"Arman");
        players.add(playerService.create(createPlayerDto));
        createPlayerDto = new CreatePlayerDto(null,"Dorna");
        players.add(playerService.create(createPlayerDto));
        BDDMockito.given(playerRepository.findAll()).willReturn(players);
        List<Player> result = playerService.getPlayers();
        Assertions.assertThat(result).size().isEqualTo(count);
    }

    @Test
    void givenPlayerId_whenGetPlayers_thenReturnPlayer() throws InstanceNotFoundException {
        long id = 1;
        Player player = playerService.create(createPlayerDto);
        BDDMockito.given(playerRepository.findById(id)).willReturn(Optional.of(player));
        Player result = playerService.getPlayerById(id);
        Assertions.assertThat(result.getName()).isEqualTo(player.getName());
    }

    @Test
    void givenPlayerId_whenGetPlayers_thenReturnInstanceNotFoundException() {
        long id = 5;
        playerService.create(createPlayerDto);
        BDDMockito.given(playerRepository.findById(id)).willReturn(Optional.empty());
        CustomException result = org.junit.jupiter.api.Assertions.assertThrows(
                CustomException.class,
                () -> playerService.getPlayerById(id));
        Assertions.assertThat(result).isInstanceOf(CustomException.class);
    }
}