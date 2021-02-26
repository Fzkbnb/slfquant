package com.slf.quant.facade.sdk.huobi.spot.client.model;

import java.math.BigDecimal;

import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.DealRole;
import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.OrderSource;
import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.OrderType;

import lombok.Data;

/**
 * The match result information.
 */
@Data
public class MatchResult
{
    private long        createdTimestamp;
    
    private BigDecimal  filledAmount;
    
    private BigDecimal  filledFees;
    
    private long        id;
    
    private long        matchId;
    
    private long        orderId;
    
    private BigDecimal  price;
    
    private OrderSource source;
    
    private String      symbol;
    
    private OrderType   type;
    
    private BigDecimal  filledPoints;
    
    private String      feeDeductCurrency;
    
    private DealRole    role;
}
