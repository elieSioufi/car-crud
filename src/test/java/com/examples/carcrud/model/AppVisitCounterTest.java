package com.examples.carcrud.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppVisitCounterTest {

    @Test
    void increment_increasesCount() {
        AppVisitCounter counter = new AppVisitCounter();
        assertEquals(0, counter.getCount());

        int result = counter.increment();
        assertEquals(1, result);
        assertEquals(1, counter.getCount());
    }

    @Test
    void increment_multipleIncrements() {
        AppVisitCounter counter = new AppVisitCounter();
        counter.increment();
        counter.increment();
        counter.increment();

        assertEquals(3, counter.getCount());
    }
}
