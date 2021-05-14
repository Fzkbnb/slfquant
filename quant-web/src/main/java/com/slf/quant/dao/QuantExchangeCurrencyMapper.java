package com.slf.quant.dao;

import com.slf.quant.facade.bean.BaseMapper;
import com.slf.quant.facade.entity.quote.QuantExchangeCurrency;
import org.apache.ibatis.annotations.Mapper;


/**
* 持久层接口
* @version 1.0
*/
@Mapper
public interface QuantExchangeCurrencyMapper extends BaseMapper<QuantExchangeCurrency>
{
}
