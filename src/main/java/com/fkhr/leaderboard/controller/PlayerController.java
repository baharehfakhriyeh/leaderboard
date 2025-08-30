package com.fkhr.leaderboard.controller;

import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;

@RestController()
@RequestMapping(value = "/players", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
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
