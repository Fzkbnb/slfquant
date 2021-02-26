package com.slf.quant.facade.sdk.huobi.spot.client;

import com.slf.quant.facade.sdk.huobi.spot.client.exception.HuobiApiException;

/**
 * The error handler for the subscription.
 */
@FunctionalInterface
public interface SubscriptionErrorHandler {

  void onError(HuobiApiException exception);
}
