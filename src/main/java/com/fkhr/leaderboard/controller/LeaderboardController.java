package com.fkhr.leaderboard.controller;

import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.service.LeaderboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.TreeMap;

@RestController
@RequestMapping(value = "/leaderboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/top/{count}")
    public ResponseEntity getTopNLeaderboard(@PathVariable int count){
        List<Player> result = leaderboardService.getTopNPlayers(count);
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
