package com.slf.quant.facade.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * QuantCommonSymbolInfo：
 *
 * @author: fzk
 * @date: 2021-03-25 14:37
 */
@Data
public class QuantCommonSymbolInfo
{
    private String     symbol;                // 交易对名称，现货统一按照BTC-USDT格式封装，合约统一按照BTC-USD-210326封装
    
    private String     baseCurrency;          // 基础币种
    
    private String     quoteCurrency;         // 计价币种
    
    private BigDecimal spotPriceTick;         // 现货价格单位
    
    private BigDecimal contractPriceTick;     // 合约价格单位
    
    private BigDecimal amtTick;               // 数量单位
    
    private BigDecimal contractVal;           // 合约面值
    
    private int        spotPricePrecision;    // 现货价格小数位数
    
    private int        contractPricePrecision;// 合约价格小数位数
    
    private int        spotAmtPrecision;      // 数量小数位数
    
    private BigDecimal minSpotAmt;            // 现货最小下单数量
}
