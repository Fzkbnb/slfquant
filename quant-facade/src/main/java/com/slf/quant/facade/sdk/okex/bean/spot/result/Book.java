package com.slf.quant.facade.sdk.okex.bean.spot.result;

import java.util.List;

public class Book {
    private List<String[]> asks;

    private List<String[]> bids;

    public List<String[]> getAsks() {
        return asks;
    }

    public void setAsks(List<String[]> asks) {
        this.asks = asks;
    }

    public List<String[]> getBids() {
        return bids;
    }

    public void setBids(List<String[]> bids) {
        this.bids = bids;
    }
}
