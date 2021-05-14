package com.slf.quant.facade.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * QuantCommonAccountModel：
 *
 * @author: fzk
 * @date: 2021-03-25 11:16
 */
@Data
public class QuantCommonAccountModel
{
    private BigDecimal spotBalance     = BigDecimal.ZERO; // 现货余额
    
    private BigDecimal contractBalance = BigDecimal.ZERO; // 合约权益
    
    private BigDecimal spotEnable      = BigDecimal.ZERO; // 现货可用
    
    private BigDecimal canWithdraw     = BigDecimal.ZERO; // 合约可转数量
    
    private BigDecimal frozenMargin    = BigDecimal.ZERO; // 挂单冻结保证金
    
    private BigDecimal leverRate;                         // 杠杆倍数
    
    private BigDecimal positionValue;
    
    private BigDecimal availableMargin;                   // 可用保证金
    
    private BigDecimal usedMargin;                        // 仓位占用保证金
    
    // 现货杠杆部分
    private BigDecimal fixMarginEnable;                   // 逐仓杠杆账户可用
    
    private BigDecimal fixMarginBorrow;                   // 逐仓杠杆账户已借
    
    private BigDecimal fixMarginFrozen;                   // 逐仓杠杆账户冻结
    
    private BigDecimal crossMarginEnable;                 // 全仓杠杆账户可用
    
    private BigDecimal crossMarginBorrow;                 // 全仓杠杆账户已借
    
    private BigDecimal crossMarginFrozen;                 // 全仓杠杆账户冻结
    
    private BigDecimal fixMarginEnableU;                  // 逐仓杠杆账户可用
    
    private BigDecimal fixMarginBorrowU;                  // 逐仓杠杆账户已借
    
    private BigDecimal fixMarginFrozenU;                  // 逐仓杠杆账户冻结
    
    private BigDecimal crossMarginEnableU;                // 全仓杠杆账户可用
    
    private BigDecimal crossMarginBorrowU;                // 全仓杠杆账户已借
    
    private BigDecimal crossMarginFrozenU;                // 全仓杠杆账户冻结
}
