package com.slf.quant.facade.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * QuantCommonPositionModel：资金费率通用模型
 *
 * @author: fzk
 * @date: 2021-03-25 11:15
 */
@Data
public class QuantCommonFundingRateModel
{
    private String     contractCode;
    
    private Long       time;
    
    private BigDecimal currentRate;   // 当期费率
    
    private BigDecimal nextRate;      // 下期费率
    
    private BigDecimal currentRateABS;// 当期费率绝对值
}
