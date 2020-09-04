/*
 * Copyright 2017 BLOCAIN, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.facade.service.strategy;

import com.slf.quant.facade.bean.BaseService;
import com.slf.quant.facade.entity.strategy.QuantGridStats;
import com.slf.quant.facade.model.GridContractSumInfo;

import java.math.BigDecimal;

public interface QuantGridStatsService extends BaseService<QuantGridStats>
{
    GridContractSumInfo findSumInfo(Long id, int i, BigDecimal contractPerValue);
}
