package com.examples.carcrud.service;

import com.examples.carcrud.dto.PaymentRecordForm;
import com.examples.carcrud.model.PaymentRecord;
import com.examples.carcrud.repository.PaymentRecordRepository;
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
class PaymentRecordServiceTest {

    @Mock
    private PaymentRecordRepository paymentRecordRepository;

    @InjectMocks
    private PaymentRecordService paymentRecordService;

    @Test
    void createFromForm_savesRecord() {
        PaymentRecordForm form = new PaymentRecordForm();
        form.setCurrency("EUR");
        form.setIban("FR76 1234");
        form.setOwnerName("Test User");
        form.setAmount(new BigDecimal("500"));

        when(paymentRecordRepository.save(any(PaymentRecord.class))).thenAnswer(i -> i.getArgument(0));

        PaymentRecord result = paymentRecordService.createFromForm(form);

        assertEquals("EUR", result.getCurrency());
        assertEquals("FR76 1234", result.getIban());
        assertEquals("Test User", result.getOwnerName());
        assertEquals(new BigDecimal("500"), result.getAmount());
    }

    @Test
    void updateFromForm_existingRecord_updates() {
        PaymentRecord existing = new PaymentRecord("EUR", "OLD-IBAN", "Old Name", new BigDecimal("100"));
        when(paymentRecordRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(paymentRecordRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PaymentRecordForm form = new PaymentRecordForm();
        form.setCurrency("USD");
        form.setIban("NEW-IBAN");
        form.setOwnerName("New Name");
        form.setAmount(new BigDecimal("999"));

        PaymentRecord result = paymentRecordService.updateFromForm(1L, form);

        assertEquals("USD", result.getCurrency());
        assertEquals("NEW-IBAN", result.getIban());
        assertEquals("New Name", result.getOwnerName());
    }

    @Test
    void toForm_convertsCorrectly() {
        PaymentRecord record = new PaymentRecord("EUR", "FR76 1234", "Test", new BigDecimal("250"));
        record.setId(5L);

        PaymentRecordForm form = paymentRecordService.toForm(record);

        assertEquals(5L, form.getId());
        assertEquals("EUR", form.getCurrency());
        assertEquals("FR76 1234", form.getIban());
        assertEquals("Test", form.getOwnerName());
        assertEquals(new BigDecimal("250"), form.getAmount());
    }
}
