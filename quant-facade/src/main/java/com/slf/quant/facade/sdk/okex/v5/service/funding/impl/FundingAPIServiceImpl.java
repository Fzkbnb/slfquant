package com.slf.quant.facade.sdk.okex.v5.service.funding.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.slf.quant.facade.sdk.okex.v5.bean.funding.param.*;
import com.slf.quant.facade.sdk.okex.v5.client.APIClient;
import com.slf.quant.facade.sdk.okex.v5.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.v5.service.funding.FundingAPIService;

public class FundingAPIServiceImpl implements FundingAPIService {

    private APIClient client;
    private FundingAPI api;

    public FundingAPIServiceImpl(APIConfiguration config) {
        this.client = new APIClient(config);
        this.api = client.createService(FundingAPI.class);
    }

    //获取充值地址信息 Get Deposit Address
    @Override
    public JSONObject getDepositAddress(String ccy) {
        return this.client.executeSync(this.api.getDepositAddress(ccy));
    }

    //获取资金账户余额信息 Get Balance

    @Override
    public JSONObject getBalance() {
        return this.client.executeSync(this.api.getBalance());
    }

    //资金划转  Funds Transfer
    @Override
    public JSONObject fundsTransfer(FundsTransfer fundsTransfer) {
        return this.client.executeSync(this.api.fundsTransfer(JSONObject.parseObject(JSON.toJSONString(fundsTransfer))));
    }

    //提币 Withdrawal
    @Override
    public JSONObject Withdrawal(Withdrawal withdrawal) {
        return this.client.executeSync(this.api.Withdrawal(JSONObject.parseObject(JSON.toJSONString(withdrawal))));
    }

    //充值记录 Get Deposit History
    @Override
    public JSONObject getDepositHistory(String ccy, String state, String after, String before, String limit) {
        return this.client.executeSync(this.api.getDepositHistory(ccy,state,after,before,limit));
    }

    //提币记录 Get Withdrawal History

    @Override
    public JSONObject getWithdrawalHistory(String ccy, String state, String after, String before, String limit) {
        return this.client.executeSync(this.api.getWithdrawalHistory(ccy,state,after,before,limit));
    }

    //获取币种列表 Get Currencies
    @Override
    public JSONObject getCurrencies() {
        return this.client.executeSync(this.api.getCurrencies());
    }

    //余币宝申购/赎回 PiggyBank Purchase/Redemption
    @Override
    public JSONObject piggyBankPurchaseRedemption(PiggyBankPurchaseRedemption piggyBankPurchaseRedemption) {
        return this.client.executeSync(this.api.piggyBankPurchaseRedemption(JSONObject.parseObject(JSON.toJSONString(piggyBankPurchaseRedemption))));
    }

    //资金流水查询 Asset Bills Details
    @Override
    public JSONObject assetBillsDetails(String ccy, String type, String after, String before, String limit) {
        return this.client.executeSync(this.api.assetBillsDetails(ccy,type,after,before,limit));
    }
}
