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
 **/
@Data
public class QuantGridProfit extends BaseEntity
{
    private Long       strategyId;  // 策略id
    
    private BigDecimal profit;      // 盈亏
    
    private Long       displayTime; // 更新时间
}