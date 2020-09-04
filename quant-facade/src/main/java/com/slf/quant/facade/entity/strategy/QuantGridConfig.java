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

import com.slf.quant.facade.bean.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fzk
 * @website https://el-admin.vip
 * @description /
 * @date 2020-07-20
 **/
@Data
public class QuantGridConfig extends BaseEntity
{
    private Long       accountId;      // 账户id
    
    private String     exchange;       // 交易所名称
    
    private String     exchangeAccount;// 交易所账号
    
    private String     currency;       // 币种
    
    private String     contractCode;   // 合约代码
    
    private String     firstDirect;    // 优先下单方向
    
    private BigDecimal entrustCont;    // 委托张数
    
    private Integer    enableMaker;    // 是否开启maker
    
    private BigDecimal priceDiffRate;  // 网格价差率
    
    private BigDecimal profitRate;     // 止盈价差率
    
    private BigDecimal maxBuyPrice;    // 最大买入价
    
    private BigDecimal minSellPrice;   // 最小卖出价
    
    private BigDecimal feeRate;        // 手续费率
    
    private BigDecimal leverRate;      // 杠杆率
    
    private Integer    status;         // 运行状态
    
    private Long       createTime;     // 配置创建时间
    
    private Long       updateTime;     // 配置更新时间
}