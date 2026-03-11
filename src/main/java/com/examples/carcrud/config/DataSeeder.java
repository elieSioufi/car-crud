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
        Object[][] carData = {
            {CarType.SEDAN, "Audi", "A6", "2023-03-15", 270, 5, true, "10000"},
            {CarType.SUV, "BMW", "X5", "2022-07-20", 250, 5, true, "15000"},
            {CarType.HATCHBACK, "Volkswagen", "Golf", "2021-01-10", 220, 5, false, "8000"},
            {CarType.COUPE, "Mercedes", "C-Class Coupe", "2023-11-05", 260, 4, true, "12000"},
            {CarType.CONVERTIBLE, "Porsche", "Boxster", "2024-02-28", 290, 2, true, "25000"},
            {CarType.SEDAN, "Toyota", "Camry", "2023-06-10", 210, 5, true, "9500"},
            {CarType.SUV, "Toyota", "RAV4", "2022-09-05", 200, 5, true, "13000"},
            {CarType.HATCHBACK, "Peugeot", "208", "2023-02-14", 210, 5, false, "7500"},
            {CarType.COUPE, "BMW", "M4", "2024-01-20", 290, 4, true, "32000"},
            {CarType.CONVERTIBLE, "Mazda", "MX-5", "2023-05-08", 240, 2, false, "14000"},
            {CarType.VAN, "Mercedes", "Vito", "2021-11-25", 180, 8, true, "18000"},
            {CarType.SEDAN, "Honda", "Civic", "2022-04-03", 220, 5, true, "8500"},
            {CarType.SUV, "Audi", "Q7", "2023-08-12", 250, 7, true, "28000"},
            {CarType.PICKUP, "Toyota", "Hilux", "2021-03-18", 175, 5, false, "11000"},
            {CarType.HATCHBACK, "Renault", "Clio", "2022-12-01", 195, 5, false, "6500"},
            {CarType.COUPE, "Audi", "TT", "2023-10-07", 265, 4, true, "19000"},
            {CarType.SUV, "Volvo", "XC90", "2022-05-22", 230, 7, true, "26000"},
            {CarType.SEDAN, "Mercedes", "E-Class", "2024-03-01", 260, 5, true, "22000"},
            {CarType.VAN, "Volkswagen", "Transporter", "2020-08-15", 170, 9, false, "16000"},
            {CarType.CONVERTIBLE, "BMW", "Z4", "2023-07-19", 280, 2, true, "27000"},
            {CarType.HATCHBACK, "Ford", "Fiesta", "2021-06-30", 200, 5, false, "5500"},
            {CarType.SEDAN, "Lexus", "IS 300", "2023-04-11", 240, 5, true, "20000"},
            {CarType.SUV, "Hyundai", "Tucson", "2022-10-28", 205, 5, true, "11500"},
            {CarType.PICKUP, "Chevrolet", "Silverado", "2021-09-14", 185, 5, true, "17000"},
            {CarType.COUPE, "Porsche", "Cayman", "2024-05-02", 295, 2, true, "35000"},
            {CarType.VAN, "Ford", "Transit", "2020-12-05", 160, 9, false, "14500"},
            {CarType.SEDAN, "Kia", "Stinger", "2023-01-25", 270, 5, true, "16500"},
            {CarType.HATCHBACK, "Mini", "Cooper S", "2022-08-09", 235, 4, true, "12500"},
            {CarType.SUV, "Porsche", "Cayenne", "2024-04-16", 270, 5, true, "38000"},
            {CarType.CONVERTIBLE, "Audi", "A5 Cabriolet", "2023-09-03", 250, 4, true, "24000"},
            {CarType.PICKUP, "Nissan", "Navara", "2021-07-21", 180, 5, false, "10500"},
            {CarType.SEDAN, "BMW", "Serie 5", "2024-02-10", 260, 5, true, "21000"},
            {CarType.VAN, "Renault", "Trafic", "2021-04-06", 165, 9, false, "13500"},
            {CarType.COUPE, "Lexus", "RC 350", "2023-12-18", 250, 4, true, "23000"},
            {CarType.HATCHBACK, "Hyundai", "i30 N", "2022-11-14", 250, 5, false, "15000"},
        };

        Car car1 = null;
        for (int i = 0; i < carData.length; i++) {
            Object[] d = carData[i];
            Car car = createCar((CarType) d[0], (String) d[1], (String) d[2],
                    LocalDate.parse((String) d[3]), (int) d[4], (int) d[5], (boolean) d[6],
                    new BigDecimal((String) d[7]));
            carRepo.save(car);
            if (i == 0) car1 = car;
        }

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

        // Purchase (sold car)
        Car soldCar = createCar(CarType.PICKUP, "Ford", "Ranger",
                LocalDate.of(2020, 5, 1), 180, 5, false, new BigDecimal("9500"));
        soldCar.setSold(true);
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

    private Car createCar(CarType type, String brand, String model, LocalDate purchaseDate,
                          int maxSpeed, int passengers, boolean automatic, BigDecimal price) {
        Car car = new Car();
        car.setType(type);
        car.setBrand(brand);
        car.setModel(model);
        car.setPurchaseDate(purchaseDate);
        car.setMaxSpeed(maxSpeed);
        car.setPassengers(passengers);
        car.setAutomaticTransmission(automatic);
        car.setSold(false);
        car.setPrice(price);
        return car;
    }
}
