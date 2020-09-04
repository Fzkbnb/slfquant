package com.slf.quant.facade.sdk.okex.service.futures.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.slf.quant.facade.sdk.okex.bean.futures.result.*;
import com.slf.quant.facade.sdk.okex.client.APIClient;
import com.slf.quant.facade.sdk.okex.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.service.futures.FuturesMarketAPIService;

import java.util.List;

/**
 * Futures market api
 *
 * @author Tony Tian
 * @version 1.0.0
 * @date 2018/3/9 16:09
 */
public class FuturesMarketAPIServiceImpl implements FuturesMarketAPIService {

    private APIClient client;
    private FuturesMarketAPI api;

    public FuturesMarketAPIServiceImpl(APIConfiguration config) {
        this.client = new APIClient(config);
        this.api = client.createService(FuturesMarketAPI.class);
    }

    @Override
    public List<Instruments> getInstruments() {
        return this.client.executeSync(this.api.getInstruments());
    }

    @Override
    public List<Currencies> getCurrencies() {
        return this.client.executeSync(this.api.getCurrencies());
    }

    @Override
    public Book getInstrumentBook(String instrumentId,  String size,String depth) {
        return this.client.executeSync(this.api.getInstrumentBook(instrumentId, size,depth));
    }

    @Override
    public Ticker getInstrumentTicker(String instrumentId) {
        return this.client.executeSync(this.api.getInstrumentTicker(instrumentId));
    }

    @Override
    public List<Ticker> getAllInstrumentTicker() {
        return this.client.executeSync(this.api.getAllInstrumentTicker());
    }

    @Override
    public List<Trades> getInstrumentTrades(String instrumentId, String after, String before, String limit) {
        return this.client.executeSync(this.api.getInstrumentTrades(instrumentId,  after,  before,  limit));
    }

    @Override
    public JSONArray getInstrumentCandles(String instrument_id, String start, String end, String granularity) {
        return this.client.executeSync(this.api.getInstrumentCandles(instrument_id, start,end, granularity));
    }

    @Override
    public Index getInstrumentIndex(String instrumentId) {
        return this.client.executeSync(this.api.getInstrumentIndex(instrumentId));
    }

    @Override
    public ExchangeRate getExchangeRate() {
        return this.client.executeSync(this.api.getExchangeRate());
    }


    @Override
    public EstimatedPrice getInstrumentEstimatedPrice(String instrumentId) {
        return this.client.executeSync(this.api.getInstrumentEstimatedPrice(instrumentId));
    }

    @Override
    public Holds getInstrumentHolds(String instrumentId) {
        return this.client.executeSync(this.api.getInstrumentHolds(instrumentId));
    }

    @Override
    public PriceLimit getInstrumentPriceLimit(String instrumentId) {
        return this.client.executeSync(this.api.getInstrumentPriceLimit(instrumentId));
    }

    @Override
    public List<Liquidation> getInstrumentLiquidation(String instrumentId, String status, String from, String to, String limit) {
        return this.client.executeSync(this.api.getInstrumentLiquidation(instrumentId, status,  from,  to,  limit));
    }

    @Override
    public JSONObject getMarkPrice(String instrumentId){
        return this.client.executeSync(this.api.getMarkPrice(instrumentId));
    }

    @Override
    public Holds getHolds(String instrumentId) {
        return this.client.executeSync(this.api.getHolds(instrumentId));
    }

}
