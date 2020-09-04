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

/**
* @website https://el-admin.vip
* @description /
* @author fzk
* @date 2020-07-17
**/
@Data
public class QuantGridStats extends BaseEntity
{
    private Long       accountId;  // 账户id
    
    private Long       strategyId; // 策略配置id
    
    private Long       buyOrderId; // 买单id
    
    private Long       sellOrderId;// 卖单id
    
    private BigDecimal buyCont;    // 买入张数
    
    private BigDecimal sellCont;   // 卖出张数
    
    private BigDecimal buyPrice;   // 买入成交价格
    
    private BigDecimal sellPrice;  // 卖出成交价格
    
    private BigDecimal buyUsdt; //买入金额
    
    private BigDecimal sellUsdt;//卖出金额
    
    private String     exchange;   // 交易所名称
    
    private String     currency;   // 币种
    
    private Long       updateTime; // 更新记录时间
    
    private Long       buyTime;    // 买单完成时间
    
    private Long       sellTime;   // 卖单完成时间
    
    private Integer    orderStatus;// 对冲状态
    
    private String     firstDirect;// 优先下单方向
}