package com.examples.carcrud.dto;

import java.math.BigDecimal;

public class PaymentRecordForm {

    private Long id;
    private String currency = "EUR";
    private String iban;
    private String ownerName;
    private BigDecimal amount;

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
