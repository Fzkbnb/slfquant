package com.slf.quant.strategy.hedge.task;

import java.util.concurrent.atomic.AtomicLong;

import com.slf.quant.facade.consts.UrlConst;
import com.slf.quant.facade.utils.QuantUtil;
import com.slf.quant.strategy.config.QuantUrlConfig;
import com.slf.quant.strategy.config.StrategyConfig;
import com.slf.quant.strategy.grid.quote.PublicWebSocketClient;
import com.slf.quant.strategy.hedge.quote.HuobiFutureMarketListener;
import com.slf.quant.strategy.hedge.quote.OkexHedgeUsdtMarketListener;
import com.slf.quant.strategy.hedge.quote.OkexHedgeUsdtMarketListenerV5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;



import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QuoteHedgeWebsocketTask
{
    // okex
    PublicWebSocketClient okexClient;
    
    @Autowired
    OkexHedgeUsdtMarketListener okexListener;
    // okex
    PublicWebSocketClient    okexClientV5;

    @Autowired
    OkexHedgeUsdtMarketListenerV5 okexListenerV5;
    @Autowired
    @Qualifier("huobiFutureMarketListener")
    HuobiFutureMarketListener huobiFutureListener;
    
    AtomicLong                okexRestartCount     = new AtomicLong(0);
    
    AtomicLong                hbSpotRestartCount   = new AtomicLong(0);
    
    AtomicLong                hbFutureRestartCount = new AtomicLong(0);
    
    int                       errorCount           = 0;
    
    int                       errorCountOkex       = 0;
    
    int                       errorCountHuobi      = 0;
    
    // huobi future
    PublicWebSocketClient     huobiFutureClient;
    
    public void start()
    {
        new Thread(() -> {
            boolean isFirstStart = true;
            while (true)
            {
                try
                {
                    if (QuantUtil.isInSettlement())
                    {
                        // 交割期间为防止错乱将关闭websocket客户端，并将16:32之后重启
                        // 交割期间为防止错乱将关闭websocket客户端，并将16:32之后重启
                        if (StrategyConfig.enableOkexV5)
                        {
                            if (null != okexClientV5)
                            {
                                okexClientV5.closeConnection();
                                okexClientV5 = null;
                            }
                        }
                        else
                        {
                            if (null != okexClient)
                            {
                                okexClient.closeConnection();
                                okexClient = null;
                            }
                        }
                        // if (null != huobiSpotClient)
                        // {
                        // huobiSpotClient.closeConnection();
                        // huobiSpotClient = null;
                        // }
                        if (null != huobiFutureClient)
                        {
                            huobiFutureClient.closeConnection();
                            huobiFutureClient = null;
                        }
                    }
                    else
                    {
                        if (StrategyConfig.enableOkexV5)
                        {
                            monitorOkexV5();
                        }
                        else
                        {
                            monitorOkex();
                        }
                        // monitorHuobiSpot();
//                        monitorHuobiFuture();
                        // if (!isFirstStart)
                        // {
                        // if (CollectionUtils.isEmpty(QuantConst.okex_depth_map.values()))
                        // {
                        // log.info("websocket缓存行情为空，行情异常!");
                        // errorCount++;
                        // }
                        // else
                        // {
                        // String[] currencys = QuantConst.hedge_currencys.split(",");
                        // int random = (int) (9 * Math.random());
                        // // okex
                        // QuoteDepth okexCurrency = QuantConst.okex_depth_map.get(currencys[random] + "-USDT");
                        // if (null == okexCurrency || System.currentTimeMillis() - okexCurrency.getUpdateTime() > 60000)
                        // {
                        // log.info("okex缓存行情{}为空或1分钟未更新!", currencys[random]);
                        // errorCount++;
                        // errorCountOkex++;
                        // }
                        // }
                        // }
                        // isFirstStart = false;
                        // if (errorCount > 10)
                        // {
                        // log.info("websocket缓存行情异常累计次数{}大于6，需排查修复!", errorCount);
                        // String msgContent = "u本位期现套利策略行情监控异常，其中okex行情获取失败" + errorCountOkex + "次，火币行情获取失败" + errorCountHuobi + "次，请及时排查处理！";
                        // errorCount = 0;
                        // errorCountHuobi = 0;
                        // errorCountOkex = 0;
                        // }
                    }
                }
                catch (Exception e)
                {
                    String msg = new StringBuilder("websocket行情客户端监控轮询任务异常：").append(e.getLocalizedMessage()).toString();
                    log.info(msg);
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        Thread.sleep(10000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    
    private void monitorOkex()
    {
        try
        {
            if (null == okexClient)
            {
                // 首次启动
                log.info("即将启动okex行情websocket客户端！");
                initOkexClient();
            }
            else if (!okexClient.getMarketListener().isConnected())
            {
                // 如果连接已经断开或者消息接收超时，要重新建立连接
                log.info("okex行情websocket客户端已断开，即将重启客户端！");
                okexClient.closeConnection();
                initOkexClient();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void initOkexClient()
    {
        log.info("okex行情客户端启动次数:{}", okexRestartCount.incrementAndGet());
        okexListener.setUrl("wss://real.okex.com:8443/ws/v3");
        okexClient = new PublicWebSocketClient(okexListener);
        okexClient.connect();
    }
    
    private void monitorHuobiFuture()
    {
        try
        {
            if (null == huobiFutureClient)
            {
                // 首次启动
                log.info("即将启动huobi合约行情websocket客户端！");
                initHuobiFutureClient();
            }
            else if ((!huobiFutureClient.getMarketListener().isConnected()))
            {
                // 如果连接已经断开或者消息接收超时，要重新建立连接
                log.info("huobi合约行情websocket客户端已断开，即将重启客户端！");
                huobiFutureClient.closeConnection();
                initHuobiFutureClient();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void initHuobiFutureClient()
    {
        log.info("huobi合约行情客户端启动次数:{}", hbFutureRestartCount.incrementAndGet());
        huobiFutureListener.setUrl(UrlConst.huobi_endpoint_websocket_future);
        huobiFutureClient = new PublicWebSocketClient(huobiFutureListener);
        huobiFutureClient.connect();
    }

    private void monitorOkexV5()
    {
        try
        {
            if (null == okexClientV5)
            {
                // 首次启动
                log.info("即将启动okexV5行情websocket客户端！");
                initOkexClientV5();
            }
            else if (!okexClientV5.getMarketListener().isConnected())
            {
                // 如果连接已经断开或者消息接收超时，要重新建立连接
                log.info("okexV5行情websocket客户端已断开，即将重启客户端！");
                okexClientV5.closeConnection();
                initOkexClientV5();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initOkexClientV5()
    {
        log.info("okexV5行情客户端启动次数:{}", okexRestartCount.incrementAndGet());
        okexListenerV5.setUrl(QuantUrlConfig.okex_endpoint_websocket_v5);
        okexClientV5 = new PublicWebSocketClient(okexListenerV5);
        okexClientV5.connect();
    }
}
