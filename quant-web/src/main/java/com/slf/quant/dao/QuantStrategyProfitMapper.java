/*
 * Copyright 2017 BLOCAIN, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.dao;

import org.apache.ibatis.annotations.Mapper;

import com.slf.quant.facade.bean.BaseMapper;
import com.slf.quant.facade.entity.strategy.QuantStrategyProfit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuantStrategyProfitMapper extends BaseMapper<QuantStrategyProfit>
{
    List<QuantStrategyProfit> findRank(@Param("displayTime") Long displayTime);
    
    List<QuantStrategyProfit> findTodayFirstStats(@Param("displayTime") Long displayTime);
}
