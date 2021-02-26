package com.slf.quant.facade.sdk.huobi.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>Author：yukai </p>
 * <p>Description:Description </p>
 * <p>Date: Create in 19:06 2018/6/12</p>
 * <p>Modify By: yukai</p>
 *
 * @version 1.0
 */
public class DepositWithdrawResponse {

    private long id;

    private String type;

    private String currency;

    @JsonProperty("tx-hash")
    private String txHash;

    private long amount;

    private String address;

    @JsonProperty("address-tag")
    private String addressTag;

    private long fee;

    private String state;

    private String chain;

    @JsonProperty("created-at")
    private long createAt;

    @JsonProperty("updated-at")
    private long updateAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressTag() {
        return addressTag;
    }

    public void setAddressTag(String addressTag) {
        this.addressTag = addressTag;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }
    @Override
    public String toString() {
        return "DepositWithdrawResponse{" +
                "id=" + id +
                ", type=" + type +
                ", currency='" + currency + '\'' +
                ", txHash='" + txHash + '\'' +
                ", amount='" + amount + '\'' +
                ", address='" + address + '\'' +
                ", addressTag='" + addressTag + '\'' +
                ", fee='" + fee + '\'' +
                ", state='" + state + '\'' +
                ", chain='" + chain + '\'' +
                ", createAt='" + createAt + '\'' +
                ", updateAt='" + updateAt + '\'' +
                '}';
    }
}
