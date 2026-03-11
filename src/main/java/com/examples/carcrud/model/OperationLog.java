package com.examples.carcrud.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_log")
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Long carId;

    private String carDescription;

    @Column(nullable = false)
    private String operationType; // "RENT" or "BUY"

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private boolean approved;

    private String bankMessage;

    private LocalDateTime timestamp;

    public OperationLog() {}

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public String getCarDescription() { return carDescription; }
    public void setCarDescription(String carDescription) { this.carDescription = carDescription; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public String getBankMessage() { return bankMessage; }
    public void setBankMessage(String bankMessage) { this.bankMessage = bankMessage; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
