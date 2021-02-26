package com.slf.quant.facade.sdk.huobi.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>Author：yukai </p>
 * <p>Description:当前成交和历史成交的结果接受bean </p>
 * <p>Date: Create in 17:13 2018/6/12</p>
 * <p>Modify By: yukai</p>
 *
 * @version 1.0
 */
public class DealListResponse {

    @JsonProperty("created-at")
    private long createAt;

    @JsonProperty("filled-amount")
    private String filledAmount;

    @JsonProperty("filled-fees")
    private String filledFees;

    private long id;

    @JsonProperty("match-id")
    private long matchId;

    @JsonProperty("order-id")
    private long orderId;

    private String price;

    private String source;

    private String symbol;

    private String type;

    @JsonProperty("role")
    private String role;

    @JsonProperty("filled-points")
    private String filledPoints;

    @JsonProperty("fee-deduct-currency")
    private String feeDeductCurrency;

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    @Override
    public String toString() {
        return "DealListResponse{" +
                "createAt=" + createAt +
                ", filledAmount='" + filledAmount + '\'' +
                ", filledFees='" + filledFees + '\'' +
                ", id=" + id +
                ", matchId=" + matchId +
                ", orderId=" + orderId +
                ", price='" + price + '\'' +
                ", source='" + source + '\'' +
                ", symbol='" + symbol + '\'' +
                ", type='" + type + '\'' +
                ", role='" + role + '\'' +
                ", filledPoints='" + filledPoints + '\'' +
                ", feeDeductCurrency='" + feeDeductCurrency + '\'' +

                '}';
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFilledPoints() {
        return filledPoints;
    }

    public void setFilledPoints(String filledPoints) {
        this.filledPoints = filledPoints;
    }

    public String getFeeDeductCurrency() {
        return feeDeductCurrency;
    }

    public void setFeeDeductCurrency(String feeDeductCurrency) {
        this.feeDeductCurrency = feeDeductCurrency;
    }
}
