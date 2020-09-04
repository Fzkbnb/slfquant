package com.slf.quant.facade.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DepthModel
{
    private BigDecimal bid_future;    // 合约买一
    
    private BigDecimal ask_future;    // 合约卖一
    
    private BigDecimal contract_price;
    
    private String     currency;
}
