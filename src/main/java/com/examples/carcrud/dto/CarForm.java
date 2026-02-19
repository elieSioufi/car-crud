package com.examples.carcrud.dto;

import com.examples.carcrud.model.CarType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CarForm {

    private Long id;
    private CarType type;
    private String brand;
    private String model;
    private LocalDate purchaseDate;
    private int maxSpeed;
    private int passengers;
    private boolean automaticTransmission;
    private boolean sold;
    private BigDecimal price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CarType getType() { return type; }
    public void setType(CarType type) { this.type = type; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public int getMaxSpeed() { return maxSpeed; }
    public void setMaxSpeed(int maxSpeed) { this.maxSpeed = maxSpeed; }

    public int getPassengers() { return passengers; }
    public void setPassengers(int passengers) { this.passengers = passengers; }

    public boolean isAutomaticTransmission() { return automaticTransmission; }
    public void setAutomaticTransmission(boolean automaticTransmission) { this.automaticTransmission = automaticTransmission; }

    public boolean isSold() { return sold; }
    public void setSold(boolean sold) { this.sold = sold; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
