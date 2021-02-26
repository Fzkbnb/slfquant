package com.slf.quant.facade.sdk.huobi.spot.client.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * The Huobi supported symbols.
 */
@Data
public class Symbol
{
    private String     baseCurrency;
    
    private String     quoteCurrency;
    
    private int        pricePrecision;
    
    private int        amountPrecision;
    
    private String     symbolPartition;
    
    private String     symbol;
    
    private Integer    valuePrecision;
    
    private BigDecimal minOrderAmt;
    
    private BigDecimal maxOrderAmt;
    
    private BigDecimal minOrderValue;
    
    private Integer    leverageRatio;
    
    private String     state;
}
