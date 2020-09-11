package com.slf.quant.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slf.quant.dao.QuantStrategyProfitMapper;
import com.slf.quant.facade.bean.BaseServiceImpl;
import com.slf.quant.facade.entity.strategy.QuantStrategyProfit;
import com.slf.quant.facade.service.strategy.QuantStrategyProfitService;

import java.util.List;

/**
 * QuantStrategyProfitServiceImpl：
 *
 * @author: fzk
 * @date: 2020-08-27 14:37
 */
@Service
public class QuantStrategyProfitServiceImpl extends BaseServiceImpl<QuantStrategyProfit> implements QuantStrategyProfitService
{
    protected QuantStrategyProfitMapper quantStrategyProfitMapper;
    
    @Autowired
    public QuantStrategyProfitServiceImpl(QuantStrategyProfitMapper quantStrategyProfitMapper)
    {
        super(quantStrategyProfitMapper);
        this.quantStrategyProfitMapper = quantStrategyProfitMapper;
    }
    
    @Override
    public List<QuantStrategyProfit> findRank(Long minTime)
    {
        return quantStrategyProfitMapper.findRank(minTime);
    }

    @Override
    public List<QuantStrategyProfit> findTodayFirstStats(Long currentDateFirstSec) {
        return quantStrategyProfitMapper.findTodayFirstStats(currentDateFirstSec);
    }
}
