package com.slf.quant.facade.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class GridContractConfig implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private Long              id;
    
    private String            strategyName;
    
    private Long              accountId;
    
    private String            exchange;
    
    private String            exchangeAccount;
    
    private String            currency;
    
    private String            contractCode;
    
    private String            firstDirect;
    
    private BigDecimal        entrustCont;          // 单笔委托数量
    
    private BigDecimal        priceDiffRate;        // 网格价差率
    
    private BigDecimal        profitRate;           // 网格盈利率
    
    private Long              createDate;
    
    private Long              updateDate;
    
    private String            apiKey;
    
    private Integer           enableMaker;
    
    private BigDecimal        maxBuyPrice;
    
    private BigDecimal        minSellPrice;
    
    private BigDecimal        leverRate;
    
    private BigDecimal        feeRate;              // 手续费率
    
    private BigDecimal        maxSellPrice;//先卖模式下，超过该价格则停止卖出加仓，以便
    
    private BigDecimal        minBUyPrice;
}
