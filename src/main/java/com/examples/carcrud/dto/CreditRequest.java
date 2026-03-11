package com.examples.carcrud.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class CreditRequest implements Serializable {

    private String correlationId;
    private String username;
    private BigDecimal amount;
    private String operationType; // "RENT" or "BUY"

    public CreditRequest() {}

    public CreditRequest(String correlationId, String username, BigDecimal amount, String operationType) {
        this.correlationId = correlationId;
        this.username = username;
        this.amount = amount;
        this.operationType = operationType;
    }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
}
