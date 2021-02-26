package com.slf.quant.facade.sdk.huobi.response;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 15:39
 */

public class Symbols {
    /**
     * base-currency : btc
     * quote-currency : usdt
     * price-precision : 2
     * amount-precision : 4
     * symbol-partition : main
     */
    @JsonProperty("base-currency")
    private String baseCurrency;
    @JsonProperty("quote-currency")
    private String quoteCurrency;
    @JsonProperty("price-precision")
    private String pricePrecision;
    @JsonProperty("amount-precision")
    private String amountPrecision;
    @JsonProperty("symbol-partition")
    private String symbolPartition;
    @JsonProperty("symbol")
    private String symbol;// 交易对
    @JsonProperty("state")
    private String state;//交易对状态；可能值: [online，offline,suspend] online - 已上线；offline - 交易对已下线，不可交易；suspend -- 交易暂停
    @JsonProperty("value-precision")
    private Integer valuePrecision;//交易对交易金额的精度（小数点后位数）
    @JsonProperty("min-order-amt")
    private Long minOrderAmt;//交易对最小下单量 (下单量指当订单类型为限价单或sell-market时，下单接口传的'amount')
    @JsonProperty("max-order-amt")
    private Long maxOrderAmt;//交易对最大下单量 (下单量指当订单类型为限价单或sell-market时，下单接口传的'amount')
    @JsonProperty("min-order-value")
    private Long minOrderValue;//最小下单金额 （下单金额指当订单类型为限价单时，下单接口传入的(amount * price)。当订单类型为buy-market时，下单接口传的'amount'）
    @JsonProperty("leverage-ratio")
    private Long leverageRatio;//交易对杠杆最大倍数

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public String getPricePrecision() {
        return pricePrecision;
    }

    public void setPricePrecision(String pricePrecision) {
        this.pricePrecision = pricePrecision;
    }

    public String getAmountPrecision() {
        return amountPrecision;
    }

    public void setAmountPrecision(String amountPrecision) {
        this.amountPrecision = amountPrecision;
    }

    public String getSymbolPartition() {
        return symbolPartition;
    }

    public void setSymbolPartition(String symbolPartition) {
        this.symbolPartition = symbolPartition;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getValuePrecision() {
        return valuePrecision;
    }

    public void setValuePrecision(Integer valuePrecision) {
        this.valuePrecision = valuePrecision;
    }

    public Long getMinOrderAmt() {
        return minOrderAmt;
    }

    public void setMinOrderAmt(Long minOrderAmt) {
        this.minOrderAmt = minOrderAmt;
    }

    public Long getMaxOrderAmt() {
        return maxOrderAmt;
    }

    public void setMaxOrderAmt(Long maxOrderAmt) {
        this.maxOrderAmt = maxOrderAmt;
    }

    public Long getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(Long minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public Long getLeverageRatio() {
        return leverageRatio;
    }

    public void setLeverageRatio(Long leverageRatio) {
        this.leverageRatio = leverageRatio;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Symbols{");
        sb.append("baseCurrency='").append(baseCurrency).append('\'');
        sb.append(", quoteCurrency='").append(quoteCurrency).append('\'');
        sb.append(", pricePrecision='").append(pricePrecision).append('\'');
        sb.append(", amountPrecision='").append(amountPrecision).append('\'');
        sb.append(", symbolPartition='").append(symbolPartition).append('\'');
        sb.append(", symbol='").append(symbol).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", valuePrecision=").append(valuePrecision);
        sb.append(", minOrderAmt=").append(minOrderAmt);
        sb.append(", maxOrderAmt=").append(maxOrderAmt);
        sb.append(", minOrderValue=").append(minOrderValue);
        sb.append(", leverageRatio=").append(leverageRatio);
        sb.append('}');
        return sb.toString();
    }
}
