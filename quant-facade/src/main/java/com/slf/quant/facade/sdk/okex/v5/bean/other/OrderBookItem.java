package com.slf.quant.facade.sdk.okex.v5.bean.other;

public interface OrderBookItem<T> {
    String getPrice();

    T getSize();
}
