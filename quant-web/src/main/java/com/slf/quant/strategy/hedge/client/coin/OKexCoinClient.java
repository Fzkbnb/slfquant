package com.slf.quant.strategy.hedge.client.coin;//package com.biex.eladmin.hedge.client.coin;
//
//import java.math.BigDecimal;
//import java.sql.Timestamp;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.biex.quant.consts.QuantConst;
//import com.biex.quant.consts.UrlConst;
//import com.biex.quant.hedge.futuretofuture.*;
//import com.biex.quant.model.ContractOrder;
//import com.biex.quant.model.QuoteDepth;
//import com.biex.quant.sdk.okex.bean.futures.result.Instruments;
//import com.biex.quant.sdk.okex.bean.futures.result.OrderResult;
//import com.biex.quant.sdk.okex.config.APIConfiguration;
//import com.biex.quant.sdk.okex.enums.I18nEnum;
//import com.biex.quant.sdk.okex.service.account.AccountAPIService;
//import com.biex.quant.sdk.okex.service.account.serviceimpl.AccountAPIServiceImpl;
//import com.biex.quant.sdk.okex.service.futures.FuturesMarketAPIService;
//import com.biex.quant.sdk.okex.service.futures.FuturesTradeAPIService;
//import com.biex.quant.sdk.okex.service.futures.serviceimpl.FuturesMarketAPIServiceImpl;
//import com.biex.quant.sdk.okex.service.futures.serviceimpl.FuturesTradeAPIServiceImpl;
//import com.biex.quant.sdk.okex.tools.OrderIdUtils;
//import com.biex.quant.util.QuantUtil;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class OKexCoinClient extends AbstractCoinClient
//{
//    private FuturesMarketAPIService marketAPIService;
//
//    private AccountAPIService       accountAPIService;
//
//    private FuturesTradeAPIService  tradeAPIService;
//
//    public OKexCoinClient(Config config, String apiKey, String secretKey, String passPhrase)
//    {
//        super(config, apiKey, secretKey, passPhrase);
//        APIConfiguration config1 = new APIConfiguration();
//        config1.setEndpoint(UrlConst.okex_endpoint);
//        config1.setApiKey(apiKey);
//        config1.setSecretKey(secretKey);
//        config1.setPassphrase(passPhrase);
//        config1.setI18n(I18nEnum.ENGLISH);
//        config1.setPrint(false);
//        marketAPIService = new FuturesMarketAPIServiceImpl(config1);
//        tradeAPIService = new FuturesTradeAPIServiceImpl(config1);
//        accountAPIService = new AccountAPIServiceImpl(config1);
//        List<Instruments> instruments = marketAPIService.getInstruments();
//        instruments = instruments.stream().filter(ins -> ins.getInstrument_id().equalsIgnoreCase(config.getLongSymbol())).collect(Collectors.toList());
//        if (!CollectionUtils.isEmpty(instruments))
//        {
//            contract_tick_size = new BigDecimal(instruments.get(0).getTick_size());
//            contract_price_precision = QuantUtil.getPrecision(contract_tick_size);
//            contractPerValue = new BigDecimal(instruments.get(0).getContract_val());
//        }
//        else
//        {
//            throw new RuntimeException("????????????????????????" + config.getLongSymbol());
//        }
//    }
//
//    @Override
//    protected Position getfuturePosition(String currency, String code)
//    {
//        Position position = new Position();
//        try
//        {
//            JSONObject json = tradeAPIService.getInstrumentPosition(code);
//            JSONArray jsonArray = json.getJSONArray("holding");
//            for (int i = 0; i < jsonArray.size(); i++)
//            {
//                JSONObject obj = jsonArray.getJSONObject(i);
//                if (code.equalsIgnoreCase(obj.getString("instrument_id")))
//                {
//                    position.setLongCont(obj.getBigDecimal("long_qty"));
//                    position.setShortCont(obj.getBigDecimal("short_qty"));
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            log.info("okex????????????????????????????????????????????????{}", e.getLocalizedMessage());
//            return null;
//        }
//        return position;
//    }
//
//    @Override
//    protected Account getFutureAccount(String currency)
//    {
//        Account model = new Account();
//        try
//        {
//            // ??????
//            QuoteDepth quoteDepth = getFutureDepth(config.getLongSymbol());
//            if (null == quoteDepth)
//            { return null; }
//            JSONObject json = tradeAPIService.getAccountsByCurrency(new StringBuilder(currency.toLowerCase()).append("-usd").toString().toLowerCase());
//            BigDecimal equity = json.getBigDecimal("equity");
//            BigDecimal marginFrozen = json.getBigDecimal("margin_frozen");// ?????????????????????
//            if (null == marginFrozen)
//            {
//                marginFrozen = BigDecimal.ZERO;
//            }
//            BigDecimal marginUnfilled = json.getBigDecimal("margin_for_unfilled");// ?????????????????????
//            model.setEquity(equity);
//            if (null == marginUnfilled)
//            {
//                marginUnfilled = BigDecimal.ZERO;
//            }
//            model.setAvailableMargin(equity.subtract(marginFrozen).subtract(marginUnfilled));// ???????????????
//            model.setAvailableCont(model.getAvailableMargin().multiply(quoteDepth.getBid()).multiply(BigDecimal.valueOf(40)).multiply(new BigDecimal("0.9")).divide(contractPerValue, 0,
//                    BigDecimal.ROUND_DOWN));// ????????????= ???????????????*????????????*????????????*????????????????????????
//        }
//        catch (Exception e)
//        {
//            log.info("okex???????????????????????????????????????{}", e.getLocalizedMessage());
//            e.printStackTrace();
//            return null;
//        }
//        return model;
//    }
//
//    @Override
//    protected Ticker getPremiumTicker()
//    {
//        Ticker ticker = new Ticker();
//        try
//        {
//            // ????????????
//            QuoteDepth longQuoteDepth = QuantConst.okex_depth_map.get(config.getLongSymbol());
//            if (null == longQuoteDepth || System.currentTimeMillis() - longQuoteDepth.getUpdateTime() > QuantConst.quoteTimeout)
//            {
//                log.info("okex{}??????????????????{}???!", config.getLongSymbol(), QuantConst.cacheLoseCount.incrementAndGet());
//                com.biex.quant.sdk.okex.bean.futures.result.Ticker future = marketAPIService.getInstrumentTicker(config.getLongSymbol());
//                ticker.setLongBid(new BigDecimal(future.getBest_bid()));
//                ticker.setLongAsk(new BigDecimal(future.getBest_ask()));
//            }
//            else
//            {
//                ticker.setLongAsk(longQuoteDepth.getAsk());
//                ticker.setLongBid(longQuoteDepth.getBid());
//            }
//            // ????????????
//            QuoteDepth shortQuoteDepth = QuantConst.okex_depth_map.get(config.getShortSymbol());
//            if (null == shortQuoteDepth || System.currentTimeMillis() - shortQuoteDepth.getUpdateTime() > QuantConst.quoteTimeout)
//            {
//                log.info("okex{}??????????????????{}???!", config.getShortSymbol(), QuantConst.cacheLoseCount.incrementAndGet());
//                com.biex.quant.sdk.okex.bean.futures.result.Ticker future = marketAPIService.getInstrumentTicker(config.getShortSymbol());
//                ticker.setShortBid(new BigDecimal(future.getBest_bid()));
//                ticker.setShortAsk(new BigDecimal(future.getBest_ask()));
//            }
//            else
//            {
//                ticker.setShortAsk(shortQuoteDepth.getAsk());
//                ticker.setShortBid(shortQuoteDepth.getBid());
//            }
//            ticker.setOpenRatio(ticker.getShortBid().subtract(ticker.getLongAsk()).divide(ticker.getLongAsk(), 6, BigDecimal.ROUND_DOWN));
//            ticker.setCloseRatio(ticker.getShortAsk().subtract(ticker.getLongBid()).divide(ticker.getLongBid(), 6, BigDecimal.ROUND_UP));
//        }
//        catch (Exception e)
//        {
//            log.info("okex?????????????????????????????????{}", e.getLocalizedMessage());
//            return null;
//        }
//        return ticker;
//    }
//
//    @Override
//    protected void cancelFutureOrder(String symbol, Long orderId)
//    {
//        try
//        {
//            tradeAPIService.cancelOrderByOrderId(symbol, String.valueOf(orderId));
//        }
//        catch (Exception e)
//        {
//            log.info("okex??????????????????ID{},???????????????{}", orderId, e.getLocalizedMessage());
//        }
//    }
//
//    @Override
//    protected QuoteDepth getFutureDepth(String symbol)
//    {
//        QuoteDepth quoteDepth = null;
//        try
//        {
//            quoteDepth = QuantConst.okex_depth_map.get(symbol);
//            if (null == quoteDepth || System.currentTimeMillis() - quoteDepth.getUpdateTime() > QuantConst.quoteTimeout)
//            {
//                log.info("okex{}??????????????????{}???!", config.getLongSymbol(), QuantConst.cacheLoseCount.incrementAndGet());
//                com.biex.quant.sdk.okex.bean.futures.result.Ticker future = marketAPIService.getInstrumentTicker(config.getLongSymbol());
//                quoteDepth = new QuoteDepth();
//                quoteDepth.setBid(new BigDecimal(future.getBest_bid()));
//                quoteDepth.setAsk(new BigDecimal(future.getBest_ask()));
//            }
//        }
//        catch (Exception e)
//        {
//            log.info("okex??????depth?????????????????????{}", e.getLocalizedMessage());
//            return null;
//        }
//        return quoteDepth;
//    }
//
//    @Override
//    protected ContractOrder getFutureOrder(String symbol, long id)
//    {
//        ContractOrder contractOrder = new ContractOrder();
//        try
//        {
//            JSONObject json = tradeAPIService.getOrderByOrderId(symbol, String.valueOf(id));
//            contractOrder.setFee(json.getBigDecimal("fee"));
//            contractOrder.setId(json.getLong("order_id"));
//            contractOrder.setDealCont(json.getBigDecimal("filled_qty"));
//            contractOrder.setEntrustCont(json.getBigDecimal("size"));
//            contractOrder.setEntrustPrice(json.getBigDecimal("price"));
//            contractOrder.setAvg_price(json.getBigDecimal("price_avg"));
//            String time = StringUtils.replace(json.getString("timestamp"), "T", " ");
//            time = StringUtils.replace(time, "Z", " ");
//            contractOrder.setEntrustTime(Timestamp.valueOf(time).getTime() + 3600000l * 8);
//            Integer state = json.getInteger("state");
//            if (state.equals(-1) || state.equals(2))
//            {
//                contractOrder.setStatus(contractOrder.getDealCont().compareTo(BigDecimal.ZERO) == 1 ? 2 : 1);
//            }
//            else
//            {
//                contractOrder.setStatus(contractOrder.getDealCont().compareTo(BigDecimal.ZERO) == 1 ? 4 : 3);
//            }
//        }
//        catch (Exception e)
//        {
//            log.info("okex?????????????????????????????????{}", e.getLocalizedMessage());
//            e.printStackTrace();
//            return null;
//        }
//        return contractOrder;
//    }
//
//    @Override
//    protected long createFutureOrder(String symbol, BigDecimal price, BigDecimal cont, int orderType)
//    {
//        try
//        {
//            com.biex.quant.sdk.okex.bean.futures.param.Order order = new com.biex.quant.sdk.okex.bean.futures.param.Order();
//            order.setInstrument_id(symbol);
//            order.setClient_oid(OrderIdUtils.generator());
//            order.setType(String.valueOf(orderType));
//            order.setSize(cont.toString());
//            order.setPrice(price.toString());
//            // int order_type = 0;
//            // if (orderType.equals(1))
//            // {
//            // order_type = 2;
//            // }
//            // else if (orderType.equals(2))
//            // {
//            // order_type = 1;
//            // }
//            // order.setOrder_type(order_type);
//            OrderResult result = tradeAPIService.order(order);
//            if (StringUtils.isEmpty(result.getError_messsage()))
//            {
//                return Long.parseLong(result.getOrder_id());
//            }
//            else
//            {
//                log.info("okex???????????????????????????{}", result.getError_messsage());
//            }
//        }
//        catch (Exception e)
//        {
//            log.info("okex???????????????????????????{}", e.getLocalizedMessage());
//        }
//        return 0;
//    }
//}
