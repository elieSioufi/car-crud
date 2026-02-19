package com.examples.carcrud.repository;

import com.examples.carcrud.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findBySoldFalse();
}
