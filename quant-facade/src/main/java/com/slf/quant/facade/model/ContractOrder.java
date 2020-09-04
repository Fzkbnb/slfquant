package com.slf.quant.facade.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ContractOrder
{
    private long       id;
    
    private boolean    isCompleted;
    
    private int        status;
    
    private BigDecimal dealCont;
    
    private BigDecimal entrustCont;
    
    private Long       entrustTime;
    
    private String     direct;
    
    private BigDecimal entrustPrice;                // 委托价格
    
    private BigDecimal avg_price = BigDecimal.ZERO; // 成交均价
    
    private BigDecimal fee       = BigDecimal.ZERO;
}
