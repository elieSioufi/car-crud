package com.examples.carcrud.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Holds the generated car report data.
 * Produced by the prototype-scoped CarReportBuilder.
 */
public class CarReport {

    private int totalCount;
    private int availableCount;
    private int soldCount;
    private BigDecimal averagePrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal totalValue;
    private Map<String, Integer> typeBreakdown;

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getAvailableCount() { return availableCount; }
    public void setAvailableCount(int availableCount) { this.availableCount = availableCount; }

    public int getSoldCount() { return soldCount; }
    public void setSoldCount(int soldCount) { this.soldCount = soldCount; }

    public BigDecimal getAveragePrice() { return averagePrice; }
    public void setAveragePrice(BigDecimal averagePrice) { this.averagePrice = averagePrice; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public Map<String, Integer> getTypeBreakdown() { return typeBreakdown; }
    public void setTypeBreakdown(Map<String, Integer> typeBreakdown) { this.typeBreakdown = typeBreakdown; }
}
