package com.slf.quant.facade.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AccountModel
{
    
    private BigDecimal contractBalance  = BigDecimal.ZERO; // 合约权益

    private BigDecimal can_withdraw     = BigDecimal.ZERO; // 合约可转数量
    
    private BigDecimal mark_price;                         // 合约标记价格
    
    private BigDecimal frozenMargin     = BigDecimal.ZERO; // 挂单冻结保证金
    
    private BigDecimal leverRate;                          // 杠杆倍数
    
    private BigDecimal liquidationPrice = BigDecimal.ZERO; // 爆仓价
    
    private BigDecimal profitReal;                         // 合约账户已实现盈亏
    
    private BigDecimal profitUnreal;                       // 合约账户未实现盈亏
    
    private BigDecimal positionValue;
    
    private BigDecimal availableMargin;                    // 可用保证金
    
    private BigDecimal usedMargin;                         // 已用保证金

}
