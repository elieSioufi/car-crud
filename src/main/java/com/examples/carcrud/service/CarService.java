package com.examples.carcrud.service;

import com.examples.carcrud.dto.CarForm;
import com.examples.carcrud.model.Car;
import com.examples.carcrud.repository.CarRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Scope("singleton") // Explicit singleton scope — one shared instance across the entire Spring container
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public List<Car> findAvailable() {
        return carRepository.findBySoldFalse();
    }

    public Optional<Car> findById(Long id) {
        return carRepository.findById(id);
    }

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public Car createFromForm(CarForm form) {
        Car car = new Car();
        applyForm(car, form);
        return carRepository.save(car);
    }

    public Car updateFromForm(Long id, CarForm form) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found: " + id));
        applyForm(car, form);
        return carRepository.save(car);
    }

    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    public CarForm toForm(Car car) {
        CarForm form = new CarForm();
        form.setId(car.getId());
        form.setType(car.getType());
        form.setBrand(car.getBrand());
        form.setModel(car.getModel());
        form.setPurchaseDate(car.getPurchaseDate());
        form.setMaxSpeed(car.getMaxSpeed());
        form.setPassengers(car.getPassengers());
        form.setAutomaticTransmission(car.isAutomaticTransmission());
        form.setSold(car.isSold());
        form.setPrice(car.getPrice());
        return form;
    }

    private void applyForm(Car car, CarForm form) {
        car.setType(form.getType());
        car.setBrand(form.getBrand());
        car.setModel(form.getModel());
        car.setPurchaseDate(form.getPurchaseDate());
        car.setMaxSpeed(form.getMaxSpeed());
        car.setPassengers(form.getPassengers());
        car.setAutomaticTransmission(form.isAutomaticTransmission());
        car.setSold(form.isSold());
        car.setPrice(form.getPrice());
    }
}
