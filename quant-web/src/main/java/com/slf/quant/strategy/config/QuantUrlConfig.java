package com.slf.quant.strategy.config;

/**
 * 行情url配置类，由于境内可用网址时常发生变更，因此要关注网址失效问题
 */
public class QuantUrlConfig
{
    public static String okex_endpoint                   = "https://www.okex.com";
    
    public static String okex_instruments_future         = "/api/futures/v3/instruments";
    
    public static String huobi_endpoint_future           = "https://api.btcgateway.pro";
    
    public static String huobi_endpoint_spot             = "https://api.huobi.pro";
    
    public static String huobi_endpoint_transfer         = "https://api.huobi.pro";
    
    public static String okex_endpoint_websocket_v3      = "wss://real.okex.com:8443/ws/v3";
    
    public static String okex_endpoint_websocket_v5      = "wss://ws.okex.com:8443/ws/v5/public";
    
    public static String huobi_endpoint_websocket_spot   = "wss://api.huobi.pro/ws";
    
    public static String huobi_endpoint_websocket_future = "wss://api.btcgateway.pro/ws";
    
    public static String huobi_endpoint_websocket_swap   = "wss://api.btcgateway.pro/swap-ws";
    
    public static String bnb_endpoint_websocket_spot     = "wss://stream.binance.com:9443/ws";
    
    public static String bnb_endpoint_websocket_future   = "wss://dstream.binance.com/ws";
    
    public static String http_huobi_otc                  = "https://otc-api.huobi.pro/v1/data/ticker/price";
}
