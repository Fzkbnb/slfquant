package com.slf.quant.facade.sdk.huobi.response;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 11:35
 */

public class Kline {


    private int id;
    private double amount;
    private int count;
    private double open;
    private double close;
    private double low;
    private double high;
    private double vol;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Kline{");
        sb.append("id=").append(id);
        sb.append(", amount=").append(amount);
        sb.append(", count=").append(count);
        sb.append(", open=").append(open);
        sb.append(", close=").append(close);
        sb.append(", low=").append(low);
        sb.append(", high=").append(high);
        sb.append(", vol=").append(vol);
        sb.append('}');
        return sb.toString();
    }
}
