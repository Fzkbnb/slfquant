package com.slf.quant.facade.sdk.huobi.spot.client.impl.utils;


import com.slf.quant.facade.sdk.huobi.spot.client.AsyncResult;
import com.slf.quant.facade.sdk.huobi.spot.client.exception.HuobiApiException;

public class FailedAsyncResult<T> implements AsyncResult<T> {

  private final HuobiApiException exception;

  public FailedAsyncResult(HuobiApiException exception) {
    this.exception = exception;
  }

  @Override
  public HuobiApiException getException() {
    return exception;
  }

  @Override
  public boolean succeeded() {
    return false;
  }

  @Override
  public T getData() {
    return null;
  }
}
