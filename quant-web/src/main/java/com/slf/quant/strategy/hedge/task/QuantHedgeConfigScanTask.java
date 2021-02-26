package com.slf.quant.strategy.hedge.task;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.slf.quant.strategy.hedge.client.usdt.AbstractHedgeUsdtClient;
import com.slf.quant.strategy.hedge.client.usdt.OKexHedgeUsdtClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slf.quant.facade.consts.KeyConst;
import com.slf.quant.facade.entity.strategy.QuantApiConfig;
import com.slf.quant.facade.entity.strategy.QuantHedgeConfig;
import com.slf.quant.facade.service.strategy.QuantApiConfigService;
import com.slf.quant.facade.service.strategy.QuantHedgeConfigService;
import com.slf.quant.facade.utils.EncryptUtils2;
import com.slf.quant.strategy.consts.TradeConst;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QuantHedgeConfigScanTask
{
    @Autowired
    private QuantHedgeConfigService  quantHedgeConfigService;
    
    @Autowired
    private QuantApiConfigService    quantApiConfigService;
    
    // @Autowired
    // private RedisUtils redisUtils;
    private ScheduledExecutorService executorService;
    
    public void start()
    {
        // 首次启动，将所有策略配置重置为停止状态
        quantHedgeConfigService.changeAllStatus(0);
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(() -> {
            try
            {
                // log.info(">>>开始最新网格配置扫描任务<<<");
                List<QuantHedgeConfig> runList = quantHedgeConfigService.selectAll();
                runList.forEach(entity -> {
                    try
                    {
                        if (entity.getStatus().equals(0))
                        {
                            TradeConst.hedge_running_map.remove(entity.getId());
                        }
                        else if ((entity.getStatus().equals(3) || entity.getStatus().equals(1)) && (!TradeConst.hedge_running_map.containsKey(entity.getId())))
                        {
                            // 如果是启用状态且程序未运行，则开启
                            QuantApiConfig apiConfig = quantApiConfigService.findByAccountIdAndExchangeAndExchangeAccount(entity.getAccountId(), entity.getExchange(),
                                    entity.getExchangeAccount());
                            if (null == apiConfig)
                            {
                                log.info("api配置不存在:{}", entity.toString());
                                return;
                            }
                            String apiKey = apiConfig.getApiKey();
                            // 私钥需要经过2次解密：
                            // String redisKey = new StringBuilder(KeyConst.REDISKEY_DIVISOR).append(entity.getId()).toString();
                            // String divisor = (String) redisUtils.get(redisKey);
                            // if (StringUtils.isEmpty(divisor))
                            // {
                            // log.info("加密因子缓存不存在，策略启动失败：{}", entity.getId());
                            // // 将状态改为2
                            // quantHedgeConfigService.changeStatus(entity.getId(), 2);
                            // return;
                            // }
                            // divisor = EncryptUtils.desDecrypt(divisor);
                            String secretKey = EncryptUtils2.desDecrypt(apiConfig.getSecretKey());
                            AbstractHedgeUsdtClient client = null;
                            if (KeyConst.EXCHANGE_HUOBI.equalsIgnoreCase(entity.getExchange()))
                            {
                                // huobi交割合约
                                // client = new HuobiFutureGridClient(entity, apiKey, secretKey, null);
                            }
                            else if (KeyConst.EXCHANGE_OKEX.equalsIgnoreCase(entity.getExchange()))
                            {
                                String passPhrase = EncryptUtils2.desDecrypt(apiConfig.getPassPhrase());
                                QuantHedgeConfig config = new QuantHedgeConfig();
                                BeanUtils.copyProperties(entity, config);
                                // okex交割合约
                                client = new OKexHedgeUsdtClient(config, apiKey, secretKey, passPhrase);
                            }
                            if (null != client)
                            {
                                // 客户端创建完成，启动
                                quantHedgeConfigService.changeStatus(entity.getId(), 1);
                                client.start();
                                TradeConst.hedge_running_map.put(entity.getId(), client);
                            }
                        }
                        else if (entity.getStatus().equals(4))
                        {
                            stop(entity.getId());
                        }
                        else if (entity.getStatus().equals(1) && TradeConst.hedge_running_map.containsKey(entity.getId()))
                        {
                            // 如果策略运行时绑定的api被解绑，同样需要关闭策略程序，并主动将启用状态重置为禁用状态。
                            QuantApiConfig apiConfig = quantApiConfigService.findByAccountIdAndExchangeAndExchangeAccount(entity.getAccountId(), entity.getExchange(),
                                    entity.getExchangeAccount());
                            AbstractHedgeUsdtClient client = TradeConst.hedge_running_map.get(entity.getId());
                            if (null != client)
                            {
                                if (null == apiConfig || (!apiConfig.getApiKey().equalsIgnoreCase(client.getApiKey())))
                                {
                                    log.info("策略运行时api被删除或解绑或替换，将停止策略：{}", entity.toString());
                                    stop(entity.getId());
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        log.info("扫描处理单个网格配置任务异常：{}", e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                });
            }
            catch (Exception e)
            {
                log.error("网格策略配置子扫描轮询任务异常：{}", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }, 0, 5000, TimeUnit.MILLISECONDS);
    }
    
    private void stop(Long id)
    {
        AbstractHedgeUsdtClient client = TradeConst.hedge_running_map.get(id);
        if (null != client)
        {// 已发出停止指令则不需要重复停
            client.setStopFlag(true);
        }
    }
}
