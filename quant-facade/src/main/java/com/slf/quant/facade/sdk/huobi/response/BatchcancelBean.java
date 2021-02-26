package com.slf.quant.facade.sdk.huobi.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 17:53
 */

public class BatchcancelBean {
    /**
     * err-msg : 记录无效
     * order-id : 2
     * err-code : base-record-invalid
     */

    @JsonProperty("err-msg")
    private String errmsg;
    @JsonProperty("order-id")
    private String orderid;
    @JsonProperty("err-code")
    private String errcode;

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    @Override
    public String toString() {
        return "BatchcancelBean{" +
                "errmsg='" + errmsg + '\'' +
                ", orderid='" + orderid + '\'' +
                ", errcode='" + errcode + '\'' +
                '}';
    }
}
