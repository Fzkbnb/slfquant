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
 * @author fzk
 * @website https://el-admin.vip
 * @description /
 * @date 2020-07-20
 **/
@Data
public class QuantQuoteChange extends BaseEntity
{
    private String     exchange;    // 交易所名称
    
    private String     currency;    // 币种
    
    private String     symbol;      // 交易对
    
    private Integer    changeCount; // 波动次数
    
    private BigDecimal changeRate;  // 波动阈值
    
    private Long       startTime;   // 开始时间
    
    private BigDecimal openPrice;   // 开盘价
    
    private BigDecimal closePrice;  // 收盘价
    
    private BigDecimal basePrice;   // 基准价
    
    private Long       updateTime;  // 更新时间
}