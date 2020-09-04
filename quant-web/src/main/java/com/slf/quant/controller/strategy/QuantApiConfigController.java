package com.slf.quant.controller.strategy;

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
import com.slf.quant.facade.entity.strategy.QuantApiConfig;
import com.slf.quant.facade.service.strategy.QuantApiConfigService;

/**
 * @author slf
 * @date 2019/7/18 12:09
 * @description Controller控制器
 */
@RestController
@RequestMapping("/strategy/quantApiConfig")
public class QuantApiConfigController extends BaseController
{
    @Autowired(required = false)
    private QuantApiConfigService quantApiConfigService;
    
    /**
     * 查询所有
     * @return
     */
    @PostMapping("/list")
    public Map<String, Object> list(Pagination pagination, QuantApiConfig entity)
    {
        PaginateResult<QuantApiConfig> result = quantApiConfigService.search(pagination, entity);
        Map<String, Object> map = new HashMap<>();
        map.put("total", result.getPage().getTotalRows());
        map.put("rows", result.getList());
        return map;
    }
    
    /**
     * 添加或修改
     */
    @RequestMapping("/save")
    public ServiceVO save(QuantApiConfig entity)
    {
        Long accountId = getOnlineUserId();
        entity.setAccountId(accountId);
        quantApiConfigService.saveConfig(entity);
        return new ServiceVO<>(SuccessCode.SUCCESS_CODE, SuccessCode.SUCCESS_MESS);
    }
    
    /**
     * 删除
     * @return
     */
    @RequestMapping("/delete")
    public ServiceVO delete(Long id)
    {
        quantApiConfigService.delete(id);
        return new ServiceVO<>(SuccessCode.SUCCESS_CODE, SuccessCode.SUCCESS_MESS);
    }
}
