package com.slf.quant.strategy.triangular.client;

import com.slf.quant.facade.entity.strategy.QuantGridConfig;
import com.slf.quant.facade.entity.strategy.QuantGridStats;
import com.slf.quant.facade.entity.strategy.QuantTriangularConfig;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.strategy.consts.TradeConst;
import com.sun.org.apache.xpath.internal.operations.Quo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AbstractTriangularClient：
 *
 * @author: fzk
 * @date: 2020-09-10 13:27
 */
@Slf4j
public class AbstractTriangularClient
{
    protected QuantTriangularConfig config;
    
    protected String                apiKey;
    
    protected String                secretKey;
    
    protected String                passPhrase;
    
    protected boolean               hasError;
    
    protected int                   runStatus;
    
    List<String>                    currencys;
    
    public AbstractTriangularClient(QuantTriangularConfig config, String apiKey, String secretKey, String passPhrase)
    {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.passPhrase = passPhrase;
        this.config = config;
        currencys = Arrays.asList(TradeConst.triangular_currencys.split(","));
    }
    
    public static void main(String[] args)
    {
        AbstractTriangularClient client = new AbstractTriangularClient(null, null, null, null);
        client.start();
    }
    
    /**
     * 策略启动入口
     * <p>
     * 需要推送日志的动作：
     */
    public final void start()
    {
        // log.info("策略:{} ，开始运行。", config.getContractCode());
        new Thread(() -> {
            try
            {
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
                    if (runStatus == 0)
                    {
                        BigDecimal maxProfit = BigDecimal.ZERO;
                        // 行情监控阶段
                        // 遍历币种
                        for (String currency : currencys)
                        {
                            // 1.获取当前币种对应的5类行情
                            String c1 = currency + "-USDT";
                            String c2 = currency + "-BTC";
                            String c3 = currency + "-ETH";
                            String c4 = "BTC-USDT";
                            String c5 = "ETH-USDT";
                            QuoteDepth depth1 = TradeConst.okex_triangular_depth_map.get(c1);
                            QuoteDepth depth2 = TradeConst.okex_triangular_depth_map.get(c2);
                            QuoteDepth depth3 = TradeConst.okex_triangular_depth_map.get(c3);
                            QuoteDepth depth4 = TradeConst.okex_triangular_depth_map.get(c4);
                            QuoteDepth depth5 = TradeConst.okex_triangular_depth_map.get(c5);
                            if (null == depth1 || null == depth2 || null == depth3 || null == depth4 || null == depth5)
                            {
                                log.info("{}相关行情获取失败！", currency);
                                continue;
                            }
                            BigDecimal discountRate = new BigDecimal("0.9985");
                            // 计算溢价,默认按10美金进行交易
                            // (1)btc市场正向
                            BigDecimal profit1 = BigDecimal.TEN.divide(depth1.getAsk(), 8, BigDecimal.ROUND_DOWN).multiply(discountRate).multiply(depth2.getBid())
                                    .multiply(discountRate).multiply(depth4.getBid()).multiply(discountRate).subtract(BigDecimal.TEN).setScale(8, BigDecimal.ROUND_DOWN);
                            // (1)eth市场正向
                            BigDecimal profit2 = BigDecimal.TEN.divide(depth1.getAsk(), 8, BigDecimal.ROUND_DOWN).multiply(discountRate).multiply(depth3.getBid())
                                    .multiply(discountRate).multiply(depth5.getBid()).multiply(discountRate).subtract(BigDecimal.TEN).setScale(8, BigDecimal.ROUND_DOWN);
                            // (1)btc市场反向
                            BigDecimal profit3 = BigDecimal.TEN.divide(depth4.getAsk(), 8, BigDecimal.ROUND_DOWN).multiply(discountRate)
                                    .divide(depth2.getAsk(), 8, BigDecimal.ROUND_DOWN).multiply(discountRate).multiply(depth1.getBid()).multiply(discountRate)
                                    .subtract(BigDecimal.TEN).setScale(8, BigDecimal.ROUND_DOWN);
                            // (1)btc市场反向
                            BigDecimal profit4 = BigDecimal.TEN.divide(depth5.getAsk(), 8, BigDecimal.ROUND_DOWN).multiply(discountRate)
                                    .divide(depth3.getAsk(), 8, BigDecimal.ROUND_DOWN).multiply(discountRate).multiply(depth1.getBid()).multiply(discountRate)
                                    .subtract(BigDecimal.TEN).setScale(8, BigDecimal.ROUND_DOWN);
                            maxProfit = ObjectUtils.max(maxProfit, profit1, profit2, profit3, profit4);
                        }
                         log.debug("本次行情监控阶段发现最高溢价为{}USDT!", maxProfit);
//                        if (maxProfit.compareTo(BigDecimal.ZERO) == 1)
//                        {
//                            log.info("监控发现正溢价：{}", maxProfit);
//                        }
                        if (maxProfit.compareTo(BigDecimal.ONE) == 1)
                        {
                            log.info("监控发现最佳溢价：{}", maxProfit);
                        }
                    }
                }
                catch (Exception e)
                {
                    log.info("triangular轮询任务执行异常：{}", e.getLocalizedMessage());
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        Thread.sleep(5000L);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            // 停止策略
            // stop();
        }).start();
    }
}
