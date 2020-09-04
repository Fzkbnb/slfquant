package com.slf.quant.facade.model.hedge;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Account
{
    private BigDecimal equity;
    
    private BigDecimal availableMargin;// 可用保证金
    
    private BigDecimal availableCont;  // 预估可开张数
}
