package com.slf.quant.facade.sdk.huobi.spot.client.model.event;

import java.util.List;

import com.slf.quant.facade.sdk.huobi.spot.client.model.Order;

/**
 * The order received by subscription of order list request.
 */
public class OrderListEvent {

  private String topic;

  private Long timestamp;

  private List<Order> orderList = null;

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  /**
   * Get the UNIX formatted timestamp generated by server in UTC.
   *
   * @return The timestamp.
   */
  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Get the order list you request.
   *
   * @return The order list.
   */
  public List<Order> getOrderList() {
    return orderList;
  }

  public void setOrderList(List<Order> orderList) {
    this.orderList = orderList;
  }
}
