package com.slf.quant.facade.sdk.okex.bean.spot.result;

public class RepaymentRequestDto {
    private String instrument_id;
    private String currency;
    private String amount;
    private String borrow_id;

    public String getClient_oid() {
        return client_oid;
    }

    public void setClient_oid(String client_oid) {
        this.client_oid = client_oid;
    }

    private String client_oid;

    public String getInstrument_id() {
        return this.instrument_id;
    }

    public void setInstrument_id(final String instrument_id) {
        this.instrument_id = instrument_id;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public String getBorrow_id() {
        return this.borrow_id;
    }

    public void setBorrow_id(final String borrow_id) {
        this.borrow_id = borrow_id;
    }
}
