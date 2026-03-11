package com.examples.carcrud.service;

import com.examples.carcrud.dto.CreditResponse;
import com.examples.carcrud.model.*;
import com.examples.carcrud.repository.OperationLogRepository;
import com.examples.carcrud.repository.PaymentRecordRepository;
import com.examples.carcrud.repository.PurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final CarService carService;
    private final CreditRequestPublisher creditRequestPublisher;
    private final OperationLogRepository operationLogRepository;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           PaymentRecordRepository paymentRecordRepository,
                           CarService carService,
                           CreditRequestPublisher creditRequestPublisher,
                           OperationLogRepository operationLogRepository) {
        this.purchaseRepository = purchaseRepository;
        this.paymentRecordRepository = paymentRecordRepository;
        this.carService = carService;
        this.creditRequestPublisher = creditRequestPublisher;
        this.operationLogRepository = operationLogRepository;
    }

    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    @Transactional
    public Purchase buycar(Long carId, AppUser user) {
        Car car = carService.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        if (car.isSold()) {
            throw new RuntimeException("Car is already sold");
        }

        // Send credit verification request to the bank via RabbitMQ
        CreditResponse bankResponse = creditRequestPublisher.verifyCreditAndWait(
                user.getUsername(), car.getPrice(), "BUY");

        // Log the operation
        OperationLog log = new OperationLog();
        log.setUsername(user.getUsername());
        log.setCarId(carId);
        log.setCarDescription(car.getBrand() + " " + car.getModel());
        log.setOperationType("BUY");
        log.setAmount(car.getPrice());
        log.setApproved(bankResponse.isApproved());
        log.setBankMessage(bankResponse.getMessage());
        operationLogRepository.save(log);

        if (!bankResponse.isApproved()) {
            throw new RuntimeException("Bank rejected the purchase: " + bankResponse.getMessage());
        }

        car.setSold(true);
        carService.save(car);

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setCar(car);
        purchase.setPrice(car.getPrice());
        Purchase saved = purchaseRepository.save(purchase);

        // Auto-create a PaymentRecord linked to this purchase
        PaymentRecord payment = new PaymentRecord();
        payment.setCurrency("EUR");
        payment.setOwnerName(user.getFirstName() + " " + user.getLastName());
        payment.setIban("PURCHASE-" + saved.getId());
        payment.setAmount(car.getPrice());
        paymentRecordRepository.save(payment);

        return saved;
    }
}
