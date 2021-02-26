package com.slf.quant.facade.sdk.huobi.spot.client.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SanJiaoModel {
    private String targetSymbol;
    private String capitalSymbol;
    private String exchange;
    private BigDecimal ratio;
    private BigDecimal targetPrice;
    private BigDecimal capitalPrice;
    private BigDecimal target_capital_price;
    private int capitalAmtPrecision;
    private int targetAmtPrecision;
    private int targetCapitalAmtPrecision;
}
