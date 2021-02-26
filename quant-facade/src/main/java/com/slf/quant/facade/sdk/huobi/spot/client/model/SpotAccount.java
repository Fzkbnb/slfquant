package com.slf.quant.facade.sdk.huobi.spot.client.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class SpotAccount implements Serializable
{
    private BigDecimal balance;// 余额
    
    private BigDecimal frozen; // 冻结
    
    private BigDecimal enable; // 可用
}
