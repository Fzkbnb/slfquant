package com.slf.quant.service.impl;

import com.slf.quant.facade.consts.KeyConst;
import com.slf.quant.facade.model.StrategyStatusModel;
import com.slf.quant.strategy.consts.TradeConst;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slf.quant.dao.QuantStrategyProfitMapper;
import com.slf.quant.facade.bean.BaseServiceImpl;
import com.slf.quant.facade.entity.strategy.QuantStrategyProfit;
import com.slf.quant.facade.service.strategy.QuantStrategyProfitService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<QuantStrategyProfit> findTodayFirstStats(Long currentDateFirstSec)
    {
        return quantStrategyProfitMapper.findTodayFirstStats(currentDateFirstSec);
    }
    
    @Override
    public QuantStrategyProfit findLastProfit(Long id)
    {
        return quantStrategyProfitMapper.findLastProfit(id);
    }
    
    @Override
    public Map<String, Object> getProfitStats(Long id)
    {
        String key = KeyConst.REDISKEY_STRATEGY_STATS + id;
        Map<String, Object> map = new HashMap<>();
        List<StrategyStatusModel> list = TradeConst.strategy_stats_map.get(key);
        if (CollectionUtils.isNotEmpty(list))
        {
            map.put("data", list);
            map.put("code", 200);
        }
        else
        {
            map.put("message", "统计信息不存在！");
            map.put("code", -1);
        }
        // 折线图数据
        QuantStrategyProfit param = new QuantStrategyProfit();
        param.setStrategyId(id);
        List<QuantStrategyProfit> profits = findList(param);
        map.put("profits", profits);
        return map;
    }
}
