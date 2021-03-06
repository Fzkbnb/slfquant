package com.slf.quant.strategy.avg.client;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.slf.quant.facade.consts.UrlConst;
import com.slf.quant.facade.entity.strategy.QuantAvgConfig;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.model.SpotOrder;
import com.slf.quant.facade.sdk.okex.bean.spot.param.PlaceOrderParam;
import com.slf.quant.facade.sdk.okex.bean.spot.result.OrderInfo;
import com.slf.quant.facade.sdk.okex.bean.spot.result.OrderResult;
import com.slf.quant.facade.sdk.okex.bean.spot.result.Product;
import com.slf.quant.facade.sdk.okex.bean.spot.result.Ticker;
import com.slf.quant.facade.sdk.okex.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.enums.I18nEnum;
import com.slf.quant.facade.sdk.okex.service.spot.SpotAccountAPIService;
import com.slf.quant.facade.sdk.okex.service.spot.SpotOrderAPIServive;
import com.slf.quant.facade.sdk.okex.service.spot.SpotProductAPIService;
import com.slf.quant.facade.sdk.okex.service.spot.impl.SpotAccountAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.service.spot.impl.SpotOrderApiServiceImpl;
import com.slf.quant.facade.sdk.okex.service.spot.impl.SpotProductAPIServiceImpl;
import com.slf.quant.facade.utils.QuantUtil;
import com.slf.quant.strategy.consts.TradeConst;
import org.apache.commons.collections.CollectionUtils;



import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class OkexAvgClient extends AbstractAvgClient
{
    private SpotAccountAPIService spotService;
    
    private SpotOrderAPIServive spotOrderAPIServive;
    
    private SpotProductAPIService spotMarketService;
    
    private String                spotSymbol;
    
    public OkexAvgClient(QuantAvgConfig config, String apiKey, String secretKey, String passPhrase)
    {
        super(config, apiKey, secretKey, passPhrase);
    }
    
    @Override
    protected void cancelSpotOrder(String currency, long orderId)
    {
        try
        {
            PlaceOrderParam order = new PlaceOrderParam();
            order.setInstrument_id(currency + "-USDT");
            spotOrderAPIServive.cancleOrderByOrderId_post(order, String.valueOf(orderId));
        }
        catch (Exception e)
        {
            log.info("okex?????????????????????{}", e.getLocalizedMessage());
        }
    }
    
    @Override
    protected long spotEntrust(String currency, BigDecimal entrustPrice, BigDecimal entrustAmt, String direct)
    {
        try
        {
            // ??????????????????????????????????????????
            PlaceOrderParam order = new PlaceOrderParam();
            order.setInstrument_id(currency.toUpperCase() + "-USDT");
            order.setType("limit");
            order.setPrice(entrustPrice.toPlainString());
            order.setSize(entrustAmt.toString());
            order.setSide(direct);
            order.setOrder_type("1");
            log.info("okex???????????????{}", order.toString());
            OrderResult result = spotOrderAPIServive.addOrder(order);
            log.info("okex???????????????{}", result.toString());
            if (StringUtils.isEmpty(result.getError_message()))
            {
                return result.getOrder_id();
            }
            else
            {
                log.info("okex??????{}???????????????{}", result.getError_message());
            }
        }
        catch (Exception e)
        {
            log.info("??????{},okex?????????????????????{}", idStr, e.getLocalizedMessage());
        }
        return 0;
    }
    
    @Override
    protected SpotOrder getSpotOrder(String currency, long orderId)
    {
        SpotOrder order = new SpotOrder();
        String symbol = currency + "-USDT";
        try
        {
            OrderInfo orderInfo = spotOrderAPIServive.getOrderByOrderId(symbol, String.valueOf(orderId));
            order.setDealAmt(new BigDecimal(orderInfo.getFilled_size()));
            order.setAvg_price(new BigDecimal(orderInfo.getPrice_avg()));
            order.setEntrustAmt(new BigDecimal(orderInfo.getSize()));
            order.setId(orderId);
            order.setEntrustPrice(new BigDecimal(orderInfo.getPrice()));
            String time = StringUtils.replace(orderInfo.getTimestamp(), "T", " ");
            time = StringUtils.replace(time, "Z", " ");
            order.setEntrustTime(Timestamp.valueOf(time).getTime() + 3600000L * 8);
            if ("2".equals(orderInfo.getState()) || order.getDealAmt().compareTo(order.getEntrustAmt()) == 0)
            {
                // ?????????????????????????????????
                order.setStatus(1);
            }
            else
            {
                order.setStatus(0);
                if ("-1".equals(orderInfo.getState()))
                {
                    log.info("??????{} ???okex???????????????????????????????????????????????????????????????!", idStr);
                    order.setStatus(1);
                }
            }
        }
        catch (Exception e)
        {
            log.info("okex?????????????????????????????????{}", e.getLocalizedMessage());
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
            // ??????
            QuoteDepth cacheModel = TradeConst.okex_depth_map.get(spotSymbol);
            if (null == cacheModel || System.currentTimeMillis() - cacheModel.getUpdateTime() > TradeConst.quoteTimeout)
            {
                Ticker ticker = spotMarketService.getTickerByProductId(spotSymbol);
                depthModel.setAsk(new BigDecimal(ticker.getBest_ask()));
                depthModel.setBid(new BigDecimal(ticker.getBest_bid()));
            }
            else
            {
                depthModel.setAsk(cacheModel.getAsk());
                depthModel.setBid(cacheModel.getBid());
            }
        }
        catch (Exception e)
        {
            log.info("okex??????depth?????????????????????{}", e.getLocalizedMessage());
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
            APIConfiguration config1 = new APIConfiguration();
            config1.setEndpoint(UrlConst.okex_endpoint);
            config1.setApiKey(apiKey);
            config1.setSecretKey(secretKey);
            config1.setPassphrase(passPhrase);
            config1.setI18n(I18nEnum.ENGLISH);
            config1.setPrint(false);
            spotSymbol = config.getCurrency() + "-USDT";
            feeRate = new BigDecimal("0.001");
            spotService = new SpotAccountAPIServiceImpl(config1);
            spotMarketService = new SpotProductAPIServiceImpl(config1);
            spotOrderAPIServive = new SpotOrderApiServiceImpl(config1);
            List<Product> products = spotMarketService.getProducts();
            products = products.stream().filter(product -> product.getInstrument_id().equalsIgnoreCase(config.getCurrency() + "-USDT")).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(products))
            {
                minSpotAmt = new BigDecimal(products.get(0).getMin_size());
                tickSize = new BigDecimal(products.get(0).getTick_size());
                pricePrecision = QuantUtil.getPrecision(new BigDecimal(products.get(0).getTick_size()));
                amtPrecision = QuantUtil.getPrecision(new BigDecimal(products.get(0).getSize_increment()));// ??????????????????????????????????????????1????????????????????????????????????????????????
            }
            else
            {
                throw new RuntimeException("????????????????????????" + config.getCurrency());
            }
        }
        catch (Exception e)
        {
            log.info("??????{}???api??????okex?????????????????????????????????:{}", config.getId(), e.getLocalizedMessage());
            hasError = true;
            e.printStackTrace();
        }
    }
}
