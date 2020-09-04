package com.slf.quant.strategy.grid.client;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.slf.quant.facade.consts.UrlConst;
import com.slf.quant.facade.entity.strategy.QuantGridConfig;
import com.slf.quant.facade.model.AccountModel;
import com.slf.quant.facade.model.ContractOrder;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.sdk.okex.bean.futures.result.Instruments;
import com.slf.quant.facade.sdk.okex.bean.swap.param.PpOrder;
import com.slf.quant.facade.sdk.okex.config.APIConfiguration;
import com.slf.quant.facade.sdk.okex.enums.I18nEnum;
import com.slf.quant.facade.sdk.okex.service.account.AccountAPIService;
import com.slf.quant.facade.sdk.okex.service.account.impl.AccountAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.service.spot.MarginOrderAPIService;
import com.slf.quant.facade.sdk.okex.service.spot.impl.MarginOrderAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.service.swap.SwapMarketAPIService;
import com.slf.quant.facade.sdk.okex.service.swap.SwapTradeAPIService;
import com.slf.quant.facade.sdk.okex.service.swap.SwapUserAPIServive;
import com.slf.quant.facade.sdk.okex.service.swap.impl.SwapMarketAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.service.swap.impl.SwapTradeAPIServiceImpl;
import com.slf.quant.facade.sdk.okex.service.swap.impl.SwapUserAPIServiceImpl;
import com.slf.quant.facade.utils.QuantUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.slf.quant.strategy.consts.TradeConst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OKexSwapGridUsdtClient extends AbstractGridUsdtClient
{
    private SwapMarketAPIService marketAPIService;
    
    private AccountAPIService accountAPIService;
    
    private SwapTradeAPIService tradeAPIService;
    
    private SwapUserAPIServive swapUserAPIServive;
    
    private MarginOrderAPIService marginOrderAPIService;
    
    public OKexSwapGridUsdtClient(QuantGridConfig config, String apiKey, String secretKey, String passPhrase)
    {
        super(config, apiKey, secretKey, passPhrase);
    }
    
    @Override
    protected AccountModel getContractAccount(String currency)
    {
        AccountModel model = new AccountModel();
        try
        {
            // 合约
            String res1 = swapUserAPIServive.selectAccount(currency.toUpperCase() + "-USDT-SWAP");
            JSONObject json = JSON.parseObject(res1).getJSONObject("info");
            model.setContractBalance(json.getBigDecimal("equity"));
            model.setCan_withdraw(json.getBigDecimal("max_withdraw"));
            model.setUsedMargin(json.getBigDecimal("margin"));
            model.setFrozenMargin(BigDecimal.ZERO);
            if (null == model.getUsedMargin())
            {
                model.setUsedMargin(BigDecimal.ZERO);
            }
            model.setAvailableMargin(model.getContractBalance().subtract(model.getFrozenMargin()).subtract(model.getUsedMargin()));
        }
        catch (Exception e)
        {
            log.info("okex永续合约账户资产获取异常：{}", e.getLocalizedMessage());
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
            log.info("okex永续合约订单ID{},撤单异常：{}", orderId, e.getLocalizedMessage());
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
                String future = marketAPIService.getTickerApi(symbol);
                JSONObject json = JSON.parseObject(future);
                depth = new QuoteDepth();
                depth.setBid(json.getBigDecimal("best_bid"));
                depth.setAsk(json.getBigDecimal("best_ask"));
            }
        }
        catch (Exception e)
        {
            log.info("okex永续次depth行情获取异常：{}", e.getLocalizedMessage());
            return null;
        }
        return depth;
    }
    
    @Override
    protected boolean cancelAllOrder()
    {
        return true;
    }
    
    @Override
    protected ContractOrder getContractOrder(String symbol, long id)
    {
        ContractOrder order = new ContractOrder();
        try
        {
            String json_str = swapUserAPIServive.selectOrderByOrderId(symbol, String.valueOf(id));
            JSONObject json = JSON.parseObject(json_str);
            order.setId(json.getLong("order_id"));
            order.setFee(json.getBigDecimal("fee"));
            order.setDealCont(json.getBigDecimal("filled_qty"));
            order.setEntrustCont(json.getBigDecimal("size"));
            order.setEntrustPrice(json.getBigDecimal("price"));
            order.setDirect("1".equalsIgnoreCase(json.getString("type")) || "4".equalsIgnoreCase(json.getString("type")) ? TradeConst.KEY_BUY : TradeConst.KEY_SELL);
            order.setAvg_price(json.getBigDecimal("price_avg"));
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
            log.info("okex永续合约订单获取异常：{}", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
        return order;
    }
    
    @Override
    protected void initApiData()
    {
        APIConfiguration config1 = new APIConfiguration();
        config1.setEndpoint(UrlConst.okex_endpoint);
        config1.setApiKey(apiKey);
        config1.setSecretKey(secretKey);
        config1.setPassphrase(passPhrase);
        config1.setI18n(I18nEnum.ENGLISH);
        config1.setPrint(false);
        marketAPIService = new SwapMarketAPIServiceImpl(config1);
        tradeAPIService = new SwapTradeAPIServiceImpl(config1);
        accountAPIService = new AccountAPIServiceImpl(config1);
        swapUserAPIServive = new SwapUserAPIServiceImpl(config1);
        marginOrderAPIService = new MarginOrderAPIServiceImpl(config1);
        String instruments_res = marketAPIService.getContractsApi();
        List<Instruments> instruments = Arrays.asList(JSON.toJavaObject(JSON.parseArray(instruments_res), Instruments[].class));
        instruments = instruments.stream().filter(ins -> ins.getInstrument_id().equalsIgnoreCase(config.getContractCode())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(instruments))
        {
            tickSize = new BigDecimal(instruments.get(0).getTick_size());
            pricePrecision = QuantUtil.getPrecision(tickSize);
            contractPerValue = new BigDecimal(instruments.get(0).getContract_val());
        }
        else
        {
            log.info("okex永续合约币对不存在：{}", config.getContractCode());
            hasError = true;
        }
    }
    
    @Override
    protected long contractEntrust(String symbol, BigDecimal entrustCont, BigDecimal entrustPrice, String direct, Integer orderType)
    {
        try
        {
            PpOrder ppOrder = new PpOrder(null, entrustCont.toString(), getOrderType(direct), "0", entrustPrice.toString(), symbol, String.valueOf(orderType));
            // 0表示不使用对手价 ，1表示使用对手价
            Object o = tradeAPIService.order(ppOrder);
            Map<String, String> result = (Map<String, String>) o;
            String orderId = result.get("order_id");
            if (!StringUtils.isEmpty(orderId))
            {
                return Long.parseLong(orderId);
            }
            else
            {
                log.info("okex永续合约下单失败，失败信息：{}", result);
            }
        }
        catch (Exception e)
        {
            log.info("okex永续合约下单异常：{}", e.getLocalizedMessage());
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
