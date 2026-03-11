package com.examples.carcrud.controller;

import com.examples.carcrud.dto.RentalRequestForm;
import com.examples.carcrud.model.AppUser;
import com.examples.carcrud.model.OperationLog;
import com.examples.carcrud.repository.OperationLogRepository;
import com.examples.carcrud.service.AppUserService;
import com.examples.carcrud.service.PurchaseService;
import com.examples.carcrud.service.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OperationApiController {

    private final PurchaseService purchaseService;
    private final RentalService rentalService;
    private final AppUserService appUserService;
    private final OperationLogRepository operationLogRepository;

    public OperationApiController(PurchaseService purchaseService,
                                  RentalService rentalService,
                                  AppUserService appUserService,
                                  OperationLogRepository operationLogRepository) {
        this.purchaseService = purchaseService;
        this.rentalService = rentalService;
        this.appUserService = appUserService;
        this.operationLogRepository = operationLogRepository;
    }

    @PostMapping("/purchase/vehicle/{id}")
    public ResponseEntity<?> purchaseVehicle(@PathVariable Long id, Principal principal) {
        AppUser user = appUserService.findByUsername(principal.getName());
        try {
            purchaseService.buycar(id, user);
            return ResponseEntity.ok(Map.of("status", "APPROVED", "message", "Purchase completed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "REJECTED", "message", e.getMessage()));
        }
    }

    @PostMapping("/rental/vehicle/{id}")
    public ResponseEntity<?> rentVehicle(@PathVariable Long id,
                                         @RequestParam String startDate,
                                         @RequestParam String endDate,
                                         @RequestParam(required = false) String comment,
                                         Principal principal) {
        AppUser user = appUserService.findByUsername(principal.getName());
        RentalRequestForm form = new RentalRequestForm();
        form.setCarId(id);
        form.setStartDate(LocalDate.parse(startDate));
        form.setEndDate(LocalDate.parse(endDate));
        form.setComment(comment);

        try {
            rentalService.createRental(form, user);
            return ResponseEntity.ok(Map.of("status", "APPROVED", "message", "Rental request submitted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "REJECTED", "message", e.getMessage()));
        }
    }

    @GetMapping("/operations")
    public ResponseEntity<List<OperationLog>> listOperations() {
        return ResponseEntity.ok(operationLogRepository.findAll());
    }
}
