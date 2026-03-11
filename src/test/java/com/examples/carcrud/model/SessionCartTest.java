package com.examples.carcrud.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionCartTest {

    @Test
    void addCar_increasesCount() {
        SessionCart cart = new SessionCart();
        cart.addCar(1L);
        cart.addCar(2L);

        assertEquals(2, cart.getCount());
        assertTrue(cart.contains(1L));
        assertTrue(cart.contains(2L));
    }

    @Test
    void addCar_duplicateIgnored() {
        SessionCart cart = new SessionCart();
        cart.addCar(1L);
        cart.addCar(1L);

        assertEquals(1, cart.getCount());
    }

    @Test
    void removeCar_removesFromCart() {
        SessionCart cart = new SessionCart();
        cart.addCar(1L);
        cart.addCar(2L);
        cart.removeCar(1L);

        assertEquals(1, cart.getCount());
        assertFalse(cart.contains(1L));
        assertTrue(cart.contains(2L));
    }

    @Test
    void clear_emptiesCart() {
        SessionCart cart = new SessionCart();
        cart.addCar(1L);
        cart.addCar(2L);
        cart.clear();

        assertEquals(0, cart.getCount());
    }

    @Test
    void getCarIds_returnsUnmodifiableSet() {
        SessionCart cart = new SessionCart();
        cart.addCar(1L);

        assertThrows(UnsupportedOperationException.class,
                () -> cart.getCarIds().add(2L));
    }
}
