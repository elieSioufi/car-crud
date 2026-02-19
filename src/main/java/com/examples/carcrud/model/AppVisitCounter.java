package com.examples.carcrud.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Application-scoped bean — a SINGLE instance shared across ALL sessions
 * and ALL users for the entire application lifetime.
 *
 * Tracks total page views across every user. The counter resets
 * only when the application restarts.
 */
@Component
@ApplicationScope
public class AppVisitCounter {

    private final AtomicInteger count = new AtomicInteger(0);

    public int increment() {
        return count.incrementAndGet();
    }

    public int getCount() {
        return count.get();
    }
}
