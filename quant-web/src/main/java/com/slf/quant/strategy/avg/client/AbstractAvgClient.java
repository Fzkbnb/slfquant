package com.slf.quant.strategy.avg.client;

import java.math.BigDecimal;

import com.slf.quant.facade.entity.strategy.QuantAvgConfig;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.model.SpotOrder;
import com.slf.quant.facade.service.strategy.QuantAvgConfigService;
import com.slf.quant.facade.utils.SpringContext;
import com.slf.quant.strategy.consts.TradeConst;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class AbstractAvgClient
{
    protected QuantAvgConfigService quantAvgConfigService = SpringContext.getBean("quantAvgConfigServiceImpl");
    
    protected QuantAvgConfig        config;
    
    protected String                apiKey;
    
    protected String                secretKey;
    
    protected String                passPhrase;
    
    protected String                idStr;
    
    protected String                exchangeAccount;
    
    protected int                   pricePrecision;
    
    protected int                   amtPrecision;
    
    protected boolean               hasError;
    
    protected long                  orderId               = 0;
    
    protected boolean               isStoped              = false;
    
    protected boolean               stopFlag              = false;
    
    protected long                  firstStopTime         = 0;
    
    protected long                  lastCacheTime         = 0;
    
    protected QuoteDepth            currentDepthModel;
    
    protected BigDecimal            tickSize;
    
    protected BigDecimal            minSpotAmt;
    
    private String                  direct;
    
    protected BigDecimal            feeRate;
    
    public AbstractAvgClient(QuantAvgConfig config, String apiKey, String secretKey, String passPhrase)
    {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.passPhrase = passPhrase;
        this.config = config;
    }
    
    /**
     * 策略启动入口
     * <p>
     * 需要推送日志的动作：
     */
    public final void start()
    {
        log.info("策略[{}]，开始运行。", config.getId());
        new Thread(() -> {
            try
            {
                initCommentData();
                initApiData();
                // 执行一次全量撤单
                if (!cancelAllOrder())
                {
                    hasError = true;
                }
            }
            catch (Exception e)
            {
                log.info("[{}]数据初始化异常，将退出策略！", config.getSymbol());
                hasError = true;
                e.printStackTrace();
            }
            while (true)
            {
                try
                {
                    // cacheSumInfo();
                    if (checkNeedStop())
                    {
                        break;
                    }
                    // 获取最新行情价格
                    if (orderId == 0)
                    {
                        currentDepthModel = getSpotDepth(config.getCurrency());
                        if (null == currentDepthModel)
                        {
                            continue;
                        }
                        log.info("当前行情：{}", currentDepthModel.toString());
                        if (config.getCurrentBasePrice().subtract(currentDepthModel.getAsk()).divide(config.getCurrentBasePrice(), 8, BigDecimal.ROUND_HALF_UP)
                                .compareTo(config.getPriceDownRate()) == 1)
                        {
                            // (基准价-行情价)/基准价大于价格下跌率阈值，则执行买入平衡
                            handleBuyMode();
                        }
                        else if (currentDepthModel.getBid().subtract(config.getCurrentBasePrice()).divide(config.getCurrentBasePrice(), 8, BigDecimal.ROUND_HALF_UP)
                                .compareTo(config.getPriceUpRate()) == 1)
                        {
                            // (行情价-基准价)/基准价大于价格上涨率阈值，则执行卖出平衡
                            handleSellMode();
                        }
                        else
                        {
                            // 继续监控行情
                            continue;
                        }
                    }
                    else
                    {
                        SpotOrder spotOrder = getSpotOrder(config.getCurrency(), orderId);
                        if (null == spotOrder)
                        {
                            continue;
                        }
                        handleSpotOrder(spotOrder);
                    }
                }
                catch (Exception e)
                {
                    log.info("策略{} ，轮询任务执行异常：{}", idStr, e.getLocalizedMessage());
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        Thread.sleep(10000L);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            // 停止策略
            stop();
        }).start();
    }
    
    private void handleSpotOrder(SpotOrder spotOrder)
    {
        int status = spotOrder.getStatus();
        switch (status)
        {
            case 1:
                // 已完成，则根据本次成交数量更新数据库
                if (spotOrder.getDealAmt().compareTo(BigDecimal.ZERO) == 1)
                {
                    config.setTradeCount(config.getTradeCount().add(BigDecimal.ONE));
                    config.setCurrentBasePrice(spotOrder.getAvg_price());
                    config.setLastPrice(spotOrder.getAvg_price());
                    config.setUpdateTime(System.currentTimeMillis());
                    BigDecimal dealBal = spotOrder.getDealAmt().multiply(spotOrder.getAvg_price());
                    if (TradeConst.KEY_BUY.equalsIgnoreCase(direct))
                    {
                        config.setCoinBalance(config.getCoinBalance().add(spotOrder.getDealAmt().multiply(BigDecimal.ONE.subtract(feeRate))).setScale(amtPrecision,
                                BigDecimal.ROUND_DOWN));
                        config.setUsdtBalance(config.getUsdtBalance().subtract(dealBal).setScale(4, BigDecimal.ROUND_DOWN));
                    }
                    else
                    {
                        config.setCoinBalance(config.getCoinBalance().subtract(spotOrder.getDealAmt()).setScale(amtPrecision, BigDecimal.ROUND_DOWN));
                        config.setUsdtBalance(config.getUsdtBalance().add(dealBal.multiply(BigDecimal.ONE.subtract(feeRate))).setScale(4, BigDecimal.ROUND_DOWN));
                    }
                    quantAvgConfigService.save(config);
                }
                orderId = 0;
                direct = null;
                break;
            case 0:
                // 未完成，则订单超时未成交则撤单
                if (System.currentTimeMillis() - spotOrder.getEntrustTime() > 10000)
                {
                    log.info("策略【{}】现货订单超时未成交，执行撤单！", config.getSymbol());
                    cancelSpotOrder(config.getCurrency(), orderId);
                }
                break;
        }
    }
    
    protected abstract void cancelSpotOrder(String currency, long orderId);
    
    protected void handleSellMode()
    {
        log.info("进入行情上涨卖出调平模式，当前行情价【{}】,基准价【{}】!", currentDepthModel.getBid(), config.getCurrentBasePrice());
        direct = TradeConst.KEY_SELL;
        // 1.计算差额
        BigDecimal diff = config.getCoinBalance().multiply(currentDepthModel.getAsk()).subtract(config.getUsdtBalance()).setScale(2, BigDecimal.ROUND_HALF_UP);
        // if (diff.compareTo(BigDecimal.TEN) == -1)
        // {
        // // 如果差额小于10美金则取消本次调平
        // log.info("当前上涨差额【{}】usdt，小于10usdt,取消调平！", diff);
        // return;
        // }
        // 2.取差额的一半作为本次买入金额
        BigDecimal entrustBal = diff.divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
        // 3.确定卖出数量
        BigDecimal entrustAmt = entrustBal.divide(currentDepthModel.getBid(), amtPrecision, BigDecimal.ROUND_DOWN);
        if (entrustAmt.compareTo(minSpotAmt) == -1)
        {
            log.info("委托数量小于最小限制,取消调平！", diff);
            return;
        }
        orderId = spotEntrust(config.getCurrency(), currentDepthModel.getBid().add(tickSize).setScale(pricePrecision, BigDecimal.ROUND_DOWN), entrustAmt,
                TradeConst.KEY_SELL);
    }
    
    private void handleBuyMode()
    {
        log.info("进入行情下跌买入调平模式，当前行情价【{}】,基准价【{}】!", currentDepthModel.getAsk(), config.getCurrentBasePrice());
        direct = TradeConst.KEY_BUY;
        // 1.计算差额
        BigDecimal diff = config.getUsdtBalance().subtract(config.getCoinBalance().multiply(currentDepthModel.getAsk())).setScale(2, BigDecimal.ROUND_HALF_UP);
        // if (diff.compareTo(BigDecimal.TEN) == -1)
        // {
        // // 如果差额小于10美金则取消本次调平
        // log.info("当前下跌差额【{}】usdt，小于10usdt,取消调平！", diff);
        // return;
        // }
        // 2.取差额的一半作为本次买入金额
        BigDecimal entrustBal = diff.divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
        // 3.确定买入数量
        BigDecimal entrustAmt = entrustBal.divide(currentDepthModel.getAsk(), amtPrecision, BigDecimal.ROUND_DOWN);
        if (entrustAmt.compareTo(minSpotAmt) == -1)
        {
            log.info("委托数量小于最小限制,取消调平！", diff);
            return;
        }
        orderId = spotEntrust(config.getCurrency(), currentDepthModel.getAsk().subtract(tickSize).setScale(pricePrecision, BigDecimal.ROUND_DOWN), entrustAmt,
                TradeConst.KEY_BUY);
    }
    
    protected abstract long spotEntrust(String currency, BigDecimal entrustPrice, BigDecimal entrustAmt, String direct);
    
    protected abstract SpotOrder getSpotOrder(String currency, long orderId);
    
    protected abstract QuoteDepth getSpotDepth(String symbol);
    
    protected abstract boolean cancelAllOrder();
    
    protected abstract void initApiData();
    
    private void initCommentData()
    {
    }
    
    private boolean checkNeedStop()
    {
        if (hasError)
        {
            // 如果程序内部发出退出指令，直接退出
            log.info("策略{},hasError=true，将退出策略！", idStr);
            return true;
        }
        if (stopFlag)
        {
            // 如果是外部干预停止（用户请求），则需要判断对冲是否完成，完成后才能停止
            if (orderId == 0)
            {
                log.info("策略{},用户发起退出策略指令！", idStr);
                return true;
            }
            else
            {
                long curtime = System.currentTimeMillis();
                if (firstStopTime == 0)
                {
                    firstStopTime = curtime;
                }
                if (System.currentTimeMillis() - firstStopTime > 65000)
                {
                    log.info("策略{},当前处于交易期间，用户发起退出策略请求超过30秒，将退出策略！", idStr);
                    return true;
                }
                log.info("策略{},当前处于交易期间，暂时不能停止用户发起的退出策略！", idStr);
            }
        }
        return false;
    }
    
    public void stop()
    {
        log.info(">>>策略[{}],关闭程序<<<", config.getId());
        quantAvgConfigService.changeStatus(config.getId(), 0);
        stopFlag = true;
        isStoped = true;
    }
}
