package com.examples.carcrud.controller;

import com.examples.carcrud.service.AppUserService;
import com.examples.carcrud.service.PurchaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/admin/purchases")
public class AdminPurchaseController {

    private final PurchaseService purchaseService;
    private final AppUserService appUserService;

    public AdminPurchaseController(PurchaseService purchaseService, AppUserService appUserService) {
        this.purchaseService = purchaseService;
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
        model.addAttribute("purchases", purchaseService.findAll());
        return "admin/purchase-list";
    }
}
