package com.slf.quant.strategy.hedge.client.usdt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.slf.quant.facade.consts.UrlConst;
import com.slf.quant.facade.entity.strategy.QuantHedgeConfig;
import com.slf.quant.facade.model.*;
import com.slf.quant.facade.model.hedge.Account;
import com.slf.quant.facade.model.hedge.Position;
import com.slf.quant.facade.model.hedge.Ticker;
import com.slf.quant.facade.sdk.okex.bean.futures.param.Order;
import com.slf.quant.facade.sdk.okex.bean.futures.result.Instruments;
import com.slf.quant.facade.sdk.okex.bean.futures.result.OrderResult;
import com.slf.quant.facade.sdk.okex.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.enums.I18nEnum;
import com.slf.quant.facade.sdk.okex.service.account.AccountAPIService;
import com.slf.quant.facade.sdk.okex.service.account.impl.AccountAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.service.futures.FuturesMarketAPIService;
import com.slf.quant.facade.sdk.okex.service.futures.FuturesTradeAPIService;
import com.slf.quant.facade.sdk.okex.service.futures.impl.FuturesMarketAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.service.futures.impl.FuturesTradeAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.utils.OrderIdUtils;
import com.slf.quant.facade.sdk.okex.v5.OkexV5CommonApiClient;
import com.slf.quant.facade.utils.QuantUtil;
import com.slf.quant.strategy.consts.TradeConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class OKexV5HedgeUsdtClient extends AbstractHedgeUsdtClient
{
    private OkexV5CommonApiClient commonApiClient;
    
    public OKexV5HedgeUsdtClient(QuantHedgeConfig config, String apiKey, String secretKey, String passPhrase)
    {
        super(config, apiKey, secretKey, passPhrase);
        commonApiClient = new OkexV5CommonApiClient(apiKey, secretKey, passPhrase);
        List<QuantCommonSymbolInfo> futureSymbolInfos = commonApiClient.getFutureSymbolInfo(
                config.getLongSymbol().contains("SWAP") ? TradeConst.OKEXV5_INSTTYPE_SWAP : TradeConst.OKEXV5_INSTTYPE_FUTURES, config.getLongSymbol());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(futureSymbolInfos))
        {
            QuantCommonSymbolInfo symbolInfo = futureSymbolInfos.get(0);
            contract_tick_size = symbolInfo.getContractPriceTick();
            contract_price_precision = symbolInfo.getContractPricePrecision();
            contractPerValue = symbolInfo.getContractVal();
        }
        else
        {
            throw new RuntimeException("okex U????????????????????????" + config.getLongSymbol());
        }
    }
    
    @Override
    protected Position getfuturePosition(String currency, String code)
    {
        Position position = new Position();
        try
        {
            QuantCommonPositionModel positionModel = commonApiClient.getFuturePosition(TradeConst.OKEXV5_INSTTYPE_FUTURES, code);
            position.setLongCont(positionModel.getLongCont());
            position.setShortCont(positionModel.getShortCont());
            position.setLong_avg_price(positionModel.getAvgPrice());
            position.setShort_avg_price(positionModel.getAvgPrice());
        }
        catch (Exception e)
        {
            log.info("okex????????????????????????????????????????????????{}", e.getLocalizedMessage());
            return null;
        }
        return position;
    }
    
    @Override
    protected Account getFutureAccount(String currency)
    {
        Account model = new Account();
        try
        {
            QuoteDepth quoteDepth = getFutureDepth(config.getLongSymbol());
            if (null == quoteDepth)
            { return null; }
            QuantCommonAccountModel accountModel = commonApiClient.getFutureAccount(currency);
            model.setEquity(accountModel.getContractBalance());
            model.setAvailableMargin(accountModel.getAvailableMargin());// ???????????????
            model.setAvailableCont(model.getAvailableMargin().divide(quoteDepth.getBid(), 8, BigDecimal.ROUND_HALF_UP).multiply(config.getLeverRate())
                    .multiply(new BigDecimal("0.9")).divide(contractPerValue, 0, BigDecimal.ROUND_DOWN));// ????????????= ???????????????/????????????*????????????*????????????????????????/????????????
        }
        catch (Exception e)
        {
            log.info("okex?????????????????????????????????{}", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
        return model;
    }
    
    @Override
    protected Ticker getPremiumTicker()
    {
        Ticker ticker = new Ticker();
        try
        {
            // ????????????
            QuoteDepth longQuoteDepth = TradeConst.okex_depth_map.get(config.getLongSymbol());
            if (null == longQuoteDepth || System.currentTimeMillis() - longQuoteDepth.getUpdateTime() > TradeConst.quoteTimeout)
            {
                log.info("okex{}??????????????????{}???!", config.getLongSymbol(), TradeConst.cacheLoseCount.incrementAndGet());
                QuantCommonDepthModel commonDepthModel = commonApiClient.getFutureDepth(config.getLongSymbol());
                ticker.setLongBid(commonDepthModel.getBid());
                ticker.setLongAsk(commonDepthModel.getAsk());
            }
            else
            {
                ticker.setLongAsk(longQuoteDepth.getAsk());
                ticker.setLongBid(longQuoteDepth.getBid());
            }
            // ????????????
            QuoteDepth shortQuoteDepth = TradeConst.okex_depth_map.get(config.getShortSymbol());
            if (null == shortQuoteDepth || System.currentTimeMillis() - shortQuoteDepth.getUpdateTime() > TradeConst.quoteTimeout)
            {
                log.info("okex{}??????????????????{}???!", config.getShortSymbol(), TradeConst.cacheLoseCount.incrementAndGet());
                QuantCommonDepthModel commonDepthModel = commonApiClient.getFutureDepth(config.getShortSymbol());
                ticker.setShortBid(commonDepthModel.getBid());
                ticker.setShortAsk(commonDepthModel.getAsk());
            }
            else
            {
                ticker.setShortAsk(shortQuoteDepth.getAsk());
                ticker.setShortBid(shortQuoteDepth.getBid());
            }
            ticker.setOpenRatio(ticker.getShortBid().subtract(ticker.getLongAsk()).divide(ticker.getLongAsk(), 6, BigDecimal.ROUND_DOWN));
            ticker.setCloseRatio(ticker.getShortAsk().subtract(ticker.getLongBid()).divide(ticker.getLongBid(), 6, BigDecimal.ROUND_UP));
        }
        catch (Exception e)
        {
            log.info("okex?????????????????????????????????{}", e.getLocalizedMessage());
            return null;
        }
        return ticker;
    }
    
    @Override
    protected void cancelFutureOrder(String symbol, Long orderId)
    {
        commonApiClient.cancelFutureOrder(symbol, orderId);
    }
    
    @Override
    protected QuoteDepth getFutureDepth(String symbol)
    {
        QuoteDepth quoteDepth = null;
        try
        {
            quoteDepth = TradeConst.okex_depth_map.get(symbol);
            if (null == quoteDepth || System.currentTimeMillis() - quoteDepth.getUpdateTime() > TradeConst.quoteTimeout)
            {
                log.info("okex{}??????????????????{}???!", config.getLongSymbol(), TradeConst.cacheLoseCount.incrementAndGet());
                QuantCommonDepthModel commonDepthModel = commonApiClient.getFutureDepth(config.getShortSymbol());
                quoteDepth = new QuoteDepth();
                quoteDepth.setBid(commonDepthModel.getBid());
                quoteDepth.setAsk(commonDepthModel.getAsk());
            }
        }
        catch (Exception e)
        {
            log.info("okex??????depth?????????????????????{}", e.getLocalizedMessage());
            return null;
        }
        return quoteDepth;
    }
    
    @Override
    protected ContractOrder getFutureOrder(String symbol, long id)
    {
        ContractOrder contractOrder = new ContractOrder();
        try
        {
            QuantCommonOrderModel order = commonApiClient.getFutureOrder(symbol, id);
            contractOrder.setFee(order.getFee());
            contractOrder.setId(id);
            contractOrder.setDealCont(order.getDealAmt());
            contractOrder.setEntrustCont(order.getEntrustAmt());
            contractOrder.setEntrustPrice(order.getEntrustPrice());
            contractOrder.setAvg_price(order.getAvgPrice());
            contractOrder.setEntrustTime(order.getCreateTime());
            contractOrder.setStatus(order.getStatus());
        }
        catch (Exception e)
        {
            log.info("okex?????????????????????????????????{}", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
        return contractOrder;
    }
    
    @Override
    protected long createFutureOrder(String symbol, BigDecimal price, BigDecimal cont, Integer orderType, boolean maker)
    {
        try
        {
            String direct = orderType.equals(1) || orderType.equals(4) ? "buy" : "sell";
            String posSide = orderType.equals(1) || orderType.equals(3) ? "long" : "short";
            return commonApiClient.futureEntrust(symbol, price, cont, direct, posSide, maker ? "post_only" : "limit", null);
        }
        catch (Exception e)
        {
            log.info("okex v5???????????????????????????{}", e.getLocalizedMessage());
        }
        return 0;
    }
}
