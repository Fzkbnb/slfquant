package com.slf.quant.strategy.grid.quote;

import java.util.concurrent.atomic.AtomicLong;

import com.slf.quant.facade.utils.QuantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QuoteWebsocketTask
{
    // okex
    PublicWebSocketClient  okexClient;
    
    @Autowired
    OkexUsdtMarketListener okexListener;
    
    AtomicLong             okexRestartCount     = new AtomicLong(0);
    
    AtomicLong             hbSpotRestartCount   = new AtomicLong(0);
    
    AtomicLong             hbFutureRestartCount = new AtomicLong(0);
    
    int                    errorCount           = 0;
    
    int                    errorCountOkex       = 0;
    
    int                    errorCountHuobi      = 0;
    
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
                        if (null != okexClient)
                        {
                            okexClient.closeConnection();
                            okexClient = null;
                        }
                    }
                    else
                    {
                        monitorOkex();
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
        okexListener.setUrl("wss://okexcomreal.bafang.com:8443/ws/v3");
        okexClient = new PublicWebSocketClient(okexListener);
        okexClient.connect();
    }
}
