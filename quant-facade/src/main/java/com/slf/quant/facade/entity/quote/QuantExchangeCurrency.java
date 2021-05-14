package com.slf.quant.facade.entity.quote;

import com.slf.quant.facade.bean.BaseEntity;
import lombok.Data;

/**
 * QuantExchangeCurrency：
 *
 * @author: fzk
 * @date: 2021-04-08 15:41
 */
@Data
public class QuantExchangeCurrency extends BaseEntity
{
    private String  type;    // depth：行情价格 fundRate :资金费率
    
    private String  currency;
    
    private Integer status;  // 1启用0禁用
}
