package com.slf.quant.facade.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class StrategyStatusModel implements Serializable
{
    private String key;
    
    private String value;
}
