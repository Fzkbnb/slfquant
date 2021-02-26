package com.slf.quant.facade.sdk.huobi.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BalanceDetail {

    private String currency;

    private String type;

    BigDecimal balance;
}
