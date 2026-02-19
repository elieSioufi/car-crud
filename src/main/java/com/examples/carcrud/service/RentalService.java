package com.examples.carcrud.service;

import com.examples.carcrud.dto.RentalRequestForm;
import com.examples.carcrud.model.*;
import com.examples.carcrud.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CarService carService;

    public RentalService(RentalRepository rentalRepository, CarService carService) {
        this.rentalRepository = rentalRepository;
        this.carService = carService;
    }

    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }

    public void validateRentalRequest(RentalRequestForm form) {
        if (form.getStartDate() == null || form.getEndDate() == null) {
            throw new IllegalArgumentException("Rental request is not valid");
        }
        if (!form.getEndDate().isAfter(form.getStartDate())) {
            throw new IllegalArgumentException("Rental request is not valid");
        }
        long days = ChronoUnit.DAYS.between(form.getStartDate(), form.getEndDate());
        if (days < 1 || days > 30) {
            throw new IllegalArgumentException("Rental request is not valid");
        }
        if (form.getCarId() == null) {
            throw new IllegalArgumentException("Rental request is not valid");
        }
        Car car = carService.findById(form.getCarId())
                .orElseThrow(() -> new IllegalArgumentException("Rental request is not valid"));
        if (car.isSold()) {
            throw new IllegalArgumentException("Rental request is not valid");
        }
    }

    public Rental createRental(RentalRequestForm form, AppUser user) {
        validateRentalRequest(form);

        Car car = carService.findById(form.getCarId()).orElseThrow();
        long daysBetween = ChronoUnit.DAYS.between(form.getStartDate(), form.getEndDate());

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setCar(car);
        rental.setStartDate(form.getStartDate());
        rental.setEndDate(form.getEndDate());
        rental.setDays((int) daysBetween);
        rental.setComment(form.getComment());
        rental.setStatus(RentalStatus.REQUESTED);

        return rentalRepository.save(rental);
    }

    public void updateStatus(Long id, RentalStatus status) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found: " + id));
        rental.setStatus(status);
        rentalRepository.save(rental);
    }
}
