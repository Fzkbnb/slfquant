package com.slf.quant.facade.model.hedge;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Ticker {
    private BigDecimal shortAsk;

    private BigDecimal shortBid;

    private BigDecimal longAsk;

    private BigDecimal longBid;

    private BigDecimal openRatio; // openRatio = (shortBid-longAsk)/longAsk

    private BigDecimal closeRatio;// closeRatio = (shortAsk-longBid)/longBid

    private BigDecimal slipRatio;
}
