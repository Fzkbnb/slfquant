package com.slf.quant.facade.sdk.huobi.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 18:50
 */

public class MatchresultsOrdersDetail {
    /**
     * id : 29553
     * order-id : 59378
     * match-id : 59335
     * symbol : ethusdt
     * type : buy-limit
     * source : api
     * price : 100.1000000000
     * filled-amount : 9.1155000000
     * filled-fees : 0.0182310000
     * created-at : 1494901400435
     */

    private long id;
    @JsonProperty("order-id")
    private long orderId;
    @JsonProperty("match-id")
    private long matchId;
    private String symbol;
    private String type;
    private String source;
    private String price;
    @JsonProperty("filled-amount")
    private String filledAmount;
    @JsonProperty("filled-fees")
    private String filledFees;
    @JsonProperty("created-at")
    private long createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFilledAmount() {
        return filledAmount;
    }

    public void setFilledAmount(String filledAmount) {
        this.filledAmount = filledAmount;
    }

    public String getFilledFees() {
        return filledFees;
    }

    public void setFilledFees(String filledFees) {
        this.filledFees = filledFees;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "MatchresultsOrdersDetail{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", matchId=" + matchId +
                ", symbol='" + symbol + '\'' +
                ", type='" + type + '\'' +
                ", source='" + source + '\'' +
                ", price='" + price + '\'' +
                ", filledAmount='" + filledAmount + '\'' +
                ", filledFees='" + filledFees + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
