package com.fkhr.leaderboard.dto.player;

import java.util.UUID;

public record CreatePlayerDto(String identifier, String name) {


    public CreatePlayerDto(String identifier, String name) {
        if (identifier == null) {
            this.identifier = UUID.randomUUID().toString();
        }
        else {
            this.identifier = identifier;
        }
        this.name = name;
    }
}
