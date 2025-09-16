package com.fkhr.leaderboard.config;

import com.fkhr.leaderboard.service.LeaderboardService;
import com.fkhr.leaderboard.websocket.LeaderboardUpdateScoreWs;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {
    private final LeaderboardService leaderboardService;

    public WebsocketConfig(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new LeaderboardUpdateScoreWs(leaderboardService),"/ws/leaderboard/update-score")
                .setAllowedOrigins("*");//todo: read origin from config
    }
}
