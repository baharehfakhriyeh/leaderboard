package com.fkhr.leaderboard.utils;

public enum CustomError {
    PLAYER_NAME_IS_REQUIRED(1001, "player_name_is_required");

    private final int code;
    private final String message;

    CustomError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
