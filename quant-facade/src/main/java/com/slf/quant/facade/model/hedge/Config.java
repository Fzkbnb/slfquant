package com.slf.quant.facade.model.hedge;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Config
{
    private String     currency;
    
    private String     shortSymbol;
    
    private String     longSymbol;
    
    private BigDecimal openRatio;
    
    private BigDecimal closeRatio;
    
    private BigDecimal entrustCont;

    private BigDecimal leverRate;


}
