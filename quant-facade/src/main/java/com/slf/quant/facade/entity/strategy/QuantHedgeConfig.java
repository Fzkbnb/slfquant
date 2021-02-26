package com.slf.quant.facade.entity.strategy;

import java.math.BigDecimal;

import com.slf.quant.facade.bean.BaseEntity;
import lombok.Data;

@Data
public class QuantHedgeConfig extends BaseEntity
{
    private Long       accountId;      // 账户id
    
    private String     exchange;       // 交易所名称
    
    private String     exchangeAccount;// 交易所账号
    
    private String     currency;       // 交易币种
    
    private String     shortSymbol;    // 做空交易对
    
    private String     longSymbol;     // 做多交易对
    
    private BigDecimal openRatio;      // 开仓溢价率
    
    private BigDecimal closeRatio;     // 平仓溢价率
    
    private BigDecimal entrustCont;    // 单笔委托张数
    
    private BigDecimal maxHoldCont;    // 最大单向持仓张数限制
    
    private BigDecimal leverRate;      // 杠杆倍数
    
    private Integer    status;         // 运行状态
    
    private Long       createTime;     // 创建时间
    
    private Long       updateTime;     // 更新时间
}
