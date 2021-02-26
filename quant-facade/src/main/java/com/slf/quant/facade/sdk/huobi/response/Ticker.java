package com.slf.quant.facade.sdk.huobi.response;

import java.math.BigDecimal;

public class Ticker {

    // amount	float	以基础币种计量的交易量
    BigDecimal amount;

    // count	integer	交易笔数
    BigDecimal count;

    // open	float	开盘价
    BigDecimal open;

    // close	float	最新价
    BigDecimal close;

    // low	float	最低价
    BigDecimal low;

    // high	float	最高价
    BigDecimal high;

    // vol	float	以报价币种计量的交易量
    BigDecimal vol;

    //symbol	string	交易对，例如btcusdt, ethbtc
    String symbol;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getVol() {
        return vol;
    }

    public void setVol(BigDecimal vol) {
        this.vol = vol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }



    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Ticker{");
        sb.append("amount=").append(amount);
        sb.append(", count=").append(count);
        sb.append(", open=").append(open);
        sb.append(", close=").append(close);
        sb.append(", low=").append(low);
        sb.append(", high=").append(high);
        sb.append(", vol=").append(vol);
        sb.append(", symbol='").append(symbol).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
