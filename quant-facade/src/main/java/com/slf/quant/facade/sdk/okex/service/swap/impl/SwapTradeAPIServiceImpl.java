package com.slf.quant.facade.sdk.okex.service.swap.impl;

import com.alibaba.fastjson.JSONObject;
import com.slf.quant.facade.sdk.okex.bean.swap.param.*;
import com.slf.quant.facade.sdk.okex.client.APIClient;
import com.slf.quant.facade.sdk.okex.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.service.swap.SwapTradeAPIService;
import com.slf.quant.facade.sdk.okex.utils.JsonUtils;

public class SwapTradeAPIServiceImpl implements SwapTradeAPIService
{
    private APIClient    client;
    
    private SwapTradeAPI api;
    
    private SwapTradeAPI swapTradeAPI;
    
    public SwapTradeAPIServiceImpl()
    {
    }
    
    public SwapTradeAPIServiceImpl(APIConfiguration config)
    {
        this.client = new APIClient(config);
        this.api = client.createService(SwapTradeAPI.class);
    }
    
    /**
     * 下单
     *
     * @param ppOrder
     * @return
     */
    @Override
    public Object order(PpOrder ppOrder)
    {
        System.out.println("下单参数：：：：：：");
        System.out.println(JsonUtils.convertObject(ppOrder, PpOrder.class));
        // return this.client.executeSync(this.api.order(JsonUtils.convertObject(ppOrder, PpOrder.class)));
        return this.client.executeSync(this.api.order(ppOrder));
    }
    
    /**
     * 批量下单
     *
     * @param ppOrders
     * @return
     */
    @Override
    public String orders(PpOrders ppOrders)
    {
        return this.client.executeSync(this.api.orders(JsonUtils.convertObject(ppOrders, PpOrders.class)));
    }
    
    @Override
    public String getOrders()
    {
        return null;
    }
    
    /**
     * 撤单
     *
     * @param instrument_id
     * @param order_id
     * @return
     */
    @Override
    public String cancelOrderByOrderId(String instrument_id, String order_id)
    {
        return this.client.executeSync(this.api.cancelOrderByOrderId(instrument_id, order_id));
    }
    
    @Override
    public String cancelOrderByClientOid(String instrument_id, String client_oid)
    {
        return this.client.executeSync(this.api.cancelOrderByClientOid(instrument_id, client_oid));
    }
    
    @Override
    public JSONObject closePosition(String instrument_id, String direct)
    {
        JSONObject json = new JSONObject();
        String dir = direct.equals("buy") ? "long" : "short";
        json.put("instrument_id", instrument_id);
        json.put("direction", dir);
        return this.client.executeSync(this.api.closePosition(json));
    }
    
    /**
     * 批量撤单
     *
     * @param instrumentId
     * @param ppCancelOrderVO
     * @return
     */
    @Override
    public String cancelOrders(String instrumentId, PpCancelOrderVO ppCancelOrderVO)
    {
        return this.client.executeSync(this.api.cancelOrders(instrumentId, JsonUtils.convertObject(ppCancelOrderVO, PpCancelOrderVO.class)));
    }
    
    // 委托策略下单
    @Override
    public String swapOrderAlgo(SwapOrderParam swapOrderParam)
    {
        System.out.println("begin swapOrder-----");
        return this.client.executeSync(this.api.swapOrderAlgo(swapOrderParam));
    }
    
    // 委托策略撤单
    @Override
    public String cancelOrderAlgo(CancelOrderAlgo cancelOrderAlgo)
    {
        System.out.println("canceling the algo order");
        return this.client.executeSync(this.api.cancelOrderAlgo(cancelOrderAlgo));
    }
    
    // 获取委托单列表
    @Override
    public String getSwapOrders(String instrument_id, String order_type, String status, String algo_id, String before, String after, String limit)
    {
        return this.client.executeSync(this.api.getSwapOrders(instrument_id, order_type, status, algo_id, before, after, limit));
    }
}
