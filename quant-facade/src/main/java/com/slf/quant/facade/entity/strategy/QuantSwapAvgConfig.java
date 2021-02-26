/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package com.slf.quant.facade.entity.strategy;

import java.math.BigDecimal;

import com.slf.quant.facade.bean.BaseEntity;

import lombok.Data;

@Data
public class QuantSwapAvgConfig extends BaseEntity
{
    private Long       accountId;
    
    private String     exchange;
    
    private String     exchangeAccount;
    
    private String     currency;
    
    private String     symbol;
    
    private Integer    status;          // 运行状态
    
    private Long       createTime;
    
    private Long       updateTime;
    
    private BigDecimal firstBasePrice;  // 初始基准价
    
    private BigDecimal currentBasePrice;// 当前基准价
    
    private BigDecimal priceUpRate;     // 行情上涨率触发值
    
    private BigDecimal priceDownRate;   // 行情下跌率触发值
    
    private BigDecimal usdtCapital;     // usdt本金
    
    private BigDecimal enableMargin;    // 可用保证金
    
    private BigDecimal positionAmt;     // 持仓数量
    
    private BigDecimal lastPrice;       // 最新行情
    
    private BigDecimal tradeCount;      // 交易次数
    
    private Integer    positionDirect;  // 持仓方向（1多头-1空头）
}