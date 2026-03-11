package com.examples.carcrud.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestTimerTest {

    @Test
    void getElapsedMs_returnsNonNegative() {
        RequestTimer timer = new RequestTimer();
        long elapsed = timer.getElapsedMs();

        assertTrue(elapsed >= 0);
    }

    @Test
    void getElapsedMs_increasesOverTime() throws InterruptedException {
        RequestTimer timer = new RequestTimer();
        Thread.sleep(50);
        long elapsed = timer.getElapsedMs();

        assertTrue(elapsed >= 10);
    }
}
