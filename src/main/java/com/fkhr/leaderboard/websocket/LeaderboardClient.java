package com.fkhr.leaderboard.websocket;

import com.fkhr.leaderboard.utils.CustomError;
import com.fkhr.leaderboard.utils.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
public class LeaderboardClient {
    private WebSocketSession session;
    public void connect() throws ExecutionException, InterruptedException, IOException {
        try {
            StandardWebSocketClient client = new StandardWebSocketClient();
            session = client.execute(
                            new PlayerClientHandler(), "ws://localhost:9091/ws/leaderboard/update-score")
                    .get();//todo: read ip:port from properties
        }catch (Exception e){
            throw new CustomException(CustomError.NOT_CONNECTED_TO_LEADERBOARD, e);
        }
    }

    public boolean isConnected(){
        return  session != null && session.isOpen();
    }

    public void sendMessage(String message) throws IOException {
        if(isConnected()){
            session.sendMessage(new TextMessage(message));
        }
    }

}
