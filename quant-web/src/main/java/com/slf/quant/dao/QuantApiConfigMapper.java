/*
 * Copyright 2017 BLOCAIN, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.dao;

import com.slf.quant.facade.entity.strategy.QuantApiConfig;
import org.apache.ibatis.annotations.Mapper;

import com.slf.quant.facade.bean.BaseMapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuantApiConfigMapper extends BaseMapper<QuantApiConfig>
{
    QuantApiConfig findByAccountIdAndExchangeAndExchangeAccount(@Param("accountId") Long accountId, @Param("exchange") String exchange,
            @Param("exchangeAccount") String exchangeAccount);
}
