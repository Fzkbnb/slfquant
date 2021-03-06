package com.slf.quant.strategy.hedge.client.usdt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.slf.quant.facade.consts.KeyConst;
import com.slf.quant.facade.entity.strategy.QuantHedgeConfig;
import com.slf.quant.facade.model.ContractOrder;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.model.StrategyStatusModel;
import com.slf.quant.facade.model.hedge.*;
import com.slf.quant.facade.service.strategy.QuantGridConfigService;
import com.slf.quant.facade.service.strategy.QuantHedgeConfigService;
import com.slf.quant.facade.service.strategy.QuantStrategyProfitService;
import com.slf.quant.facade.utils.SpringContext;
import com.slf.quant.strategy.consts.TradeConst;
import org.apache.commons.lang3.ObjectUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class AbstractHedgeUsdtClient
{
    protected QuantHedgeConfigService    quantHedgeConfigService    = SpringContext.getBean("quantHedgeConfigServiceImpl");
    
    protected QuantStrategyProfitService quantStrategyProfitService = SpringContext.getBean("quantStrategyProfitServiceImpl");
    
    protected QuantHedgeConfig           config;
    
    private String                       apiKey;
    
    private String                       secretKey;
    
    private String                       passPhrase;
    
    private HedgeModel                   hedgeModel;
    
    private Ticker                       ticker;
    
    private Account                      account;
    
    private Position                     longPosition;
    
    private Position                     shortPosition;
    
    private boolean                      stopFlag                   = false;
    
    private boolean                      isOpenHedging              = false;
    
    private boolean                      isCloseHedging             = false;
    
    private int                          runStatus;
    
    protected BigDecimal                 contractPerValue;                                                                    // ????????????
    
    protected int                        contract_price_precision;
    
    protected BigDecimal                 contract_tick_size;                                                                  // ????????????????????????????????????0.01???
    
    protected ScheduledExecutorService   monitorEs;
    
    public AbstractHedgeUsdtClient(QuantHedgeConfig config, String apiKey, String secretKey, String passPhrase)
    {
        this.config = config;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.passPhrase = passPhrase;
        monitorEs = new ScheduledThreadPoolExecutor(1);
    }
    
    public void start()
    {
        monitorEs.scheduleWithFixedDelay(() -> monitorRisk(), 0, 15, TimeUnit.SECONDS);
        new Thread(() -> {
            long sleepTime = 0;
            while (!stopFlag)
            {
                try
                {
                    if (runStatus == 1)
                    {
                        // ??????????????????
                        sleepTime = 0;
                        if (hedgeModel.getSellOrderId() == 0)
                        {
                            // ??????????????????????????????
                            BigDecimal price = getOpenPrice();
                            BigDecimal cont = ObjectUtils.min(config.getEntrustCont(), account.getAvailableCont().divide(BigDecimal.valueOf(2), 0, BigDecimal.ROUND_DOWN));
                            if (cont.compareTo(BigDecimal.valueOf(3)) < 1)
                            {
                                runStatus = 0;
                                sleepTime = 3000;
                                continue;
                            }
                            log.info("?????????????????????{}|{}[{},{}]", price, cont, ticker.getShortBid(), ticker.getShortAsk());
                            long sellOrderId = createFutureOrder(config.getShortSymbol(), price, cont, 2, getOneMakerType());
                            hedgeModel.setSellOrderId(sellOrderId);
                            continue;
                        }
                        if (hedgeModel.getBuyOrderId() == 0)
                        {
                            ContractOrder sellContractOrder = getFutureOrder(config.getShortSymbol(), hedgeModel.getSellOrderId());
                            if (null == sellContractOrder)
                            {
                                continue;
                            }
                            switch (sellContractOrder.getStatus())
                            {
                                case 1:// ??????????????????
                                    cleanOpenHedgeInfo();
                                    break;
                                case 2:// ??????????????????
                                    hedgeModel.setShortAvgPrice(sellContractOrder.getAvg_price());
                                    hedgeModel.setHedgeCont(sellContractOrder.getDealCont());
                                    QuoteDepth quoteDepth = getFutureDepth(config.getLongSymbol());
                                    if (null != quoteDepth)
                                    {
                                        BigDecimal price = quoteDepth.getAsk().multiply(hedgeModel.getSlipRatio()).setScale(contract_price_precision, BigDecimal.ROUND_UP);
                                        log.info("?????????????????????{}|{}[{},{}]", price, hedgeModel.getHedgeCont(), quoteDepth.getBid(), quoteDepth.getAsk());
                                        Long buyOrderId = createFutureOrder(config.getLongSymbol(), price, hedgeModel.getHedgeCont(), 1, geTwoMakerType());
                                        hedgeModel.setBuyOrderId(buyOrderId);
                                    }
                                    break;
                                case 3:// ??????????????????
                                    if (System.currentTimeMillis() - sellContractOrder.getEntrustTime() > 3000)
                                    {
                                        cancelFutureOrder(config.getShortSymbol(), hedgeModel.getSellOrderId());
                                    }
                                    break;
                                case 4:// ??????????????????
                                    cancelFutureOrder(config.getShortSymbol(), hedgeModel.getSellOrderId());
                                    break;
                            }
                        }
                        else
                        {
                            ContractOrder buyContractOrder = getFutureOrder(config.getLongSymbol(), hedgeModel.getBuyOrderId());
                            if (null == buyContractOrder)
                            {
                                continue;
                            }
                            switch (buyContractOrder.getStatus())
                            {
                                case 1:// ??????????????????
                                       // todo ???????????????????????????
                                    log.info("?????????????????????????????????????????????????????????????????????");
                                    stopFlag = true;
                                    break;
                                case 2:// ??????????????????
                                    if (buyContractOrder.getDealCont().compareTo(hedgeModel.getHedgeCont()) == 0)
                                    {
                                        hedgeModel.setLongAvgPrice(buyContractOrder.getAvg_price());
                                        hedgeModel.setSuccess(true);
                                        cleanOpenHedgeInfo();
                                    }
                                    else
                                    {
                                        // todo ???????????????????????????
                                        log.info("???????????????????????????????????????????????????????????????????????????");
                                        stopFlag = true;
                                    }
                                    break;
                                case 3:// ??????????????????
                                    if (System.currentTimeMillis() - buyContractOrder.getEntrustTime() > 3000)
                                    {
                                        cancelFutureOrder(config.getLongSymbol(), hedgeModel.getBuyOrderId());
                                    }
                                    break;
                                case 4:// ??????????????????
                                    if (System.currentTimeMillis() - buyContractOrder.getEntrustTime() > 3000)
                                    {
                                        cancelFutureOrder(config.getLongSymbol(), hedgeModel.getBuyOrderId());
                                    }
                                    break;
                            }
                        }
                    }
                    else if (runStatus == 2)
                    {
                        // ????????????
                        sleepTime = 0;
                        if (hedgeModel.getBuyOrderId() == 0)
                        {
                            // ?????????????????????????????????
                            BigDecimal price = ticker.getShortAsk();
                            BigDecimal cont = ObjectUtils.min(config.getEntrustCont(), shortPosition.getShortCont(), longPosition.getLongCont());
                            if (cont.compareTo(BigDecimal.ZERO) < 1)
                            {
                                sleepTime = 3000;
                                runStatus = 0;
                                continue;
                            }
                            log.info("?????????????????????{}|{}[{},{}]", price, cont, ticker.getShortBid(), ticker.getShortAsk());
                            Long buyOrderId = createFutureOrder(config.getShortSymbol(), price, cont, 4, getOneMakerType());
                            hedgeModel.setBuyOrderId(buyOrderId);
                            continue;
                        }
                        if (hedgeModel.getSellOrderId() == 0)
                        {
                            ContractOrder buyContractOrder = getFutureOrder(config.getShortSymbol(), hedgeModel.getBuyOrderId());
                            switch (buyContractOrder.getStatus())
                            {
                                case 1:// ??????????????????
                                    cleanCloseHedgeInfo();
                                    break;
                                case 2:// ??????????????????
                                    hedgeModel.setHedgeCont(buyContractOrder.getDealCont());
                                    hedgeModel.setShortAvgPrice(buyContractOrder.getAvg_price());
                                    QuoteDepth quoteDepth = getFutureDepth(config.getLongSymbol());
                                    if (null != quoteDepth)
                                    {
                                        BigDecimal price = quoteDepth.getBid().multiply(hedgeModel.getSlipRatio()).setScale(contract_price_precision,
                                                BigDecimal.ROUND_DOWN);
                                        log.info("?????????????????????{}|{}[{},{}]", price, hedgeModel.getHedgeCont(), quoteDepth.getBid(), quoteDepth.getAsk());
                                        Long sellOrderId = createFutureOrder(config.getLongSymbol(), price, hedgeModel.getHedgeCont(), 3, geTwoMakerType());
                                        hedgeModel.setSellOrderId(sellOrderId);
                                    }
                                    break;
                                case 3:// ??????????????????
                                    if (System.currentTimeMillis() - buyContractOrder.getEntrustTime() > 3000)
                                    {
                                        cancelFutureOrder(config.getShortSymbol(), hedgeModel.getBuyOrderId());
                                    }
                                    break;
                                case 4:// ??????????????????
                                    cancelFutureOrder(config.getShortSymbol(), hedgeModel.getBuyOrderId());
                                    break;
                            }
                        }
                        else
                        {
                            ContractOrder sellContractOrder = getFutureOrder(config.getLongSymbol(), hedgeModel.getSellOrderId());
                            switch (sellContractOrder.getStatus())
                            {
                                case 1:// ??????????????????,????????????
                                    log.info("???????????????????????????????????????????????????????????????????????????");
                                    hedgeModel.setSellOrderId(0);
                                    break;
                                case 2:// ??????????????????
                                    if (sellContractOrder.getDealCont().compareTo(hedgeModel.getHedgeCont()) == 0)
                                    {
                                        hedgeModel.setLongAvgPrice(sellContractOrder.getAvg_price());
                                        hedgeModel.setSuccess(true);
                                        cleanCloseHedgeInfo();
                                    }
                                    else
                                    {
                                        // todo ???????????????????????????
                                        log.info("?????????????????????????????????????????????????????????????????????????????????");
                                        stopFlag = true;
                                    }
                                    break;
                                case 3:// ??????????????????
                                    if (System.currentTimeMillis() - sellContractOrder.getEntrustTime() > 3000)
                                    {
                                        cancelFutureOrder(config.getLongSymbol(), hedgeModel.getSellOrderId());
                                    }
                                    break;
                                case 4:// ??????????????????
                                    if (System.currentTimeMillis() - sellContractOrder.getEntrustTime() > 3000)
                                    {
                                        cancelFutureOrder(config.getLongSymbol(), hedgeModel.getSellOrderId());
                                    }
                                    break;
                            }
                        }
                    }
                    else
                    {
                        // ??????????????????????????????????????????????????????????????????3???
                        sleepTime = 3000;
                        account = getFutureAccount(config.getCurrency());
                        if (null == account)
                        {
                            continue;
                        }
                        shortPosition = getfuturePosition(config.getCurrency(), config.getShortSymbol());
                        if (null == shortPosition)
                        {
                            continue;
                        }
                        longPosition = getfuturePosition(config.getCurrency(), config.getLongSymbol());
                        if (null == longPosition)
                        {
                            continue;
                        }
                        ticker = getPremiumTicker();
                        if (null == ticker)
                        {
                            continue;
                        }
                        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        BigDecimal openSlip = ticker.getOpenRatio().subtract(config.getOpenRatio());
                        if (openSlip.compareTo(BigDecimal.ZERO) == 1)
                        {
                            // ??????????????????????????????????????????????????????????????????????????????
                            if (shortPosition.getShortCont().compareTo(config.getMaxHoldCont()) > -1 || longPosition.getLongCont().compareTo(config.getMaxHoldCont()) > -1)
                            {
                                continue;
                            }
                            hedgeModel = new HedgeModel();
                            hedgeModel.setSlipRatio(BigDecimal.ONE.add(openSlip.multiply(TradeConst.slipRatio)));
                            ticker.setSlipRatio(openSlip.multiply(TradeConst.slipRatio));
                            hedgeModel.setPriceMode(getPriceMode());
                            sleepTime = 0;
                            runStatus = 1;
                        }
                        else if (ticker.getCloseRatio().compareTo(config.getCloseRatio()) == -1)
                        {
                            hedgeModel = new HedgeModel();
                            hedgeModel.setSlipRatio(BigDecimal.ONE.subtract(config.getCloseRatio().subtract(ticker.getCloseRatio()).multiply(TradeConst.slipRatio)));
                            ticker.setSlipRatio(config.getCloseRatio().subtract(ticker.getCloseRatio()).multiply(TradeConst.slipRatio));
                            hedgeModel.setPriceMode(getPriceMode());
                            sleepTime = 0;
                            runStatus = 2;
                        }
                    }
                }
                catch (Exception e)
                {
                    log.info("?????????????????????{}", e.getLocalizedMessage());
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        Thread.sleep(sleepTime);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    
    private void monitorRisk()
    {
        try
        {
            if (null == account || null == ticker || null == longPosition || null == shortPosition)
            { return; }
            // todo
            List<StrategyStatusModel> models = new ArrayList<>();
            StrategyStatusModel m1 = new StrategyStatusModel();
            m1.setKey("???/?????????(???)");
            m1.setValue(longPosition.getLongCont() + "/" + shortPosition.getShortCont());
            StrategyStatusModel m2 = new StrategyStatusModel();
            m2.setKey("???/?????????/???????????????");
            String openRateStr = "-";
            if (longPosition.getLong_avg_price().compareTo(BigDecimal.ZERO) == 1)
            {
                BigDecimal currentPremiumRate = shortPosition.getShort_avg_price().subtract(longPosition.getLong_avg_price()).divide(longPosition.getLong_avg_price(), 4,
                        BigDecimal.ROUND_DOWN);
                openRateStr = currentPremiumRate.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_DOWN) + "%";
            }
            m2.setValue(longPosition.getLong_avg_price() + "/" + shortPosition.getShort_avg_price() + "/" + openRateStr);
            StrategyStatusModel m3 = new StrategyStatusModel();
            m3.setKey("??????/?????????????????????");
            m3.setValue(config.getCloseRatio().multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_DOWN) + "%/"
                    + ticker.getCloseRatio().multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_DOWN) + "%");
            StrategyStatusModel m4 = new StrategyStatusModel();
            m4.setKey("????????????/???????????????(USDT)");
            m4.setValue(account.getEquity() + "/" + account.getAvailableMargin());
            models.add(m1);
            models.add(m2);
            models.add(m3);
            models.add(m4);
            TradeConst.strategy_stats_map.put(KeyConst.REDISKEY_STRATEGY_STATS + config.getId(), models);
        }
        catch (Exception e)
        {
            log.info("????????????{}????????????????????????:{}", config.getId(), e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    
    public void stop()
    {
        log.info(">>>????????????{},????????????<<<", config.getId());
        quantHedgeConfigService.changeStatus(config.getId(), 0);
        stopFlag = true;
    }
    
    private boolean getOneMakerType()
    {
        if ("????????????".equalsIgnoreCase(hedgeModel.getPriceMode()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    private boolean geTwoMakerType()
    {
        if ("????????????".equalsIgnoreCase(hedgeModel.getPriceMode()) || "????????????".equalsIgnoreCase(hedgeModel.getPriceMode()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    private BigDecimal getOpenPrice()
    {
        if ("????????????".equalsIgnoreCase(hedgeModel.getPriceMode()))
        {
            return ticker.getShortBid();
        }
        else
        {
            return ticker.getShortBid().add(contract_tick_size);
        }
    }
    
    /**
     * ????????????????????????
     */
    private String getPriceMode()
    {
        if (ticker.getSlipRatio().compareTo(new BigDecimal("0.0012")) == 1)
        {
            return "????????????";// ?????????????????????
        }
        else if (ticker.getSlipRatio().compareTo(new BigDecimal("0.0006")) == 1)
        {
            return "????????????";// ?????????maker,?????????taker
        }
        else
        {
            return "????????????";// ?????????maker
        }
    }
    
    protected abstract Position getfuturePosition(String currency, String code);
    
    protected abstract Account getFutureAccount(String currency);
    
    protected abstract Ticker getPremiumTicker();
    
    protected abstract void cancelFutureOrder(String shortSymbol, Long sellOrderId);
    
    protected abstract QuoteDepth getFutureDepth(String symbol);
    
    protected abstract ContractOrder getFutureOrder(String symbol, long id);
    
    private void cleanOpenHedgeInfo()
    {
        if (hedgeModel.isSuccess())
        {
            log.info("????????????????????????????????????????????????/????????????????????????{}/{}??????",
                    hedgeModel.getShortAvgPrice().subtract(hedgeModel.getLongAvgPrice()).divide(hedgeModel.getLongAvgPrice(), 4, BigDecimal.ROUND_DOWN),
                    config.getOpenRatio());
        }
        hedgeModel = null;
        runStatus = 0;
    }
    
    private void cleanCloseHedgeInfo()
    {
        if (hedgeModel.isSuccess())
        {
            log.info("????????????????????????????????????????????????/????????????????????????{}/{}??????",
                    hedgeModel.getShortAvgPrice().subtract(hedgeModel.getLongAvgPrice()).divide(hedgeModel.getLongAvgPrice(), 4, BigDecimal.ROUND_DOWN),
                    config.getCloseRatio());
        }
        hedgeModel = null;
        runStatus = 0;
    }
    
    /**
     * ????????????
     *
     * @param price     ????????????
     * @param cont      ????????????
     * @param orderType ???????????????1?????????2?????????3?????????4??????
     * @return
     */
    protected abstract long createFutureOrder(String symbol, BigDecimal price, BigDecimal cont, Integer orderType, boolean enableMaker);
}
