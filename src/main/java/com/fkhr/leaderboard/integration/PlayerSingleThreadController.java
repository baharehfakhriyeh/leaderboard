package com.fkhr.leaderboard.integration;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.service.PlayerService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;

@RestController()
@RequestMapping(value = "/single-thread/players", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlayerSingleThreadController {
    private final PlayerService playerService;

    public PlayerSingleThreadController(@Qualifier("player_single_thread") PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity createPlayer(@RequestBody CreatePlayerDto createPlayerDto){
        Player player = playerService.create(createPlayerDto);
        return new ResponseEntity(player, HttpStatus.OK);
    }

    @PutMapping("/score")
    public ResponseEntity updatePlayerScore(@RequestBody UpdatePlayerScoreDto updatePlayerScoreDto) throws InstanceNotFoundException {
        Player player = playerService.updateScore(updatePlayerScoreDto);
        return new ResponseEntity(player, HttpStatus.OK);
    }

}
