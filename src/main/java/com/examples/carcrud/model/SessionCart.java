package com.examples.carcrud.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Session-scoped bean — one instance per HTTP session.
 * Persists across multiple requests from the same user and is
 * destroyed when the session ends (logout or timeout).
 *
 * Implements a shopping cart that remembers which cars the user
 * wants to buy as they navigate different pages.
 */
@Component
@SessionScope
public class SessionCart implements Serializable {

    private final Set<Long> carIds = new LinkedHashSet<>();

    public void addCar(Long carId) {
        carIds.add(carId);
    }

    public void removeCar(Long carId) {
        carIds.remove(carId);
    }

    public void clear() {
        carIds.clear();
    }

    public Set<Long> getCarIds() {
        return Collections.unmodifiableSet(carIds);
    }

    public int getCount() {
        return carIds.size();
    }

    public boolean contains(Long carId) {
        return carIds.contains(carId);
    }
}
