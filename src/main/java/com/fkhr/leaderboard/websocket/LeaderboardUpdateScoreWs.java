package com.fkhr.leaderboard.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class LeaderboardUpdateScoreWs extends TextWebSocketHandler {
    private final LeaderboardService leaderboardService;

    public LeaderboardUpdateScoreWs(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        ObjectMapper objectMapper = new ObjectMapper();
        Player player = objectMapper.readValue(message.getPayload(), Player.class);
        leaderboardService.updateLeaderboard(player);
        session.sendMessage(new TextMessage(String.format("Leaderboard updated with %s", message.getPayload())));
    }
}
