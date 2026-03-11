package com.examples.carcrud.service;

import com.examples.carcrud.dto.CreditResponse;
import com.examples.carcrud.model.*;
import com.examples.carcrud.repository.OperationLogRepository;
import com.examples.carcrud.repository.PaymentRecordRepository;
import com.examples.carcrud.repository.PurchaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock private PurchaseRepository purchaseRepository;
    @Mock private PaymentRecordRepository paymentRecordRepository;
    @Mock private CarService carService;
    @Mock private CreditRequestPublisher creditRequestPublisher;
    @Mock private OperationLogRepository operationLogRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void buycar_approved_marksSoldAndCreatesRecords() {
        Car car = createAvailableCar();
        AppUser user = createTestUser();

        when(carService.findById(1L)).thenReturn(Optional.of(car));
        when(creditRequestPublisher.verifyCreditAndWait(eq("john"), any(BigDecimal.class), eq("BUY")))
                .thenReturn(new CreditResponse("corr-1", "john", true, "Approved"));
        when(operationLogRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(carService.save(any(Car.class))).thenAnswer(i -> i.getArgument(0));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(i -> {
            Purchase p = i.getArgument(0);
            p.setId(10L);
            return p;
        });
        when(paymentRecordRepository.save(any(PaymentRecord.class))).thenAnswer(i -> i.getArgument(0));

        Purchase result = purchaseService.buycar(1L, user);

        assertNotNull(result);
        assertTrue(car.isSold());
        assertEquals(new BigDecimal("15000"), result.getPrice());
        verify(carService).save(car);
        verify(purchaseRepository).save(any(Purchase.class));
        verify(paymentRecordRepository).save(any(PaymentRecord.class));
        verify(operationLogRepository).save(any(OperationLog.class));
    }

    @Test
    void buycar_rejected_throwsAndDoesNotMarkSold() {
        Car car = createAvailableCar();
        AppUser user = createTestUser();

        when(carService.findById(1L)).thenReturn(Optional.of(car));
        when(creditRequestPublisher.verifyCreditAndWait(eq("john"), any(BigDecimal.class), eq("BUY")))
                .thenReturn(new CreditResponse("corr-1", "john", false, "Insufficient credit"));
        when(operationLogRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThrows(RuntimeException.class, () -> purchaseService.buycar(1L, user));
        assertFalse(car.isSold());
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void buycar_alreadySold_throws() {
        Car car = createAvailableCar();
        car.setSold(true);
        AppUser user = createTestUser();

        when(carService.findById(1L)).thenReturn(Optional.of(car));

        assertThrows(RuntimeException.class, () -> purchaseService.buycar(1L, user));
    }

    @Test
    void buycar_carNotFound_throws() {
        AppUser user = createTestUser();
        when(carService.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> purchaseService.buycar(99L, user));
    }

    private Car createAvailableCar() {
        Car car = new Car();
        car.setId(1L);
        car.setBrand("BMW");
        car.setModel("X5");
        car.setPrice(new BigDecimal("15000"));
        car.setSold(false);
        return car;
    }

    private AppUser createTestUser() {
        AppUser user = new AppUser("john", "encoded", "USER", "John", "Doe");
        user.setId(2L);
        return user;
    }
}
