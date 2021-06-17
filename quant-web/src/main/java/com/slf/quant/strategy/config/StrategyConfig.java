package com.slf.quant.strategy.config;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * StrategyConfig：
 *
 * @author: fzk
 * @date: 2021-04-12 10:35
 */
@Component
public class StrategyConfig
{
    @Resource
    private StrategyProperties strategyProperties;
    
    public static boolean     enableOkexV5;

    public static String      env;
    
    @PostConstruct
    public void init()
    {
        enableOkexV5 = strategyProperties.getEnableOkexV5();
        env = strategyProperties.getEnv();
    }
}
