/*
 * Copyright 2017 BLOCAIN, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.facade.service.strategy;

import com.slf.quant.facade.bean.BaseService;
import com.slf.quant.facade.entity.strategy.QuantApiConfig;

public interface QuantApiConfigService extends BaseService<QuantApiConfig>
{
    QuantApiConfig findByAccountIdAndExchangeAndExchangeAccount(Long accountId, String exchange, String exchangeAccount);

    void saveConfig(QuantApiConfig entity);
}
