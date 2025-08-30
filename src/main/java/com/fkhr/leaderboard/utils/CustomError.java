package com.fkhr.leaderboard.utils;

import org.springframework.http.HttpStatus;

public enum CustomError {
    PLAYER_NAME_IS_REQUIRED(2001, "player_name_is_required", HttpStatus.ACCEPTED),

    ;

    private final int code;
    private final String message;
    private final HttpStatus status;

    CustomError(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
