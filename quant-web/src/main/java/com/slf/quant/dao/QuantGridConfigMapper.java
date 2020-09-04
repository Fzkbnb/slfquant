/*
 * Copyright 2017 BLOCAIN, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.dao;

import com.slf.quant.facade.bean.BaseMapper;
import com.slf.quant.facade.entity.strategy.QuantGridConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuantGridConfigMapper extends BaseMapper<QuantGridConfig>
{
    void changeStatus(@Param("id") Long id, @Param("status") int i);
    
    void changeAllStatus(@Param("status") int i);
}
