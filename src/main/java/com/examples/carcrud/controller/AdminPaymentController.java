package com.examples.carcrud.controller;

import com.examples.carcrud.dto.PaymentRecordForm;
import com.examples.carcrud.model.PaymentRecord;
import com.examples.carcrud.service.AppUserService;
import com.examples.carcrud.service.PaymentRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    private final PaymentRecordService paymentService;
    private final AppUserService appUserService;

    public AdminPaymentController(PaymentRecordService paymentService, AppUserService appUserService) {
        this.paymentService = paymentService;
        this.appUserService = appUserService;
    }

    @GetMapping
    public String list(Model model, Principal principal) {
        addUserInfo(model, principal);
        model.addAttribute("payments", paymentService.findAll());
        return "admin/payment-list";
    }

    @GetMapping("/new")
    public String newPayment(Model model, Principal principal) {
        addUserInfo(model, principal);
        model.addAttribute("paymentForm", new PaymentRecordForm());
        return "admin/payment-form";
    }

    @PostMapping
    public String create(@ModelAttribute PaymentRecordForm form) {
        paymentService.createFromForm(form);
        return "redirect:/admin/payments";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model, Principal principal) {
        addUserInfo(model, principal);
        PaymentRecord record = paymentService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment record not found"));
        model.addAttribute("payment", record);
        return "admin/payment-view";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model, Principal principal) {
        addUserInfo(model, principal);
        PaymentRecord record = paymentService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment record not found"));
        model.addAttribute("paymentForm", paymentService.toForm(record));
        return "admin/payment-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute PaymentRecordForm form) {
        paymentService.updateFromForm(id, form);
        return "redirect:/admin/payments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        paymentService.deleteById(id);
        return "redirect:/admin/payments";
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
