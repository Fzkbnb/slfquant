package com.slf.quant.facade.sdk.huobi.spot.client.model;

import java.math.BigDecimal;

import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.*;

import lombok.Data;


/**
 * The detail order information.
 */
@Data
public class Order {

  private AccountType accountType;
  private BigDecimal amount;
  private BigDecimal price;
  private long createdTimestamp = 0;
  private long canceledTimestamp = 0;
  private long finishedTimestamp = 0;
  private long orderId = 0;
  private String symbol;
  private BigDecimal stopPrice;
  private OrderType type = null;
  private BigDecimal filledAmount;
  private BigDecimal filledCashAmount;
  private BigDecimal filledFees;
  private OrderSource source = null;
  private OrderState state = null;
  private StopOrderOperator operator = null;
}
