package com.fkhr.leaderboard.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.service.LeaderboardService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
/**
 * This class should be put in the server service
 */
@Controller
public class LeaderboardMessageController {
    private final LeaderboardService leaderboardService;

    public LeaderboardMessageController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @MessageMapping("/update-score")
    @SendTo("/topic/updated-score")
    public String updateScore(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Player player = objectMapper.readValue(message, Player.class);
        leaderboardService.updateLeaderboard(player);
        return String.format("Leaderboard updated with %s", player.toString());
    }

}
