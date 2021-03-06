package com.slf.quant.facade.sdk.okex.bean.swap.result;

public class ApiUserRiskVO {

    private String long_leverage;
    private String short_leverage;
    private String margin_mode;
    private String instrument_id;

    public ApiUserRiskVO() {
    }

    public ApiUserRiskVO(String long_leverage, String short_leverage, String margin_mode, String instrument_id) {
        this.long_leverage = long_leverage;
        this.short_leverage = short_leverage;
        this.margin_mode = margin_mode;
        this.instrument_id = instrument_id;
    }

    public String getLong_leverage() {
        return long_leverage;
    }

    public void setLong_leverage(String long_leverage) {
        this.long_leverage = long_leverage;
    }

    public String getShort_leverage() {
        return short_leverage;
    }

    public void setShort_leverage(String short_leverage) {
        this.short_leverage = short_leverage;
    }

    public String getMargin_mode() {
        return margin_mode;
    }

    public void setMargin_mode(String margin_mode) {
        this.margin_mode = margin_mode;
    }

    public String getInstrument_id() {
        return instrument_id;
    }

    public void setInstrument_id(String instrument_id) {
        this.instrument_id = instrument_id;
    }

}
