package com.fkhr.leaderboard.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.service.LeaderboardService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaScoresConsumerBridge {
    private final KafkaProducer kafkaProducer;
    private final LeaderboardService leaderboardService;

    public KafkaScoresConsumerBridge(KafkaProducer kafkaProducer, LeaderboardService leaderboardService) {
        this.kafkaProducer = kafkaProducer;

        this.leaderboardService = leaderboardService;
    }

    @KafkaListener(topics = {Topics.UPDATE_SCORE}, groupId = "leaderboard-bridge")
    public void listen(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Player player = objectMapper.readValue(message, Player.class);
        leaderboardService.updateLeaderboard(player);
        String responseStr = String.format("Leaderboard updated with %s", player.toString());
        kafkaProducer.send(Topics.UPDATED_SCORE, responseStr);
    }
}
