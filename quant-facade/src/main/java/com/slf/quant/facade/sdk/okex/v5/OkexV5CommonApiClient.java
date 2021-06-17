package com.slf.quant.facade.sdk.okex.v5;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.slf.quant.facade.consts.KeyConst;
import com.slf.quant.facade.consts.UrlConst;
import com.slf.quant.facade.model.*;
import com.slf.quant.facade.sdk.okex.v5.bean.account.param.SetLeverage;
import com.slf.quant.facade.sdk.okex.v5.bean.trade.param.CancelOrder;
import com.slf.quant.facade.sdk.okex.v5.bean.trade.param.ClosePositions;
import com.slf.quant.facade.sdk.okex.v5.bean.trade.param.PlaceOrder;
import com.slf.quant.facade.sdk.okex.v5.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.v5.service.account.AccountAPIService;
import com.slf.quant.facade.sdk.okex.v5.service.account.impl.AccountAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.v5.service.marketData.MarketDataAPIService;
import com.slf.quant.facade.sdk.okex.v5.service.marketData.impl.MarketDataAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.v5.service.publicData.PublicDataAPIService;
import com.slf.quant.facade.sdk.okex.v5.service.publicData.impl.PublicDataAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.v5.service.trade.TradeAPIService;
import com.slf.quant.facade.sdk.okex.v5.service.trade.impl.TradeAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.v5.utils.OrderIdUtils;
import com.slf.quant.facade.utils.QuantUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * OkexV5CommonApiClient：
 *
 * @author: fzk
 * @date: 2021-03-25 10:45
 */
@Slf4j
public class OkexV5CommonApiClient
{
    // 行情数据（实时变动的数据）
    private MarketDataAPIService marketDataAPIService;
    
    // 公共数据（定期变动数据，例如合约信息，资金费率，爆仓单等）
    private PublicDataAPIService publicDataAPIService;
    
    // 交易服务
    private TradeAPIService      tradeAPIService;
    
    // 账户资产服务
    private AccountAPIService    accountAPIService;
    
    public OkexV5CommonApiClient(String apiKey, String secretKey, String passPhrase)
    {
        APIConfiguration config = new APIConfiguration();
        config.setEndpoint(UrlConst.okex_endpoint);
        config.setApiKey(apiKey);
        config.setSecretKey(secretKey);
        config.setPassphrase(passPhrase);
        config.setPrint(true);
        marketDataAPIService = new MarketDataAPIServiceImpl(config);
        publicDataAPIService = new PublicDataAPIServiceImpl(config);
        tradeAPIService = new TradeAPIServiceImpl(config);
        accountAPIService = new AccountAPIServiceImpl(config);
    }
    
