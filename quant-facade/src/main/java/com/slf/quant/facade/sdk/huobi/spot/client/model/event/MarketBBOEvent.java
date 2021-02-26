package com.slf.quant.facade.sdk.huobi.spot.client.model.event;


import com.slf.quant.facade.sdk.huobi.spot.client.impl.RestApiJsonParser;
import com.slf.quant.facade.sdk.huobi.spot.client.impl.utils.JsonWrapper;
import com.slf.quant.facade.sdk.huobi.spot.client.impl.utils.TimeService;
import com.slf.quant.facade.sdk.huobi.spot.client.model.MarketBBO;

/**
 * The Market BBO data received by request of market bbo.
 */

public class MarketBBOEvent {

  private String ch;

  private Long timestamp;

  private MarketBBO data;

  public String getCh() {
    return ch;
  }

  public void setCh(String ch) {
    this.ch = ch;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public MarketBBO getData() {
    return data;
  }

  public void setData(MarketBBO data) {
    this.data = data;
  }

  public static RestApiJsonParser<MarketBBOEvent> getParser(){
    return (jsonWrapper) -> {
      return parse(jsonWrapper);
    };
  }

  public static MarketBBOEvent parse(JsonWrapper jsonWrapper) {
    MarketBBOEvent event = new MarketBBOEvent();
    event.setCh(jsonWrapper.getStringOrDefault("ch",null));
    event.setTimestamp(TimeService.convertCSTInMillisecondToUTC(jsonWrapper.getLong("ts")));
    event.setData(MarketBBO.parse(jsonWrapper.getJsonObject("tick")));
    return event;
  }

}
