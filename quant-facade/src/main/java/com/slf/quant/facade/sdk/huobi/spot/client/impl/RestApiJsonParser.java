package com.slf.quant.facade.sdk.huobi.spot.client.impl;


import com.slf.quant.facade.sdk.huobi.spot.client.impl.utils.JsonWrapper;

@FunctionalInterface
public interface RestApiJsonParser<T> {

  T parseJson(JsonWrapper json);
}
