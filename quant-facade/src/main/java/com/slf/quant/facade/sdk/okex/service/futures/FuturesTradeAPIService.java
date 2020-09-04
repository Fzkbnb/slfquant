package com.slf.quant.facade.sdk.okex.service.futures;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.slf.quant.facade.sdk.okex.bean.futures.param.*;
import com.slf.quant.facade.sdk.okex.bean.futures.result.CancelFuturesOrdeResult;
import com.slf.quant.facade.sdk.okex.bean.futures.result.FuturesOrderResult;
import com.slf.quant.facade.sdk.okex.bean.futures.result.OrderResult;
import retrofit2.http.Body;

/**
 * Futures Trade API Service
 *
 * @author Tony Tian
 * @version 1.0.0
 * @date 2018/3/9 18:52
 */
public interface FuturesTradeAPIService {

    /**
     * Get all of futures contract position list
     */
    JSONObject getPositions();

    /**
     * Get the futures contract product position
     *
     * @param instrumentId The id of the futures contract eg: BTC-USD-0331"
     */
    JSONObject getInstrumentPosition(String instrumentId);

    /**
     * Get all of futures contract account list
     */
    JSONObject getAccounts();


    JSONObject getAccountsByCurrency(String underlying);


    JSONArray getAccountsLedgerByCurrency(String underlying, String after, String before, String limit, String type);

    /**
     * Get the futures contract product holds
     *
     * @param instrumentId The id of the futures contract eg: BTC-USD-0331"
     */
    JSONObject getAccountsHoldsByInstrumentId(String instrumentId);

    /**
     * Create a new order
     */
    OrderResult order(Order order);

    /**
     * Batch create new order.(Max of 5 orders are allowed per request))
     */
    JSONObject orders(Orders orders);

    /**
     * Cancel the order
     *
     * @param instrument_id The id of the futures contract eg: BTC-USD-0331"
     * @param order_id   the order id provided by okex.com eg: 372238304216064
     */
    JSONObject cancelOrderByOrderId(String instrument_id, String order_id);

    JSONObject cancelOrderByClientOid(String instrument_id, String client_oid);

    /**
     * Batch Cancel the orders of this product id
     *
     * @param instrumentId The id of the futures contract eg: BTC-USD-0331"
     */
    JSONObject cancelOrders(String instrumentId, CancelOrders cancelOrders);


    JSONObject getOrders(String instrument_id, String state, String after, String before, String limit);

    /**
     * Get all of futures contract a order by order id
     *
     * @param instrument_id  eg: futures id
     */
    JSONObject getOrderByOrderId(String instrument_id, String order_id);
    JSONObject getOrderByClientOid(String instrument_id, String client_oid);

    JSONArray getFills(String instrument_id, String order_id, String before, String after, String limit);

    /**
     * Get the futures LeverRate
     *
     * @param underlying eg: btc
     */
    JSONObject getInstrumentLeverRate(String underlying);


    /**
     * Change the futures Fixed LeverRate
     *
     * @param underlying       eg: btc-usd
     * @param instrumentId   eg: BTC-USD-190628
     * @param direction      eg: long
     * @param leverage       eg: 10
     * @return
     */
    JSONObject changeLeverageOnFixed(String underlying, String instrumentId, String direction, String leverage);

    /**
     * Change the futures Cross LeverRate
     *
     * @param underlying      eg: btc
     * @param leverage      eg: 10
     * @return
     */
    JSONObject changeLeverageOnCross(String underlying, String leverage);

    JSONObject closePositions(ClosePositions closePositions);

    JSONObject cancelAll(CancelAll cancelAll);

    JSONObject changeMarginMode(ChangeMarginMode changeMarginMode);

    JSONObject changeLiquiMode(ChangeLiquiMode changeLiquiMode);

    /**
     * 策略委托下单
     * @param futuresOrderParam
     * @return
     */
    FuturesOrderResult futuresOrder(@Body FuturesOrderParam futuresOrderParam);

    /**
     * 策略委托撤单
     * @param cancelFuturesOrder
     * @return
     */
    CancelFuturesOrdeResult cancelFuturesOrder(@Body CancelFuturesOrder cancelFuturesOrder);

    /**
     * 查看策略委托订单
     * @param instrument_id
     * @param order_type
     * @param status
     * @param algo_ids
     * @param after
     * @param before
     * @param limit
     * @return
     */
    String findFuturesOrder(String instrument_id,
                            String order_type,
                            String status,
                            String algo_ids,
                            String after,
                            String before,
                            String limit);

    JSONObject getTradeFee();
}
