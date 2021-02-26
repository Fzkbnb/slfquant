package com.slf.quant.facade.sdk.huobi.request;

/**
 * @author : yukai
 * @version : 1.0
 * @program : com.blocain.exchange.payment.exchange.huobi.model
 * @discription : 查询当前委托和历史委托的请求参数
 * @create : 2018-06-08-10
 **/
public class EntrustListsModel {

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 订单类型组合
     */
    private String types;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 订单状态组合
     */
    private String states;

    /**
     * 订单起始ID
     */
    private String from;

    /**
     * 查询方向
     */
    private String direct;

    /**
     * 查询记录大小
     */
    private String size;

    public EntrustListsModel() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
