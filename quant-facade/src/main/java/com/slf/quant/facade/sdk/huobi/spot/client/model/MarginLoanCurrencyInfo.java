package com.slf.quant.facade.sdk.huobi.spot.client.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MarginLoanCurrencyInfo
{
    /**
     * Currency
     */
    private String     currency;
    
    /**
     * Basic daily interest rate
     */
    private BigDecimal interestRate;
    
    /**
     * Minimal loanable amount
     */
    private BigDecimal minLoanAmt;
    
    /**
     * Maximum loanable amount
     */
    private BigDecimal maxLoanAmt;
    
    /**
     * Remaining loanable amount
     */
    private BigDecimal loanableAmt;
    
    /**
     * Actual interest rate (if deduction is inapplicable or disabled, return basic daily interest rate)
     */
    private BigDecimal actualRate;
}
