package com.slf.quant.facade.sdk.huobi.spot.client.impl;

import com.slf.quant.facade.sdk.huobi.spot.client.SubscriptionErrorHandler;
import com.slf.quant.facade.sdk.huobi.spot.client.SubscriptionListener;
import com.slf.quant.facade.sdk.huobi.spot.client.impl.utils.Handler;

class WebsocketRequest<T> {

  WebsocketRequest(SubscriptionListener<T> listener, SubscriptionErrorHandler errorHandler) {
    this.updateCallback = listener;
    this.errorHandler = errorHandler;
  }

  String name;
  Handler<WebSocketConnection> connectionHandler;
  Handler<WebSocketConnection> authHandler = null;
  final SubscriptionListener<T> updateCallback;
  RestApiJsonParser<T> jsonParser;
  final SubscriptionErrorHandler errorHandler;
}
