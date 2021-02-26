package com.slf.quant.facade.sdk.huobi.spot.client.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * The best bid/ask consisting of price and amount.
 */
@Data
public class BestQuote
{
    private long       timestamp;
    
    private BigDecimal askPrice;
    
    private BigDecimal askAmount;
    
    private BigDecimal bidPrice;
    
    private BigDecimal bidAmount;
}
