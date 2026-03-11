package com.examples.carcrud.service;

import com.examples.carcrud.dto.CarReport;
import com.examples.carcrud.model.Car;
import com.examples.carcrud.model.CarType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CarReportBuilderTest {

    @Test
    void build_emptyReport() {
        CarReportBuilder builder = new CarReportBuilder();
        CarReport report = builder.build();

        assertEquals(0, report.getTotalCount());
        assertEquals(0, report.getAvailableCount());
        assertEquals(0, report.getSoldCount());
        assertEquals(BigDecimal.ZERO, report.getAveragePrice());
        assertEquals(BigDecimal.ZERO, report.getTotalValue());
    }

    @Test
    void build_withCars_calculatesCorrectly() {
        CarReportBuilder builder = new CarReportBuilder();
        builder.addCar(createCar(CarType.SEDAN, "10000", false));
        builder.addCar(createCar(CarType.SUV, "20000", false));
        builder.addCar(createCar(CarType.SEDAN, "15000", true));

        CarReport report = builder.build();

        assertEquals(3, report.getTotalCount());
        assertEquals(2, report.getAvailableCount());
        assertEquals(1, report.getSoldCount());
        assertEquals(new BigDecimal("15000.00"), report.getAveragePrice());
        assertEquals(new BigDecimal("10000"), report.getMinPrice());
        assertEquals(new BigDecimal("20000"), report.getMaxPrice());
        assertEquals(new BigDecimal("45000"), report.getTotalValue());
        assertEquals(2, report.getTypeBreakdown().get("SEDAN"));
        assertEquals(1, report.getTypeBreakdown().get("SUV"));
    }

    @Test
    void build_singleCar() {
        CarReportBuilder builder = new CarReportBuilder();
        builder.addCar(createCar(CarType.COUPE, "25000", false));

        CarReport report = builder.build();

        assertEquals(1, report.getTotalCount());
        assertEquals(1, report.getAvailableCount());
        assertEquals(new BigDecimal("25000.00"), report.getAveragePrice());
        assertEquals(new BigDecimal("25000"), report.getMinPrice());
        assertEquals(new BigDecimal("25000"), report.getMaxPrice());
    }

    private Car createCar(CarType type, String price, boolean sold) {
        Car car = new Car();
        car.setType(type);
        car.setPrice(new BigDecimal(price));
        car.setSold(sold);
        return car;
    }
}
