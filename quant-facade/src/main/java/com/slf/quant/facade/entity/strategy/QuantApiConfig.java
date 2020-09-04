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

/**
 * @author fzk
 * @date 2020-07-20
 **/
@Data
public class QuantApiConfig extends BaseEntity
{
    private Long    accountId;      // 账户id
    
    private String  exchange;       // 交易所名称
    
    private String  exchangeAccount;// 交易所账号
    
    private String  apiKey;         // apikey
    
    private String  secretKey;      // secretKey
    
    private String  passPhrase;     // passPhrase
    
    private Integer authType;       // 权限类型
    
    private String  divisor;        // 加密因子
    
    private String  remark;         // 备注
    
    private Long    createTime;     // 创建时间
    
    private Long    updateTime;     // 更新时间
}