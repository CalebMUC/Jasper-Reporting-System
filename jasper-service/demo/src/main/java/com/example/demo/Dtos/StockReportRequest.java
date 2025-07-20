package com.example.demo.Dtos;

import java.math.BigDecimal;

public class StockReportRequest {
    private BigDecimal lowStockThreshold;
    private String format;

    public BigDecimal getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(BigDecimal lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
