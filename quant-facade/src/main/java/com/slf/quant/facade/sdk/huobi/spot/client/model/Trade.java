package com.slf.quant.facade.sdk.huobi.spot.client.model;

import java.math.BigDecimal;

import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.TradeDirection;

import lombok.Data;

/**
 * The trade information with price and amount etc.
 */
@Data
public class Trade {

  private Long uniqueTradeId;
  private String tradeId;
  private long timestamp;
  private BigDecimal price;
  private BigDecimal amount;
  private TradeDirection direction;

}
