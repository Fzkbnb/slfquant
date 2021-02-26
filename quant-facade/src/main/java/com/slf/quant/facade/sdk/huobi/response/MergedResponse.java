package com.slf.quant.facade.sdk.huobi.response;

import com.slf.quant.facade.sdk.huobi.tool.ApiException;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 14:15
 */

public class MergedResponse<T> {

    /**
     * status : ok
     * ch : market.ethusdt.detail.merged
     * ts : 1499225276950
     * tick : {"id":1499225271,"ts":1499225271000,"close":1885,"open":1960,"high":1985,"low":1856,"amount":81486.2926,"count":42122,"vol":1.57052744857082E8,"ask":[1885,21.8804],"bid":[1884,1.6702]}
     */

    private String status;
    private String ch;
    private String ts;
    private T tick;

    public T checkAndReturn() {
        if ("ok".equals(status)) {
            return tick;
        }
        throw new ApiException(status, ts);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public T getTick() {
        return tick;
    }

    public void setTick(T tick) {
        this.tick = tick;
    }
}
