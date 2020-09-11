/*
 * Copyright 2017 BLOCAIN, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.facade.service.strategy;

import com.slf.quant.facade.bean.BaseService;
import com.slf.quant.facade.entity.strategy.QuantStrategyProfit;

import java.util.List;

public interface QuantStrategyProfitService extends BaseService<QuantStrategyProfit>
{
    List<QuantStrategyProfit> findRank(Long minTime);

    List<QuantStrategyProfit> findTodayFirstStats(Long currentDateFirstSec);
}
