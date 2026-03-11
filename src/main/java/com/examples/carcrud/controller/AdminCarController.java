package com.examples.carcrud.controller;

import com.examples.carcrud.dto.CarForm;
import com.examples.carcrud.dto.CarReport;
import com.examples.carcrud.model.Car;
import com.examples.carcrud.model.CarType;
import com.examples.carcrud.service.CarReportBuilder;
import com.examples.carcrud.service.CarService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/admin/cars")
public class AdminCarController {

    private final CarService carService;
    private final ObjectProvider<CarReportBuilder> reportBuilderProvider;

    public AdminCarController(CarService carService,
                              ObjectProvider<CarReportBuilder> reportBuilderProvider) {
        this.carService = carService;
        this.reportBuilderProvider = reportBuilderProvider;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("cars", carService.findAll());
        return "admin/car-list";
    }

    @GetMapping("/new")
    public String newCar(Model model) {
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
    public String view(@PathVariable Long id, Model model) {
        Car car = carService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        model.addAttribute("car", car);
        return "admin/car-view";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Car car = carService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
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

    @GetMapping("/report")
    public String report(Model model) {
        CarReportBuilder builder = reportBuilderProvider.getObject();
        for (Car car : carService.findAll()) {
            builder.addCar(car);
        }
        CarReport report = builder.build();
        model.addAttribute("report", report);
        return "admin/car-report";
    }
}
