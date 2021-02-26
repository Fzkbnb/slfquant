/*
 * Copyright 2017 BLOCAIN, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.facade.service.strategy;

import com.slf.quant.facade.bean.BaseService;
import com.slf.quant.facade.entity.strategy.QuantGridStats;
import com.slf.quant.facade.model.GridContractSumInfo;
import com.slf.quant.facade.model.GridHedgeContModel;

import java.math.BigDecimal;
import java.util.List;

public interface QuantGridStatsService extends BaseService<QuantGridStats>
{
    GridContractSumInfo findSumInfo(Long id, int i, BigDecimal contractPerValue);

    List<GridHedgeContModel> findHedgeCount(Long strategyId);
}
