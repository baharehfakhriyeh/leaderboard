package com.fkhr.leaderboard.websocket;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

/**
 * This class should be put in the client service
 */
public class LeaderboardUpdateScoreStompSessionHandler extends StompSessionHandlerAdapter {
    private final Object message;

    public LeaderboardUpdateScoreStompSessionHandler(Object message) {
        this.message = message;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/updated-score", this);
       // session.send("/player/update-score", message);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("Received from player: " + payload);
    }
}
