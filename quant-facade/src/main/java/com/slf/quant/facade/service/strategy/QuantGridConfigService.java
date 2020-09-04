/*
 * Copyright 2017 BLOCAIN, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.facade.service.strategy;

import com.slf.quant.facade.bean.BaseService;
import com.slf.quant.facade.entity.strategy.QuantGridConfig;


public interface QuantGridConfigService extends BaseService<QuantGridConfig>
{

    void changeStatus(Long id, int i);

    void changeAllStatus(int i);
}
