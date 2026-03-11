package com.examples.carcrud.service;

import com.examples.carcrud.dto.CreditResponse;
import com.examples.carcrud.dto.RentalRequestForm;
import com.examples.carcrud.model.*;
import com.examples.carcrud.repository.OperationLogRepository;
import com.examples.carcrud.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CarService carService;
    private final CreditRequestPublisher creditRequestPublisher;
    private final OperationLogRepository operationLogRepository;

    public RentalService(RentalRepository rentalRepository, CarService carService,
                         CreditRequestPublisher creditRequestPublisher,
                         OperationLogRepository operationLogRepository) {
        this.rentalRepository = rentalRepository;
        this.carService = carService;
        this.creditRequestPublisher = creditRequestPublisher;
        this.operationLogRepository = operationLogRepository;
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

        // Calculate rental cost: 1% of car price per day
        BigDecimal rentalCost = car.getPrice()
                .multiply(BigDecimal.valueOf(daysBetween))
                .divide(BigDecimal.valueOf(100));

        // Send credit verification request to the bank via RabbitMQ
        CreditResponse bankResponse = creditRequestPublisher.verifyCreditAndWait(
                user.getUsername(), rentalCost, "RENT");

        // Log the operation
        OperationLog log = new OperationLog();
        log.setUsername(user.getUsername());
        log.setCarId(form.getCarId());
        log.setCarDescription(car.getBrand() + " " + car.getModel());
        log.setOperationType("RENT");
        log.setAmount(rentalCost);
        log.setApproved(bankResponse.isApproved());
        log.setBankMessage(bankResponse.getMessage());
        operationLogRepository.save(log);

        if (!bankResponse.isApproved()) {
            throw new RuntimeException("Bank rejected the rental: " + bankResponse.getMessage());
        }

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
