package com.fkhr.leaderboard.utils;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class CustomeException extends RuntimeException{
    HttpStatus status;
    String message;
    int code;
    Throwable cause;
    LocalDateTime time;


}
