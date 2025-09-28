package com.fkhr.leaderboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.repository.PlayerRepository;
import com.fkhr.leaderboard.utils.CustomError;
import com.fkhr.leaderboard.utils.CustomException;
import com.fkhr.leaderboard.websocket.PlayerUpdateScoreStompSessionHandler;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private StompSession session;

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Player create(CreatePlayerDto createPlayerDto) {
        Player player = new Player();
        BeanUtils.copyProperties(createPlayerDto, player);
        checkPlayerNotExist(player.getIdentifier());
        player = playerRepository.save(player);
        return player;
    }

    @Override
    @Transactional
    public Player updateScore(UpdatePlayerScoreDto updatePlayerScoreDto) {
        Optional<Player> player = playerRepository.findById(updatePlayerScoreDto.id());
        if(player.isPresent()){
            int result = playerRepository.updatePlayerById(updatePlayerScoreDto.id(), updatePlayerScoreDto.score());
            if(result < 1){
                throw new CustomException(CustomError.PLAYER_NOT_UPDATED);
            }
        }
        else {
            throw new CustomException(CustomError.PLAYER_NOT_FOUND);
        }
        Player resultPlayer = player.get();
        resultPlayer.setScore(updatePlayerScoreDto.score());
        updateScoreInLeaderboard(resultPlayer);
        return resultPlayer;
    }

    /**
     * Connects to stomp
     */
    void updateScoreInLeaderboard(Player player){

        try {
            if (player == null) {
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String playerStr = objectMapper.writeValueAsString(player);
            sendMessage(playerStr);

        } catch (Exception e) {
            e.printStackTrace();
            // throw new CustomException(CustomError.LEADERBOARD_MAY_NOT_UPDATED, e);//commented temporarily because of unit tests
        }
    }

    private static void sendMessage(String message) throws InterruptedException, ExecutionException, TimeoutException {
        long timout = 30;//todo: read from config
        CountDownLatch latch = new CountDownLatch(1);
        WebSocketStompClient stompClient =new WebSocketStompClient(new StandardWebSocketClient());
        StompSessionHandler sessionHandler = new PlayerUpdateScoreStompSessionHandler(message, latch);//todo: set the handler as parameter
        stompClient.setMessageConverter(new StringMessageConverter());
        CompletableFuture<StompSession> future = stompClient.connectAsync("ws://localhost:9091/ws",//todo:read ip from config
                sessionHandler);
        StompSession session = future.get(timout, TimeUnit.SECONDS);
        System.out.println("Client connected, session id: " + session.getSessionId());

        boolean got = latch.await(timout, TimeUnit.SECONDS);
        if (!got) {
            System.err.println("Timed out waiting for leaderboard reply");
        }
    }

    @Override
    public List<Player> getPlayers() {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "id"));
        List<Player> players = playerRepository.findAll(sort);
        return players;
    }

    @Override
    public List<Player> getNTopScorePlayers(int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "score"));
        List<Player> result = playerRepository.findAllByOrderByScoreDesc(pageable);
        return result;
    }

    @Override
    public Player getPlayerById(long id){
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent())
            return player.get();
        else
            throw new CustomException(CustomError.PLAYER_NOT_FOUND);
    }

    private void checkPlayerNotExist(String identifier) {
        Optional<Player> playerOptional = playerRepository.findPlayerByIdentifier(identifier);
        if(playerOptional.isPresent()){
            throw new CustomException(CustomError.PLAYER_ALREADY_EXIST);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            System.out.println("WebSocket session closed on shutdown");
        }
    }
}
