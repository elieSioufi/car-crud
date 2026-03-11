package com.examples.carcrud.service;

import com.examples.carcrud.dto.CreditResponse;
import com.examples.carcrud.dto.RentalRequestForm;
import com.examples.carcrud.model.*;
import com.examples.carcrud.repository.OperationLogRepository;
import com.examples.carcrud.repository.RentalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock private RentalRepository rentalRepository;
    @Mock private CarService carService;
    @Mock private CreditRequestPublisher creditRequestPublisher;
    @Mock private OperationLogRepository operationLogRepository;

    @InjectMocks
    private RentalService rentalService;

    @Test
    void validateRentalRequest_validRequest_doesNotThrow() {
        RentalRequestForm form = validRentalForm();
        Car car = createAvailableCar();
        when(carService.findById(1L)).thenReturn(Optional.of(car));

        assertDoesNotThrow(() -> rentalService.validateRentalRequest(form));
    }

    @Test
    void validateRentalRequest_nullDates_throws() {
        RentalRequestForm form = new RentalRequestForm();
        form.setCarId(1L);

        assertThrows(IllegalArgumentException.class,
                () -> rentalService.validateRentalRequest(form));
    }

    @Test
    void validateRentalRequest_endBeforeStart_throws() {
        RentalRequestForm form = new RentalRequestForm();
        form.setCarId(1L);
        form.setStartDate(LocalDate.of(2025, 6, 15));
        form.setEndDate(LocalDate.of(2025, 6, 10));

        assertThrows(IllegalArgumentException.class,
                () -> rentalService.validateRentalRequest(form));
    }

    @Test
    void validateRentalRequest_tooManyDays_throws() {
        RentalRequestForm form = new RentalRequestForm();
        form.setCarId(1L);
        form.setStartDate(LocalDate.of(2025, 1, 1));
        form.setEndDate(LocalDate.of(2025, 3, 1));

        assertThrows(IllegalArgumentException.class,
                () -> rentalService.validateRentalRequest(form));
    }

    @Test
    void validateRentalRequest_soldCar_throws() {
        RentalRequestForm form = validRentalForm();
        Car car = createAvailableCar();
        car.setSold(true);
        when(carService.findById(1L)).thenReturn(Optional.of(car));

        assertThrows(IllegalArgumentException.class,
                () -> rentalService.validateRentalRequest(form));
    }

    @Test
    void createRental_approved_createsRental() {
        RentalRequestForm form = validRentalForm();
        Car car = createAvailableCar();
        AppUser user = createTestUser();

        when(carService.findById(1L)).thenReturn(Optional.of(car));
        when(creditRequestPublisher.verifyCreditAndWait(eq("elie"), any(BigDecimal.class), eq("RENT")))
                .thenReturn(new CreditResponse("corr-1", "elie", true, "Approved"));
        when(operationLogRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(i -> i.getArgument(0));

        Rental result = rentalService.createRental(form, user);

        assertNotNull(result);
        assertEquals(RentalStatus.REQUESTED, result.getStatus());
        assertEquals(5, result.getDays());
        verify(rentalRepository).save(any(Rental.class));
        verify(operationLogRepository).save(any(OperationLog.class));
    }

    @Test
    void createRental_rejected_throws() {
        RentalRequestForm form = validRentalForm();
        Car car = createAvailableCar();
        AppUser user = createTestUser();

        when(carService.findById(1L)).thenReturn(Optional.of(car));
        when(creditRequestPublisher.verifyCreditAndWait(eq("elie"), any(BigDecimal.class), eq("RENT")))
                .thenReturn(new CreditResponse("corr-1", "elie", false, "Insufficient credit"));
        when(operationLogRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThrows(RuntimeException.class,
                () -> rentalService.createRental(form, user));
    }

    @Test
    void updateStatus_existingRental_updatesStatus() {
        Rental rental = new Rental();
        rental.setStatus(RentalStatus.REQUESTED);
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        rentalService.updateStatus(1L, RentalStatus.APPROVED);

        assertEquals(RentalStatus.APPROVED, rental.getStatus());
        verify(rentalRepository).save(rental);
    }

    private RentalRequestForm validRentalForm() {
        RentalRequestForm form = new RentalRequestForm();
        form.setCarId(1L);
        form.setStartDate(LocalDate.of(2025, 6, 10));
        form.setEndDate(LocalDate.of(2025, 6, 15));
        form.setComment("Test rental");
        return form;
    }

    private Car createAvailableCar() {
        Car car = new Car();
        car.setId(1L);
        car.setBrand("Audi");
        car.setModel("A6");
        car.setPrice(new BigDecimal("10000"));
        car.setSold(false);
        return car;
    }

    private AppUser createTestUser() {
        AppUser user = new AppUser("elie", "encoded", "USER", "Elie", "Sioufi");
        user.setId(1L);
        return user;
    }
}
