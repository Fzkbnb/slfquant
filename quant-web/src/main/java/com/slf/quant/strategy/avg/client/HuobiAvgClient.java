package com.slf.quant.strategy.avg.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.slf.quant.facade.sdk.huobi.spot.client.RequestOptions;
import com.slf.quant.facade.sdk.huobi.spot.client.SyncRequestClient;
import com.slf.quant.facade.sdk.huobi.spot.client.model.LastTradeAndBestQuote;
import com.slf.quant.facade.sdk.huobi.spot.client.model.Order;
import com.slf.quant.facade.sdk.huobi.spot.client.model.Symbol;
import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.AccountType;
import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.OrderState;
import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.OrderType;
import com.slf.quant.facade.sdk.huobi.spot.client.model.request.NewOrderRequest;

import com.slf.quant.facade.consts.UrlConst;
import com.slf.quant.facade.entity.strategy.QuantAvgConfig;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.model.SpotOrder;
import com.slf.quant.strategy.consts.TradeConst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HuobiAvgClient extends AbstractAvgClient
{
    SyncRequestClient spotClient; // 现货客户端
    
    private String    spotSymbol;
    
    public HuobiAvgClient(QuantAvgConfig config, String apiKey, String secretKey, String passPhrase)
    {
        super(config, apiKey, secretKey, passPhrase);
    }
    
    @Override
    protected void cancelSpotOrder(String currency, long orderId)
    {
        try
        {
            spotClient.cancelOrder(currency.toLowerCase() + "usdt", orderId);
        }
        catch (Exception e)
        {
            log.info("策略{} ，huobi现货撤单异常：{}", idStr, e.getLocalizedMessage());
        }
    }
    
    @Override
    protected long spotEntrust(String currency, BigDecimal entrustPrice, BigDecimal entrustAmt, String direct)
    {
        long orderId = 0;
        try
        {
            String clientOrderId = "T" + System.nanoTime();
            OrderType order_type;
            if ("buy".equalsIgnoreCase(direct))
            {
                order_type = OrderType.BUY_LIMIT_MAKER;
            }
            else
            {
                order_type = OrderType.SELL_LIMIT_MAKER;
            }
            NewOrderRequest newOrderRequest = new NewOrderRequest(currency.toLowerCase() + "usdt", AccountType.SPOT, order_type, entrustAmt, entrustPrice, clientOrderId,
                    null, null);
            orderId = spotClient.createOrder(newOrderRequest);
        }
        catch (Exception e)
        {
            log.info("策略{} ，huobi现货委托异常：{}", config.getId(), e.getLocalizedMessage());
        }
        return orderId;
    }
    
    @Override
    protected SpotOrder getSpotOrder(String currency, long orderId)
    {
        SpotOrder order = new SpotOrder();
        String symbol = currency.toLowerCase() + "usdt";
        try
        {
            Order spotOrder = spotClient.getOrder(symbol, orderId);
            order.setDealAmt(spotOrder.getFilledAmount());
            if (spotOrder.getFilledAmount().compareTo(BigDecimal.ZERO) == 0)
            {
                order.setAvg_price(BigDecimal.ZERO);
            }
            else
            {
                order.setAvg_price(spotOrder.getFilledCashAmount().divide(spotOrder.getFilledAmount(), 8, BigDecimal.ROUND_HALF_UP));
            }
            order.setEntrustAmt(spotOrder.getAmount());
            order.setId(orderId);
            order.setEntrustPrice(spotOrder.getPrice());
            order.setEntrustTime(spotOrder.getCreatedTimestamp());
            String res = spotOrder.getState().toString();
            if (OrderState.FILLED.toString().equals(res))
            {
                // 仅当全部成交时认为完成
                order.setStatus(1);
            }
            else
            {
                order.setStatus(0);
                if (OrderState.CANCELED.toString().equals(res) || OrderState.PARTIALCANCELED.toString().equals(res))
                {
                    log.info("策略{} ，huobi现货订单未完全成交已撤单，将认为是完成状态!", idStr);
                    order.setStatus(1);
                }
            }
        }
        catch (Exception e)
        {
            log.info("策略{} ，huobi现货订单信息获取失败：{}", idStr, e.getLocalizedMessage());
            return null;
        }
        return order;
    }
    
    @Override
    protected QuoteDepth getSpotDepth(String symbol)
    {
        QuoteDepth depthModel = new QuoteDepth();
        try
        {
            String symbolSpot = config.getCurrency().toLowerCase() + "usdt";
            QuoteDepth cacheModel = TradeConst.huobi_depth_map.get(symbolSpot);
            if (null == cacheModel || System.currentTimeMillis() - cacheModel.getUpdateTime() > TradeConst.quoteTimeout)
            {
                LastTradeAndBestQuote quote = spotClient.getLastTradeAndBestQuote(symbolSpot);
                depthModel.setAsk(quote.getAskPrice());
                depthModel.setBid(quote.getBidPrice());
            }
            else
            {
                depthModel.setAsk(cacheModel.getAsk());
                depthModel.setBid(cacheModel.getBid());
            }
        }
        catch (Exception e)
        {
            log.info("策略{} ，huobi现货depth行情获取异常：{}", config.getId(), e.getLocalizedMessage());
            return null;
        }
        return depthModel;
    }
    
    @Override
    protected boolean cancelAllOrder()
    {
        return true;
    }
    
    @Override
    protected void initApiData()
    {
        try
        {
            spotClient = SyncRequestClient.create(apiKey, secretKey, new RequestOptions(UrlConst.huobi_endpoint_spot));
            spotSymbol = config.getCurrency().toLowerCase() + "usdt";
            List<Symbol> symbols = spotClient.getSymbols();
            Symbol symbol = symbols.stream().filter(s -> s.getSymbol().equals(spotSymbol)).collect(Collectors.toList()).get(0);
            pricePrecision = symbol.getPricePrecision();
            tickSize = BigDecimal.valueOf(Math.pow(10, -pricePrecision));
            amtPrecision = symbol.getAmountPrecision();
            minSpotAmt = symbol.getMinOrderAmt();
        }
        catch (Exception e)
        {
            log.info("策略{}，api数据huobi初始化失败，将关闭策略:{}", config.getId(), e.getLocalizedMessage());
            hasError = true;
            e.printStackTrace();
        }
    }
}
