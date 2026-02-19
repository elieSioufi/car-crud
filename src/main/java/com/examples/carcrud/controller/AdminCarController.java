package com.examples.carcrud.controller;

import com.examples.carcrud.dto.CarForm;
import com.examples.carcrud.dto.CarReport;
import com.examples.carcrud.model.Car;
import com.examples.carcrud.model.CarType;
import com.examples.carcrud.service.AppUserService;
import com.examples.carcrud.service.CarReportBuilder;
import com.examples.carcrud.service.CarService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequestMapping("/admin/cars")
public class AdminCarController {

    private final CarService carService;
    private final AppUserService appUserService;
    private final ObjectProvider<CarReportBuilder> reportBuilderProvider; // Prototype scope — fresh instance each call

    public AdminCarController(CarService carService, AppUserService appUserService,
                              ObjectProvider<CarReportBuilder> reportBuilderProvider) {
        this.carService = carService;
        this.appUserService = appUserService;
        this.reportBuilderProvider = reportBuilderProvider;
    }

    @GetMapping
    public String list(Model model, Principal principal) {
        addUserInfo(model, principal);
        model.addAttribute("cars", carService.findAll());
        return "admin/car-list";
    }

    @GetMapping("/new")
    public String newCar(Model model, Principal principal) {
        addUserInfo(model, principal);
        model.addAttribute("carForm", new CarForm());
        model.addAttribute("carTypes", CarType.values());
        return "admin/car-form";
    }

    @PostMapping
    public String create(@ModelAttribute CarForm carForm) {
        carService.createFromForm(carForm);
        return "redirect:/admin/cars";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model, Principal principal) {
        addUserInfo(model, principal);
        Car car = carService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        model.addAttribute("car", car);
        return "admin/car-view";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model, Principal principal) {
        addUserInfo(model, principal);
        Car car = carService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        model.addAttribute("carForm", carService.toForm(car));
        model.addAttribute("carTypes", CarType.values());
        return "admin/car-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute CarForm carForm) {
        carService.updateFromForm(id, carForm);
        return "redirect:/admin/cars";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        carService.deleteById(id);
        return "redirect:/admin/cars";
    }

    /**
     * Generates a car inventory report using the PROTOTYPE-scoped CarReportBuilder.
     * Each call to reportBuilderProvider.getObject() creates a brand-new instance,
     * ensuring no stale data from previous reports.
     */
    @GetMapping("/report")
    public String report(Model model, Principal principal) {
        addUserInfo(model, principal);

        // Get a FRESH prototype instance
        CarReportBuilder builder = reportBuilderProvider.getObject();

        // Feed all cars into the builder
        for (Car car : carService.findAll()) {
            builder.addCar(car);
        }

        // Build the report
        CarReport report = builder.build();
        model.addAttribute("report", report);
        return "admin/car-report";
    }

    private void addUserInfo(Model model, Principal principal) {
        if (principal != null) {
            var user = appUserService.findByUsername(principal.getName());
            if (user != null) {
                model.addAttribute("currentUser", user);
            }
        }
    }
}
