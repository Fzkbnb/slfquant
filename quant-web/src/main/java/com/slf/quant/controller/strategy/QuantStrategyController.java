package com.slf.quant.controller.strategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.slf.quant.facade.entity.strategy.QuantQuoteChange;
import com.slf.quant.facade.entity.strategy.QuantStrategyProfit;
import com.slf.quant.facade.model.GridHedgeContModel;
import com.slf.quant.facade.service.strategy.QuantGridStatsService;
import com.slf.quant.facade.service.strategy.QuantQuoteChangeService;
import com.slf.quant.facade.service.strategy.QuantStrategyProfitService;
import com.slf.quant.facade.utils.DateUtils;
import com.slf.quant.strategy.consts.TradeConst;
import com.slf.quant.util.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.slf.quant.config.BaseController;
import com.slf.quant.domain.ServiceVO;
import com.slf.quant.domain.SuccessCode;
import com.slf.quant.facade.bean.PaginateResult;
import com.slf.quant.facade.bean.Pagination;
import com.slf.quant.facade.entity.strategy.QuantApiConfig;
import com.slf.quant.facade.service.strategy.QuantApiConfigService;
import sun.util.calendar.CalendarUtils;

/**
 * @author slf
 * @date 2019/7/18 12:09
 * @description Controller控制器
 */
@RestController
@RequestMapping("/strategy/index")
public class QuantStrategyController extends BaseController
{
    @Autowired(required = false)
    private QuantStrategyProfitService quantStrategyProfitService;
    
    @Autowired(required = false)
    private QuantQuoteChangeService    quantQuoteChangeService;
    @Autowired(required = false)
    private QuantGridStatsService quantGridStatsService;
    /**
     * 查询收益排名
     * @return
     */
    @PostMapping("/profitRank")
    public Map<String, Object> profitRank()
    {
        getOnlineUserId();
        Map<String, Object> map = new HashMap<>();
        // 历史最后一笔统计
        List<QuantStrategyProfit> hislist = quantStrategyProfitService.findRank(null);
        // 当天最后一笔统计
        // List<QuantStrategyProfit> daylist = quantStrategyProfitService.findRank(DateUtils.getCurrentDateFirstSec());
        // // 当天第一笔统计
        // long firstSec = DateUtils.getCurrentDateFirstSec();
        // List<QuantStrategyProfit> firstlist = quantStrategyProfitService.findTodayFirstStats(firstSec);
        // daylist.forEach(stats -> {
        // BigDecimal firstProfit = BigDecimal.ZERO;
        // List<QuantStrategyProfit> matchList = firstlist.stream().filter(l -> l.getStrategyId().equals(stats.getStrategyId())).collect(Collectors.toList());
        // if (CollectionUtils.isNotEmpty(matchList))
        // {
        // QuantStrategyProfit firstStats = matchList.get(0);
        // if (firstStats.getDisplayTime().equals(firstSec))
        // {
        // firstProfit = firstStats.getProfit();
        // }
        // }
        // stats.setProfit(stats.getProfit().subtract(firstProfit).setScale(2, BigDecimal.ROUND_DOWN));
        // });
        // map.put("dayRank", daylist);
        map.put("hisRank", hislist);
        map.put("code", 200);
        return map;
    }
    
    /**
     * 查询波动排名
     * @return
     */
    @PostMapping("/changeRank")
    public Map<String, Object> changeRank()
    {
        getOnlineUserId();
        Map<String, Object> map = new HashMap<>();
        QuantQuoteChange query = new QuantQuoteChange();
        query.setStartTime(DateUtils.getCurrentDateFirstSec());
        query.setChangeRate(new BigDecimal("0.0015"));
        List<QuantQuoteChange> list0015 = quantQuoteChangeService.findList(query);
        map.put("changeRank0015", list0015);
        query.setChangeRate(new BigDecimal("0.005"));
        List<QuantQuoteChange> list005 = quantQuoteChangeService.findList(query);
        map.put("changeRank005", list005);
        map.put("code", 200);
        return map;
    }
    
    /**
     * 查询溢价率排名
     * @return
     */
    @PostMapping("/premiumRank")
    public Map<String, Object> premiumRank()
    {
        getOnlineUserId();
        Map<String, Object> map = new HashMap<>();
        map.put("premiumRank", TradeConst.premiumRateRankList);
        map.put("code", 200);
        return map;
    }
    
    @PostMapping("/stats")
    public Map<String, Object> stats(Long id)
    {
        return quantStrategyProfitService.getProfitStats(id);
    }


    @PostMapping("/gridCount")
    public Map<String, Object> gridCount()
    {
        getOnlineUserId();
        Map<String, Object> map = new HashMap<>();
        // 历史最后一笔统计
        List<GridHedgeContModel> hislist = quantGridStatsService.findHedgeCount(null);
        // 当天最后一笔统计
        // List<QuantStrategyProfit> daylist = quantStrategyProfitService.findRank(DateUtils.getCurrentDateFirstSec());
        // // 当天第一笔统计
        // long firstSec = DateUtils.getCurrentDateFirstSec();
        // List<QuantStrategyProfit> firstlist = quantStrategyProfitService.findTodayFirstStats(firstSec);
        // daylist.forEach(stats -> {
        // BigDecimal firstProfit = BigDecimal.ZERO;
        // List<QuantStrategyProfit> matchList = firstlist.stream().filter(l -> l.getStrategyId().equals(stats.getStrategyId())).collect(Collectors.toList());
        // if (CollectionUtils.isNotEmpty(matchList))
        // {
        // QuantStrategyProfit firstStats = matchList.get(0);
        // if (firstStats.getDisplayTime().equals(firstSec))
        // {
        // firstProfit = firstStats.getProfit();
        // }
        // }
        // stats.setProfit(stats.getProfit().subtract(firstProfit).setScale(2, BigDecimal.ROUND_DOWN));
        // });
        // map.put("dayRank", daylist);
        map.put("data", hislist);
        map.put("code", 200);
        return map;
    }
}
