package com.examples.carcrud.service;

import com.examples.carcrud.dto.CarReport;
import com.examples.carcrud.model.Car;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Prototype-scoped bean — a NEW instance is created every time
 * this bean is requested from the Spring container.
 *
 * This builder accumulates car data internally (counts, prices, types),
 * so it MUST be prototype-scoped. If it were singleton, data from a
 * previous report would leak into the next one.
 *
 * Usage: get a fresh instance via ObjectProvider, call addCar() for
 * each car, then call build() to get the final report.
 */
@Component
@Scope("prototype")
public class CarReportBuilder {

    private final List<Car> cars = new ArrayList<>();

    public void addCar(Car car) {
        cars.add(car);
    }

    public CarReport build() {
        CarReport report = new CarReport();

        report.setTotalCount(cars.size());
        report.setAvailableCount((int) cars.stream().filter(c -> !c.isSold()).count());
        report.setSoldCount((int) cars.stream().filter(Car::isSold).count());

        if (!cars.isEmpty()) {
            BigDecimal totalPrice = cars.stream()
                    .map(Car::getPrice)
                    .filter(p -> p != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long priceCount = cars.stream().filter(c -> c.getPrice() != null).count();
            if (priceCount > 0) {
                report.setAveragePrice(totalPrice.divide(BigDecimal.valueOf(priceCount), 2, RoundingMode.HALF_UP));
            } else {
                report.setAveragePrice(BigDecimal.ZERO);
            }

            report.setMinPrice(cars.stream()
                    .map(Car::getPrice)
                    .filter(p -> p != null)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO));

            report.setMaxPrice(cars.stream()
                    .map(Car::getPrice)
                    .filter(p -> p != null)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO));

            report.setTotalValue(totalPrice);
        } else {
            report.setAveragePrice(BigDecimal.ZERO);
            report.setMinPrice(BigDecimal.ZERO);
            report.setMaxPrice(BigDecimal.ZERO);
            report.setTotalValue(BigDecimal.ZERO);
        }

        Map<String, Integer> typeBreakdown = new LinkedHashMap<>();
        for (Car car : cars) {
            String type = car.getType() != null ? car.getType().name() : "UNKNOWN";
            typeBreakdown.merge(type, 1, Integer::sum);
        }
        report.setTypeBreakdown(typeBreakdown);

        return report;
    }
}
