package com.slf.quant.facade.sdk.okex.bean.spot.param;

import lombok.Data;

@Data
public class PlaceOrderParam
{
    /**
     * 客户端下单 标示id 非必填
     */
    private String client_oid;
    
    private String order_id;
    
    public String getInstrument_id()
    {
        return instrument_id;
    }
    
    public void setInstrument_id(String instrument_id)
    {
        this.instrument_id = instrument_id;
    }
    
    private String instrument_id;
    
    /**
     * 币对如 etc_eth
     */
    /**
     * 买卖类型 buy/sell
     */
    private String side;
    
    /**
     * 订单类型 限价单 limit 市价单 market
     */
    private String type;
    
    /**
     * 交易数量
     */
    private String size;
    
    /**
     * 限价单使用 价格
     */
    private String price;
    
    /**
     * 市价单使用 价格
     */
    private String notional;
    
    private String order_type;
    
    /**
     * 1币币交易 2杠杆交易
     */
    private String margin_trading;
    
    public String getOrder_type()
    {
        return order_type;
    }
    
    public void setOrder_type(String order_type)
    {
        this.order_type = order_type;
    }
    
    /**
     * 来源（web app ios android）
     */
    private Byte source = 0;
}
