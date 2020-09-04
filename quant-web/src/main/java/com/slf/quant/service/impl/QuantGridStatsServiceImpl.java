package com.slf.quant.service.impl;

import com.slf.quant.facade.model.GridContractSumInfo;
import org.springframework.beans.factory.annotation.Autowired;

import com.slf.quant.facade.bean.BaseServiceImpl;
import com.slf.quant.facade.entity.strategy.QuantGridStats;
import com.slf.quant.facade.service.strategy.QuantGridStatsService;
import com.slf.quant.dao.QuantGridStatsMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * QuantGridStatsServiceImpl：
 *
 * @author: fzk
 * @date: 2020-08-27 14:37
 */
@Service
public class QuantGridStatsServiceImpl extends BaseServiceImpl<QuantGridStats> implements QuantGridStatsService
{
    protected QuantGridStatsMapper quantGridStatsMapper;
    
    @Autowired
    public QuantGridStatsServiceImpl(QuantGridStatsMapper quantGridStatsMapper)
    {
        super(quantGridStatsMapper);
        this.quantGridStatsMapper = quantGridStatsMapper;
    }
    
    @Override
    public GridContractSumInfo findSumInfo(Long strategyId, int orderStatus, BigDecimal contractValue)
    {
        return quantGridStatsMapper.findSumInfo(strategyId,orderStatus,contractValue);
    }
}
