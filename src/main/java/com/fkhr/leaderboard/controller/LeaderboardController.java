package com.fkhr.leaderboard.controller;

import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.service.LeaderboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/leaderboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(
            LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/top/{count}")
    public ResponseEntity getTopNLeaderboard(@PathVariable int count){
        List<Player> result = leaderboardService.getTopNPlayers(count);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/range")
    public ResponseEntity getLeaderboardInRangeOfScore(@RequestParam(defaultValue = "0") int minScore,
                                              @RequestParam(defaultValue = "0") int maxScore){
        List<Player> result = leaderboardService.getPlayersByRangeOfScore(minScore, maxScore);
        return new  ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/player/{id}")
    public ResponseEntity getPlayerScoreById(@PathVariable long id){
         Player player = leaderboardService.getPlayerScoreById(id).orElse(null);
         return new ResponseEntity(player, HttpStatus.OK);
    }

}
