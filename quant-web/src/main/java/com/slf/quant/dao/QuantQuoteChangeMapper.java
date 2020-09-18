/*
 * Copyright 2017 BLOCAIN, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.dao;

import org.apache.ibatis.annotations.Mapper;

import com.slf.quant.facade.bean.BaseMapper;
import com.slf.quant.facade.entity.strategy.QuantQuoteChange;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuantQuoteChangeMapper extends BaseMapper<QuantQuoteChange>
{
}
