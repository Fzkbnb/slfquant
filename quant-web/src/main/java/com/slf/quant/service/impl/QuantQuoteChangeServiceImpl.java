package com.slf.quant.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slf.quant.dao.QuantQuoteChangeMapper;
import com.slf.quant.facade.bean.BaseServiceImpl;
import com.slf.quant.facade.entity.strategy.QuantQuoteChange;
import com.slf.quant.facade.service.strategy.QuantQuoteChangeService;

/**
 * QuantQuoteChangeServiceImpl：
 *
 * @author: fzk
 * @date: 2020-08-27 14:37
 */
@Service
public class QuantQuoteChangeServiceImpl extends BaseServiceImpl<QuantQuoteChange> implements QuantQuoteChangeService
{
    protected QuantQuoteChangeMapper quantQuoteChangeMapper;

    @Autowired
    public QuantQuoteChangeServiceImpl(QuantQuoteChangeMapper quantQuoteChangeMapper)
    {
        super(quantQuoteChangeMapper);
        this.quantQuoteChangeMapper = quantQuoteChangeMapper;
    }
}
