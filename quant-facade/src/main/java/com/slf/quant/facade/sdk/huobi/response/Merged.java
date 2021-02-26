package com.slf.quant.facade.sdk.huobi.response;

import java.util.List;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 14:16
 */

public class Merged {

    /**
     * id : 1499225271
     * ts : 1499225271000
     * close : 1885
     * open : 1960
     * high : 1985
     * low : 1856
     * amount : 81486.2926
     * count : 42122
     * vol : 1.57052744857082E8
     * ask : [1885,21.8804]
     * bid : [1884,1.6702]
     */

    private long id;
    private long ts;
    private String close;
    private String open;
    private String high;
    private String low;
    private String amount;
    private String count;
    private String vol;
    private List<String> ask;
    private List<String> bid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public List<String> getAsk() {
        return ask;
    }

    public void setAsk(List<String> ask) {
        this.ask = ask;
    }

    public List<String> getBid() {
        return bid;
    }

    public void setBid(List<String> bid) {
        this.bid = bid;
    }

    @Override
    public String toString() {
        return "Merged{" +
                "id=" + id +
                ", ts=" + ts +
                ", close='" + close + '\'' +
                ", open='" + open + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", amount='" + amount + '\'' +
                ", count='" + count + '\'' +
                ", vol='" + vol + '\'' +
                ", ask=" + ask +
                ", bid=" + bid +
                '}';
    }
}
