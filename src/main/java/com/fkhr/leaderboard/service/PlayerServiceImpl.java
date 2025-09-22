package com.fkhr.leaderboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fkhr.leaderboard.dto.player.CreatePlayerDto;
import com.fkhr.leaderboard.dto.player.UpdatePlayerScoreDto;
import com.fkhr.leaderboard.model.Player;
import com.fkhr.leaderboard.repository.PlayerRepository;
import com.fkhr.leaderboard.utils.CustomError;
import com.fkhr.leaderboard.utils.CustomException;
import com.fkhr.leaderboard.websocket.LeaderboardClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final LeaderboardClient leaderboardClient;

    public PlayerServiceImpl(PlayerRepository playerRepository, LeaderboardClient leaderboardClient) {
        this.playerRepository = playerRepository;
        this.leaderboardClient = leaderboardClient;
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

    void updateScoreInLeaderboard(Player player) {
        try {
            if (player == null) {
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            leaderboardClient.connect();
            if(leaderboardClient.isConnected()) {
                leaderboardClient.sendMessage(objectMapper.writeValueAsString(player));
            }
        }catch (Exception e){
            e.printStackTrace();
           // throw new CustomException(CustomError.LEADERBOARD_MAY_NOT_UPDATED, e);
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
}
