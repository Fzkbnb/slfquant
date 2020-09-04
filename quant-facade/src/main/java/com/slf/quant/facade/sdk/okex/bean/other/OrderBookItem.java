package com.slf.quant.facade.sdk.okex.bean.other;

public interface OrderBookItem<T> {
    String getPrice();

    T getSize();
}
