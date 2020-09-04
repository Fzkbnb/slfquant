package com.slf.quant.facade.model;


import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import lombok.Data;

@Data
public class QuoteDepth implements Comparable
{
    private long               updateTime;
    
    private BigDecimal ask;
    
    private BigDecimal         bid;
    
    private BigDecimal         avg;
    
    private String             instrument_id;
    
    // 买单列表
    private List<List<String>> bids;
    
    // 卖单列表
    private List<List<String>> asks;
    
    public void buildAskBid()
    {
        if (CollectionUtils.isNotEmpty(bids))
        {
            bid = new BigDecimal(bids.get(0).get(0));
        }
        if (CollectionUtils.isNotEmpty(asks))
        {
            ask = new BigDecimal(asks.get(0).get(0));
        }
        avg = ask.add(bid).divide(BigDecimal.valueOf(2), 8, BigDecimal.ROUND_HALF_UP);
        updateTime = System.currentTimeMillis();
    }
    
    @Override
    public int compareTo(Object o)
    {
        QuoteDepth quoteDepth = (QuoteDepth) o;
        return this.avg.compareTo(quoteDepth.getAvg());
    }
}
