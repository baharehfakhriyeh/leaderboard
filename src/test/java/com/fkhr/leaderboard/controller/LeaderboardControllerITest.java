package com.fkhr.leaderboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fkhr.leaderboard.service.PlayerService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.management.InstanceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class LeaderboardControllerITest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PlayerService playerService;


    @BeforeEach
    void setUp() throws InstanceNotFoundException {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void givenCont_whenGetTopNLeaderboard_thenReturnPlayerList() throws Exception {
        int count = 3;
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/leaderboard/top/" + count)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        );
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(3)));
    }

    @Test
    void givenMultipleThread_whenGetTopNLeaderboard_thenReturnPlayerListFromEachThread() throws Exception {
        callAsMultiThread(param -> {
            try {
                givenCont_whenGetTopNLeaderboard_thenReturnPlayerList();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 100);
    }

    @Test
    void givenRange_whenGetLeaderboardInRangeOfScore_thenReturnPlayerListInThatRange() throws Exception {
        String minScore = "40";
        String maxScore = "80";
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/leaderboard/range")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("minScore", minScore)
                .queryParam("maxScore", maxScore));
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(3)));
    }

    @Test
    void givenMultipleThreads_whenGetLeaderboardInRangeOfScore_thenReturnPlayerListFromEachThread() throws InterruptedException {
        callAsMultiThread(param -> {
            try {
                 givenRange_whenGetLeaderboardInRangeOfScore_thenReturnPlayerListInThatRange();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 100);

    }

    private void callAsMultiThread(Consumer action, int threadCount) throws InterruptedException {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        Callable<Integer> task = () -> {
            try {
                action.accept(null);
                return 1;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            tasks.add(task);
        }
        List<Future<Integer>> result = executorService.invokeAll(tasks);
        AtomicInteger sum = new AtomicInteger();
        result.forEach(
                item -> {
                    try {
                        sum.addAndGet(item.get());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        executorService.shutdown();
        System.out.println(sum + " thread(s) finished.");
    }
}