package com.slf.quant.service.impl;

import com.slf.quant.facade.bean.BaseServiceImpl;
import com.slf.quant.facade.entity.strategy.QuantGridConfig;
import com.slf.quant.facade.service.strategy.QuantGridConfigService;
import com.slf.quant.dao.QuantGridConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * QuantGridConfigServiceImpl：
 *
 * @author: fzk
 * @date: 2020-08-27 14:37
 */
@Service
public class QuantGridConfigServiceImpl extends BaseServiceImpl<QuantGridConfig> implements QuantGridConfigService
{
    protected QuantGridConfigMapper quantGridConfigMapper;
    
    @Autowired
    public QuantGridConfigServiceImpl(QuantGridConfigMapper quantGridConfigMapper)
    {
        super(quantGridConfigMapper);
        this.quantGridConfigMapper = quantGridConfigMapper;
    }
    
    @Override
    public void changeStatus(Long id, int i)
    {
        quantGridConfigMapper.changeStatus(id, i);
    }
    
    @Override
    public void changeAllStatus(int i)
    {
        quantGridConfigMapper.changeAllStatus(i);
    }
}
