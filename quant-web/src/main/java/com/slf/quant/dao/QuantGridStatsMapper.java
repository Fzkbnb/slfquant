/*
 * Copyright 2017 BLOCAIN, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.dao;

import com.slf.quant.facade.entity.strategy.QuantGridStats;
import com.slf.quant.facade.model.GridContractSumInfo;
import com.slf.quant.facade.model.GridHedgeContModel;
import org.apache.ibatis.annotations.Mapper;

import com.slf.quant.facade.bean.BaseMapper;
import com.slf.quant.facade.entity.strategy.QuantGridConfig;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface QuantGridStatsMapper extends BaseMapper<QuantGridStats>
{
    GridContractSumInfo findSumInfo(@Param("strategyId") Long strategyId, @Param("orderStatus") int orderStatus, @Param("contractValue") BigDecimal contractValue);
    
    List<GridHedgeContModel> findHedgeCount(@Param("strategyId") Long strategyId);
}
