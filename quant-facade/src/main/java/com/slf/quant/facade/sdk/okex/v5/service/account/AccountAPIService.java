package com.slf.quant.facade.sdk.okex.v5.service.account;

import com.alibaba.fastjson.JSONObject;
import com.slf.quant.facade.sdk.okex.v5.bean.account.param.*;


public interface AccountAPIService {

    //查看账户余额 Get Balance
    JSONObject getBalance(String ccy);

    /**
     * {
     *     "code": "0",
     *     "msg": "",
     *     "data": [{
     *         "instId": "BTC-USDT",
     *         "instType": "MARGIN",
     *         "mgnMode": "cross",
     *         "posId": "111111222",
     *         "posSide": "net",
     *         "pos": "10",
     *         "ccy": "BTC",
     *         "posCcy": "BTC",
     *         "availPos": "2",
     *         "avgPx": "3320",
     *         "upl": "0.00020928",
     *         "uplRatio": "0.00020928",
     *         "lever": "2",
     *         "liqPx": "0.00020928",
     *         "imr": "2",
     *         "margin": "",
     *         "mgnRatio": "",
     *         "mmr": "2",
     *         "liab": "0.00020928",
     *         "liabCcy": "USDT",
     *         "interest": "2",
     *         "tradeId": "2",
     *         "optVal": "",
     *         "adl": "2",
     *         "last":"12353.5",
     *         "cTime": "1597026383085",
     *         "uTime": "1597026383085"
     *     }]
     * }
     * @param instType
     * @param instId
     * @return
     */
    //查看持仓信息 Get Positions
    JSONObject getPositions(String instType, String instId);

    //账单流水查询（近七天） Get Bills Details (last 7 days)
    JSONObject getBillsDetails7Days(String instType, String ccy, String mgnMode, String ctType, String type, String subType, String after, String before, String limit);

    //账单流水查询（近七天） Get Bills Details (last 3 months)
    JSONObject getBillsDetails3Months(String instType, String ccy, String mgnMode, String ctType, String type, String subType, String after, String before, String limit);

    //查看账户配置 Get Account Configuration
    JSONObject getAccountConfiguration();

    //设置持仓模式 Set Position mode
    JSONObject setPositionMode(SetPositionMode setPositionMode);

    //设置杠杆倍数 Set Leverage
    JSONObject setLeverage(SetLeverage setLeverage);

    //获取最大可交易数量 Get Maximum Tradable Size For Instrument
    JSONObject getMaximumTradableSizeForInstrument(String instId, String tdMode, String ccy, String px);

    //获取最大可用数量 Get Maximum Tradable Size For Instrument
    JSONObject getMaximumAvailableTradableAmount(String instId, String tdMode, String ccy, String reduceOnly);

    //调整保证金 Increase/Decrease margin
     JSONObject increaseDecreaseMargin(IncreaseDecreaseMargin increaseDecreaseMargin);

    //获取杠杆倍数 Get Leverage
    JSONObject getLeverage(String instId, String mgnMode);

    //获取币币逐仓杠杆最大可借 Get the maximum loan of isolated MARGIN
    JSONObject getTheMaximumLoanOfIsolatedMARGIN(String instId);

    /**
     * {
     *     "code": "0",
     *     "msg": "",
     *     "data": [{
     *             "category": "1",
     *             "delivery": "",
     *             "exercise": "",
     *             "instType": "SPOT",
     *             "level": "101",
     *             "maker": "-0.001",
     *             "taker": "-0.0015",
     *             "ts": "1608623351857"
     *         }
     *     ]
     * }
     * @param instType
     * @param instId
     * @param uly
     * @param category
     * @return
     */
    //获取当前账户交易手续费费率 Get Fee Rates
    JSONObject getFeeRates(String instType, String instId, String uly, String category);

    //获取计息记录 Get interest-accrued
    JSONObject getInterestAccrued(String instId, String ccy, String mgnMode, String after, String before, String limit);

    //期权希腊字母PA/BS切换 Set the display type of Greeks
    JSONObject setTheDisplayTypeOfGreeks(SetTheDisplayTypeOfGreeks setTheDisplayTypeOfGreeks);

    //查看账户最大可转余额 Get Maximum Withdrawals
    JSONObject getMaximumWithdrawals(String ccy);
}
