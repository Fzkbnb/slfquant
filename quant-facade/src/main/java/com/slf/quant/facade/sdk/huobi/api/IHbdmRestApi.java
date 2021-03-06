package com.slf.quant.facade.sdk.huobi.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;



import com.slf.quant.facade.sdk.huobi.request.Order;
import retrofit2.HttpException;

public interface IHbdmRestApi
{
    /**
     * 期货行情
     * 
     * @param symbol
     *            "BTC","ETH"...
     * @param contractType
     *            合约类型: this_week:当周 next_week:下周 quarter:季度
     * @param contract_code
     *            合约code
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractInfo(String symbol, String contractType, String contractCode) throws HttpException, IOException;

    /**
     *
     * symbol	true	string	品种代码		"BTC","ETH"...
     * trade_type	true	int	交易类型		0:全部,5: 卖出强平,6: 买入强平
     * create_date	true	int	日期		7，90（7天或者90天）
     * page_index	false	int	页码,不填默认第1页
     * page_size	false	int	不填默认20，不得多于50
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public String contractLiquidationOrders(String symbol, int tradeType, int createDate, int pageIndex, int pageSize) throws HttpException, IOException;
    
    /**
     * 风险准备金
     * @param symbol
     * @return
     */
    public String contractInsuranceFund(String symbol);
    
    /**
     * 获取账户权益和仓位
     * @param symbol
     * @return
     */
    public String contractAccountAndPosition(String symbol);
    
    /**
     * 获取合约指数
     * 
     * @param symbol
     *            "BTC","ETH"...
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractIndex(String symbol) throws HttpException, IOException;
    
    /**
     * 获取合约最高限价和最低限价
     * 
     * @param symbol
     *            "BTC","ETH"...
     * @param contractType
     *            合约类型: this_week:当周 next_week:下周 quarter:季度
     * @param contract_code
     *            合约code
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public String futurePriceLimit(String symbol, String contractType, String contractCode) throws HttpException, IOException;
    
    /**
     * 获取当前可用合约总持仓量
     * 
     * @param symbol
     *            "BTC","ETH"...
     * @param contractType
     *            合约类型: this_week:当周 next_week:下周 quarter:季度
     * @param contract_code
     *            合约code
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public String futureOpenInterest(String symbol, String contractType, String contractCode) throws HttpException, IOException;
    
    /**
     * 获取行情深度数据
     * 
     * @param symbol
     *            "BTC","ETH"...
     * @param type
     *            step0, step1, step2, step3, step4, step5（合并深度0-5）；step0时，不合并深度
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public String futureMarketDepth(String symbol, String type) throws HttpException, IOException;
    
    /**
     * 获取K线数据
     * 
     * @param symbol
     *            "BTC","ETH"...
     * @param period
     *            K线类型 1min, 5min, 15min, 30min, 60min, 1hour,4hour,1day, 1mon
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public String futureMarketHistoryKline(String symbol, String period, String size) throws HttpException, IOException;
    
    /**
     * 获取聚合行情
     * 
     * @param symbol
     *            如"BTC_CW"表示BTC当周合约，"BTC_NW"表示BTC次周合约，"BTC_CQ"表示BTC季度合约
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public String futureMarketDetailMerged(String symbol) throws HttpException, IOException;
    
    /**
     * 获取市场最近成交记录
     * 
     * @param symbol
     *            如"BTC_CW"表示BTC当周合约，"BTC_NW"表示BTC次周合约，"BTC_CQ"表示BTC季度合约
     * @return size 获取交易记录的数量 [1, 2000]
     * @throws HttpException
     * @throws IOException
     */
    public String futureMarketDetailTrade(String symbol, String size) throws HttpException, IOException;
    
    /**
     * 批量获取最近的交易记录
     * 
     * @param symbol
     *            如"BTC_CW"表示BTC当周合约，"BTC_NW"表示BTC次周合约，"BTC_CQ"表示BTC季度合约
     * @return size 获取交易记录的数量 [1, 2000]
     * @throws HttpException
     * @throws IOException
     */
    public String futureMarketHistoryTrade(String symbol, String size) throws HttpException, IOException;
    
    /**
     * 获取用户账户信息
     * 
     * @param symbol
     *            "BTC","ETH"...如果缺省，默认返回所有品种
     * @return size 获取交易记录的数量 [1, 2000]
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractAccountInfo(String symbol) throws HttpException, IOException;
    
    /**
     * 获取用户持仓信息
     * 
     * @param symbol
     *            "BTC","ETH"...如果缺省，默认返回所有品种
     * @return size 获取交易记录的数量 [1, 2000]
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractPositionInfo(String symbol) throws HttpException, IOException;
    
    /**
     * 获取用户账户信息
     *
     * @param symbol
     *            "BTC","ETH"...如果缺省，默认返回所有品种
     * @return size 获取交易记录的数量 [1, 2000]
     * @throws HttpException
     * @throws IOException
     */
    public String swapContractAccountInfo(String symbol) throws HttpException, IOException;
    
