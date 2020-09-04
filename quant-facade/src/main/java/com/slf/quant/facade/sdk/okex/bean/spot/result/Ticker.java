package com.slf.quant.facade.sdk.okex.bean.spot.result;

import lombok.Data;

@Data
public class Ticker
{
    private String last;
    
    private String open_24h;
    
    private String high_24h;
    
    private String low_24h;
    
    private String base_volume_24h;
    
    private String timestamp;
    
    private String quote_volume_24h;
    
    private String best_ask;
    
    private String best_bid;
    
    private String instrument_id;
    
    private String best_ask_size;
    
    private String best_bid_size;
}
