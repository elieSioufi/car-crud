package com.examples.carcrud.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payment_record")
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String currency = "EUR";

    @Column(nullable = false)
    private String iban;

    @Column(nullable = false)
    private String ownerName;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    public PaymentRecord() {}

    public PaymentRecord(String currency, String iban, String ownerName, BigDecimal amount) {
        this.currency = currency;
        this.iban = iban;
        this.ownerName = ownerName;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
