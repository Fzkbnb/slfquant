package com.slf.quant.strategy.grid.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.slf.quant.facade.consts.KeyConst;
import com.slf.quant.facade.entity.strategy.QuantGridConfig;
import com.slf.quant.facade.entity.strategy.QuantStrategyProfit;
import com.slf.quant.facade.entity.strategy.QuantGridStats;
import com.slf.quant.facade.model.*;
import com.slf.quant.facade.service.strategy.QuantGridConfigService;
import com.slf.quant.facade.service.strategy.QuantStrategyProfitService;
import com.slf.quant.facade.service.strategy.QuantGridStatsService;
import com.slf.quant.facade.utils.SpringContext;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;

import com.slf.quant.strategy.consts.TradeConst;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class AbstractGridUsdtClient
{
    protected QuantGridStatsService      quantGridStatsService      = SpringContext.getBean("quantGridStatsServiceImpl");
    
    protected QuantGridConfigService     quantGridConfigService     = SpringContext.getBean("quantGridConfigServiceImpl");
    
    protected QuantStrategyProfitService quantStrategyProfitService = SpringContext.getBean("quantStrategyProfitServiceImpl");
    
    protected QuantGridConfig            config;
    
    protected List<QuantGridStats>       orders                     = new ArrayList<>();
    
    protected String                     apiKey;
    
    protected String                     secretKey;
    
    protected String                     passPhrase;
    
    protected String                     idStr;
    
    protected String                     contractCode;
    
    protected String                     exchangeAccount;
    
    protected int                        pricePrecision;
    
    protected boolean                    hasError;
    
    protected long                       orderId                    = 0;
    
    protected Integer                    fixNum;
    
    protected boolean                    isStoped                   = false;
    
    protected boolean                    stopFlag                   = false;
    
    protected BigDecimal                 spotFeeRate_taker;
    
    protected BigDecimal                 spotFeeRate_cal;
    
    protected BigDecimal                 spotFeeRate_maker;
    
    protected String                     spotFeeRate_taker_str;
    
    protected String                     spotFeeRate_maker_str;
    
    protected long                       firstStopTime              = 0;
    
    protected long                       lastCacheTime              = 0;
    
    protected long                       lastSaveProfitTime         = 0;
    
    protected QuoteDepth                 currentDepthModel;
    
    protected BigDecimal                 startSellPrice;
    
    protected BigDecimal                 startBuyPrice;
    
    protected BigDecimal                 tickSize;
    
    protected BigDecimal                 contractPerValue;
    
    public AbstractGridUsdtClient(QuantGridConfig config, String apiKey, String secretKey, String passPhrase)
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
        log.info("策略:{} ，开始运行。", config.getContractCode());
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
                log.info("数据初始化异常，将退出策略！");
                hasError = true;
                e.printStackTrace();
            }
            while (true)
            {
                try
                {
                    cacheSumInfo();
                    if (checkNeedStop())
                    {
                        break;
                    }
                    if (isFirstBuy())
                    {
                        // 主动买多模式
                        handleFirstBuyMode();
                    }
                    else
                    {
                        // 主动卖空模式
                        handleFirstSellMode();
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
                        Thread.sleep(3000L);
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
    
    private void cacheSumInfo()
    {
        // 每隔10秒监控一次
        long curtime = System.currentTimeMillis();
        if (curtime - lastCacheTime > 10000)
        {
            if (null == currentDepthModel)
            { return; }
            lastCacheTime = System.currentTimeMillis();
            GridContractSumInfo model = new GridContractSumInfo();
            // 累计买入金额，累计买入数量，累计卖出金额，累计卖出数量通过sql查询一次性得出
            GridContractSumInfo realModel = quantGridStatsService.findSumInfo(config.getId(), 1, contractPerValue);
            GridContractSumInfo unrealModel = quantGridStatsService.findSumInfo(config.getId(), 0, contractPerValue);
            // 盈亏计算
            BigDecimal disCount = BigDecimal.ONE.subtract(BigDecimal.valueOf(2).multiply(config.getFeeRate()));
            if (isFirstBuy())
            {
                BigDecimal profitReal1 = realModel.getSumSellBal().subtract(realModel.getSumBuyBal());
                BigDecimal profitReal2 = unrealModel.getSumSellBal()
                        .subtract(unrealModel.getAvgBuyPrice().multiply(contractPerValue).multiply(unrealModel.getSumSellCont()));
                model.setProfitReal(profitReal1.add(profitReal2).multiply(disCount).setScale(4, BigDecimal.ROUND_HALF_UP));
                BigDecimal leaveBuyBal = unrealModel.getSumBuyBal()
                        .subtract(unrealModel.getAvgBuyPrice().multiply(contractPerValue).multiply(unrealModel.getSumSellCont()));
                BigDecimal profitUnreal = unrealModel.getSumBuyCont().subtract(unrealModel.getSumSellCont()).multiply(contractPerValue).multiply(currentDepthModel.getBid())
                        .subtract(leaveBuyBal).multiply(disCount).setScale(4, BigDecimal.ROUND_HALF_UP);
                model.setProfitUnreal(profitUnreal);
            }
            else
            {
                // 盈利币量计算
                BigDecimal profitReal1 = realModel.getSumSellBal().subtract(realModel.getSumBuyBal());
                BigDecimal profitReal2 = unrealModel.getSumBuyCont().multiply(contractPerValue).multiply(unrealModel.getAvgSellPrice())
                        .subtract(unrealModel.getSumBuyBal());
                model.setProfitReal(profitReal1.add(profitReal2).multiply(disCount).setScale(4, BigDecimal.ROUND_HALF_UP));
                BigDecimal leaveSellBal = unrealModel.getSumSellBal()
                        .subtract(unrealModel.getAvgSellPrice().multiply(contractPerValue).multiply(unrealModel.getSumBuyCont()));
                BigDecimal futureBuyCont = unrealModel.getSumSellCont().subtract(unrealModel.getSumBuyCont());
                BigDecimal profitUnreal = leaveSellBal.subtract(currentDepthModel.getAsk().multiply(futureBuyCont).multiply(contractPerValue)).multiply(disCount)
                        .setScale(4, BigDecimal.ROUND_HALF_UP);
                model.setProfitUnreal(profitUnreal);
            }
            log.info(">>>当前实时统计信息({})<<<", config.getFirstDirect());
            log.info("总卖出/买入金额：{}/{}", realModel.getSumSellBal().add(unrealModel.getSumSellBal()).setScale(2, BigDecimal.ROUND_HALF_UP),
                    realModel.getSumBuyBal().add(unrealModel.getSumBuyBal()).setScale(2, BigDecimal.ROUND_HALF_UP));
            log.info("已实现/未实现盈亏：{}/{}", model.getProfitReal(), model.getProfitUnreal());
            log.info("对冲触发价：低于{}则买/高于{}则卖", startBuyPrice, startSellPrice);
            List<StrategyStatusModel> models = new ArrayList<>();
            StrategyStatusModel m1 = new StrategyStatusModel();
            m1.setKey("总卖出/买入(USDT)");
            m1.setValue(realModel.getSumSellBal().add(unrealModel.getSumSellBal()).setScale(2, BigDecimal.ROUND_HALF_UP) + "/"
                    + realModel.getSumBuyBal().add(unrealModel.getSumBuyBal()).setScale(2, BigDecimal.ROUND_HALF_UP));
            StrategyStatusModel m2 = new StrategyStatusModel();
            m2.setKey("已实现/未实现盈亏(USDT)");
            m2.setValue(model.getProfitReal() + "/" + model.getProfitUnreal());
            StrategyStatusModel m3 = new StrategyStatusModel();
            m3.setKey("账户权益/持仓权益(USDT)");
            m3.setValue("/");
            StrategyStatusModel m4 = new StrategyStatusModel();
            m4.setKey("开仓均价/对冲触发价");
            m4.setValue("/");
            models.add(m1);
            models.add(m2);
            models.add(m3);
            models.add(m4);
            TradeConst.strategy_stats_map.put(KeyConst.REDISKEY_STRATEGY_STATS + config.getId(), models);
            // redisUtils.set(KeyConst.REDISKEY_GRID_STATUS + config.getId(), models);
            // 每隔一小时保存一次盈亏统计信息到数据库用于绘制折线图
            if (curtime - lastSaveProfitTime > 60000L * 60)
            {
                long displayTime = curtime - curtime % (60000 * 60);
                QuantStrategyProfit quantStrategyProfit = new QuantStrategyProfit();
                quantStrategyProfit.setStrategyId(config.getId());
                quantStrategyProfit.setAccountId(config.getAccountId());
                quantStrategyProfit.setStrategyType(KeyConst.STRATEGYTYPE_GRID);
                quantStrategyProfit.setDisplayTime(displayTime);
                quantStrategyProfit.setProfit(model.getProfit().add(model.getProfitUnreal()).setScale(2, BigDecimal.ROUND_DOWN));
                quantStrategyProfitService.insert(quantStrategyProfit);
                lastSaveProfitTime = displayTime;
            }
        }
    }
    
    private void handleFirstSellMode()
    {
        if (orderId == 0)
        {
            // 无订单则获取最新行情
            currentDepthModel = getContractDepth(config.getContractCode());
            if (null == currentDepthModel)
            { return; }
            if (CollectionUtils.isEmpty(orders))
            {
                // 无委托则立即挂单
                BigDecimal quotePrice = currentDepthModel.getBid();
                // 如果行情价触发最低卖价限制，则直接取消卖出
                if (quotePrice.compareTo(config.getMinSellPrice()) < 1)
                { return; }
                handleContractEntrust(config.getContractCode(), config.getEntrustCont(), currentDepthModel.getAsk(), TradeConst.KEY_SELL,
                        config.getEnableMaker().equals(1) ? 1 : 0);
                return;
            }
            else
            {
                // 有委托则根据行情和已有委托确定是下买单还是卖单
                QuantGridStats stats = orders.get(0);
                BigDecimal buyLimitPrice = stats.getSellPrice().multiply(BigDecimal.ONE.subtract(config.getProfitRate())).setScale(pricePrecision,
                        BigDecimal.ROUND_HALF_UP);
                startBuyPrice = buyLimitPrice;
                if (currentDepthModel.getAsk().compareTo(buyLimitPrice) < 1)
                {
                    // 如果最新卖一价小于盈利价差率算出的买单价，则可以下对冲买单
                    BigDecimal diffRate = buyLimitPrice.subtract(currentDepthModel.getAsk()).divide(currentDepthModel.getAsk(), 4, BigDecimal.ROUND_HALF_UP);
                    int orderType = 0;
                    BigDecimal entrustPrice = currentDepthModel.getAsk();
                    if (config.getEnableMaker().equals(1) && diffRate.compareTo(new BigDecimal("0.001")) == -1)
                    {
                        // 当用户设置了maker优先且价差率小于0.1%时，下maker单，否则下普通委托
                        orderType = 1;
                        entrustPrice = currentDepthModel.getAsk().subtract(tickSize).setScale(pricePrecision, BigDecimal.ROUND_UP);
                    }
                    handleContractEntrust(config.getContractCode(), stats.getSellCont().subtract(stats.getBuyCont()), entrustPrice, TradeConst.KEY_BUY, orderType);
                    return;
                }
                else
                {
                    // 如果行情价未触发对冲，则从最高价卖单检测是否需要补充卖单
                    for (int i = 0; i < orders.size(); i++)
                    {
                        BigDecimal quotePrice = currentDepthModel.getBid();
                        // 如果行情价触发最低卖价限制，则直接取消卖出
                        if (quotePrice.compareTo(config.getMinSellPrice()) < 1)
                        { return; }
                        int orderType = config.getEnableMaker().equals(1) ? 1 : 0;
                        BigDecimal entrustPrice = orderType == 1 ? quotePrice.add(tickSize) : quotePrice;
                        QuantGridStats model = orders.get(i);
                        // 计算网格价差
                        BigDecimal priceDiff = model.getSellPrice().multiply(config.getPriceDiffRate()).setScale(pricePrecision, BigDecimal.ROUND_HALF_UP);
                        if (quotePrice.compareTo(model.getSellPrice()) > -1)
                        {
                            if (quotePrice.compareTo(model.getSellPrice().add(priceDiff)) > -1 && i == 0)
                            {
                                // 如果行情价满足价差要求且上方无订单，则向上新建订单
                                handleContractEntrust(config.getContractCode(), config.getEntrustCont().setScale(0, BigDecimal.ROUND_DOWN),
                                        entrustPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN), TradeConst.KEY_SELL, orderType);
                            }
                            return;
                        }
                        else
                        {
                            // 如果行情价大于买单价，则判断价差是否满足向下补单的要求
                            if (quotePrice.compareTo(model.getSellPrice().subtract(priceDiff)) < 1)
                            {
                                // 首先必须满足当前委托价-价差>=行情价
                                if (i + 1 < orders.size())
                                {
                                    // 如果如果向下已存在其他订单,当行情价满足 低委托价+价差《=行情价时才在中间补单，否则取消补单
                                    QuantGridStats nextModel = orders.get(i + 1);
                                    if (quotePrice.compareTo(nextModel.getSellPrice().add(priceDiff)) == -1)
                                    { return; }
                                }
                                // 执行中间补单
                                handleContractEntrust(config.getContractCode(), config.getEntrustCont().setScale(0, BigDecimal.ROUND_DOWN),
                                        entrustPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN), TradeConst.KEY_SELL, orderType);
                                return;
                            }
                        }
                    }
                }
            }
        }
        else
        {
            ContractOrder contractOrder = getContractOrder(config.getContractCode(), orderId);
            switch (contractOrder.getStatus())
            {
                case 1:// 已完成无成交
                    orderId = 0;
                    break;
                case 2:// 已完成有成交
                    if (TradeConst.KEY_BUY.equalsIgnoreCase(contractOrder.getDirect()))
                    {
                        // 如果是平仓
                        // 完善缓存信息
                        completeFirstSellStats(contractOrder);
                        orderId = 0;
                    }
                    else
                    {
                        // 如果是加仓或补仓
                        initFirstSellStats(contractOrder);
                        orderId = 0;
                    }
                    break;
                case 3:// 未完成无成交
                    if (System.currentTimeMillis() - contractOrder.getEntrustTime() > 10000)
                    {
                        cancelContractOrder(config.getContractCode(), orderId);
                    }
                    break;
                case 4:// 未完成有成交
                    if (System.currentTimeMillis() - contractOrder.getEntrustTime() > 10000)
                    {
                        cancelContractOrder(config.getContractCode(), orderId);
                    }
                    break;
            }
        }
    }
    
    protected abstract void cancelContractOrder(String contractCode, long orderId);
    
    protected abstract QuoteDepth getContractDepth(String currency);
    
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
    
    protected abstract boolean cancelAllOrder();
    
    private void handleFirstBuyMode()
    {
        if (orderId == 0)
        {
            // 无订单则获取最新行情
            currentDepthModel = getContractDepth(config.getContractCode());
            if (null == currentDepthModel)
            { return; }
            if (CollectionUtils.isEmpty(orders))
            {
                // 无委托则立即挂单
                BigDecimal quotePrice = currentDepthModel.getAsk();
                if (quotePrice.compareTo(config.getMaxBuyPrice()) > -1)
                { return; }
                handleContractEntrust(config.getContractCode(), config.getEntrustCont(), currentDepthModel.getBid(), TradeConst.KEY_BUY,
                        config.getEnableMaker().equals(1) ? 1 : 0);
                return;
            }
            else
            {
                // 有委托则根据行情和已有委托确定是下买单还是卖单
                QuantGridStats stats = orders.get(0);
                BigDecimal sellLimitPrice = stats.getBuyPrice().multiply(BigDecimal.ONE.add(config.getProfitRate())).setScale(pricePrecision, BigDecimal.ROUND_HALF_UP);
                startSellPrice = sellLimitPrice;
                if (currentDepthModel.getBid().compareTo(sellLimitPrice) > -1)
                {
                    // 如果最新买一价大于盈利价差率算出的卖单价，则可以下对冲卖单
                    BigDecimal diffRate = currentDepthModel.getBid().subtract(sellLimitPrice).divide(sellLimitPrice, 4, BigDecimal.ROUND_HALF_UP);
                    int orderType = 0;
                    BigDecimal entrustPrice = currentDepthModel.getBid();
                    if (config.getEnableMaker().equals(1) && diffRate.compareTo(new BigDecimal("0.001")) == -1)
                    {
                        // 当用户设置了maker优先且价差率小于1%时，下maker单，否则下普通委托
                        orderType = 1;
                        entrustPrice = currentDepthModel.getBid().add(tickSize).setScale(pricePrecision, BigDecimal.ROUND_UP);
                    }
                    BigDecimal entrustCont = stats.getBuyCont().subtract(stats.getSellCont());
                    handleContractEntrust(config.getContractCode(), entrustCont, entrustPrice, TradeConst.KEY_SELL, orderType);
                    return;
                }
                else
                {
                    // 如果行情价未触发对冲，则从最低价买单检测是否需要补充买单
                    for (int i = 0; i < orders.size(); i++)
                    {
                        BigDecimal quotePrice = currentDepthModel.getAsk();
                        // 如果行情价触发最高买价限制，则直接取消买入
                        if (quotePrice.compareTo(config.getMaxBuyPrice()) > -1)
                        { return; }
                        int orderType = config.getEnableMaker().equals(1) ? 1 : 0;
                        BigDecimal entrustPrice = orderType == 1 ? quotePrice.subtract(tickSize) : quotePrice;
                        QuantGridStats model = orders.get(i);
                        // 计算网格价差
                        BigDecimal priceDiff = model.getBuyPrice().multiply(config.getPriceDiffRate()).setScale(pricePrecision, BigDecimal.ROUND_HALF_UP);
                        if (model.getBuyPrice().compareTo(quotePrice) > -1)
                        {
                            if (model.getBuyPrice().subtract(priceDiff).compareTo(quotePrice) > -1 && i == 0)
                            {
                                // 如果行情价满足价差要求且下方无订单，则向下新建订单
                                handleContractEntrust(config.getContractCode(), config.getEntrustCont().setScale(0, BigDecimal.ROUND_DOWN),
                                        entrustPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN), TradeConst.KEY_BUY, orderType);
                            }
                            return;
                        }
                        else
                        {
                            // 如果行情价大于买单价，则判断价差是否满足向上补单的要求
                            if (model.getBuyPrice().add(priceDiff).compareTo(quotePrice) < 1)
                            {
                                // 首先必须满足当前委托价+价差《=行情价
                                if (i + 1 < orders.size())
                                {
                                    // 如果如果向上已存在其他订单,当行情价满足 高委托价-价差>=行情价时才在中间补单，否则取消补单
                                    QuantGridStats nextModel = orders.get(i + 1);
                                    if (nextModel.getBuyPrice().subtract(priceDiff).compareTo(quotePrice) == -1)
                                    { return; }
                                }
                                // 执行中间补单
                                handleContractEntrust(config.getContractCode(), config.getEntrustCont().setScale(0, BigDecimal.ROUND_DOWN),
                                        entrustPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN), TradeConst.KEY_BUY, orderType);
                                return;
                            }
                        }
                    }
                }
            }
        }
        else
        {
            ContractOrder contractOrder = getContractOrder(config.getContractCode(), orderId);
            if (null == contractOrder)
            { return; }
            switch (contractOrder.getStatus())
            {
                case 1:// 已完成无成交
                    orderId = 0;
                    break;
                case 2:// 已完成有成交
                    if (TradeConst.KEY_BUY.equalsIgnoreCase(contractOrder.getDirect()))
                    {
                        initFirstBuyStats(contractOrder);
                        orderId = 0;
                    }
                    else
                    {
                        // 如果是加仓或补仓
                        completeFirstBuyStats(contractOrder);
                        orderId = 0;
                    }
                    break;
                case 3:// 未完成无成交
                    if (System.currentTimeMillis() - contractOrder.getEntrustTime() > 10000)
                    {
                        cancelContractOrder(config.getContractCode(), orderId);
                    }
                    break;
                case 4:// 未完成有成交
                    if (System.currentTimeMillis() - contractOrder.getEntrustTime() > 10000)
                    {
                        cancelContractOrder(config.getContractCode(), orderId);
                    }
                    break;
            }
        }
    }
    
    private void completeFirstBuyStats(ContractOrder contractOrder)
    {
        QuantGridStats stats = orders.get(0);
        stats.setSellOrderId(contractOrder.getId());
        if (stats.getSellCont().compareTo(BigDecimal.ZERO) == 0)
        {
            stats.setSellPrice(BigDecimal.ONE);
        }
        BigDecimal sumCont = stats.getSellCont().add(contractOrder.getDealCont());
        BigDecimal sumAmt = stats.getSellCont().multiply(contractPerValue)
                .add(contractOrder.getDealCont().multiply(contractPerValue).setScale(8, BigDecimal.ROUND_HALF_UP));
        BigDecimal avgPrice = stats.getSellCont().multiply(contractPerValue).multiply(stats.getSellPrice())
                .add(contractOrder.getDealCont().multiply(contractPerValue).multiply(contractOrder.getAvg_price()))
                .divide(sumAmt, pricePrecision, BigDecimal.ROUND_HALF_UP);
        stats.setSellCont(sumCont);
        stats.setSellPrice(avgPrice);
        stats.setSellUsdt(stats.getSellCont().multiply(contractPerValue).multiply(avgPrice).setScale(2, BigDecimal.ROUND_HALF_UP));
        stats.setSellTime(System.currentTimeMillis());
        if (sumCont.compareTo(stats.getBuyCont()) == 0)
        {
            // 此时认为对冲完成
            orders.remove(0);
            stats.setOrderStatus(1);
            stats.setUpdateTime(System.currentTimeMillis());
        }
        quantGridStatsService.updateByPrimaryKey(stats);
    }
    
    private void completeFirstSellStats(ContractOrder contractOrder)
    {
        QuantGridStats stats = orders.get(0);
        stats.setBuyOrderId(contractOrder.getId());
        BigDecimal sumCont = stats.getBuyCont().add(contractOrder.getDealCont());
        if (stats.getBuyCont().compareTo(BigDecimal.ZERO) == 0)
        {
            stats.setBuyPrice(BigDecimal.ONE);
        }
        BigDecimal sumAmt = stats.getBuyCont().multiply(contractPerValue).add(contractOrder.getDealCont().multiply(contractPerValue).setScale(8, BigDecimal.ROUND_HALF_UP));
        BigDecimal avgPrice = stats.getBuyCont().multiply(contractPerValue).multiply(stats.getBuyPrice())
                .add(contractOrder.getDealCont().multiply(contractPerValue).multiply(contractOrder.getAvg_price()))
                .divide(sumAmt, pricePrecision, BigDecimal.ROUND_HALF_UP);
        stats.setBuyCont(sumCont);
        stats.setBuyPrice(avgPrice);
        stats.setBuyUsdt(stats.getBuyCont().multiply(contractPerValue).multiply(avgPrice).setScale(2, BigDecimal.ROUND_HALF_UP));
        stats.setBuyTime(System.currentTimeMillis());
        if (sumCont.compareTo(stats.getSellCont()) == 0)
        {
            // 此时认为对冲完成
            orders.remove(0);
            stats.setOrderStatus(1);
            stats.setUpdateTime(System.currentTimeMillis());
        }
        quantGridStatsService.updateByPrimaryKey(stats);
    }
    
    private void initFirstBuyStats(ContractOrder contractOrder)
    {
        // 更新统计信息
        QuantGridStats stats;
        if (null != fixNum)
        {
        }
        else
        {
            // 新建订单模式
            stats = new QuantGridStats();
            stats.setOrderStatus(0);
            stats.setAccountId(config.getAccountId());
            stats.setStrategyId(config.getId());
            stats.setExchange("okex");
            stats.setCurrency(config.getCurrency());
            stats.setSellOrderId(0L);
            stats.setSellCont(BigDecimal.ZERO);
            stats.setSellPrice(BigDecimal.ZERO);
            stats.setFirstDirect(TradeConst.KEY_BUY);
            stats.setBuyOrderId(contractOrder.getId());
            stats.setBuyCont(contractOrder.getDealCont());
            stats.setBuyPrice(contractOrder.getAvg_price());
            stats.setBuyUsdt(stats.getBuyCont().multiply(contractPerValue).multiply(stats.getBuyPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
            stats.setSellUsdt(BigDecimal.ZERO);
            stats.setBuyTime(System.currentTimeMillis());
            quantGridStatsService.save(stats);
            orders.add(stats);
            // 按成交均价低到高排序
            Collections.sort(orders, Comparator.comparing(QuantGridStats::getBuyPrice));
        }
        fixNum = null;
    }
    
    private void initFirstSellStats(ContractOrder contractOrder)
    {
        // 更新统计信息
        QuantGridStats stats;
        if (null != fixNum)
        {
            // 补单模式
            // stats = orders.get(fixNum);
            // stats.setSellOrderId(contractOrder.getId());
            // BigDecimal dealCont = stats.getSellCont().add(contractOrder.getDealCont());
            // BigDecimal avgPrice = stats.getSellCont().multiply(stats.getSellPrice()).add(contractOrder.getDealCont().multiply(contractPerValue)).divide(dealAmt,
            // pricePrecision, BigDecimal.ROUND_HALF_UP);
            // stats.setSellAmt(dealAmt);
            // stats.setBuyPrice(avgPrice);
            // stats.setUpdateTime(System.currentTimeMillis());
            // QuantStrategyGridContractStatsService.updateByPrimaryKeyPartSelective(stats);
        }
        else
        {
            // 新建订单模式
            stats = new QuantGridStats();
            stats.setOrderStatus(0);
            stats.setAccountId(config.getAccountId());
            stats.setStrategyId(config.getId());
            stats.setExchange("okex");
            stats.setCurrency(config.getCurrency());
            stats.setSellOrderId(contractOrder.getId());
            stats.setSellCont(contractOrder.getDealCont());
            stats.setSellPrice(contractOrder.getAvg_price());
            stats.setSellUsdt(contractOrder.getDealCont().multiply(contractPerValue).multiply(contractOrder.getAvg_price()).setScale(2, BigDecimal.ROUND_HALF_UP));
            stats.setBuyUsdt(BigDecimal.ZERO);
            stats.setFirstDirect(TradeConst.KEY_SELL);
            stats.setSellTime(System.currentTimeMillis());
            stats.setBuyOrderId(0L);
            stats.setBuyCont(BigDecimal.ZERO);
            stats.setBuyPrice(BigDecimal.ZERO);
            stats.setUpdateTime(System.currentTimeMillis());
            log.info("新建统计信息：{}", stats.toString());
            quantGridStatsService.save(stats);
            orders.add(stats);
            // 按成交均价低到高排序
            Collections.sort(orders, Comparator.comparing(QuantGridStats::getSellPrice).reversed());
        }
        fixNum = null;
    }
    
    protected void handleContractEntrust(String symbol, BigDecimal entrustCont, BigDecimal entrustPrice, String direct, Integer orderType)
    {
        if ((TradeConst.KEY_BUY.equalsIgnoreCase(direct) && isFirstBuy()) || (TradeConst.KEY_SELL.equalsIgnoreCase(direct) && (!isFirstBuy())))
        {
            // 如果是加仓，需要判断可开是否充足
            AccountModel accountModel = getContractAccount(config.getCurrency());
            if (null == accountModel)
            { return; }
            // 计算合约可开张数
            BigDecimal enableCont = accountModel.getAvailableMargin().divide(currentDepthModel.getAsk(), 8, BigDecimal.ROUND_HALF_UP).multiply(config.getLeverRate())
                    .divide(contractPerValue, 0, BigDecimal.ROUND_DOWN);
            if (enableCont.compareTo(BigDecimal.valueOf(2)) == -1)
            {
                log.info("合约可开张数不足，取消交易！");
                return;
            }
            entrustCont = ObjectUtils.min(entrustCont, enableCont);
        }
        orderId = contractEntrust(symbol, entrustCont.setScale(0, BigDecimal.ROUND_DOWN), entrustPrice.setScale(pricePrecision, BigDecimal.ROUND_DOWN), direct, orderType);
        if (orderId < 0)
        {
            orderId = 0;
        }
    }
    
    protected abstract AccountModel getContractAccount(String currency);
    
    protected abstract long contractEntrust(String currency, BigDecimal entrustAmt, BigDecimal entrustPrice, String direct, Integer orderType);
    
    protected abstract ContractOrder getContractOrder(String currency, long orderId);
    
    protected abstract void initApiData();
    
    private void initCommentData()
    {
        try
        {
            // 将数据库原未对冲完成的订单统计记录加载到缓存
            QuantGridStats query = new QuantGridStats();
            query.setAccountId(config.getAccountId());
            query.setStrategyId(config.getId());
            query.setOrderStatus(0);
            orders = quantGridStatsService.findList(query);
            // 按成交均价低到高排序
            if (isFirstBuy())
            {
                Collections.sort(orders, Comparator.comparing(QuantGridStats::getBuyPrice));
            }
            else
            {
                Collections.sort(orders, Comparator.comparing(QuantGridStats::getSellPrice).reversed());
            }
        }
        catch (Exception e)
        {
            log.info("策略{}，通用数据初始化失败，将关闭策略:{}", config.getId(), e.getLocalizedMessage());
            hasError = true;
            e.printStackTrace();
        }
    }
    
    protected boolean isFirstBuy()
    {
        return "buy".equalsIgnoreCase(config.getFirstDirect());
    }
    
    public void stop()
    {
        log.info(">>>策略{},关闭程序<<<", config.getId());
        quantGridConfigService.changeStatus(config.getId(), 0);
        stopFlag = true;
        isStoped = true;
    }
}
