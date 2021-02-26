package com.slf.quant.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slf.quant.dao.QuantSwapAvgConfigMapper;
import com.slf.quant.facade.bean.BaseServiceImpl;
import com.slf.quant.facade.entity.strategy.QuantSwapAvgConfig;
import com.slf.quant.facade.service.strategy.QuantSwapAvgConfigService;

/**
 * QuantSwapAvgConfigServiceImpl：
 *
 * @author: fzk
 * @date: 2020-08-27 14:37
 */
@Service
public class QuantSwapAvgConfigServiceImpl extends BaseServiceImpl<QuantSwapAvgConfig> implements QuantSwapAvgConfigService
{
    protected QuantSwapAvgConfigMapper quantSwapAvgConfigMapper;

    @Autowired
    public QuantSwapAvgConfigServiceImpl(QuantSwapAvgConfigMapper quantSwapAvgConfigMapper)
    {
        super(quantSwapAvgConfigMapper);
        this.quantSwapAvgConfigMapper = quantSwapAvgConfigMapper;
    }
    
    @Override
    public void changeStatus(Long id, int i)
    {
        quantSwapAvgConfigMapper.changeStatus(id, i);
    }
    
    @Override
    public void changeAllStatus(int i)
    {
        quantSwapAvgConfigMapper.changeAllStatus(i);
    }
}
