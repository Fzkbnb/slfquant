package com.slf.quant.facade.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class KeepAvgConfig implements Serializable
{
    private String     longSymbol;
    
    private String     shortSymbol;
    
    private BigDecimal maxDiffValue;// 当总权益与上次调平后的初始总权益差值超过该值，则执行一次调平
    
    private BigDecimal longCont;
    
    private BigDecimal shortCont;
    
    private BigDecimal longFixCont; // 多头单笔调平张数
    
    private BigDecimal shortFixCont;// 空头单笔调平张数
}
