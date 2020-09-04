package com.slf.quant.facade.sdk.okex.service.spot;


import com.alibaba.fastjson.JSONArray;
import com.slf.quant.facade.sdk.okex.bean.spot.result.Book;
import com.slf.quant.facade.sdk.okex.bean.spot.result.Product;
import com.slf.quant.facade.sdk.okex.bean.spot.result.Ticker;
import com.slf.quant.facade.sdk.okex.bean.spot.result.Trade;

import java.util.List;

public interface SpotProductAPIService {

    /**
     * 单个币对行情
     *
     * @param product
     * @return
     */
    Ticker getTickerByProductId(String product);

    /**
     * 行情列表
     *
     * @return
     */
    //List<Ticker> getTickers();
    String getTickers();

    List<Ticker> getTickers1();

    /**
     * @param instrument_id
     * @param size
     * @param depth
     * @return
     */
    Book bookProductsByProductId(String instrument_id, String size, String depth);

    /**
     * 币对列表S
     *
     * @return
     */
    List<Product> getProducts();

    /**
     * 交易列表
     *
     * @param instrument_id
     * @param limit
     * @return
     */
    List<Trade> getTrades(String instrument_id, String limit);

    /**
     * @param instrument_id
     * @param granularity
     * @param start
     * @param end
     * @return
     */
    JSONArray getCandles(String instrument_id, String granularity, String start, String end);

    List<String[]> getCandles_1(String product, String granularity, String start, String end);

    String getIndex(String instrument_id);

}
