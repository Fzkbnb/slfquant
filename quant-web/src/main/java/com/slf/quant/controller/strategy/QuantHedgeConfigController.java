package com.slf.quant.controller.strategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.slf.quant.config.BaseController;
import com.slf.quant.domain.ServiceVO;
import com.slf.quant.domain.SuccessCode;
import com.slf.quant.facade.bean.PaginateResult;
import com.slf.quant.facade.bean.Pagination;
import com.slf.quant.facade.entity.strategy.QuantHedgeConfig;
import com.slf.quant.facade.service.strategy.QuantHedgeConfigService;
import com.slf.quant.facade.service.strategy.QuantStrategyProfitService;

/**
 * @author slf
 * @date 2019/7/18 12:09
 * @description Controller控制器
 */
@RestController
@RequestMapping("/strategy/quantHedgeConfig")
public class QuantHedgeConfigController extends BaseController
{
    @Autowired(required = false)
    private QuantHedgeConfigService    quantHedgeConfigService;
    
    @Autowired(required = false)
    private QuantStrategyProfitService quantStrategyProfitService;
    
    @PostMapping("/list")
    // @RequiresPermissions(value = "查询列表")
    public Map<String, Object> list(Pagination pagination, QuantHedgeConfig entity)
    {
        Long accountId = getOnlineUserId();
        entity.setAccountId(accountId);
        PaginateResult<QuantHedgeConfig> result = quantHedgeConfigService.search(pagination, entity);
        Map<String, Object> map = new HashMap<>();
        map.put("total", result.getPage().getTotalRows());
        map.put("rows", result.getList());
        return map;
    }
    
    /**
     * 添加或修改
     */
    @RequestMapping("/save")
    // @RequiresPermissions(value = "角色管理")
    public ServiceVO save(QuantHedgeConfig entity)
    {
        Long accountId = getOnlineUserId();
        entity.setAccountId(accountId);
        if (null == entity.getId())
        {
            // 新增模式下，需要为以下字段赋默认值
            entity.setCurrency(entity.getLongSymbol().substring(0, entity.getLongSymbol().indexOf("-")));
            entity.setCreateTime(System.currentTimeMillis());
            entity.setExchange(entity.getExchangeAccount().substring(0, entity.getExchangeAccount().indexOf("-")));
        }
        // 目前最高写死10倍杠杆
        if (entity.getLeverRate().compareTo(BigDecimal.TEN) == 1)
        {
            entity.setLeverRate(BigDecimal.TEN);
        }
        entity.setUpdateTime(System.currentTimeMillis());
        quantHedgeConfigService.save(entity);
        return new ServiceVO<>(SuccessCode.SUCCESS_CODE, SuccessCode.SUCCESS_MESS);
    }
    
    /**
     * 删除
     * @return
     */
    @RequestMapping("/delete")
    // @RequiresPermissions(value = "角色管理")
    public ServiceVO delete(Long id)
    {
        quantHedgeConfigService.delete(id);
        return new ServiceVO<>(SuccessCode.SUCCESS_CODE, SuccessCode.SUCCESS_MESS);
    }
}
