package com.slf.quant.facade.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对冲策略运行状态信息
 */
@Data
@NoArgsConstructor
public class GridContractSumInfo implements Serializable
{
    private BigDecimal sumBuyCont;
    
    private BigDecimal sumBuyBal;
    
    private BigDecimal sumSellCont;
    
    private BigDecimal sumSellBal;
    
    private Long       monitorTime;   // 统计时间
    
    private BigDecimal avgSellPrice;
    
    private BigDecimal avgBuyPrice;
    
    private BigDecimal startSellPrice;
    
    private BigDecimal startBuyPrice;
    
    private BigDecimal profit;
    
    private BigDecimal profitReal;
    
    private BigDecimal profitUnreal;
}
