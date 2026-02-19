package com.examples.carcrud.service;

import com.examples.carcrud.model.AppUser;
import com.examples.carcrud.model.Car;
import com.examples.carcrud.model.PaymentRecord;
import com.examples.carcrud.model.Purchase;
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

    public PurchaseService(PurchaseRepository purchaseRepository,
                           PaymentRecordRepository paymentRecordRepository,
                           CarService carService) {
        this.purchaseRepository = purchaseRepository;
        this.paymentRecordRepository = paymentRecordRepository;
        this.carService = carService;
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
