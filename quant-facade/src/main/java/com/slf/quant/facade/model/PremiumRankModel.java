package com.slf.quant.facade.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class PremiumRankModel implements Serializable
{
    private String longSymbol;
    
    private String shortSymbol;
    
    private String premiumRate;
    
    private Long   updateTime;
}
