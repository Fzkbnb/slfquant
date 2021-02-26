package com.slf.quant.strategy.grid.client;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.slf.quant.facade.consts.UrlConst;
import com.slf.quant.facade.entity.strategy.QuantGridConfig;
import com.slf.quant.facade.model.AccountModel;
import com.slf.quant.facade.model.ContractOrder;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.sdk.okex.bean.futures.param.Order;
import com.slf.quant.facade.sdk.okex.bean.futures.result.Instruments;
import com.slf.quant.facade.sdk.okex.bean.futures.result.OrderResult;
import com.slf.quant.facade.sdk.okex.bean.futures.result.Ticker;
import com.slf.quant.facade.sdk.okex.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.enums.I18nEnum;
import com.slf.quant.facade.sdk.okex.service.account.AccountAPIService;
import com.slf.quant.facade.sdk.okex.service.account.impl.AccountAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.service.futures.FuturesMarketAPIService;
import com.slf.quant.facade.sdk.okex.service.futures.FuturesTradeAPIService;
import com.slf.quant.facade.sdk.okex.service.futures.impl.FuturesMarketAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.service.futures.impl.FuturesTradeAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.utils.OrderIdUtils;
import com.slf.quant.facade.utils.QuantUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.slf.quant.strategy.consts.TradeConst;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OKexGridUsdtClient extends AbstractGridUsdtClient
{
    private FuturesMarketAPIService marketAPIService;
    
    private AccountAPIService accountAPIService;
    
    private FuturesTradeAPIService tradeAPIService;
    
    public OKexGridUsdtClient(QuantGridConfig config, String apiKey, String secretKey, String passPhrase)
    {
        super(config, apiKey, secretKey, passPhrase);
        APIConfiguration config1 = new APIConfiguration();
        config1.setEndpoint(UrlConst.okex_endpoint);
        config1.setApiKey(apiKey);
        config1.setSecretKey(secretKey);
        config1.setPassphrase(passPhrase);
        config1.setI18n(I18nEnum.ENGLISH);
        config1.setPrint(false);
        marketAPIService = new FuturesMarketAPIServiceImpl(config1);
        tradeAPIService = new FuturesTradeAPIServiceImpl(config1);
        accountAPIService = new AccountAPIServiceImpl(config1);
        List<Instruments> instruments = marketAPIService.getInstruments();
        instruments = instruments.stream().filter(ins -> ins.getInstrument_id().equalsIgnoreCase(config.getContractCode())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(instruments))
        {
            tickSize = new BigDecimal(instruments.get(0).getTick_size());
            pricePrecision = QuantUtil.getPrecision(tickSize);
            contractPerValue = new BigDecimal(instruments.get(0).getContract_val());
        }
        else
        {
            throw new RuntimeException("合约币对不存在：" + config.getContractCode());
        }
    }
    
    // @Override
    // protected Position getfuturePosition(String currency, String code)
    // {
    // Position position = new Position();
    // try
    // {
    // JSONObject json = tradeAPIService.getInstrumentPosition(code);
    // JSONArray jsonArray = json.getJSONArray("holding");
    // for (int i = 0; i < jsonArray.size(); i++)
    // {
    // JSONObject obj = jsonArray.getJSONObject(i);
    // if (code.equalsIgnoreCase(obj.getString("instrument_id")))
    // {
    // position.setLongCont(obj.getBigDecimal("long_qty"));
    // position.setShortCont(obj.getBigDecimal("short_qty"));
    // }
    // }
    // }
    // catch (Exception e)
    // {
    // log.info("okex币本位交割合约持仓信息获取异常：{}", e.getLocalizedMessage());
    // return null;
    // }
    // return position;
    // }
    @Override
    protected AccountModel getContractAccount(String currency)
    {
        AccountModel model = new AccountModel();
        try
        {
            // 合约
            JSONObject json = tradeAPIService.getAccountsByCurrency(new StringBuilder(currency.toLowerCase()).append("-usdt").toString().toLowerCase());
            BigDecimal equity = json.getBigDecimal("equity");
            BigDecimal marginFrozen = json.getBigDecimal("margin_frozen");// 持仓冻结保证金
            if (null == marginFrozen)
            {
                marginFrozen = BigDecimal.ZERO;
            }
            BigDecimal marginUnfilled = json.getBigDecimal("margin_for_unfilled");// 挂单冻结保证金
            model.setContractBalance(equity);
            if (null == marginUnfilled)
            {
                marginUnfilled = BigDecimal.ZERO;
            }
            model.setAvailableMargin(equity.subtract(marginFrozen).subtract(marginUnfilled));// 可用保证金
        }
        catch (Exception e)
        {
            log.info("okex交割合约账户资产获取异常：{}", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
        return model;
    }
    
    @Override
    protected void cancelContractOrder(String symbol, long orderId)
    {
        try
        {
            tradeAPIService.cancelOrderByOrderId(symbol, String.valueOf(orderId));
        }
        catch (Exception e)
        {
            log.info("okex交割合约订单ID{},撤单异常：{}", orderId, e.getLocalizedMessage());
        }
    }
    
    @Override
    protected QuoteDepth getContractDepth(String symbol)
    {
        QuoteDepth depth = null;
        try
        {
            depth = TradeConst.okex_depth_map.get(symbol);
            if (null == depth || System.currentTimeMillis() - depth.getUpdateTime() > TradeConst.quoteTimeout)
            {
                log.info("okex{}缓存行情失效{}次!", config.getContractCode(), TradeConst.cacheLoseCount.incrementAndGet());
                Ticker future = marketAPIService.getInstrumentTicker(config.getContractCode());
                depth = new QuoteDepth();
                depth.setBid(new BigDecimal(future.getBest_bid()));
                depth.setAsk(new BigDecimal(future.getBest_ask()));
            }
        }
        catch (Exception e)
        {
            log.info("okex交割depth行情获取异常：{}", e.getLocalizedMessage());
            return null;
        }
        return depth;
    }

    @Override
    protected long closePositionByMarket(String currency, String firstDirect) {
        return 0;
    }

    @Override
    protected boolean cancelAllOrder() {
        return true;
    }



    @Override
    protected ContractOrder getContractOrder(String symbol, long id)
    {
        ContractOrder order = new ContractOrder();
        try
        {
            JSONObject json = tradeAPIService.getOrderByOrderId(symbol, String.valueOf(id));
            order.setFee(json.getBigDecimal("fee"));
            order.setId(json.getLong("order_id"));
            order.setDealCont(json.getBigDecimal("filled_qty"));
            order.setEntrustCont(json.getBigDecimal("size"));
            order.setEntrustPrice(json.getBigDecimal("price"));
            order.setAvg_price(json.getBigDecimal("price_avg"));
            order.setDirect("1".equalsIgnoreCase(json.getString("type")) || "4".equalsIgnoreCase(json.getString("type")) ? TradeConst.KEY_BUY : TradeConst.KEY_SELL);
            String time = StringUtils.replace(json.getString("timestamp"), "T", " ");
            time = StringUtils.replace(time, "Z", " ");
            order.setEntrustTime(Timestamp.valueOf(time).getTime() + 3600000l * 8);
            Integer state = json.getInteger("state");
            if (state.equals(-1) || state.equals(2))
            {
                order.setStatus(order.getDealCont().compareTo(BigDecimal.ZERO) == 1 ? 2 : 1);
            }
            else
            {
                order.setStatus(order.getDealCont().compareTo(BigDecimal.ZERO) == 1 ? 4 : 3);
            }
        }
        catch (Exception e)
        {
            log.info("okex交割合约订单获取异常：{}", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
        return order;
    }

    @Override
    protected void initApiData() {

    }

    @Override
    protected long contractEntrust(String symbol, BigDecimal entrustCont, BigDecimal entrustPrice, String direct, Integer orderType)
    {
        try
        {
            Order order = new Order();
            order.setInstrument_id(symbol);
            order.setClient_oid(OrderIdUtils.generator());
            order.setType(getOrderType(direct));
            order.setSize(entrustCont.toString());
            order.setPrice(entrustPrice.toString());
            order.setOrder_type(String.valueOf(orderType));
            OrderResult result = tradeAPIService.order(order);
            if (StringUtils.isEmpty(result.getError_messsage()))
            {
                return Long.parseLong(result.getOrder_id());
            }
            else
            {
                log.info("okex交割合约下单失败：{}", result.getError_messsage());
            }
        }
        catch (Exception e)
        {
            log.info("okex交割合约下单异常：{}", e.getLocalizedMessage());
        }
        return 0;
    }


    private String getOrderType(String direct)
    {
        if (isFirstBuy())
        {
            if (TradeConst.KEY_BUY.equalsIgnoreCase(direct))
            {
                return "1";
            }
            else
            {
                return "3";
            }
        }
        else
        {
            if (TradeConst.KEY_BUY.equalsIgnoreCase(direct))
            {
                return "4";
            }
            else
            {
                return "2";
            }
        }
    }
}
