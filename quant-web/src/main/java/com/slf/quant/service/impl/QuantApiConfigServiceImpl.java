package com.slf.quant.service.impl;

import com.slf.quant.facade.utils.EncryptUtils2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slf.quant.dao.QuantApiConfigMapper;
import com.slf.quant.facade.bean.BaseServiceImpl;
import com.slf.quant.facade.entity.strategy.QuantApiConfig;
import com.slf.quant.facade.service.strategy.QuantApiConfigService;

/**
 * QuantApiConfigServiceImpl：
 *
 * @author: fzk
 * @date: 2020-08-27 14:37
 */
@Service
public class QuantApiConfigServiceImpl extends BaseServiceImpl<QuantApiConfig> implements QuantApiConfigService
{
    protected QuantApiConfigMapper quantApiConfigMapper;
    
    @Autowired
    public QuantApiConfigServiceImpl(QuantApiConfigMapper quantApiConfigMapper)
    {
        super(quantApiConfigMapper);
        this.quantApiConfigMapper = quantApiConfigMapper;
    }
    
    @Override
    public QuantApiConfig findByAccountIdAndExchangeAndExchangeAccount(Long accountId, String exchange, String exchangeAccount)
    {
        return quantApiConfigMapper.findByAccountIdAndExchangeAndExchangeAccount(accountId, exchange, exchangeAccount);
    }
    
    @Override
    public void saveConfig(QuantApiConfig entity)
    {
        if (null == entity.getId())
        {
            // 新增模式下，需要为以下字段赋默认值
            entity.setCreateTime(System.currentTimeMillis());
            entity.setExchange(entity.getExchangeAccount().substring(0, entity.getExchangeAccount().indexOf("-")));
            // todo 加密因子功能暂时忽略
            entity.setDivisor("default");
            String secretKey = EncryptUtils2.desEncrypt(entity.getSecretKey());
            entity.setSecretKey(secretKey);
            entity.setPassPhrase(EncryptUtils2.desEncrypt(entity.getPassPhrase()));
        }
        entity.setUpdateTime(System.currentTimeMillis());
        save(entity);
    }
}
