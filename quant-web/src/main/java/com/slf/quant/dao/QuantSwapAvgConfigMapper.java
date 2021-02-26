/*
 * Copyright 2017 BLOCAIN, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.slf.quant.facade.bean.BaseMapper;
import com.slf.quant.facade.entity.strategy.QuantSwapAvgConfig;

@Mapper
public interface QuantSwapAvgConfigMapper extends BaseMapper<QuantSwapAvgConfig>
{
    void changeStatus(@Param("id") Long id, @Param("status") int i);
    
    void changeAllStatus(@Param("status") int i);
}
