package com.examples.carcrud.dto;

import java.time.LocalDate;

public class RentalRequestForm {

    private Long carId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String comment;

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
