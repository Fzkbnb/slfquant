package com.slf.quant.strategy.swapavg.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.slf.quant.facade.consts.KeyConst;
import com.slf.quant.facade.entity.strategy.QuantSwapAvgConfig;
import com.slf.quant.facade.entity.strategy.QuantStrategyProfit;
import com.slf.quant.facade.model.*;
import com.slf.quant.facade.service.strategy.QuantSwapAvgConfigService;
import com.slf.quant.facade.service.strategy.QuantStrategyProfitService;
import com.slf.quant.facade.utils.SpringContext;
import com.slf.quant.strategy.consts.TradeConst;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class AbstractSwapAvgClient
{
    protected QuantStrategyProfitService quantStrategyProfitService = SpringContext.getBean("quantStrategyProfitServiceImpl");
    
    protected QuantSwapAvgConfigService  quantSwapAvgConfigService  = SpringContext.getBean("quantSwapAvgConfigServiceImpl");
    
    protected QuantSwapAvgConfig         config;
    
    protected String                     apiKey;
    
    protected String                     secretKey;
    
    protected String                     passPhrase;
    
    protected String                     idStr;
    
    protected String                     exchangeAccount;
    
    protected int                        pricePrecision;
    
    protected int                        amtPrecision;
    
    protected boolean                    hasError;
    
    protected long                       orderId                    = 0;
    
    protected boolean                    isStoped                   = false;
    
    protected boolean                    stopFlag                   = false;
    
    protected long                       firstStopTime              = 0;
    
    protected long                       lastCacheTime              = 0;
    
    protected long                       lastSaveProfitTime         = 0;
    
    protected QuoteDepth                 currentDepthModel;
    
    protected AccountModel               accountModel;
    
    protected BigDecimal                 tickSize;
    
    protected BigDecimal                 minSpotAmt;
    
    private Integer                      tradeMode;
    
    protected BigDecimal                 feeRate;
    
    private BigDecimal                   contractPerValue;                                                                    // 合约面值
    
    public AbstractSwapAvgClient(QuantSwapAvgConfig config, String apiKey, String secretKey, String passPhrase)
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
                log.info("[{}]数据初始化异常，将退出策略！", config.getCurrency());
                hasError = true;
                e.printStackTrace();
            }
            while (true)
            {
                try
                {
                    cacheStatsInfo();
                    if (checkNeedStop())
                    {
                        break;
                    }
                    // 获取最新行情价格
                    if (orderId == 0)
                    {
                        currentDepthModel = getContractDepth(config.getCurrency());
                        if (null == currentDepthModel)
                        {
                            continue;
                        }
                        accountModel = getContractAccount(config.getCurrency());
                        if (null == accountModel)
                        {
                            continue;
                        }
                        log.info("当前行情：{}", currentDepthModel.toString());
                        if (config.getCurrentBasePrice().subtract(currentDepthModel.getAsk()).divide(config.getCurrentBasePrice(), 8, BigDecimal.ROUND_HALF_UP)
                                .compareTo(config.getPriceDownRate()) == 1)
                        {
                            // (基准价-行情价)/基准价大于价格下跌率阈值，则执行做多平衡
                            handleBuyMode();
                        }
                        else if (currentDepthModel.getBid().subtract(config.getCurrentBasePrice()).divide(config.getCurrentBasePrice(), 8, BigDecimal.ROUND_HALF_UP)
                                .compareTo(config.getPriceUpRate()) == 1)
                        {
                            // (行情价-基准价)/基准价大于价格上涨率阈值，则执行做空平衡
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
                        ContractOrder contractOrder = getContractOrder(config.getCurrency(), orderId);
                        if (null == contractOrder)
                        {
                            continue;
                        }
                        handleContractOrder(contractOrder);
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
    
    protected abstract ContractOrder getContractOrder(String currency, long orderId);
    
    protected abstract AccountModel getContractAccount(String currency);
    
    protected abstract QuoteDepth getContractDepth(String currency);
    
    // 保存统计信息
    private void cacheStatsInfo()
    {
        // 每隔10秒监控一次
        long curtime = System.currentTimeMillis();
        if (curtime - lastCacheTime > 10000)
        {
            if (null == currentDepthModel)
            { return; }
            lastCacheTime = System.currentTimeMillis();
            // 永续合约盈亏计算直接使用接口返回的合约权益-初始本金
            BigDecimal currentBalance = config.getEnableMargin().add(config.getPositionAmt().multiply(contractPerValue).multiply(currentDepthModel.getBid()));
            BigDecimal profit = currentBalance.subtract(config.getUsdtCapital()).setScale(2, BigDecimal.ROUND_DOWN);
            List<StrategyStatusModel> models = new ArrayList<>();
            StrategyStatusModel m1 = new StrategyStatusModel();
            m1.setKey("初始行情价/当前行情价");
            m1.setValue(config.getFirstBasePrice().setScale(pricePrecision, BigDecimal.ROUND_HALF_UP) + "/"
                    + currentDepthModel.getBid().setScale(pricePrecision, BigDecimal.ROUND_HALF_UP));
            StrategyStatusModel m2 = new StrategyStatusModel();
            m2.setKey("初始本金/当前本金(USDT)");
            m2.setValue(config.getUsdtCapital().setScale(2, BigDecimal.ROUND_DOWN) + "/" + currentBalance.setScale(2, BigDecimal.ROUND_DOWN));
            StrategyStatusModel m3 = new StrategyStatusModel();
            m3.setKey("交易次数/盈利率");
            m3.setValue(config.getTradeCount() + "/" + profit.divide(config.getUsdtCapital(), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)) + "%");
            models.add(m1);
            models.add(m2);
            models.add(m3);
            TradeConst.strategy_stats_map.put(KeyConst.REDISKEY_STRATEGY_STATS + config.getId(), models);
            // 每隔一小时新建一次盈亏统计信息到数据库用于绘制折线图
            if (curtime - lastSaveProfitTime > 60000L * 60)
            {
                long displayTime = curtime - curtime % (60000 * 60);
                QuantStrategyProfit quantStrategyProfit = new QuantStrategyProfit();
                quantStrategyProfit.setStrategyId(config.getId());
                quantStrategyProfit.setAccountId(config.getAccountId());
                quantStrategyProfit.setStrategyType(KeyConst.STRATEGYTYPE_AVG);
                quantStrategyProfit.setDisplayTime(displayTime);
                quantStrategyProfit.setProfit(profit);
                quantStrategyProfitService.insert(quantStrategyProfit);
                lastSaveProfitTime = displayTime;
            }
        }
    }
    
    private void handleContractOrder(ContractOrder contractOrder)
    {
        int status = contractOrder.getStatus();
        switch (status)
        {
            case 1:
                // 已完成，则根据本次成交数量更新数据库
                if (contractOrder.getDealCont().compareTo(BigDecimal.ZERO) == 1)
                {
                    config.setTradeCount(config.getTradeCount().add(BigDecimal.ONE));
                    config.setCurrentBasePrice(contractOrder.getAvg_price());
                    config.setLastPrice(contractOrder.getAvg_price());
                    config.setUpdateTime(System.currentTimeMillis());
                    BigDecimal dealBal = contractOrder.getDealCont().multiply(contractPerValue).multiply(contractOrder.getAvg_price());
                    if (tradeMode.equals(1) || tradeMode.equals(2))
                    {
                        config.setPositionAmt(config.getPositionAmt().add(contractOrder.getDealCont().multiply(contractPerValue)));
                        config.setEnableMargin(config.getEnableMargin().subtract(dealBal).setScale(4, BigDecimal.ROUND_DOWN));
                    }
                    else
                    {
                        config.setPositionAmt(config.getPositionAmt().subtract(contractOrder.getDealCont().multiply(contractPerValue)));
                        config.setEnableMargin(config.getEnableMargin().add(dealBal.multiply(BigDecimal.ONE.subtract(feeRate))).setScale(4, BigDecimal.ROUND_DOWN));
                    }
                    quantSwapAvgConfigService.save(config);
                }
                orderId = 0;
                tradeMode = null;
                break;
            case 0:
                // 未完成，则订单超时未成交则撤单
                if (System.currentTimeMillis() - contractOrder.getEntrustTime() > 10000)
                {
                    log.info("策略【{}】订单超时未成交，执行撤单！", config.getCurrency());
                    cancelContractOrder(config.getCurrency(), orderId);
                }
                break;
        }
    }
    
    protected abstract void cancelContractOrder(String currency, long orderId);
    
    protected void handleSellMode()
    {
        log.info("进入行情上涨卖出调平模式，当前行情价【{}】,基准价【{}】!", currentDepthModel.getBid(), config.getCurrentBasePrice());
        // 1.计算差额
        BigDecimal diff = config.getPositionAmt().multiply(currentDepthModel.getAsk()).subtract(config.getEnableMargin()).setScale(2, BigDecimal.ROUND_HALF_UP);
        // if (diff.compareTo(BigDecimal.TEN) == -1)
        // {
        // // 如果差额小于10美金则取消本次调平
        // log.info("当前上涨差额【{}】usdt，小于10usdt,取消调平！", diff);
        // return;
        // }
        // 2.取差额的一半作为本次买入金额
        BigDecimal entrustBal = diff.divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
        // 3.确定卖出数量
        BigDecimal entrustCont = entrustBal.divide(currentDepthModel.getBid().multiply(contractPerValue), 0, BigDecimal.ROUND_DOWN);
        if (entrustCont.compareTo(BigDecimal.ONE) == -1)
        {
            log.info("委托张数小于1,取消调平！", diff);
            return;
        }
        tradeMode = config.getPositionDirect().equals(1) ? 3 : 2;
        orderId = contractEntrust(config.getCurrency(), currentDepthModel.getBid().add(tickSize).setScale(pricePrecision, BigDecimal.ROUND_DOWN), entrustCont, tradeMode);
    }
    
    private void handleBuyMode()
    {
        log.info("进入行情下跌买入调平模式，当前行情价【{}】,基准价【{}】!", currentDepthModel.getAsk(), config.getCurrentBasePrice());
        // 1.计算差额
        BigDecimal diff = config.getEnableMargin().subtract(config.getPositionAmt().multiply(currentDepthModel.getAsk())).setScale(2, BigDecimal.ROUND_HALF_UP);
        // if (diff.compareTo(BigDecimal.TEN) == -1)
        // {
        // // 如果差额小于10美金则取消本次调平
        // log.info("当前下跌差额【{}】usdt，小于10usdt,取消调平！", diff);
        // return;
        // }
        // 2.取差额的一半作为本次买入金额
        BigDecimal entrustBal = diff.divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
        // 3.确定买入数量
        BigDecimal entrustCont = entrustBal.divide(currentDepthModel.getAsk().multiply(contractPerValue), 0, BigDecimal.ROUND_DOWN);
        if (entrustCont.compareTo(BigDecimal.ONE) == -1)
        {
            log.info("委托张数小于1,取消调平！", diff);
            return;
        }
        // 交易模式：1买入开多，2卖出开空，3卖出平多，4买入平空，所有这里只能是1和4
        tradeMode = config.getPositionDirect().equals(1) ? 1 : 4;
        orderId = contractEntrust(config.getCurrency(), currentDepthModel.getAsk().subtract(tickSize).setScale(pricePrecision, BigDecimal.ROUND_DOWN), entrustCont,
                tradeMode);
    }
    
    protected abstract long contractEntrust(String currency, BigDecimal setScale, BigDecimal entrustCont, int mode);
    
    protected abstract boolean cancelAllOrder();
    
    protected abstract void initApiData();
    
    private void initCommentData()
    {
        if (lastSaveProfitTime == 0)
        {
            QuantStrategyProfit lastProfit = quantStrategyProfitService.findLastProfit(config.getId());
            if (null != lastProfit)
            {
                lastSaveProfitTime = lastProfit.getDisplayTime();
            }
        }
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
        quantSwapAvgConfigService.changeStatus(config.getId(), 0);
        stopFlag = true;
        isStoped = true;
    }
}