    /**
     * 获取指定现货交易对信息，参数symbol为空时表示获取所有交易对信息
     * @param symbol
     * @return
     */
    public List<QuantCommonSymbolInfo> getSpotSymbolInfo(String symbol)
    {
        List<QuantCommonSymbolInfo> list = new ArrayList<>();
        try
        {
            JSONArray array = publicDataAPIService.getInstruments("SPOT", "", symbol).getJSONArray("data");
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject json = array.getJSONObject(i);
                QuantCommonSymbolInfo symbolInfo = new QuantCommonSymbolInfo();
                symbolInfo.setBaseCurrency(json.getString("baseCcy"));
                symbolInfo.setQuoteCurrency(json.getString("quoteCcy"));
                symbolInfo.setSymbol(symbolInfo.getBaseCurrency() + "-" + symbolInfo.getQuoteCurrency());
                symbolInfo.setMinSpotAmt(json.getBigDecimal("minSz"));
                symbolInfo.setSpotPriceTick(json.getBigDecimal("tickSz"));
                symbolInfo.setSpotPricePrecision(QuantUtil.getPrecision(json.getBigDecimal("tickSz")));
                symbolInfo.setSpotAmtPrecision(QuantUtil.getPrecision(json.getBigDecimal("lotSz")));
                list.add(symbolInfo);
            }
            if (StringUtils.isNotEmpty(symbol))
            {
                list = list.stream().filter(symbolInfo -> symbolInfo.getSymbol().equalsIgnoreCase(symbol)).collect(Collectors.toList());
            }
        }
        catch (Exception e)
        {
            log.info("okex现货交易对信息获取失败：{}", e.getLocalizedMessage());
        }
        return list;
    }
    
    /**
     * 获取指定交割合约交易对信息，参数symbol为空时表示获取所有交易对信息
     * @param symbol
     * @return
     */
    public List<QuantCommonSymbolInfo> getFutureSymbolInfo(String instType, String symbol)
    {
        List<QuantCommonSymbolInfo> list = new ArrayList<>();
        try
        {
            JSONArray array = publicDataAPIService.getInstruments(instType, "", symbol).getJSONArray("data");
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject json = array.getJSONObject(i);
                QuantCommonSymbolInfo symbolInfo = new QuantCommonSymbolInfo();
                String code = json.getString("instId");
                symbolInfo.setBaseCurrency(code.substring(0, code.indexOf("-")));
                symbolInfo.setQuoteCurrency(code.contains("-USD-") ? "USD" : "USDT");
                symbolInfo.setSymbol(json.getString("instId"));
                symbolInfo.setContractPriceTick(json.getBigDecimal("tickSz"));
                symbolInfo.setContractPricePrecision(QuantUtil.getPrecision(json.getBigDecimal("tickSz")));
                symbolInfo.setContractVal(json.getBigDecimal("ctVal"));
                list.add(symbolInfo);
            }
            if (StringUtils.isNotEmpty(symbol))
            {
                list = list.stream().filter(symbolInfo -> symbolInfo.getSymbol().equalsIgnoreCase(symbol)).collect(Collectors.toList());
            }
        }
        catch (Exception e)
        {
            log.info("okex合约交易对信息获取失败：{}|{}", symbol, e.getLocalizedMessage());
        }
        return list;
    }
    
    public QuantCommonPositionModel getFuturePosition(String instType, String contractCode)
    {
        QuantCommonPositionModel model = new QuantCommonPositionModel();
        try
        {
            JSONObject result = accountAPIService.getPositions(instType, contractCode);
            if ("0".equalsIgnoreCase(result.getString("code")))
            {
                JSONArray array = result.getJSONArray("data");
                model.setLiquidationPrice(BigDecimal.ZERO);
                // model.setLastPrice(getContractDepthByCode(contractCode).getContract_price());
                model.setLongCont(BigDecimal.ZERO);
                model.setShortCont(BigDecimal.ZERO);
                for (int i = 0; i < array.size(); i++)
                {
                    JSONObject json = array.getJSONObject(i);
                    model.setMarginMode(json.getString("mgnMode"));
                    if ("long".equalsIgnoreCase(json.getString("posSide")))
                    {
                        // 多头
                        model.setLongCont(json.getBigDecimal("pos"));
                    }
                    else if ("short".equalsIgnoreCase(json.getString("posSide")))
                    {
                        // 空头
                        model.setShortCont(json.getBigDecimal("pos"));
                    }
                    else
                    {
                        throw new RuntimeException("仓位设置有误：" + json.getString("posSide"));
                    }
                    model.setLiquidationPrice(json.getBigDecimal("liqPx"));
                    model.setLastPrice(json.getBigDecimal("last"));
                    model.setLeverage(json.getBigDecimal("lever"));
                    model.setAvgPrice(json.getBigDecimal("avgPx"));
                    model.setAvailPos(json.getBigDecimal("availPos"));
                    model.setUpl(json.getBigDecimal("upl"));
                }
            }
            else
            {
                log.info("okexV5获取持仓信息接口返回错误信息：{}", result.getString("msg"));
                return null;
            }
        }
        catch (Exception e)
        {
            log.info("okexV5合约持仓信息获取失败：{}|{}", contractCode, e.getLocalizedMessage());
        }
        return model;
    }
    
    public void transfer(String currency, BigDecimal amount, int from, int to)
    {
    }
    
    /**
     * 交割合约撤单接口
     * @param contractCode
     * @param orderId
     */
    public void cancelFutureOrder(String contractCode, long orderId)
    {
        try
        {
            CancelOrder cancelOrder = new CancelOrder();
            cancelOrder.setInstId(contractCode);
            cancelOrder.setOrdId(String.valueOf(orderId));
            tradeAPIService.cancelOrder(cancelOrder);
        }
        catch (Exception e)
        {
            log.info("okexV5合约撤单异常：{}", e.getLocalizedMessage());
        }
    }
    
    /**
     * 获取交割合约订单信息
     * status:1已完成无成交2已完成有成交3未完成无成交4未完成有成交
     *
     * @param contractCode
     * @param orderId
     * @return
     */
    public QuantCommonOrderModel getFutureOrder(String contractCode, long orderId)
    {
        QuantCommonOrderModel order = new QuantCommonOrderModel();
        try
        {
            JSONObject json = tradeAPIService.getOrderDetails(contractCode, String.valueOf(orderId), "");
            if ("0".equalsIgnoreCase(json.getString("code")))
            {
                JSONObject res = json.getJSONArray("data").getJSONObject(0);
                order.setFee(res.getBigDecimal("fee"));// 手续费字段为负数表示扣掉
                if (null == order.getFee())
                {
                    order.setFee(BigDecimal.ZERO);
                }
                order.setOrderId(orderId);
                order.setDealAmt(res.getBigDecimal("accFillSz"));
                order.setEntrustAmt(res.getBigDecimal("sz"));
                order.setEntrustPrice(res.getBigDecimal("px"));
                order.setAvgPrice(res.getBigDecimal("avgPx"));
                order.setPnl(res.getBigDecimal("pnl"));
                order.setEntrustSide(res.getString("side"));
                order.setPosSide(res.getString("posSide"));
                // 如果有成交则计算手续费率：手续费*-1*成交均价/成交张数*面值
                order.setCreateTime(res.getLongValue("cTime"));
                /**
                 * state	订单状态
                 * canceled：撤单成功
                 * live：等待成交
                 * partially_filled：部分成交
                 * filled：完全成交
                 */
                String state = res.getString("state");
                if ("filled".equalsIgnoreCase(state))
                {
                    order.setStatus(2);
                }
                else if ("canceled".equalsIgnoreCase(state))
                {
                    order.setStatus(order.getDealAmt().compareTo(BigDecimal.ZERO) == 1 ? 2 : 1);
                }
                else
                {
                    order.setStatus(order.getDealAmt().compareTo(BigDecimal.ZERO) == 1 ? 4 : 3);
                }
            }
            else
            {
                log.info("okexV5合约订单信息获取返回错误信息：{}", json.getString("msg"));
                return null;
            }
        }
        catch (Exception e)
        {
            log.info("okexV5合约订单获取失败：{}", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
        return order;
    }
    
    public void cancelSpotOrder(String symbol, long orderId)
    {
        try
        {
            CancelOrder cancelOrder = new CancelOrder();
            cancelOrder.setInstId(symbol);
            cancelOrder.setOrdId(String.valueOf(orderId));
            tradeAPIService.cancelOrder(cancelOrder);
        }
        catch (Exception e)
        {
            log.info("okexV5现货撤单异常：", e.getLocalizedMessage());
        }
    }
    
    public List<QuantCommonOrderModel> getSpotOpenOrders(String symbol)
    {
        List<QuantCommonOrderModel> orders = new ArrayList<>();
        JSONObject json = tradeAPIService.getOrderList("SPOT", null, symbol, null, null, null, null, null);
        if ("0".equalsIgnoreCase(json.getString("code")))
        {
            // 获取成功
            JSONArray array = json.getJSONArray("data");
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject res = array.getJSONObject(i);
                QuantCommonOrderModel order = new QuantCommonOrderModel();
                order.setOrderId(res.getLong("ordId"));
                order.setSymbol(res.getString("instId"));
                orders.add(order);
            }
        }
        else
        {
            log.info("okex V5未完成现货订单列表信息获取异常：{}", json.toString());
        }
        return orders;
    }
    
    /**
     * 交割合约普通下单
     * v3
     * order_type
     * 0：普通委托（order type不填或填0都是普通委托）
     * 1：只做Maker（Post only）
     * 2：全部成交或立即取消（FOK）
     * 3：立即成交并取消剩余（IOC）
     * 4：市价委托
     * @return
     */
    public long futureEntrust(String contractCode, BigDecimal price, BigDecimal cont, String direct, String posSide, String orderType, String tdMode)
    {
        /**
         *  tdMode
         * 交易模式，下单时需要指定
         * 简单交易模式：
         * - 币币和期权买方：cash
         * 单币种保证金模式：
         * - 逐仓杠杆：isolated
         * - 全仓杠杆：cross
         * - 全仓币币：cash
         * - 全仓交割/永续/期权：cross
         * - 逐仓交割/永续/期权：isolated
         * 跨币种保证金模式：
         * - 逐仓杠杆：isolated
         * - 全仓币币：cross
         * - 全仓交割/永续/期权：cross
         * - 逐仓交割/永续/期权：isolated
         */
        PlaceOrder placeOrder = new PlaceOrder();
        if (StringUtils.isEmpty(tdMode))
        {
            tdMode = "cross";//默认全仓
        }
        placeOrder.setTdMode(tdMode);
        placeOrder.setInstId(contractCode);
        placeOrder.setClOrdId(OrderIdUtils.generator());
        placeOrder.setSide(direct);
        placeOrder.setPosSide(posSide);
        placeOrder.setSz(cont.toString());
        placeOrder.setPx(price.toString());
        /**
         * ordType    String	是	订单类型
         * market：市价单
         * limit：限价单
         * post_only：只做maker单
         * fok：全部成交或立即取消
         * ioc：立即成交并取消剩余
         */
        placeOrder.setOrdType(orderType);
        // 接口调用
        JSONObject result = tradeAPIService.placeOrder(placeOrder);
        /**
         * {
         *     "code":"0",
         *     "msg":"",
         *     "data":[
         *         {
         *             "clOrdId":"oktswap6",
         *             "ordId":"12345689",
         *             "tag":"",
         *             "sCode":"0",
         *             "sMsg":""
         *         }
         *     ]
         * }
         */
        if ("0".equalsIgnoreCase(result.getString("code")))
        {
            return result.getJSONArray("data").getJSONObject(0).getLong("ordId");
        }
        else
        {
            log.info("okexV5合约下单接口返回错误信息： {}", result.getString("msg"));
            throw new RuntimeException(result.getString("msg"));
        }
    }
    
    /**
     *
     * @param symbol
     * @param price
     * @param amt
     * @param direct
     * @param orderType
     * @param tdMode 保证金模式，一般现货默认逐仓，不用传参
     * @return
     */
    public long spotEntrust(String symbol, BigDecimal price, BigDecimal amt, String direct, String orderType, String tdMode)
    {
        PlaceOrder placeOrder = new PlaceOrder();
        if (StringUtils.isEmpty(tdMode))
        {
            tdMode = "cash";// 币币现货
        }
        placeOrder.setTdMode(tdMode);
        placeOrder.setInstId(symbol);
        placeOrder.setClOrdId(OrderIdUtils.generator());
        placeOrder.setSide(direct);
        placeOrder.setSz(amt.toString());
        placeOrder.setPx(price.toString());
        /**
         * ordType    String	是	订单类型
         * market：市价单
         * limit：限价单
         * post_only：只做maker单
         * fok：全部成交或立即取消
         * ioc：立即成交并取消剩余
         */
        placeOrder.setOrdType(orderType);
        // 接口调用
        JSONObject result = tradeAPIService.placeOrder(placeOrder);
        /**
         * {
         *     "code":"0",
         *     "msg":"",
         *     "data":[
         *         {
         *             "clOrdId":"oktswap6",
         *             "ordId":"12345689",
         *             "tag":"",
         *             "sCode":"0",
         *             "sMsg":""
         *         }
         *     ]
         * }
         */
        if ("0".equalsIgnoreCase(result.getString("code")))
        {
            return result.getJSONArray("data").getJSONObject(0).getLong("ordId");
        }
        else
        {
            log.info("okexV5现货下单接口失败：{}", result.getString("msg"));
            throw new RuntimeException(result.getString("msg"));
        }
    }
    
    public QuantCommonAccountModel getFutureAccount(String currency)
    {
        QuantCommonAccountModel model = new QuantCommonAccountModel();
        try
        {
            /**
             * {
             *     "code": "0",
             *     "msg": "",
             *     "data": [{
             *         "uTime": "1614846244194",
             *         "totalEq": "91884.8502560037982063",
             *         "adjEq": "91884.8502560037982063",
             *         "isoEq": "0",
             *         "imr": "0",
             *         "mmr": "0",
             *         "mgnRatio": "100000",
             *         "details": [{
             *                 "availBal": "",
             *                 "availEq": "1",
             *                 "ccy": "BTC",
             *                 "disEq": "50559.01",
             *                 "eq": "1",
             *                 "frozenBal": "0",
             *                 "interest": "0",
             *                 "isoEq": "0",
             *                 "liab": "0",
             *                 "mgnRatio": "",
             *                 "ordFrozen": "0",
             *                 "upl": "0",
             *                 "uplLiab": "0"
             *             }
             *         ]
             *     }]
             * }
             */
            JSONObject result = accountAPIService.getBalance(currency);
            if ("0".equalsIgnoreCase(result.getString("code")))
            {
                JSONObject json = result.getJSONArray("data").getJSONObject(0).getJSONArray("details").getJSONObject(0);
                model.setContractBalance(json.getBigDecimal("eq"));
                model.setCanWithdraw(json.getBigDecimal("availEq"));// 与可用保证金共用了
                model.setAvailableMargin(json.getBigDecimal("availEq"));
            }
            else
            {
                log.info("okexV5获取账户资产接口失败：{}", result.getString("msg"));
                return null;
            }
        }
        catch (Exception e)
        {
            log.info("okexV5合约账户资产获取异常：{}", e.getLocalizedMessage());
            return null;
        }
        return model;
    }
    
    public QuantCommonAccountModel getSpotAccount(String currency)
    {
        QuantCommonAccountModel model = new QuantCommonAccountModel();
        try
        {
            /**
             * {
             *     "code": "0",
             *     "msg": "",
             *     "data": [{
             *         "uTime": "1614846244194",
             *         "totalEq": "91884.8502560037982063",
             *         "adjEq": "91884.8502560037982063",
             *         "isoEq": "0",
             *         "imr": "0",
             *         "mmr": "0",
             *         "mgnRatio": "100000",
             *         "details": [{
             *                 "availBal": "",
             *                 "availEq": "1",
             *                 "ccy": "BTC",
             *                 "disEq": "50559.01",
             *                 "eq": "1",
             *                 "frozenBal": "0",
             *                 "interest": "0",
             *                 "isoEq": "0",
             *                 "liab": "0",
             *                 "mgnRatio": "",
             *                 "ordFrozen": "0",
             *                 "upl": "0",
             *                 "uplLiab": "0"
             *             }
             *         ]
             *     }]
             * }
             */
            JSONObject result = accountAPIService.getBalance(currency.toUpperCase());
            if ("0".equalsIgnoreCase(result.getString("code")))
            {
                JSONObject json = result.getJSONArray("data").getJSONObject(0).getJSONArray("details").getJSONObject(0);
                model.setSpotBalance(json.getBigDecimal("eq"));
                model.setSpotEnable(json.getBigDecimal("availEq"));
            }
            else
            {
                log.info("okexV5获取账户资产接口返回错误信息：{}", result.getString("msg"));
                return null;
            }
        }
        catch (Exception e)
        {
            log.info("okexV5现货账户资产获取异常：{}", e.getLocalizedMessage());
            return null;
        }
        return model;
    }
    
    public QuantCommonDepthModel getFutureDepth(String contractCode)
    {
        QuantCommonDepthModel depthModel = new QuantCommonDepthModel();
        try
        {
            JSONObject json = marketDataAPIService.getTicker(contractCode).getJSONArray("data").getJSONObject(0);
            depthModel.setBid(json.getBigDecimal("bidPx"));
            depthModel.setAsk(json.getBigDecimal("askPx"));
            depthModel.setPrice(depthModel.getAsk().add(depthModel.getBid()).divide(BigDecimal.valueOf(2), 8, BigDecimal.ROUND_HALF_UP));
            String currency = contractCode.substring(0, contractCode.indexOf("-"));
            depthModel.setCurrency(currency);
        }
        catch (Exception e)
        {
            log.info("okexV5合约depth行情获取异常:{}", e.getLocalizedMessage());
            return null;
        }
        return depthModel;
    }
    
    public QuantCommonDepthModel getSpotDepth(String symbol)
    {
        QuantCommonDepthModel depthModel = new QuantCommonDepthModel();
        try
        {
            JSONObject json = marketDataAPIService.getTicker(symbol).getJSONArray("data").getJSONObject(0);
            depthModel.setBid(json.getBigDecimal("bidPx"));
            depthModel.setAsk(json.getBigDecimal("askPx"));
            depthModel.setPrice(depthModel.getAsk().add(depthModel.getBid()).divide(BigDecimal.valueOf(2), 8, BigDecimal.ROUND_HALF_UP));
            String currency = symbol.substring(0, symbol.indexOf("-"));
            depthModel.setCurrency(currency);
        }
        catch (Exception e)
        {
            log.info("okexV5现货depth行情获取异常:{}", e.getLocalizedMessage());
            return null;
        }
        return depthModel;
    }
    
    public QuantCommonOrderModel getSpotOrder(String symbol, long orderId)
    {
        String currency = symbol.substring(0, symbol.indexOf("-"));
        QuantCommonOrderModel order = new QuantCommonOrderModel();
        order.setCurrency(currency);
        try
        {
            JSONObject json = tradeAPIService.getOrderDetails(symbol, String.valueOf(orderId), "");
            if ("0".equalsIgnoreCase(json.getString("code")))
            {
                JSONObject res = json.getJSONArray("data").getJSONObject(0);
                order.setFee(res.getBigDecimal("fee"));// 手续费字段为负数表示扣掉
                if (null == order.getFee())
                {
                    order.setFee(BigDecimal.ZERO);
                }
                order.setOrderId(orderId);
                order.setDealAmt(res.getBigDecimal("accFillSz"));
                order.setEntrustAmt(res.getBigDecimal("sz"));
                order.setAvgPrice(res.getBigDecimal("avgPx"));
                if ("market".equalsIgnoreCase(json.getString("ordType")))
                {
                    order.setEntrustPrice(order.getAvgPrice());
                }
                else
                {
                    order.setEntrustPrice(res.getBigDecimal("px"));
                }
                order.setCreateTime(res.getLongValue("cTime"));
                // 成交量大于0则计算手续费率
                if (order.getDealAmt().compareTo(BigDecimal.ZERO) == 1)
                {
                    String rebate = res.getString("rebate");
                    String rebateCcy = res.getString("rebateCcy");
                    String fee = res.getString("fee");
                    String side = res.getString("side");
                    // 先判断是否返佣了rebate
                    if (StringUtils.isNotEmpty(rebate) && StringUtils.isNotEmpty(rebateCcy))
                    {
                        order.setFee(new BigDecimal(res.getString("rebate")));
                        if ("USDT".equalsIgnoreCase(rebateCcy))
                        {
                            // 如果是u，则
                            order.setFeeRate(
                                    order.getFee().multiply(BigDecimal.valueOf(-1)).divide(order.getDealAmt().multiply(order.getAvgPrice()), 6, BigDecimal.ROUND_HALF_UP));
                        }
                        else
                        {
                            order.setFeeRate(order.getFee().multiply(BigDecimal.valueOf(-1)).divide(order.getDealAmt(), 6, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                    else
                    {
                        if (StringUtils.isEmpty(fee))
                        {
                            order.setFee(BigDecimal.ZERO);
                        }
                        else
                        {
                            order.setFee(new BigDecimal(fee));
                        }
                        if (KeyConst.SELL.equalsIgnoreCase(side))
                        {
                            order.setFeeRate(
                                    order.getFee().multiply(BigDecimal.valueOf(-1)).divide(order.getDealAmt().multiply(order.getAvgPrice()), 6, BigDecimal.ROUND_HALF_UP));// 卖出则收usdt
                        }
                        else if (KeyConst.BUY.equalsIgnoreCase(side))
                        {
                            order.setFeeRate(order.getFee().multiply(BigDecimal.valueOf(-1)).divide(order.getDealAmt(), 6, BigDecimal.ROUND_HALF_UP));// 买入则收币
                        }
                        else
                        {
                            order.setFeeRate(BigDecimal.ZERO);
                        }
                    }
                }
                else
                {
                    order.setFee(BigDecimal.ZERO);
                    order.setFeeRate(BigDecimal.ZERO);
                }
                /**
                 * state	订单状态
                 * canceled：撤单成功
                 * live：等待成交
                 * partially_filled：部分成交
                 * filled：完全成交
                 */
                String state = res.getString("state");
                if ("filled".equalsIgnoreCase(state))
                {
                    order.setStatus(1);
                }
                else
                {
                    order.setStatus(0);
                    if ("canceled".equalsIgnoreCase(state))
                    {
                        order.setCancel(true);
                    }
                }
            }
            else
            {
                log.info("okexV5现货订单信息获取返回错误信息：{}", json.getString("msg"));
                return null;
            }
        }
        catch (Exception e)
        {
            log.info("okex现货订单信息获取失败：{}", e.getLocalizedMessage());
            return null;
        }
        return order;
    }
    
    public JSONObject getSpotTradeFeeRate()
    {
        return accountAPIService.getFeeRates("SPOT", "", "", "1").getJSONArray("data").getJSONObject(0);
    }
    
    public JSONObject getContractTradeFeeRate(String instType)
    {
        return accountAPIService.getFeeRates(instType, "", "", "1").getJSONArray("data").getJSONObject(0);
    }
    
    /**
     * 修改单币种全仓模式杠杆倍数
     * @param contractCode
     * @param lever
     */
    public void changeContractLeverOnCross(String contractCode, int lever)
    {
        SetLeverage leverage = new SetLeverage();
        leverage.setInstId(contractCode);
        leverage.setMgnMode("cross");
        leverage.setLever(String.valueOf(lever));
        accountAPIService.setLeverage(leverage);
    }
    
    public List<QuantCommonOrderModel> getContractOpenOrders(String insType, String contractCode)
    {
        List<QuantCommonOrderModel> orders = new ArrayList<>();
        JSONObject json = tradeAPIService.getOrderList(insType, null, contractCode, null, null, null, null, null);
        if ("0".equalsIgnoreCase(json.getString("code")))
        {
            // 获取成功
            JSONArray array = json.getJSONArray("data");
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject res = array.getJSONObject(i);
                QuantCommonOrderModel order = new QuantCommonOrderModel();
                order.setOrderId(res.getLong("ordId"));
                order.setSymbol(res.getString("instId"));
                orders.add(order);
            }
        }
        else
        {
            log.info("okex V5未完成合约订单列表信息获取异常：{}", json.toString());
        }
        return orders;
    }
    
    /**
     * 获取永续合约资金费率，获取失败则返回null
     * {
     *     "code":"0",
     *     "msg":"",
     *     "data":[
     *      {
     *         "instType":"SWAP",
     *         "instId":"BTC-USDT-SWAP",
     *         "fundingRate":"0.018",
     *         "nextFundingRate":"0.017",
     *         "fundingTime":"1597026383085"
     *     }
     *   ]
     * }
     * @return
     */
    public QuantCommonFundingRateModel getFundingRate(String contractCode)
    {
        try
        {
            JSONObject res = publicDataAPIService.getFundingRate(contractCode);
            JSONObject json = res.getJSONArray("data").getJSONObject(0);
            QuantCommonFundingRateModel model = new QuantCommonFundingRateModel();
            model.setCurrentRate(json.getBigDecimal("fundingRate"));
            model.setCurrentRateABS(model.getCurrentRate().abs());
            model.setNextRate(json.getBigDecimal("nextFundingRate"));
            model.setTime(json.getLong("fundingTime"));
            model.setContractCode(contractCode);
            return model;
        }
        catch (Exception e)
        {
            log.info("okex v5永续合约资金费率获取失败：{}|{}", contractCode, e.getLocalizedMessage());
            return null;
        }
    }
    
    public long closeAllPositionByMaket(String instId, String posDirect, String marginMode, String marginCurrency)
    {
        ClosePositions param = new ClosePositions();
        param.setInstId(instId);
        param.setPosSide(posDirect);
        param.setMgnMode(marginMode);
        param.setCcy(marginCurrency);
        JSONObject json = tradeAPIService.closePositions(param);
        return "0".equalsIgnoreCase(json.getString("code")) ? 1 : 0;
    }
}
