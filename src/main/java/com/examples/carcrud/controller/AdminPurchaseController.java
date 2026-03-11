package com.examples.carcrud.controller;

import com.examples.carcrud.service.PurchaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/purchases")
public class AdminPurchaseController {

    private final PurchaseService purchaseService;

    public AdminPurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("purchases", purchaseService.findAll());
        return "admin/purchase-list";
    }
}
