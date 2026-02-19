package com.examples.carcrud.config;

import com.examples.carcrud.model.*;
import com.examples.carcrud.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepo;
    private final CarRepository carRepo;
    private final RentalRepository rentalRepo;
    private final PurchaseRepository purchaseRepo;
    private final PaymentRecordRepository paymentRepo;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AppUserRepository userRepo, CarRepository carRepo,
                      RentalRepository rentalRepo, PurchaseRepository purchaseRepo,
                      PaymentRecordRepository paymentRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.carRepo = carRepo;
        this.rentalRepo = rentalRepo;
        this.purchaseRepo = purchaseRepo;
        this.paymentRepo = paymentRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) {
            return;
        }

        // Users
        AppUser admin = new AppUser("admin", passwordEncoder.encode("adminpass"), "ADMIN", "Admin", "System");
        AppUser elie = new AppUser("elie", passwordEncoder.encode("eliepass"), "USER", "Elie", "Sioufi");
        AppUser john = new AppUser("john", passwordEncoder.encode("johnpass"), "USER", "John", "Doe");
        userRepo.save(admin);
        userRepo.save(elie);
        userRepo.save(john);

        // Cars
        Car car1 = new Car();
        car1.setType(CarType.SEDAN);
        car1.setBrand("Audi");
        car1.setModel("A6");
        car1.setPurchaseDate(LocalDate.of(2023, 3, 15));
        car1.setMaxSpeed(270);
        car1.setPassengers(5);
        car1.setAutomaticTransmission(true);
        car1.setSold(false);
        car1.setPrice(new BigDecimal("10000"));
        carRepo.save(car1);

        Car car2 = new Car();
        car2.setType(CarType.SUV);
        car2.setBrand("BMW");
        car2.setModel("X5");
        car2.setPurchaseDate(LocalDate.of(2022, 7, 20));
        car2.setMaxSpeed(250);
        car2.setPassengers(5);
        car2.setAutomaticTransmission(true);
        car2.setSold(false);
        car2.setPrice(new BigDecimal("15000"));
        carRepo.save(car2);

        Car car3 = new Car();
        car3.setType(CarType.HATCHBACK);
        car3.setBrand("Volkswagen");
        car3.setModel("Golf");
        car3.setPurchaseDate(LocalDate.of(2021, 1, 10));
        car3.setMaxSpeed(220);
        car3.setPassengers(5);
        car3.setAutomaticTransmission(false);
        car3.setSold(false);
        car3.setPrice(new BigDecimal("8000"));
        carRepo.save(car3);

        Car car4 = new Car();
        car4.setType(CarType.COUPE);
        car4.setBrand("Mercedes");
        car4.setModel("C-Class Coupe");
        car4.setPurchaseDate(LocalDate.of(2023, 11, 5));
        car4.setMaxSpeed(260);
        car4.setPassengers(4);
        car4.setAutomaticTransmission(true);
        car4.setSold(false);
        car4.setPrice(new BigDecimal("12000"));
        carRepo.save(car4);

        Car car5 = new Car();
        car5.setType(CarType.CONVERTIBLE);
        car5.setBrand("Porsche");
        car5.setModel("Boxster");
        car5.setPurchaseDate(LocalDate.of(2024, 2, 28));
        car5.setMaxSpeed(290);
        car5.setPassengers(2);
        car5.setAutomaticTransmission(true);
        car5.setSold(false);
        car5.setPrice(new BigDecimal("25000"));
        carRepo.save(car5);

        // Rental
        Rental rental = new Rental();
        rental.setUser(elie);
        rental.setCar(car1);
        rental.setStartDate(LocalDate.of(2025, 1, 10));
        rental.setEndDate(LocalDate.of(2025, 1, 15));
        rental.setDays(5);
        rental.setComment("Business trip");
        rental.setStatus(RentalStatus.APPROVED);
        rental.setCreatedAt(LocalDateTime.of(2025, 1, 8, 10, 30));
        rentalRepo.save(rental);

        // Purchase
        Car soldCar = new Car();
        soldCar.setType(CarType.PICKUP);
        soldCar.setBrand("Ford");
        soldCar.setModel("Ranger");
        soldCar.setPurchaseDate(LocalDate.of(2020, 5, 1));
        soldCar.setMaxSpeed(180);
        soldCar.setPassengers(5);
        soldCar.setAutomaticTransmission(false);
        soldCar.setSold(true);
        soldCar.setPrice(new BigDecimal("9500"));
        carRepo.save(soldCar);

        Purchase purchase = new Purchase();
        purchase.setUser(john);
        purchase.setCar(soldCar);
        purchase.setPrice(soldCar.getPrice());
        purchase.setPurchaseDate(LocalDateTime.of(2025, 1, 20, 14, 0));
        purchaseRepo.save(purchase);

        // Payment Records
        paymentRepo.save(new PaymentRecord("EUR", "FR76 3000 6000 0112 3456 7890 189", "John Doe", new BigDecimal("1500")));
        paymentRepo.save(new PaymentRecord("EUR", "DE89 3704 0044 0532 0130 00", "Elie Sioufi", new BigDecimal("2500")));
        paymentRepo.save(new PaymentRecord("EUR", "IT60 X054 2811 1010 0000 0123 456", "Elie", new BigDecimal("800")));
    }
}
