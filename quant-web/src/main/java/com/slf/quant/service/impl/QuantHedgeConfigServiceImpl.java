package com.slf.quant.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slf.quant.dao.QuantHedgeConfigMapper;
import com.slf.quant.facade.bean.BaseServiceImpl;
import com.slf.quant.facade.entity.strategy.QuantHedgeConfig;
import com.slf.quant.facade.service.strategy.QuantHedgeConfigService;

/**
 * QuantHedgeConfigServiceImpl：
 *
 * @author: fzk
 * @date: 2020-08-27 14:37
 */
@Service
public class QuantHedgeConfigServiceImpl extends BaseServiceImpl<QuantHedgeConfig> implements QuantHedgeConfigService
{
    protected QuantHedgeConfigMapper quantHedgeConfigMapper;

    @Autowired
    public QuantHedgeConfigServiceImpl(QuantHedgeConfigMapper quantHedgeConfigMapper)
    {
        super(quantHedgeConfigMapper);
        this.quantHedgeConfigMapper = quantHedgeConfigMapper;
    }
    
    @Override
    public void changeStatus(Long id, int i)
    {
        quantHedgeConfigMapper.changeStatus(id, i);
    }
    
    @Override
    public void changeAllStatus(int i)
    {
        quantHedgeConfigMapper.changeAllStatus(i);
    }
}
