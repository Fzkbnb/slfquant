package com.slf.quant.strategy.grid.client;

import java.math.BigDecimal;

import java.util.List;

import com.slf.quant.facade.model.*;
import com.slf.quant.facade.sdk.okex.v5.OkexV5CommonApiClient;
import org.apache.commons.collections.CollectionUtils;

import com.slf.quant.facade.entity.strategy.QuantGridConfig;

import com.slf.quant.strategy.consts.TradeConst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OKexV5SwapGridUsdtClient extends AbstractGridUsdtClient
{
    private OkexV5CommonApiClient commonApiClient;
    
    public OKexV5SwapGridUsdtClient(QuantGridConfig config, String apiKey, String secretKey, String passPhrase)
    {
        super(config, apiKey, secretKey, passPhrase);
        commonApiClient = new OkexV5CommonApiClient(apiKey, secretKey, passPhrase);
    }
    
    @Override
    protected AccountModel getContractAccount(String currency)
    {
        AccountModel model = new AccountModel();
        try
        {
            // 合约
            QuantCommonAccountModel accountModel = commonApiClient.getFutureAccount(currency);
            model.setContractBalance(accountModel.getContractBalance());
            model.setCan_withdraw(accountModel.getCanWithdraw());
            model.setAvailableMargin(accountModel.getAvailableMargin());
        }
        catch (Exception e)
        {
            log.info("okex v5永续合约账户资产获取异常：{}", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
        return model;
    }
    
    @Override
    protected void cancelContractOrder(String symbol, long orderId)
    {
        commonApiClient.cancelFutureOrder(symbol, orderId);
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
                QuantCommonDepthModel commonDepthModel = commonApiClient.getFutureDepth(symbol);
                depth = new QuoteDepth();
                depth.setBid(commonDepthModel.getBid());
                depth.setAsk(commonDepthModel.getAsk());
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
    protected long closePositionByMarket(String currency, String firstDirect)
    {
        return commonApiClient.closeAllPositionByMaket(config.getContractCode(), firstDirect, TradeConst.OKEXV5_MARGINMODE_CROSS, currency);
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
            QuantCommonOrderModel contractOrder = commonApiClient.getFutureOrder(symbol, id);
            order.setId(id);
            order.setFee(contractOrder.getFee());
            order.setDealCont(contractOrder.getDealAmt());
            order.setEntrustCont(contractOrder.getEntrustAmt());
            order.setEntrustPrice(contractOrder.getEntrustPrice());
            order.setDirect(contractOrder.getEntrustSide());
            order.setAvg_price(contractOrder.getAvgPrice());
            order.setEntrustTime(contractOrder.getCreateTime());
            order.setStatus(contractOrder.getStatus());
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
        List<QuantCommonSymbolInfo> futureSymbolInfos = commonApiClient.getFutureSymbolInfo(TradeConst.OKEXV5_INSTTYPE_SWAP, config.getContractCode());
        if (CollectionUtils.isNotEmpty(futureSymbolInfos))
        {
            QuantCommonSymbolInfo symbolInfo = futureSymbolInfos.get(0);
            tickSize = symbolInfo.getContractPriceTick();
            pricePrecision = symbolInfo.getContractPricePrecision();
            contractPerValue = symbolInfo.getContractVal();
        }
        else
        {
            throw new RuntimeException("okex U合约币对不存在：" + config.getContractCode());
        }
    }
    
    @Override
    protected long contractEntrust(String symbol, BigDecimal entrustCont, BigDecimal entrustPrice, String direct, Integer orderType)
    {
        try
        {
            return commonApiClient.futureEntrust(symbol, entrustPrice, entrustCont, direct, isFirstBuy() ? TradeConst.KEY_LONG : TradeConst.KEY_SHORT, direct, null);
        }
        catch (Exception e)
        {
            log.info("okex v5永续合约下单异常：{}", e.getLocalizedMessage());
        }
        return 0;
    }
}
