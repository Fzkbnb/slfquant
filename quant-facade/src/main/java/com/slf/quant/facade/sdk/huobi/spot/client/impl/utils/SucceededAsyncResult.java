package com.slf.quant.facade.sdk.huobi.spot.client.impl.utils;


import com.slf.quant.facade.sdk.huobi.spot.client.AsyncResult;
import com.slf.quant.facade.sdk.huobi.spot.client.exception.HuobiApiException;

public class SucceededAsyncResult<T> implements AsyncResult<T> {

  private final T data;

  public SucceededAsyncResult(T data) {
    this.data = data;
  }

  @Override
  public HuobiApiException getException() {
    return null;
  }

  @Override
  public boolean succeeded() {
    return true;
  }

  @Override
  public T getData() {
    return data;
  }
}
