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

        // Additional cars
        Car c7 = new Car(); c7.setType(CarType.SEDAN); c7.setBrand("Toyota"); c7.setModel("Camry"); c7.setPurchaseDate(LocalDate.of(2023, 6, 10)); c7.setMaxSpeed(210); c7.setPassengers(5); c7.setAutomaticTransmission(true); c7.setSold(false); c7.setPrice(new BigDecimal("9500")); carRepo.save(c7);
        Car c8 = new Car(); c8.setType(CarType.SUV); c8.setBrand("Toyota"); c8.setModel("RAV4"); c8.setPurchaseDate(LocalDate.of(2022, 9, 5)); c8.setMaxSpeed(200); c8.setPassengers(5); c8.setAutomaticTransmission(true); c8.setSold(false); c8.setPrice(new BigDecimal("13000")); carRepo.save(c8);
        Car c9 = new Car(); c9.setType(CarType.HATCHBACK); c9.setBrand("Peugeot"); c9.setModel("208"); c9.setPurchaseDate(LocalDate.of(2023, 2, 14)); c9.setMaxSpeed(210); c9.setPassengers(5); c9.setAutomaticTransmission(false); c9.setSold(false); c9.setPrice(new BigDecimal("7500")); carRepo.save(c9);
        Car c10 = new Car(); c10.setType(CarType.COUPE); c10.setBrand("BMW"); c10.setModel("M4"); c10.setPurchaseDate(LocalDate.of(2024, 1, 20)); c10.setMaxSpeed(290); c10.setPassengers(4); c10.setAutomaticTransmission(true); c10.setSold(false); c10.setPrice(new BigDecimal("32000")); carRepo.save(c10);
        Car c11 = new Car(); c11.setType(CarType.CONVERTIBLE); c11.setBrand("Mazda"); c11.setModel("MX-5"); c11.setPurchaseDate(LocalDate.of(2023, 5, 8)); c11.setMaxSpeed(240); c11.setPassengers(2); c11.setAutomaticTransmission(false); c11.setSold(false); c11.setPrice(new BigDecimal("14000")); carRepo.save(c11);
        Car c12 = new Car(); c12.setType(CarType.VAN); c12.setBrand("Mercedes"); c12.setModel("Vito"); c12.setPurchaseDate(LocalDate.of(2021, 11, 25)); c12.setMaxSpeed(180); c12.setPassengers(8); c12.setAutomaticTransmission(true); c12.setSold(false); c12.setPrice(new BigDecimal("18000")); carRepo.save(c12);
        Car c13 = new Car(); c13.setType(CarType.SEDAN); c13.setBrand("Honda"); c13.setModel("Civic"); c13.setPurchaseDate(LocalDate.of(2022, 4, 3)); c13.setMaxSpeed(220); c13.setPassengers(5); c13.setAutomaticTransmission(true); c13.setSold(false); c13.setPrice(new BigDecimal("8500")); carRepo.save(c13);
        Car c14 = new Car(); c14.setType(CarType.SUV); c14.setBrand("Audi"); c14.setModel("Q7"); c14.setPurchaseDate(LocalDate.of(2023, 8, 12)); c14.setMaxSpeed(250); c14.setPassengers(7); c14.setAutomaticTransmission(true); c14.setSold(false); c14.setPrice(new BigDecimal("28000")); carRepo.save(c14);
        Car c15 = new Car(); c15.setType(CarType.PICKUP); c15.setBrand("Toyota"); c15.setModel("Hilux"); c15.setPurchaseDate(LocalDate.of(2021, 3, 18)); c15.setMaxSpeed(175); c15.setPassengers(5); c15.setAutomaticTransmission(false); c15.setSold(false); c15.setPrice(new BigDecimal("11000")); carRepo.save(c15);
        Car c16 = new Car(); c16.setType(CarType.HATCHBACK); c16.setBrand("Renault"); c16.setModel("Clio"); c16.setPurchaseDate(LocalDate.of(2022, 12, 1)); c16.setMaxSpeed(195); c16.setPassengers(5); c16.setAutomaticTransmission(false); c16.setSold(false); c16.setPrice(new BigDecimal("6500")); carRepo.save(c16);
        Car c17 = new Car(); c17.setType(CarType.COUPE); c17.setBrand("Audi"); c17.setModel("TT"); c17.setPurchaseDate(LocalDate.of(2023, 10, 7)); c17.setMaxSpeed(265); c17.setPassengers(4); c17.setAutomaticTransmission(true); c17.setSold(false); c17.setPrice(new BigDecimal("19000")); carRepo.save(c17);
        Car c18 = new Car(); c18.setType(CarType.SUV); c18.setBrand("Volvo"); c18.setModel("XC90"); c18.setPurchaseDate(LocalDate.of(2022, 5, 22)); c18.setMaxSpeed(230); c18.setPassengers(7); c18.setAutomaticTransmission(true); c18.setSold(false); c18.setPrice(new BigDecimal("26000")); carRepo.save(c18);
        Car c19 = new Car(); c19.setType(CarType.SEDAN); c19.setBrand("Mercedes"); c19.setModel("E-Class"); c19.setPurchaseDate(LocalDate.of(2024, 3, 1)); c19.setMaxSpeed(260); c19.setPassengers(5); c19.setAutomaticTransmission(true); c19.setSold(false); c19.setPrice(new BigDecimal("22000")); carRepo.save(c19);
        Car c20 = new Car(); c20.setType(CarType.VAN); c20.setBrand("Volkswagen"); c20.setModel("Transporter"); c20.setPurchaseDate(LocalDate.of(2020, 8, 15)); c20.setMaxSpeed(170); c20.setPassengers(9); c20.setAutomaticTransmission(false); c20.setSold(false); c20.setPrice(new BigDecimal("16000")); carRepo.save(c20);
        Car c21 = new Car(); c21.setType(CarType.CONVERTIBLE); c21.setBrand("BMW"); c21.setModel("Z4"); c21.setPurchaseDate(LocalDate.of(2023, 7, 19)); c21.setMaxSpeed(280); c21.setPassengers(2); c21.setAutomaticTransmission(true); c21.setSold(false); c21.setPrice(new BigDecimal("27000")); carRepo.save(c21);
        Car c22 = new Car(); c22.setType(CarType.HATCHBACK); c22.setBrand("Ford"); c22.setModel("Fiesta"); c22.setPurchaseDate(LocalDate.of(2021, 6, 30)); c22.setMaxSpeed(200); c22.setPassengers(5); c22.setAutomaticTransmission(false); c22.setSold(false); c22.setPrice(new BigDecimal("5500")); carRepo.save(c22);
        Car c23 = new Car(); c23.setType(CarType.SEDAN); c23.setBrand("Lexus"); c23.setModel("IS 300"); c23.setPurchaseDate(LocalDate.of(2023, 4, 11)); c23.setMaxSpeed(240); c23.setPassengers(5); c23.setAutomaticTransmission(true); c23.setSold(false); c23.setPrice(new BigDecimal("20000")); carRepo.save(c23);
        Car c24 = new Car(); c24.setType(CarType.SUV); c24.setBrand("Hyundai"); c24.setModel("Tucson"); c24.setPurchaseDate(LocalDate.of(2022, 10, 28)); c24.setMaxSpeed(205); c24.setPassengers(5); c24.setAutomaticTransmission(true); c24.setSold(false); c24.setPrice(new BigDecimal("11500")); carRepo.save(c24);
        Car c25 = new Car(); c25.setType(CarType.PICKUP); c25.setBrand("Chevrolet"); c25.setModel("Silverado"); c25.setPurchaseDate(LocalDate.of(2021, 9, 14)); c25.setMaxSpeed(185); c25.setPassengers(5); c25.setAutomaticTransmission(true); c25.setSold(false); c25.setPrice(new BigDecimal("17000")); carRepo.save(c25);
        Car c26 = new Car(); c26.setType(CarType.COUPE); c26.setBrand("Porsche"); c26.setModel("Cayman"); c26.setPurchaseDate(LocalDate.of(2024, 5, 2)); c26.setMaxSpeed(295); c26.setPassengers(2); c26.setAutomaticTransmission(true); c26.setSold(false); c26.setPrice(new BigDecimal("35000")); carRepo.save(c26);
        Car c27 = new Car(); c27.setType(CarType.VAN); c27.setBrand("Ford"); c27.setModel("Transit"); c27.setPurchaseDate(LocalDate.of(2020, 12, 5)); c27.setMaxSpeed(160); c27.setPassengers(9); c27.setAutomaticTransmission(false); c27.setSold(false); c27.setPrice(new BigDecimal("14500")); carRepo.save(c27);
        Car c28 = new Car(); c28.setType(CarType.SEDAN); c28.setBrand("Kia"); c28.setModel("Stinger"); c28.setPurchaseDate(LocalDate.of(2023, 1, 25)); c28.setMaxSpeed(270); c28.setPassengers(5); c28.setAutomaticTransmission(true); c28.setSold(false); c28.setPrice(new BigDecimal("16500")); carRepo.save(c28);
        Car c29 = new Car(); c29.setType(CarType.HATCHBACK); c29.setBrand("Mini"); c29.setModel("Cooper S"); c29.setPurchaseDate(LocalDate.of(2022, 8, 9)); c29.setMaxSpeed(235); c29.setPassengers(4); c29.setAutomaticTransmission(true); c29.setSold(false); c29.setPrice(new BigDecimal("12500")); carRepo.save(c29);
        Car c30 = new Car(); c30.setType(CarType.SUV); c30.setBrand("Porsche"); c30.setModel("Cayenne"); c30.setPurchaseDate(LocalDate.of(2024, 4, 16)); c30.setMaxSpeed(270); c30.setPassengers(5); c30.setAutomaticTransmission(true); c30.setSold(false); c30.setPrice(new BigDecimal("38000")); carRepo.save(c30);
        Car c31 = new Car(); c31.setType(CarType.CONVERTIBLE); c31.setBrand("Audi"); c31.setModel("A5 Cabriolet"); c31.setPurchaseDate(LocalDate.of(2023, 9, 3)); c31.setMaxSpeed(250); c31.setPassengers(4); c31.setAutomaticTransmission(true); c31.setSold(false); c31.setPrice(new BigDecimal("24000")); carRepo.save(c31);
        Car c32 = new Car(); c32.setType(CarType.PICKUP); c32.setBrand("Nissan"); c32.setModel("Navara"); c32.setPurchaseDate(LocalDate.of(2021, 7, 21)); c32.setMaxSpeed(180); c32.setPassengers(5); c32.setAutomaticTransmission(false); c32.setSold(false); c32.setPrice(new BigDecimal("10500")); carRepo.save(c32);
        Car c33 = new Car(); c33.setType(CarType.SEDAN); c33.setBrand("BMW"); c33.setModel("Serie 5"); c33.setPurchaseDate(LocalDate.of(2024, 2, 10)); c33.setMaxSpeed(260); c33.setPassengers(5); c33.setAutomaticTransmission(true); c33.setSold(false); c33.setPrice(new BigDecimal("21000")); carRepo.save(c33);
        Car c34 = new Car(); c34.setType(CarType.VAN); c34.setBrand("Renault"); c34.setModel("Trafic"); c34.setPurchaseDate(LocalDate.of(2021, 4, 6)); c34.setMaxSpeed(165); c34.setPassengers(9); c34.setAutomaticTransmission(false); c34.setSold(false); c34.setPrice(new BigDecimal("13500")); carRepo.save(c34);
        Car c35 = new Car(); c35.setType(CarType.COUPE); c35.setBrand("Lexus"); c35.setModel("RC 350"); c35.setPurchaseDate(LocalDate.of(2023, 12, 18)); c35.setMaxSpeed(250); c35.setPassengers(4); c35.setAutomaticTransmission(true); c35.setSold(false); c35.setPrice(new BigDecimal("23000")); carRepo.save(c35);
        Car c36 = new Car(); c36.setType(CarType.HATCHBACK); c36.setBrand("Hyundai"); c36.setModel("i30 N"); c36.setPurchaseDate(LocalDate.of(2022, 11, 14)); c36.setMaxSpeed(250); c36.setPassengers(5); c36.setAutomaticTransmission(false); c36.setSold(false); c36.setPrice(new BigDecimal("15000")); carRepo.save(c36);

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
