package com.fkhr.leaderboard.websocket;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.util.concurrent.CountDownLatch;

public class PlayerUpdateScoreStompSessionHandler extends StompSessionHandlerAdapter {
    private final String message;
    private final CountDownLatch latch;

    public PlayerUpdateScoreStompSessionHandler(String message, CountDownLatch latch) {
        this.message = message;
        this.latch = latch;
    }
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/updated-score", this );

        session.send("/leaderboard/update-score", message);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("Received from leaderboard: " + payload);
        latch.countDown();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.err.println("Transport error: ");
        exception.printStackTrace();
    }

    @Override
    public void handleException(StompSession session, StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {
        System.err.println("handleException: " + exception.getMessage());
        exception.printStackTrace();
    }
}
