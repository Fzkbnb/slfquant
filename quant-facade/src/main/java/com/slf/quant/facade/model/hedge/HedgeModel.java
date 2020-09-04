package com.slf.quant.facade.model.hedge;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class HedgeModel
{
    private BigDecimal longAvgPrice;
    
    private BigDecimal shortAvgPrice;
    
    private BigDecimal slipRatio;
    
    private BigDecimal hedgeCont;
    
    private long       sellOrderId;
    
    private long       buyOrderId;
    
    private boolean    isSuccess;
    
    private String     priceMode;
}
