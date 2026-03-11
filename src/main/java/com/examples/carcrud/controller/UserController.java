package com.examples.carcrud.controller;

import com.examples.carcrud.dto.BuyForm;
import com.examples.carcrud.dto.RentalRequestForm;
import com.examples.carcrud.model.AppUser;
import com.examples.carcrud.model.Car;
import com.examples.carcrud.model.SessionCart;
import com.examples.carcrud.service.AppUserService;
import com.examples.carcrud.service.CarService;
import com.examples.carcrud.service.PurchaseService;
import com.examples.carcrud.service.RentalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    private final CarService carService;
    private final RentalService rentalService;
    private final PurchaseService purchaseService;
    private final AppUserService appUserService;
    private final SessionCart sessionCart;

    public UserController(CarService carService, RentalService rentalService,
                          PurchaseService purchaseService, AppUserService appUserService,
                          SessionCart sessionCart) {
        this.carService = carService;
        this.rentalService = rentalService;
        this.purchaseService = purchaseService;
        this.appUserService = appUserService;
        this.sessionCart = sessionCart;
    }

    @ModelAttribute
    public void addCartCount(Model model) {
        model.addAttribute("cartCount", sessionCart.getCount());
    }

    @GetMapping("/main")
    public String main() {
        return "user/main";
    }

    @GetMapping("/rent")
    public String rentForm(Model model) {
        model.addAttribute("rentalForm", new RentalRequestForm());
        model.addAttribute("availableCars", carService.findAvailable());
        return "user/rent";
    }

    @PostMapping("/rent")
    public String rent(@ModelAttribute RentalRequestForm rentalForm, Model model, Principal principal) {
        AppUser user = appUserService.findByUsername(principal.getName());
        try {
            rentalService.createRental(rentalForm, user);
            model.addAttribute("success", "Rental request submitted successfully!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("rentalForm", rentalForm);
            model.addAttribute("availableCars", carService.findAvailable());
            model.addAttribute("error", "Rental request is not valid");
            return "user/rent";
        }
        model.addAttribute("rentalForm", new RentalRequestForm());
        model.addAttribute("availableCars", carService.findAvailable());
        return "user/rent";
    }

    @GetMapping("/buy")
    public String buyForm(Model model) {
        model.addAttribute("buyForm", new BuyForm());
        model.addAttribute("availableCars", carService.findAvailable());
        return "user/buy";
    }

    @PostMapping("/buy")
    public String buy(@ModelAttribute BuyForm buyForm, Model model, Principal principal) {
        AppUser user = appUserService.findByUsername(principal.getName());
        try {
            purchaseService.buycar(buyForm.getCarId(), user);
            sessionCart.removeCar(buyForm.getCarId());
            model.addAttribute("success", "Car purchased successfully!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("buyForm", new BuyForm());
        model.addAttribute("availableCars", carService.findAvailable());
        return "user/buy";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long carId) {
        sessionCart.addCar(carId);
        return "redirect:/buy";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long carId) {
        sessionCart.removeCar(carId);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        List<Car> cartCars = getCartCars();
        model.addAttribute("cartCars", cartCars);
        model.addAttribute("cartTotal", calcTotal(cartCars));
        return "user/cart";
    }

    @PostMapping("/cart/buy")
    public String buyAllFromCart(Model model, Principal principal) {
        AppUser user = appUserService.findByUsername(principal.getName());
        int bought = 0;
        for (Long carId : List.copyOf(sessionCart.getCarIds())) {
            try {
                purchaseService.buycar(carId, user);
                sessionCart.removeCar(carId);
                bought++;
            } catch (RuntimeException ignored) {
                sessionCart.removeCar(carId);
            }
        }
        model.addAttribute("success", bought + " car(s) purchased successfully!");
        List<Car> cartCars = getCartCars();
        model.addAttribute("cartCars", cartCars);
        model.addAttribute("cartTotal", calcTotal(cartCars));
        return "user/cart";
    }

    private List<Car> getCartCars() {
        return sessionCart.getCarIds().stream()
                .map(carService::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .filter(car -> !car.isSold())
                .toList();
    }

    private BigDecimal calcTotal(List<Car> cars) {
        return cars.stream()
                .map(Car::getPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
