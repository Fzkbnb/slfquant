package com.slf.quant.facade.sdk.huobi.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>Author：yukai </p>
 * <p>Description:Description </p>
 * <p>Date: Create in 11:19 2018/6/12</p>
 * <p>Modify By: yukai</p>
 *
 * @version 1.0
 */
public class EntrustModel {
    @JsonProperty("account-id")
    private long accountId;

    private String amount;

    @JsonProperty("canceled-at")
    private long cancelAt;

    @JsonProperty("created-at")
    private long createAt;

    @JsonProperty("field-amount")
    private String fieldAmount;

    @JsonProperty("field-cash-amount")
    private String fieldCashAmount;

    @JsonProperty("field-fees")
    private String fieldFees;

    @JsonProperty("finished-at")
    private long finishAt;

    private long id;

    private String price;

    private String source;

    private String state;

    private String symbol;

    private String type;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getCancelAt() {
        return cancelAt;
    }

    public void setCancelAt(long cancelAt) {
        this.cancelAt = cancelAt;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getFieldAmount() {
        return fieldAmount;
    }

    public void setFieldAmount(String fieldAmount) {
        this.fieldAmount = fieldAmount;
    }

    public String getFieldCashAmount() {
        return fieldCashAmount;
    }

    public void setFieldCashAmount(String fieldCashAmount) {
        this.fieldCashAmount = fieldCashAmount;
    }

    public String getFieldFees() {
        return fieldFees;
    }

    public void setFieldFees(String fieldFees) {
        this.fieldFees = fieldFees;
    }

    public long getFinishAt() {
        return finishAt;
    }

    public void setFinishAt(long finishAt) {
        this.finishAt = finishAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
        return "EntrustModel{" +
                "accountId=" + accountId +
                ", amount='" + amount + '\'' +
                ", cancelAt=" + cancelAt +
                ", createAt=" + createAt +
                ", fieldAmount='" + fieldAmount + '\'' +
                ", fieldCashAmount='" + fieldCashAmount + '\'' +
                ", fieldFees='" + fieldFees + '\'' +
                ", finishAt=" + finishAt +
                ", id=" + id +
                ", price='" + price + '\'' +
                ", source='" + source + '\'' +
                ", state='" + state + '\'' +
                ", symbol='" + symbol + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
