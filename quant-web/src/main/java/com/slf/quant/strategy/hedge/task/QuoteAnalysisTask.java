package com.slf.quant.strategy.hedge.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.model.hedge.PremiumRatioModel;
import com.slf.quant.facade.utils.QuantUtil;
import com.slf.quant.strategy.consts.TradeConst;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 行情监控评估是否需要以下原则
 * 1.只有当溢价率绝对值超过千8时才考虑开仓，千8以下只需等待监控报警；
 * 2.开仓溢价率差设置不应当低于千3，这是按最大手续费率万5*4=千2，千3意味着开平对冲完成后最低保留千1*杠杆率的利润
 * 3.当周行情仅在周五交割后到周日纳入行情监控，超过周日则尽量不再对当周进行操作
 *
 *
 */
@Slf4j
@Service
public class QuoteAnalysisTask
{
    public void start()
    {
        new Thread(() -> {
            while (true)
            {
                try
                {
                    long curtime = System.currentTimeMillis();
                    String cw = QuantUtil.getWeekDate(curtime, 0);
                    String nw = QuantUtil.getWeekDate(curtime, 7);
                    String cq = QuantUtil.getQuanterDate(curtime);
                    String nq = QuantUtil.getNextQuanterDate(curtime);
                    List<String> currencys = Arrays.asList(TradeConst.hedge_currencys.split(","));
                    // 1.okex 币本位
                    // List<PremiumRatioModel> sets = new ArrayList<>();
                    // for (String currency : currencys)
                    // {
                    // QuoteDepth cwQuoteDepth = getOkexContractPrice(currency + "-USD-" + cw);
                    // QuoteDepth nwQuoteDepth = getOkexContractPrice(currency + "-USD-" + nw);
                    // QuoteDepth cqQuoteDepth = getOkexContractPrice(currency + "-USD-" + cq);
                    // QuoteDepth nqQuoteDepth = getOkexContractPrice(currency + "-USD-" + nq);
                    // if (null != cwQuoteDepth && null != nwQuoteDepth && null != cqQuoteDepth && null != nqQuoteDepth)
                    // {
                    // QuoteDepth maxQuoteDepth = ObjectUtils.max(cwQuoteDepth, nwQuoteDepth, cqQuoteDepth, nqQuoteDepth);
                    // QuoteDepth minQuoteDepth = ObjectUtils.min(cwQuoteDepth, nwQuoteDepth, cqQuoteDepth, nqQuoteDepth);
                    // PremiumRatioModel premiumRatioModel = PremiumRatioModel.builder().longQuoteDepth(minQuoteDepth).shortQuoteDepth(maxQuoteDepth)
                    // .ratio(maxQuoteDepth.getBid().subtract(minQuoteDepth.getAsk()).divide(minQuoteDepth.getAsk(), 4, BigDecimal.ROUND_HALF_UP)).build();
                    // sets.add(premiumRatioModel);
                    // }
                    // else
                    // {
                    // log.info("{}行情缓存不存在或超时，本次取消当前币种溢价率统计！", currency);
                    // }
                    // }
                    // 对溢价率进行排序
                    // Collections.sort(sets);
                    // log.info(">>>本次筛选出的okex-币本位溢价率从高到低排序如下<<<");
                    // sets.forEach(model -> {
                    // log.info("空头合约：{}，多头合约：{}，溢价率：{}", model.getShortQuoteDepth().getInstrument_id(), model.getLongQuoteDepth().getInstrument_id(),
                    // model.getRatio().multiply(BigDecimal.valueOf(100)).toPlainString() + "%");
                    // });
                    // log.info(">>>排序结束<<<");
                    // 2.okex u本位
                    List<PremiumRatioModel> setsU = new ArrayList<>();
                     for (String currency : currencys)
                     {
                     QuoteDepth cwQuoteDepth = getOkexContractPrice(currency + "-USDT-" + cw);
                     QuoteDepth nwQuoteDepth = getOkexContractPrice(currency + "-USDT-" + nw);
                     QuoteDepth cqQuoteDepth = getOkexContractPrice(currency + "-USDT-" + cq);
                     QuoteDepth nqQuoteDepth = getOkexContractPrice(currency + "-USDT-" + nq);
                     if (null != cwQuoteDepth && null != nwQuoteDepth && null != cqQuoteDepth && null != nqQuoteDepth)
                     {
                     QuoteDepth maxQuoteDepth = ObjectUtils.max(cwQuoteDepth, nwQuoteDepth, cqQuoteDepth, nqQuoteDepth);
                     QuoteDepth minQuoteDepth = ObjectUtils.min(cwQuoteDepth, nwQuoteDepth, cqQuoteDepth, nqQuoteDepth);
                     PremiumRatioModel premiumRatioModel = PremiumRatioModel.builder().longQuoteDepth(minQuoteDepth).shortQuoteDepth(maxQuoteDepth)
                     .ratio(maxQuoteDepth.getBid().subtract(minQuoteDepth.getAsk()).divide(minQuoteDepth.getAsk(), 4, BigDecimal.ROUND_HALF_UP)).build();
                     setsU.add(premiumRatioModel);
                     }
                     else
                     {
                     log.info("{}行情缓存不存在或超时，本次取消当前币种溢价率统计！", currency);
                     }
                     }
                    for (String currency : currencys)
                    {
                        QuoteDepth nwQuoteDepth = getOkexContractPrice(currency + "-USDT-" + nw);
                        QuoteDepth cqQuoteDepth = getOkexContractPrice(currency + "-USDT-" + cq);
                        if (null != nwQuoteDepth && null != cqQuoteDepth)
                        {
                            QuoteDepth maxQuoteDepth = ObjectUtils.max(nwQuoteDepth, cqQuoteDepth);
                            QuoteDepth minQuoteDepth = ObjectUtils.min(nwQuoteDepth, cqQuoteDepth);
                            PremiumRatioModel premiumRatioModel = PremiumRatioModel.builder().longQuoteDepth(minQuoteDepth).shortQuoteDepth(maxQuoteDepth)
                                    .ratio(maxQuoteDepth.getBid().subtract(minQuoteDepth.getAsk()).divide(minQuoteDepth.getAsk(), 4, BigDecimal.ROUND_HALF_UP)).build();
                            setsU.add(premiumRatioModel);
                        }
                        else
                        {
                            log.info("{}行情缓存不存在或超时，本次取消当前币种溢价率统计！", currency);
                        }
                    }
                    // 对溢价率进行排序
                    Collections.sort(setsU);
                    log.info(">>>本次筛选出的okex-u本位溢价率从高到低排序如下<<<");
                    setsU.forEach(model -> {
                        log.info("空头合约：{}，多头合约：{}，溢价率：{}", model.getShortQuoteDepth().getInstrument_id(), model.getLongQuoteDepth().getInstrument_id(),
                                model.getRatio().multiply(BigDecimal.valueOf(100)).toPlainString() + "%");
                    });
                    log.info(">>>排序结束<<<");
                    // 3.huobi 币本位
                    // List<PremiumRatioModel> huobiList = new ArrayList<>();
                    // for (String currency : currencys)
                    // {
                    // QuoteDepth cwQuoteDepth = getHuobiContractPrice(currency + "_CW");
                    // QuoteDepth nwQuoteDepth = getHuobiContractPrice(currency + "_NW");
                    // QuoteDepth cqQuoteDepth = getHuobiContractPrice(currency + "_CQ");
                    // // QuoteDepth nqDepth = getContractPrice(currency + "-USD-" + nq);
                    // if (null != cwQuoteDepth && null != nwQuoteDepth && null != cqQuoteDepth)
                    // {
                    // QuoteDepth maxQuoteDepth = ObjectUtils.max(cwQuoteDepth, nwQuoteDepth, cqQuoteDepth);
                    // QuoteDepth minQuoteDepth = ObjectUtils.min(cwQuoteDepth, nwQuoteDepth, cqQuoteDepth);
                    // PremiumRatioModel premiumRatioModel = PremiumRatioModel.builder().longQuoteDepth(minQuoteDepth).shortQuoteDepth(maxQuoteDepth)
                    // .ratio(maxQuoteDepth.getBid().subtract(minQuoteDepth.getAsk()).divide(minQuoteDepth.getAsk(), 4, BigDecimal.ROUND_HALF_UP)).build();
                    // huobiList.add(premiumRatioModel);
                    // }
                    // else
                    // {
                    // log.info("{}行情缓存不存在或超时，本次取消当前币种溢价率统计！", currency);
                    // }
                    // }
                    // 对溢价率进行排序
                    // Collections.sort(huobiList);
                    // log.info(">>>本次筛选出的huobi-币本位溢价率从高到低排序如下<<<");
                    // huobiList.forEach(model -> {
                    // log.info("空头合约：{}，多头合约：{}，溢价率：{}", model.getShortQuoteDepth().getInstrument_id(), model.getLongQuoteDepth().getInstrument_id(),
                    // model.getRatio().multiply(BigDecimal.valueOf(100)).toPlainString() + "%");
                    // });
                    // log.info(">>>排序结束<<<");
                }
                catch (Exception e)
                {
                    String msg = new StringBuilder("行情分析轮询任务异常：").append(e.getLocalizedMessage()).toString();
                    log.info(msg);
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    
    private QuoteDepth getOkexContractPrice(String symbol)
    {
        QuoteDepth quoteDepth = TradeConst.okex_depth_map.get(symbol);
        if (null != quoteDepth && System.currentTimeMillis() - quoteDepth.getUpdateTime() < 60000)
        { return quoteDepth; }
        return null;
    }
    
    private QuoteDepth getHuobiContractPrice(String symbol)
    {
        QuoteDepth quoteDepth = TradeConst.huobi_depth_map.get(symbol);
        if (null != quoteDepth && System.currentTimeMillis() - quoteDepth.getUpdateTime() < 60000)
        { return quoteDepth; }
        return null;
    }
}
