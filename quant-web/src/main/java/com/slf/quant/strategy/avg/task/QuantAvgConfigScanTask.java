package com.slf.quant.strategy.avg.task;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.slf.quant.facade.consts.KeyConst;
import com.slf.quant.facade.entity.strategy.QuantApiConfig;
import com.slf.quant.facade.entity.strategy.QuantAvgConfig;
import com.slf.quant.facade.service.strategy.QuantApiConfigService;
import com.slf.quant.facade.service.strategy.QuantAvgConfigService;
import com.slf.quant.facade.utils.EncryptUtils2;
import com.slf.quant.strategy.avg.client.AbstractAvgClient;
import com.slf.quant.strategy.avg.client.OkexAvgClient;
import com.slf.quant.strategy.consts.TradeConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QuantAvgConfigScanTask
{
    @Autowired
    private QuantAvgConfigService    quantAvgConfigService;
    
    @Autowired
    private QuantApiConfigService    quantApiConfigService;
    
    private ScheduledExecutorService executorService;
    
    public void start()
    {
        // 首次启动，将所有策略配置重置为停止状态
        quantAvgConfigService.changeAllStatus(0);
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(() -> {
            try
            {
                List<QuantAvgConfig> runList = quantAvgConfigService.selectAll();
                runList.forEach(entity -> {
                    try
                    {
                        if (entity.getStatus().equals(0))
                        {
                            TradeConst.avg_running_map.remove(entity.getId());
                        }
                        else if ((entity.getStatus().equals(3) || entity.getStatus().equals(1)) && (!TradeConst.avg_running_map.containsKey(entity.getId())))
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
                            String secretKey = EncryptUtils2.desDecrypt(apiConfig.getSecretKey());
                            AbstractAvgClient client = null;
                            if (KeyConst.EXCHANGE_HUOBI.equalsIgnoreCase(entity.getExchange()))
                            {
                            }
                            else if (KeyConst.EXCHANGE_OKEX.equalsIgnoreCase(entity.getExchange()))
                            {
                                String passPhrase = EncryptUtils2.desDecrypt(apiConfig.getPassPhrase());
                                QuantAvgConfig config = new QuantAvgConfig();
                                BeanUtils.copyProperties(entity, config);
                                // okex交割合约
                                client = new OkexAvgClient(config, apiKey, secretKey, passPhrase);
                            }
                            if (null != client)
                            {
                                // 客户端创建完成，启动
                                log.info("【{}】创建客户端成功！");
                                quantAvgConfigService.changeStatus(entity.getId(), 1);
                                client.start();
                                TradeConst.avg_running_map.put(entity.getId(), client);
                            }
                            else
                            {
                                log.info("【{}】创建客户端失败！");
                            }
                        }
                        else if (entity.getStatus().equals(4))
                        {
                            stop(entity.getId());
                        }
                        else if (entity.getStatus().equals(1) && TradeConst.avg_running_map.containsKey(entity.getId()))
                        {
                            // 如果策略运行时绑定的api被解绑，同样需要关闭策略程序，并主动将启用状态重置为禁用状态。
                            QuantApiConfig apiConfig = quantApiConfigService.findByAccountIdAndExchangeAndExchangeAccount(entity.getAccountId(), entity.getExchange(),
                                    entity.getExchangeAccount());
                            AbstractAvgClient client = TradeConst.avg_running_map.get(entity.getId());
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
                        log.info("扫描处理单个动态平衡策略配置任务异常：{}", e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                });
            }
            catch (Exception e)
            {
                log.error("动态平衡策略配置子扫描轮询任务异常：{}", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }, 0, 5000, TimeUnit.MILLISECONDS);
    }
    
    private void stop(Long id)
    {
        quantAvgConfigService.changeStatus(id, 4);
        AbstractAvgClient client = TradeConst.avg_running_map.get(id);
        if (null != client)
        {// 已发出停止指令则不需要重复停
            client.setStopFlag(true);
        }
    }
}
