package com.slf.quant.facade.sdk.okex.service.spot.impl;


import com.alibaba.fastjson.JSONArray;
import com.slf.quant.facade.sdk.okex.bean.spot.result.Book;
import com.slf.quant.facade.sdk.okex.bean.spot.result.Product;
import com.slf.quant.facade.sdk.okex.bean.spot.result.Ticker;
import com.slf.quant.facade.sdk.okex.bean.spot.result.Trade;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface SpotProductAPI {

    @GET("/api/spot/v3/instruments")
    Call<List<Product>> getProducts();

    @GET("/api/spot/v3/instruments/{instrument_id}/book")
    Call<Book> bookProductsByProductId(@Path("instrument_id") String instrument_id,
                                       @Query("size") String size,
                                       @Query("depth") String depth);

    @GET("/api/spot/v3/instruments/ticker")
    Call<String> getTickers();

    @GET("/api/spot/v3/instruments/ticker")
    Call<List<Ticker>> getTickers1();


    @GET("/api/spot/v3/instruments/{instrument_id}/ticker")
    Call<Ticker> getTickerByProductId(@Path("instrument_id") String product);


    @GET("/api/spot/v3/instruments/{instrument_id}/trades")
    Call<List<Trade>> getTrades(@Path("instrument_id") String instrument_id,
                                @Query("limit") String limit);

    @GET("/api/spot/v3/instruments/{instrument_id}/candles")
    Call<JSONArray> getCandles(@Path("instrument_id") String instrument_id,
                               @Query("granularity") String granularity,
                               @Query("start") String start,
                               @Query("end") String end);

    @GET("/api/spot/v3/instruments/{instrument_id}/candles")
    Call<List<String[]>> getCandles_1(@Path("instrument_id") String product,
                                      @Query("granularity") String type,
                                      @Query("start") String start,
                                      @Query("end") String end);

    @GET("/api/index/v3/{instrument_id}/constituents")
    Call<String> getIndex(@Path("instrument_id") String instrument_id);

}
