package com.slf.quant.facade.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class HuobiMarketModel implements Serializable
{
    private String ch;
    
    private Long   ts;
    
    private String tick;
}
