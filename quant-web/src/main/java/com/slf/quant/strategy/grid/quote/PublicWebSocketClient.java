package com.slf.quant.strategy.grid.quote;

import java.util.concurrent.TimeUnit;

import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * webSocket client
 *
 * @author oker
 * @create 2019-06-12 15:57
 **/
@Data
public class PublicWebSocketClient
{
    private AbstractMarketListener marketListener;
    
    protected WebSocket webSocket = null;
    
    public PublicWebSocketClient(AbstractMarketListener listener)
    {
        this.marketListener = listener;
    }
    
    /**
     * 向服务器发起连接
     */
    public void connect()
    {
        // 连接超时配置为10秒，发送心跳频率为20秒
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).pingInterval(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url(marketListener.getUrl()).build();
        webSocket = client.newWebSocket(request, marketListener);
    }
    
    /**
     * 断开连接
     * Close Connection
     */
    public void closeConnection()
    {
        if (null != webSocket)
        {
            webSocket.close(1000, "client close connect !!!");
        }
    }
    
    /**
     * 判断接收消息超时
     * @return
     */
    public boolean isMessageTimeout()
    {
        return false;
        // return System.currentTimeMillis() - getMarketListener().getLastMessageTime() > QuantSocketConfig.market_message_timeout;
    }
}
