package com.slf.quant.facade.sdk.huobi.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author : yukai
 * @version : 1.0
 * @program : com.blocain.exchange.payment.exchange.huobi.model
 * @discription : 提币申请请求参数
 * @create : 2018-06-08-10
 **/
public class ApplyWithdrawModel {

    /**
     * address	true	string	提现地址	仅支持在官网上相应币种地址列表 中的地址
     * amount	true	string	提币数量
     * currency	true	string	资产类型	btc, ltc, bch, eth, etc ...(火币全球站支持的币种)
     * fee	    true	string	转账手续费
     * chain	false	string	提USDT至OMNI时须设置此参数为"usdt"，提USDT至TRX时须设置此参数为"trc20usdt"，其他币种提现无须设置此参数
     * addr-tag
     */

    /**
     * 提现地址
     */
    private String address;

    /**
     * 提币数量
     */
    private String amount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 转账手续费
     */
    private String fee;

    /**
     * 链
     */
    private String chain;

    /**
     * 虚拟币共享地址tag,XRP特有
     */
    @JsonProperty("addr-tag")
    private String addrTag;

    public ApplyWithdrawModel() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getAddrTag() {
        return addrTag;
    }

    public void setAddrTag(String addrTag) {
        this.addrTag = addrTag;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ApplyWithdrawModel{");
        sb.append("address='").append(address).append('\'');
        sb.append(", amount='").append(amount).append('\'');
        sb.append(", currency='").append(currency).append('\'');
        sb.append(", fee='").append(fee).append('\'');
        sb.append(", chain='").append(chain).append('\'');
        sb.append(", addrTag='").append(addrTag).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
