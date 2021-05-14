package com.slf.quant.facade.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * QuantCommonPositionModel：
 *
 * @author: fzk
 * @date: 2021-03-25 11:15
 */
@Data
public class QuantCommonPositionModel
{
    private String     currency;        // 合约基础币种
    
    private String     contractCode;    // 合约代码
    
    private String     marginMode;      // 保证金模式(cross：全仓 isolated：逐仓)
    
    private BigDecimal amount;          // 持仓数量
    
    private BigDecimal cont;            // 持仓张数
    
    private BigDecimal lastPrice;       // 最新成交价
    
    private BigDecimal avgPrice;        // 开仓均价
    
    private BigDecimal leverage;        // 杠杆倍数
    
    private BigDecimal liquidationPrice;// 预估强平价
    
    private BigDecimal shortCont;       // 空头持仓张数
    
    private BigDecimal longCont;        // 多头持仓张数
    
    private BigDecimal availPos;        // 可平张数
    
    private BigDecimal upl;             // 仓位未实现收益
}
