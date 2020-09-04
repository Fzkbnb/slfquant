package com.slf.quant.facade.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class OkexMarketModel implements Serializable
{
    private String table;
    
    private String instrument_id;
    
    private String data;
}
