package com.examples.carcrud.service;

import com.examples.carcrud.dto.CarForm;
import com.examples.carcrud.model.Car;
import com.examples.carcrud.model.CarType;
import com.examples.carcrud.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    @Test
    void findAll_returnsAllCars() {
        Car car = createTestCar();
        when(carRepository.findAll()).thenReturn(List.of(car));

        List<Car> result = carService.findAll();

        assertEquals(1, result.size());
        assertEquals("BMW", result.get(0).getBrand());
    }

    @Test
    void findAvailable_returnsOnlyUnsoldCars() {
        Car car = createTestCar();
        when(carRepository.findBySoldFalse()).thenReturn(List.of(car));

        List<Car> result = carService.findAvailable();

        assertEquals(1, result.size());
        assertFalse(result.get(0).isSold());
    }

    @Test
    void findById_existingCar_returnsCar() {
        Car car = createTestCar();
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        Optional<Car> result = carService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("BMW", result.get().getBrand());
    }

    @Test
    void findById_nonExisting_returnsEmpty() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Car> result = carService.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void createFromForm_savesNewCar() {
        CarForm form = new CarForm();
        form.setType(CarType.SUV);
        form.setBrand("Audi");
        form.setModel("Q7");
        form.setPrice(new BigDecimal("28000"));
        form.setMaxSpeed(250);
        form.setPassengers(7);
        form.setAutomaticTransmission(true);
        form.setPurchaseDate(LocalDate.of(2023, 8, 12));

        when(carRepository.save(any(Car.class))).thenAnswer(i -> i.getArgument(0));

        Car result = carService.createFromForm(form);

        assertEquals("Audi", result.getBrand());
        assertEquals("Q7", result.getModel());
        assertEquals(CarType.SUV, result.getType());
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void updateFromForm_existingCar_updatesIt() {
        Car existing = createTestCar();
        existing.setId(1L);
        when(carRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(carRepository.save(any(Car.class))).thenAnswer(i -> i.getArgument(0));

        CarForm form = new CarForm();
        form.setType(CarType.SEDAN);
        form.setBrand("Mercedes");
        form.setModel("E-Class");
        form.setPrice(new BigDecimal("22000"));
        form.setMaxSpeed(260);
        form.setPassengers(5);
        form.setAutomaticTransmission(true);

        Car result = carService.updateFromForm(1L, form);

        assertEquals("Mercedes", result.getBrand());
        assertEquals("E-Class", result.getModel());
    }

    @Test
    void updateFromForm_nonExistingCar_throws() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        CarForm form = new CarForm();
        assertThrows(RuntimeException.class, () -> carService.updateFromForm(99L, form));
    }

    @Test
    void deleteById_deletesFromRepo() {
        carService.deleteById(1L);
        verify(carRepository).deleteById(1L);
    }

    @Test
    void toForm_convertsCorrectly() {
        Car car = createTestCar();
        car.setId(5L);

        CarForm form = carService.toForm(car);

        assertEquals(5L, form.getId());
        assertEquals("BMW", form.getBrand());
        assertEquals("X5", form.getModel());
        assertEquals(CarType.SUV, form.getType());
        assertEquals(new BigDecimal("15000"), form.getPrice());
    }

    private Car createTestCar() {
        Car car = new Car();
        car.setType(CarType.SUV);
        car.setBrand("BMW");
        car.setModel("X5");
        car.setPurchaseDate(LocalDate.of(2022, 7, 20));
        car.setMaxSpeed(250);
        car.setPassengers(5);
        car.setAutomaticTransmission(true);
        car.setSold(false);
        car.setPrice(new BigDecimal("15000"));
        return car;
    }
}
