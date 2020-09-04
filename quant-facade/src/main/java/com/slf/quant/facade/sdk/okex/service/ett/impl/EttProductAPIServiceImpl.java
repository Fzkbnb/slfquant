package com.slf.quant.facade.sdk.okex.service.ett.impl;


import com.slf.quant.facade.sdk.okex.bean.ett.result.EttConstituentsResult;
import com.slf.quant.facade.sdk.okex.bean.ett.result.EttSettlementDefinePrice;
import com.slf.quant.facade.sdk.okex.client.APIClient;
import com.slf.quant.facade.sdk.okex.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.service.ett.EttProductAPIService;

import java.util.List;

/**
 * @author chuping.cui
 * @date 2018/7/5
 */
public class EttProductAPIServiceImpl implements EttProductAPIService {

    private final APIClient client;
    private final EttProductAPI api;

    public EttProductAPIServiceImpl(APIConfiguration config) {
        this.client = new APIClient(config);
        this.api = this.client.createService(EttProductAPI.class);
    }

    @Override
    public EttConstituentsResult getConstituents(String ett) {
        return this.client.executeSync(this.api.getConstituents(ett));
    }

    @Override
    public List<EttSettlementDefinePrice> getDefinePrice(String ett) {
        return this.client.executeSync(this.api.getDefinePrice(ett));
    }
}
