package com.slf.quant.facade.sdk.okex.service.spot.impl;


import com.alibaba.fastjson.JSONObject;
import com.slf.quant.facade.sdk.okex.bean.spot.param.OrderParamDto;
import com.slf.quant.facade.sdk.okex.bean.spot.param.PlaceOrderParam;
import com.slf.quant.facade.sdk.okex.bean.spot.result.Fills;
import com.slf.quant.facade.sdk.okex.bean.spot.result.OrderInfo;
import com.slf.quant.facade.sdk.okex.bean.spot.result.OrderResult;
import com.slf.quant.facade.sdk.okex.client.APIClient;
import com.slf.quant.facade.sdk.okex.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.service.spot.MarginOrderAPIService;

import java.util.List;
import java.util.Map;

public class MarginOrderAPIServiceImpl implements MarginOrderAPIService {
    private final APIClient client;
    private final MarginOrderAPI marginOrderAPI;

    public MarginOrderAPIServiceImpl(final APIConfiguration config) {
        this.client = new APIClient(config);
        this.marginOrderAPI = this.client.createService(MarginOrderAPI.class);
    }

    @Override
    public OrderResult addOrder(final PlaceOrderParam order) {
        return this.client.executeSync(this.marginOrderAPI.addOrder(order));
    }

    @Override
    public Map<String, List<OrderResult>> addOrders(final List<PlaceOrderParam> order) {
        return this.client.executeSync(this.marginOrderAPI.addOrders(order));
    }

    @Override
    public OrderResult cancleOrderByOrderId(final PlaceOrderParam order, final String orderId) {
        return this.client.executeSync(this.marginOrderAPI.cancleOrdersByProductIdAndOrderId(orderId, order));
    }

    @Override
    public OrderResult cancleOrderByOrderId_post(final PlaceOrderParam order, final String orderId) {
        return this.client.executeSync(this.marginOrderAPI.cancleOrdersByProductIdAndOrderId_1(orderId, order));
    }

    @Override
    public Map<String, JSONObject> cancleOrders(final List<OrderParamDto> cancleOrders) {
        return this.client.executeSync(this.marginOrderAPI.batchCancleOrders(cancleOrders));
    }

    @Override
    public Map<String, Object> cancleOrders_post(final List<OrderParamDto> cancleOrders) {
        return this.client.executeSync(this.marginOrderAPI.batchCancleOrders_1(cancleOrders));
    }

    @Override
    public OrderInfo getOrderByProductIdAndOrderId(final String instrumentId, final String orderId) {
        return this.client.executeSync(this.marginOrderAPI.getOrderByProductIdAndOrderId(orderId, instrumentId));
    }

    @Override
    public OrderInfo getOrderByClientOid(String instrumentId, String client_oid) {
        return client.executeSync(this.marginOrderAPI.getOrderByClientOid(instrumentId,client_oid));
    }

    @Override
    public List<OrderInfo> getOrders(final String instrumentId, final String status, final String from, final String to, final String limit) {
        return this.client.executeSync(this.marginOrderAPI.getOrders(instrumentId, status, from, to, limit));
    }

    @Override
    public List<OrderInfo> getPendingOrders(final String before, final String after, final String limit, final String instrument_id) {
        return this.client.executeSync(this.marginOrderAPI.getPendingOrders(before, after, limit, instrument_id));
    }

    @Override
    public List<Fills> getFills(final String orderId, final String instrumentId, final String after, final String to, final String limit) {
        return this.client.executeSync(this.marginOrderAPI.getFills(orderId, instrumentId, after, to, limit));
    }
}
