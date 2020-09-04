package com.slf.quant.facade.sdk.okex.service.spot.impl;


import com.slf.quant.facade.sdk.okex.bean.spot.param.OrderAlgoParam;
import com.slf.quant.facade.sdk.okex.bean.spot.param.OrderParamDto;
import com.slf.quant.facade.sdk.okex.bean.spot.param.PlaceOrderParam;
import com.slf.quant.facade.sdk.okex.bean.spot.result.*;
import com.slf.quant.facade.sdk.okex.client.APIClient;
import com.slf.quant.facade.sdk.okex.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.service.spot.SpotOrderAPIServive;

import java.util.List;
import java.util.Map;

/**
 * 币币订单相关接口
 **/
public class SpotOrderApiServiceImpl implements SpotOrderAPIServive {
    private final APIClient client;
    private final SpotOrderAPI spotOrderAPI;

    public SpotOrderApiServiceImpl(final APIConfiguration config) {
        this.client = new APIClient(config);
        this.spotOrderAPI = this.client.createService(SpotOrderAPI.class);
    }

    @Override
    public OrderResult addOrder(final PlaceOrderParam order) {
        return this.client.executeSync(this.spotOrderAPI.addOrder(order));
    }

    @Override
    public Map<String, List<OrderResult>> addOrders(final List<PlaceOrderParam> order) {
        return this.client.executeSync(this.spotOrderAPI.addOrders(order));
    }

    @Override
    public OrderResult cancleOrderByOrderId(final PlaceOrderParam order, final String orderId) {
        return this.client.executeSync(this.spotOrderAPI.cancleOrderByOrderId(orderId, order));
    }

    @Override
    public OrderResult cancleOrderByOrderId_post(final PlaceOrderParam order, final String orderId) {
        return this.client.executeSync(this.spotOrderAPI.cancleOrderByOrderId_1(orderId, order));
    }

    @Override
    public Map<String, Object> batchCancleOrders_2(List<OrderParamDto> cancleOrders) {
        return this.client.executeSync(this.spotOrderAPI.batchCancleOrders_2(cancleOrders));
    }

    @Override
    public Map<String, Object> batch_orderCle(List<OrderParamDto> orderParamDto) {
        return this.client.executeSync(this.spotOrderAPI.batch_orderCle(orderParamDto));
    }

    @Override
    public OrderResult cancleOrderByClientOid(PlaceOrderParam order, String client_oid) {
        return this.client.executeSync(this.spotOrderAPI.cancleOrderByOrderId(client_oid, order));
    }

    @Override
    public Map<String, BatchOrdersResult> cancleOrders(final List<OrderParamDto> cancleOrders) {
        return this.client.executeSync(this.spotOrderAPI.batchCancleOrders(cancleOrders));
    }

    @Override
    public Map<String, Object> cancleOrders_post(final List<OrderParamDto> cancleOrders) {
        return this.client.executeSync(this.spotOrderAPI.batchCancleOrders_1(cancleOrders));
    }



    @Override
    public OrderInfo getOrderByOrderId(final String product, final String orderId) {
        return this.client.executeSync(this.spotOrderAPI.getOrderByOrderId(orderId, product));
    }

    @Override
    public OrderInfo getOrderByClientOid(String instrument_id, String client_oid) {
        return this.client.executeSync(this.spotOrderAPI.getOrderByClientOid(client_oid,instrument_id));
    }

    @Override
    public List<OrderInfo> getOrders(final String product, final String state, final String after, final String before, final String limit) {
        return this.client.executeSync(this.spotOrderAPI.getOrders(product, state, after, before, limit));
    }

    @Override
    public List<OrderInfo> getPendingOrders(final String before, final String after, final String limit, final String instrument_id) {
        return this.client.executeSync(this.spotOrderAPI.getPendingOrders(before, after, limit, instrument_id));
    }

    @Override
    public List<Fills> getFills(final String orderId, final String product, final String before, final String after, final String limit) {
        return this.client.executeSync(this.spotOrderAPI.getFills(orderId, product, before, after, limit));
    }

    @Override
    public OrderAlgoResult addorder_algo(OrderAlgoParam order) {
        return this.client.executeSync(this.spotOrderAPI.addorder_algo(order));
    }

    @Override
    public OrderAlgoResult cancelOrder_algo(OrderAlgoParam order) {
        return this.client.executeSync(this.spotOrderAPI.cancelOrder_algo(order));
    }

    @Override
    public String getAlgoOrder(final String instrument_id, final String order_type, final String status, final String algo_id, final String before, final String after, final String limit) {
        return this.client.executeSync(this.spotOrderAPI.getAlgoOrder(instrument_id,order_type,status,algo_id,before,after,limit));
    }


}
