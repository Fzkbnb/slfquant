package com.slf.quant.facade.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * QuantCommonDepthModel：
 *
 * @author: fzk
 * @date: 2021-03-25 11:16
 */
@Data
public class QuantCommonDepthModel
{
    private String     currency;
    
    private String     symbol;
    
    private BigDecimal price;
    
    private BigDecimal bid;
    
    private BigDecimal ask;
    
    private Long       updateTime;
}
