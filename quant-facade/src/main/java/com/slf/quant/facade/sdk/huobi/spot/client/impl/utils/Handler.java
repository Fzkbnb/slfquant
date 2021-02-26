package com.slf.quant.facade.sdk.huobi.spot.client.impl.utils;

@FunctionalInterface
public interface Handler<T> {

  void handle(T t);
}
