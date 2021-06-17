package com.slf.quant.facade.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class OkexV5MarketModel implements Serializable
{
    private String arg;
    
    private String instrument_id;
    
    private String data;
}
