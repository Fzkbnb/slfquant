package com.slf.quant.strategy.hedge.client.usdt;

import java.math.BigDecimal;

import com.slf.quant.facade.model.ContractOrder;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.model.hedge.*;
import com.slf.quant.strategy.consts.TradeConst;
import org.apache.commons.lang3.ObjectUtils;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class AbstractUsdtClient
{
    protected Config     config;
    
    private String       apiKey;
    
    private String       secretKey;
    
    private String       passPhrase;
    
    private HedgeModel hedgeModel;
    
    private Ticker ticker;
    
    private Account account;
    
    private Position longPosition;
    
    private Position     shortPosition;
    
    private boolean      stopFlag       = false;
    
    private boolean      isOpenHedging  = false;
    
    private boolean      isCloseHedging = false;
    
    private int          runStatus;
    
    protected BigDecimal contractPerValue;        // 合约面值
    
    protected int        contract_price_precision;
    
    protected BigDecimal contract_tick_size;      // 合约价格最小精度值（例如0.01）
    
    public AbstractUsdtClient(Config config, String apiKey, String secretKey, String passPhrase)
    {
        this.config = config;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.passPhrase = passPhrase;
    }
    
    public void start()
    {
        new Thread(() -> {
            long sleepTime = 0;
            while (!stopFlag)
            {
                try
                {
                    if (runStatus == 1)
                    {
                        // 开仓阶段逻辑
                        sleepTime = 0;
                        if (hedgeModel.getSellOrderId() == 0)
                        {
                            // 高卖单未下单，则下单
                            BigDecimal price = getOpenPrice();
                            BigDecimal cont = ObjectUtils.min(config.getEntrustCont(), account.getAvailableCont().divide(BigDecimal.valueOf(2), 0, BigDecimal.ROUND_DOWN));
                            if (cont.compareTo(BigDecimal.valueOf(3)) < 1)
                            {
                                runStatus = 0;
                                sleepTime = 3000;
                                continue;
                            }
                            log.info("开仓做空下单：{}|{}[{},{}]", price, cont, ticker.getShortBid(), ticker.getShortAsk());
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
                                case 1:// 已完成无成交
                                    cleanOpenHedgeInfo();
                                    break;
                                case 2:// 已完成有成交
                                    hedgeModel.setShortAvgPrice(sellContractOrder.getAvg_price());
                                    hedgeModel.setHedgeCont(sellContractOrder.getDealCont());
                                    QuoteDepth quoteDepth = getFutureDepth(config.getLongSymbol());
                                    if (null != quoteDepth)
                                    {
                                        BigDecimal price = quoteDepth.getAsk().multiply(hedgeModel.getSlipRatio()).setScale(contract_price_precision, BigDecimal.ROUND_UP);
                                        log.info("开仓做多下单：{}|{}[{},{}]", price, hedgeModel.getHedgeCont(), quoteDepth.getBid(), quoteDepth.getAsk());
                                        Long buyOrderId = createFutureOrder(config.getLongSymbol(), price, hedgeModel.getHedgeCont(), 1, geTwoMakerType());
                                        hedgeModel.setBuyOrderId(buyOrderId);
                                    }
                                    break;
                                case 3:// 未完成无成交
                                    if (System.currentTimeMillis() - sellContractOrder.getEntrustTime() > 3000)
                                    {
                                        cancelFutureOrder(config.getShortSymbol(), hedgeModel.getSellOrderId());
                                    }
                                    break;
                                case 4:// 未完成有成交
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
                                case 1:// 已完成无成交
                                       // todo 报警关停，人工干预
                                    log.info("对冲开仓买多订单已完成无成交，请及时人工处理！");
                                    stopFlag = true;
                                    break;
                                case 2:// 已完成有成交
                                    if (buyContractOrder.getDealCont().compareTo(hedgeModel.getHedgeCont()) == 0)
                                    {
                                        hedgeModel.setLongAvgPrice(buyContractOrder.getAvg_price());
                                        hedgeModel.setSuccess(true);
                                        cleanOpenHedgeInfo();
                                    }
                                    else
                                    {
                                        // todo 报警关停，人工干预
                                        log.info("对冲开仓买多订单已完成未完全成交，请及时人工处理！");
                                        stopFlag = true;
                                    }
                                    break;
                                case 3:// 未完成无成交
                                    if (System.currentTimeMillis() - buyContractOrder.getEntrustTime() > 3000)
                                    {
                                        cancelFutureOrder(config.getLongSymbol(), hedgeModel.getBuyOrderId());
                                    }
                                    break;
                                case 4:// 未完成有成交
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
                        // 平仓阶段
                        sleepTime = 0;
                        if (hedgeModel.getBuyOrderId() == 0)
                        {
                            // 买入平空未下单，则下单
                            BigDecimal price = ticker.getShortAsk();
                            BigDecimal cont = ObjectUtils.min(config.getEntrustCont(), shortPosition.getShortCont(), longPosition.getLongCont());
                            if (cont.compareTo(BigDecimal.ZERO) < 1)
                            {
                                sleepTime = 3000;
                                runStatus = 0;
                                continue;
                            }
                            log.info("平仓平空下单：{}|{}[{},{}]", price, cont, ticker.getShortBid(), ticker.getShortAsk());
                            Long buyOrderId = createFutureOrder(config.getShortSymbol(), price, cont, 4, getOneMakerType());
                            hedgeModel.setBuyOrderId(buyOrderId);
                            continue;
                        }
                        if (hedgeModel.getSellOrderId() == 0)
                        {
                            ContractOrder buyContractOrder = getFutureOrder(config.getShortSymbol(), hedgeModel.getBuyOrderId());
                            switch (buyContractOrder.getStatus())
                            {
                                case 1:// 已完成无成交
                                    cleanCloseHedgeInfo();
                                    break;
                                case 2:// 已完成有成交
                                    hedgeModel.setHedgeCont(buyContractOrder.getDealCont());
                                    hedgeModel.setShortAvgPrice(buyContractOrder.getAvg_price());
                                    QuoteDepth quoteDepth = getFutureDepth(config.getLongSymbol());
                                    if (null != quoteDepth)
                                    {
                                        BigDecimal price = quoteDepth.getBid().multiply(hedgeModel.getSlipRatio()).setScale(contract_price_precision,
                                                BigDecimal.ROUND_DOWN);
                                        log.info("平仓平多下单：{}|{}[{},{}]", price, hedgeModel.getHedgeCont(), quoteDepth.getBid(), quoteDepth.getAsk());
                                        Long sellOrderId = createFutureOrder(config.getLongSymbol(), price, hedgeModel.getHedgeCont(), 3, geTwoMakerType());
                                        hedgeModel.setSellOrderId(sellOrderId);
                                    }
                                    break;
                                case 3:// 未完成无成交
                                    if (System.currentTimeMillis() - buyContractOrder.getEntrustTime() > 3000)
                                    {
                                        cancelFutureOrder(config.getShortSymbol(), hedgeModel.getBuyOrderId());
                                    }
                                    break;
                                case 4:// 未完成有成交
                                    cancelFutureOrder(config.getShortSymbol(), hedgeModel.getBuyOrderId());
                                    break;
                            }
                        }
                        else
                        {
                            ContractOrder sellContractOrder = getFutureOrder(config.getLongSymbol(), hedgeModel.getSellOrderId());
                            switch (sellContractOrder.getStatus())
                            {
                                case 1:// 已完成无成交,重新尝试
                                    log.info("对冲平仓卖出平多订单已完成无成交，请及时人工处理！");
                                    hedgeModel.setSellOrderId(0);
                                    break;
                                case 2:// 已完成有成交
                                    if (sellContractOrder.getDealCont().compareTo(hedgeModel.getHedgeCont()) == 0)
                                    {
                                        hedgeModel.setLongAvgPrice(sellContractOrder.getAvg_price());
                                        hedgeModel.setSuccess(true);
                                        cleanCloseHedgeInfo();
                                    }
                                    else
                                    {
                                        // todo 报警关停，人工干预
                                        log.info("对冲平仓卖出平多订单已完成未完全成交，请及时人工处理！");
                                        stopFlag = true;
                                    }
                                    break;
                                case 3:// 未完成无成交
                                    if (System.currentTimeMillis() - sellContractOrder.getEntrustTime() > 3000)
                                    {
                                        cancelFutureOrder(config.getLongSymbol(), hedgeModel.getSellOrderId());
                                    }
                                    break;
                                case 4:// 未完成有成交
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
                        // 未处于对冲期间，即行情监控时间，轮询时间置为3秒
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
                        // 计算实际开仓溢价率与配置开仓溢价率的差值，根据差值程度来确定下单成交效率模式
                        BigDecimal openSlip = ticker.getOpenRatio().subtract(config.getOpenRatio());
                        if (openSlip.compareTo(BigDecimal.ZERO) == 1)
                        {
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
                    log.info("轮训逻辑出错：{}", e.getLocalizedMessage());
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
    
    private boolean getOneMakerType()
    {
        if ("高效模式".equalsIgnoreCase(hedgeModel.getPriceMode()))
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
        if ("高效模式".equalsIgnoreCase(hedgeModel.getPriceMode()) || "中效模式".equalsIgnoreCase(hedgeModel.getPriceMode()))
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
        if ("高效模式".equalsIgnoreCase(hedgeModel.getPriceMode()))
        {
            return ticker.getShortBid();
        }
        else
        {
            return ticker.getShortBid().add(contract_tick_size);
        }
    }
    
    /**
     * 获取价格效率模式
     */
    private String getPriceMode()
    {
        if (ticker.getSlipRatio().compareTo(new BigDecimal("0.0012")) == 1)
        {
            return "高效模式";// 多空都使用吃单
        }
        else if (ticker.getSlipRatio().compareTo(new BigDecimal("0.0006")) == 1)
        {
            return "中效模式";// 第一笔maker,第二笔taker
        }
        else
        {
            return "低效模式";// 都使用maker
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
            log.info("单笔对冲开仓完成，实际开仓溢价率/配置开仓溢价率（{}/{}）。",
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
            log.info("单笔对冲平仓完成，实际平仓溢价率/配置平仓溢价率（{}/{}）。",
                    hedgeModel.getShortAvgPrice().subtract(hedgeModel.getLongAvgPrice()).divide(hedgeModel.getLongAvgPrice(), 4, BigDecimal.ROUND_DOWN),
                    config.getCloseRatio());
        }
        hedgeModel = null;
        runStatus = 0;
    }
    
    /**
     * 创建订单
     *
     * @param price     委托价格
     * @param cont      委托张数
     * @param orderType 订单类型：1开多；2开空；3平多；4平空
     * @return
     */
    protected abstract long createFutureOrder(String symbol, BigDecimal price, BigDecimal cont, int orderType, boolean enableMaker);
}
