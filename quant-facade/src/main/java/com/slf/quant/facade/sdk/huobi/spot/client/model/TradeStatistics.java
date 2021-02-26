package com.slf.quant.facade.sdk.huobi.spot.client.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * The summary of trading in the market for the last 24 hours
 */
@Data
public class TradeStatistics {

    private long timestamp;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal amount;
    private BigDecimal high;
    private BigDecimal low;
    private long count;
    private BigDecimal volume;


}
