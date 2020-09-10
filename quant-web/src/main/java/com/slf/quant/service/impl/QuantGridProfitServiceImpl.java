package com.slf.quant.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slf.quant.dao.QuantGridProfitMapper;
import com.slf.quant.facade.bean.BaseServiceImpl;
import com.slf.quant.facade.entity.strategy.QuantGridProfit;
import com.slf.quant.facade.service.strategy.QuantGridProfitService;

/**
 * QuantGridProfitServiceImpl：
 *
 * @author: fzk
 * @date: 2020-08-27 14:37
 */
@Service
public class QuantGridProfitServiceImpl extends BaseServiceImpl<QuantGridProfit> implements QuantGridProfitService
{
    protected QuantGridProfitMapper quantGridProfitMapper;
    
    @Autowired
    public QuantGridProfitServiceImpl(QuantGridProfitMapper quantGridProfitMapper)
    {
        super(quantGridProfitMapper);
        this.quantGridProfitMapper = quantGridProfitMapper;
    }
}
