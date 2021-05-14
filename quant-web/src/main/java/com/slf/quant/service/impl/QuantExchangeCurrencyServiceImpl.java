package com.slf.quant.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.slf.quant.dao.QuantExchangeCurrencyMapper;
import com.slf.quant.facade.bean.BaseServiceImpl;
import com.slf.quant.facade.entity.quote.QuantExchangeCurrency;
import com.slf.quant.facade.service.quote.QuantExchangeCurrencyService;

/**
* 量化交易币种服务接口实现类
*
* @author: fzk
* @date: 2021-4-16 11:18:20
*/
@Service
public class QuantExchangeCurrencyServiceImpl extends BaseServiceImpl<QuantExchangeCurrency> implements QuantExchangeCurrencyService
{
    protected QuantExchangeCurrencyMapper quantExchangeCurrencyMapper;
    
    @Autowired
    public QuantExchangeCurrencyServiceImpl(QuantExchangeCurrencyMapper quantExchangeCurrencyMapper)
    {
        super(quantExchangeCurrencyMapper);
        this.quantExchangeCurrencyMapper = quantExchangeCurrencyMapper;
    }
}