    /**
     * 获取用户持仓信息
     *
     * @param symbol
     *            "BTC","ETH"...如果缺省，默认返回所有品种
     * @return size 获取交易记录的数量 [1, 2000]
     * @throws HttpException
     * @throws IOException
     */
    public String swapContractPositionInfo(String symbol) throws HttpException, IOException;
    
    public String swapContractInfo(String symbol) throws HttpException, IOException;
    
    /**
     * 获取用户订单信息
     * 
     * @param symbol
     *            "BTC","ETH"...
     * @param contractType
     *            合约类型: this_week:当周 next_week:下周 month:当月 quarter:季度
     * @param contractCode
     *            BTC1403
     * @param client_order_id
     *            客户自己填写和维护，这次一定要大于上一次
     * @param price
     *            价格
     * @param volume
     *            委托数量(张)
     * @param direction
     *            "buy":买 "sell":卖
     * @param offset
     *            "open":开 "close":平
     * @param leverRate
     *            杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]
     * @param orderPriceType
     *            "limit":限价 "opponent":对手价
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractOrder(String symbol, String contractType, String contractCode, String clientOrderId, String price, String volume, String direction,
                                      String offset, String leverRate, String orderPriceType) throws HttpException, IOException;
    
    /**
     * 批量下单
     * 
     * @param symbol
     *            "BTC","ETH"...
     * @param contractType
     *            合约类型: this_week:当周 next_week:下周 month:当月 quarter:季度
     * @param contractCode
     *            BTC1403
     * @param client_order_id
     *            客户自己填写和维护，这次一定要大于上一次
     * @param price
     *            价格
     * @param volume
     *            委托数量(张)
     * @param direction
     *            "buy":买 "sell":卖
     * @param offset
     *            "open":开 "close":平
     * @param leverRate
     *            杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]
     * @param orderPriceType
     *            "limit":限价 "opponent":对手价
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractBatchorder(List<Order> orders) throws HttpException, IOException;
    
    /**
     * 撤销订单
     * 
     * @param orderId
     *            订单ID（ 多个订单ID中间以","分隔,一次最多允许撤消50个订单 ）
     * @return clientOrderId 客户订单ID(多个订单ID中间以","分隔,一次最多允许撤消50个订单)
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractCancel(String orderId, String clientOrderId, String symbol) throws HttpException, IOException;
    
    /**
     * 全部撤单
     * 
     * @param symbol
     *            品种代码，如"BTC","ETH"...
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractCancelall(String symbol, String contractCode) throws HttpException, IOException;
    
    /**
     * 获取合约订单信息
     * 
     * @param orderId
     *            订单ID（ 多个订单ID中间以","分隔,一次最多允许撤消50个订单 ）
     * @param clientOrderId
     *            客户订单ID(多个订单ID中间以","分隔,一次最多允许撤消50个订单)
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractOrderInfo(String orderId, String clientOrderId, String symbol, String orderType) throws HttpException, IOException;
    
    /**
     * 获取订单明细信息
     * 
     * @param symbol
     *            "BTC","ETH"...
     * @param orderId
     *            订单id
     * @param pageIndex
     *            第几页,不填第一页
     * @param pageSize
     *            不填默认20，不得多于50
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractOrderDetail(String symbol, String orderId, String pageIndex, String pageSize, String createdAt, String orderType)
            throws HttpException, IOException;
    
    /**
     * 获取合约当前未成交委托
     * 
     * @param symbol
     *            "BTC","ETH"...
     * @param pageIndex
     *            第几页,不填第一页
     * @param pageSize
     *            不填默认20，不得多于50
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractOpenorders(String symbol, String pageIndex, String pageSize) throws HttpException, IOException;
    
    /**
     * 获取合约历史委托
     * 
     * @param symbol
     *            "BTC","ETH"...
     * @param tradeType
     *            0:全部,1:买入开多,2: 卖出开空,3: 买入平空,4: 卖出平多,5: 卖出强平,6: 买入强平,7:交割平多,8: 交割平空
     * @param type
     *            1:所有订单，2：已结束订单
     * @param status
     *            0:全部,3:未成交, 4: 部分成交,5: 部分成交已撤单,6: 全部成交,7:已撤单 createDate
     *            7，90（7天或者90天）
     * @param pageIndex
     *            第几页,不填第一页
     * @param pageSize
     *            不填默认20，不得多于50
     * @throws HttpException
     * @throws IOException
     */
    public String futureContractHisorders(String symbol, String tradeType, String type, String status, String createDate, String pageIndex, String pageSize)
            throws HttpException, IOException;
    
    /**
     * 划转接口
     * @param trx
     * @param s
     * @param s1
     */
    String transfer(String urlPre, String trx, String s, String s1);
    
    String swapTransfer(String url, String currency, String from, String to, BigDecimal amount);
    
    String futureTradeFee(String currency);
}
