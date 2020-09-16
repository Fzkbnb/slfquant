package com.slf.quant.controller.strategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slf.quant.config.BaseController;
import com.slf.quant.domain.SuccessCode;
import com.slf.quant.facade.bean.PaginateResult;
import com.slf.quant.facade.bean.Pagination;
import com.slf.quant.facade.consts.KeyConst;

import com.slf.quant.facade.entity.strategy.QuantGridConfig;
import com.slf.quant.facade.entity.strategy.QuantStrategyProfit;
import com.slf.quant.facade.model.StrategyStatusModel;
import com.slf.quant.facade.service.strategy.QuantGridConfigService;
import com.slf.quant.facade.service.strategy.QuantStrategyProfitService;
import com.slf.quant.strategy.consts.TradeConst;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.slf.quant.domain.ServiceVO;

/**
 * @author slf
 * @date 2019/7/18 12:09
 * @description Controller控制器
 */
@RestController
@RequestMapping("/strategy/quantGridConfig")
public class QuantGridConfigController extends BaseController
{
    @Autowired(required = false)
    private QuantGridConfigService quantGridConfigService;
    
    @Autowired(required = false)
    private QuantStrategyProfitService quantStrategyProfitService;
    
    /**
     * 查询所有
     * @return
     */
    // @RequestMapping("/listAll")
    // @RequiresPermissions(value = {"用户管理","角色管理"}, logical = Logical.OR)
    // public Map<String,Object> listAll() {
    // return roleService.listAll();
    // }
    @PostMapping("/list")
    // @RequiresPermissions(value = "查询列表")
    public Map<String, Object> list(Pagination pagination, QuantGridConfig entity)
    {
        Long accountId = getOnlineUserId();
        entity.setAccountId(accountId);
        PaginateResult<QuantGridConfig> result = quantGridConfigService.search(pagination, entity);
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
    public ServiceVO save(QuantGridConfig entity)
    {
        Long accountId = getOnlineUserId();
        entity.setAccountId(accountId);
        if (null == entity.getId())
        {
            // 新增模式下，需要为以下字段赋默认值
            entity.setCurrency(entity.getContractCode().substring(0, entity.getContractCode().indexOf("-")));
            entity.setCreateTime(System.currentTimeMillis());
            entity.setEnableMaker(1);
            entity.setExchange(entity.getExchangeAccount().substring(0, entity.getExchangeAccount().indexOf("-")));
        }
        entity.setUpdateTime(System.currentTimeMillis());
        quantGridConfigService.save(entity);
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
        quantGridConfigService.delete(id);
        return new ServiceVO<>(SuccessCode.SUCCESS_CODE, SuccessCode.SUCCESS_MESS);
    }
}
