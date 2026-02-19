package com.examples.carcrud.controller;

import com.examples.carcrud.model.RentalStatus;
import com.examples.carcrud.service.AppUserService;
import com.examples.carcrud.service.RentalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/admin/rentals")
public class AdminRentalController {

    private final RentalService rentalService;
    private final AppUserService appUserService;

    public AdminRentalController(RentalService rentalService, AppUserService appUserService) {
        this.rentalService = rentalService;
        this.appUserService = appUserService;
    }

    @GetMapping
    public String list(Model model, Principal principal) {
        if (principal != null) {
            var user = appUserService.findByUsername(principal.getName());
            if (user != null) {
                model.addAttribute("currentUser", user);
            }
        }
        model.addAttribute("rentals", rentalService.findAll());
        return "admin/rental-list";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id) {
        rentalService.updateStatus(id, RentalStatus.APPROVED);
        return "redirect:/admin/rentals";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id) {
        rentalService.updateStatus(id, RentalStatus.REJECTED);
        return "redirect:/admin/rentals";
    }
}
