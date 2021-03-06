package com.slf.quant.facade.sdk.okex.service.ett.impl;


import com.slf.quant.facade.sdk.okex.bean.ett.param.EttCreateOrderParam;
import com.slf.quant.facade.sdk.okex.bean.ett.result.CursorPager;
import com.slf.quant.facade.sdk.okex.bean.ett.result.EttCancelOrderResult;
import com.slf.quant.facade.sdk.okex.bean.ett.result.EttCreateOrderResult;
import com.slf.quant.facade.sdk.okex.bean.ett.result.EttOrder;
import com.slf.quant.facade.sdk.okex.client.APIClient;
import com.slf.quant.facade.sdk.okex.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.service.ett.EttOrderAPIService;

import java.math.BigDecimal;

/**
 * @author chuping.cui
 * @date 2018/7/5
 */
public class EttOrderAPIServiceImpl implements EttOrderAPIService {

    private final APIClient client;
    private final EttOrderAPI api;

    public EttOrderAPIServiceImpl(APIConfiguration config) {
        this.client = new APIClient(config);
        this.api = this.client.createService(EttOrderAPI.class);
    }

    @Override
    public EttCreateOrderResult createOrder(String ett, Integer type, BigDecimal amount, BigDecimal size, String clientOid) {
        EttCreateOrderParam param = new EttCreateOrderParam();
        param.setEtt(ett);
        param.setType(type);
        param.setAmount(amount);
        param.setSize(size);
        param.setClientOid(clientOid);
        return this.client.executeSync(this.api.createOrder(param));
    }

    @Override
    public EttCancelOrderResult cancelOrder(String orderId) {
        return this.client.executeSync(this.api.cancelOrder(orderId));
    }

    @Override
    public CursorPager<EttOrder> getOrder(String ett, Integer type, Integer status, String before, String after, int limit) {
        return this.client.executeSyncCursorPager(this.api.getOrder(ett, type, status, before, after, limit));
    }

    @Override
    public EttOrder getOrder(String orderId) {
        return this.client.executeSync(this.api.getOrder(orderId));
    }
}
