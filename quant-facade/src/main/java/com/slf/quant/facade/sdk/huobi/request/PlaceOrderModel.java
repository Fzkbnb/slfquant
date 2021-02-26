package com.slf.quant.facade.sdk.huobi.request;

/**
 * @author : yukai
 * @version : 1.0
 * @program : com.blocain.exchange.payment.exchange.huobi.model
 * @discription : 火币下单请求model
 * @create : 2018-06-08-09
 **/
public class PlaceOrderModel {

    /**
     * 账户ID
     */
    private String accountId;

    /**
     * 下单数量
     */
    private String amount;

    /**
     * 下单价格，市价单不传该参数
     */
    private String price;

    /**
     * 订单来源
     */
    private String source;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 订单类型
     */
    private String type;

    private String clientOrderId;

    public PlaceOrderModel() {
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }
}
