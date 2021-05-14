package com.slf.quant.facade.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * QuantCommonOrderModel：
 *
 * @author: fzk
 * @date: 2021-03-25 11:17
 */
@Data
public class QuantCommonOrderModel
{
    private Long       orderId;
    
    private String     symbol;
    
    private BigDecimal dealAmt;      // 成交张数
    
    private BigDecimal entrustAmt;   // 委托张数
    
    private Integer    status;       // 订单状态（0.无成交，1.完全成交）
    
    private boolean    isCompleted;  // 订单是否完成（仅当已撤单或全部成交时设为已完成）
    
    private BigDecimal entrustPrice; // 委托价格
    
    private BigDecimal avgPrice;     // 成交均价
    
    private Long       createTime;   // 订单创建时间
    
    private BigDecimal fee;          // 手续费，负表示收取，正表示返佣
    
    private String     currency;     // 交易币种
    
    private BigDecimal feeRate;      // 手续费率，正表示收取，负表示返佣
    
    private boolean    isCancel;
    
    private BigDecimal pnl;          // 盈亏
    
    private String     entrustSide;  // 委托方向：buy/sell
    
    private String     posSide;      // 仓位方向：long/short
}
