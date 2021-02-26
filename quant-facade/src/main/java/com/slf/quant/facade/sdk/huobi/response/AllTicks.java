package com.slf.quant.facade.sdk.huobi.response;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 14:52
 */

public class AllTicks {

    private int id;
    private long ts;
    private Ticker[] data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public Ticker[] getData() {
        return data;
    }

    public void setData(Ticker[] data) {
        this.data = data;
    }
}
