package com.slf.quant.service.impl;

import com.slf.quant.dao.QuantAvgConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slf.quant.facade.bean.BaseServiceImpl;
import com.slf.quant.facade.entity.strategy.QuantAvgConfig;
import com.slf.quant.facade.service.strategy.QuantAvgConfigService;

/**
 * QuantAvgConfigServiceImpl：
 *
 * @author: fzk
 * @date: 2020-08-27 14:37
 */
@Service
public class QuantAvgConfigServiceImpl extends BaseServiceImpl<QuantAvgConfig> implements QuantAvgConfigService
{
    protected QuantAvgConfigMapper quantAvgConfigMapper;
    
    @Autowired
    public QuantAvgConfigServiceImpl(QuantAvgConfigMapper quantAvgConfigMapper)
    {
        super(quantAvgConfigMapper);
        this.quantAvgConfigMapper = quantAvgConfigMapper;
    }
    
    @Override
    public void changeStatus(Long id, int i)
    {
        quantAvgConfigMapper.changeStatus(id, i);
    }
    
    @Override
    public void changeAllStatus(int i)
    {
        quantAvgConfigMapper.changeAllStatus(i);
    }
}
