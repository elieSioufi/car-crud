package com.examples.carcrud.service;

import com.examples.carcrud.dto.PaymentRecordForm;
import com.examples.carcrud.model.PaymentRecord;
import com.examples.carcrud.repository.PaymentRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentRecordService {

    private final PaymentRecordRepository paymentRecordRepository;

    public PaymentRecordService(PaymentRecordRepository paymentRecordRepository) {
        this.paymentRecordRepository = paymentRecordRepository;
    }

    public List<PaymentRecord> findAll() {
        return paymentRecordRepository.findAll();
    }

    public Optional<PaymentRecord> findById(Long id) {
        return paymentRecordRepository.findById(id);
    }

    public PaymentRecord createFromForm(PaymentRecordForm form) {
        PaymentRecord record = new PaymentRecord();
        applyForm(record, form);
        return paymentRecordRepository.save(record);
    }

    public PaymentRecord updateFromForm(Long id, PaymentRecordForm form) {
        PaymentRecord record = paymentRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment record not found: " + id));
        applyForm(record, form);
        return paymentRecordRepository.save(record);
    }

    public void deleteById(Long id) {
        paymentRecordRepository.deleteById(id);
    }

    public PaymentRecordForm toForm(PaymentRecord record) {
        PaymentRecordForm form = new PaymentRecordForm();
        form.setId(record.getId());
        form.setCurrency(record.getCurrency());
        form.setIban(record.getIban());
        form.setOwnerName(record.getOwnerName());
        form.setAmount(record.getAmount());
        return form;
    }

    private void applyForm(PaymentRecord record, PaymentRecordForm form) {
        record.setCurrency(form.getCurrency());
        record.setIban(form.getIban());
        record.setOwnerName(form.getOwnerName());
        record.setAmount(form.getAmount());
    }
}
