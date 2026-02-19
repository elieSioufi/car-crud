package com.examples.carcrud.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Request-scoped bean — a NEW instance is created for each HTTP request
 * and destroyed when the request ends.
 *
 * Records the start time on construction so we can measure
 * how long the request took to process.
 */
@Component
@RequestScope
public class RequestTimer {

    private final long startTime;

    public RequestTimer() {
        this.startTime = System.nanoTime();
    }

    public long getElapsedMs() {
        return (System.nanoTime() - startTime) / 1_000_000;
    }
}
