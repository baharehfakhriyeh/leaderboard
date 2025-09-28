package com.fkhr.leaderboard.repository;

import com.fkhr.leaderboard.model.Player;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Modifying
    @Query("update Player set score = :score where id = :id")
    int updatePlayerById(long id, int score);
    Optional<Player> findPlayerByIdentifier(String identifier);
    List<Player> findAllByOrderByScoreDesc(Pageable pageable);

}
