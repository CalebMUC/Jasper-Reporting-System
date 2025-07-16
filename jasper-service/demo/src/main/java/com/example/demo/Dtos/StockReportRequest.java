package com.example.demo.Dtos;

import java.math.BigDecimal;

public class StockReportRequest {
    private BigDecimal LowStockThreshold;

    //Getter
    public BigDecimal getLowStockThreshold(){
        return LowStockThreshold;
    }
    //setter
    public void setLowStockThreshold(BigDecimal LowStockThreshold){
        this.LowStockThreshold = LowStockThreshold;
    }

}
