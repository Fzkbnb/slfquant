package com.slf.quant.facade.sdk.huobi.spot.client.model;

import java.util.List;

import lombok.Data;

@Data
public class MarginLoanInfo
{
    private String                       symbol;
    
    private List<MarginLoanCurrencyInfo> currencies;
}
