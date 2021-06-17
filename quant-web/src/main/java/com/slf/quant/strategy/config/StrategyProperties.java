package com.slf.quant.strategy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * StrategyProperties：策略运行环境配置
 *
 * @author: fzk
 * @date: 2021-04-12 10:20
 */
@Data
@Component
@ConfigurationProperties(value = "com.slfquant.strategy")
public class StrategyProperties
{
    private Boolean enableOkexV5;
    
    private String  env;
}
