package com.slf.quant.strategy.grid.quote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.slf.quant.facade.utils.ZipUtil;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

@Slf4j
@Data
@Service
public class AbstractMarketListener extends WebSocketListener
{
    protected long                   lastMessageTime = 0;
    
    private boolean                  isConnected     = false;
    
    private String                   url;
    
    private ScheduledExecutorService executorService;
    
    @Override
    public void onOpen(WebSocket webSocket, Response response)
    {
        log.info("websocket行情客户端{}连接成功！", url);
        // 连接建立后，根据配置订阅相应内容
        isConnected = true;
        subscribe(webSocket);
        if (!isOkex())
        {
            executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleWithFixedDelay(() -> {
                JSONObject json = new JSONObject();
                json.put("pong", System.currentTimeMillis());
                String msg = json.toJSONString();
                webSocket.send(msg);
            }, 0, 5, TimeUnit.SECONDS);
        }
    }
    
    @Override
    public void onMessage(WebSocket webSocket, String message)
    {
        lastMessageTime = System.currentTimeMillis();
        handleStringMessage(message);
    }
    
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason)
    {
    }
    
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason)
    {
        System.out.println("连接关闭");
        isConnected = false;
    }
    
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response)
    {
        t.printStackTrace();
        System.out.println(response);
        log.info("({})websocket行情客户端连接失败！", url);
        isConnected = false;
    }
    
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes)
    {
        String message = null;
        try
        {
            if (isOkex())
            {
                message = uncompressOkex(bytes.toByteArray());
            }
            else
            {
                message = new String(ZipUtil.decompress(bytes.toByteArray()), "UTF-8");
            }
        }
        catch (Exception e)
        {
        }
        if (StringUtils.isNotEmpty(message))
        {
            lastMessageTime = System.currentTimeMillis();
            if ((!isOkex()) && message.indexOf("\"ping\":") > 0)
            {// 火币需要显式发送pong消息
                JSONObject jsonObject = JSONObject.parseObject(message);
                String pong = jsonObject.toString();
                // webSocket.send(pong.replace("ping", "pong"));
            }
            handleStringMessage(message);
        }
    }
    
    private boolean isOkex()
    {
        return url.contains("okex");
    }
    
    /**
     * 解压函数
     * Decompression function
     *
     * @param bytes
     * @return
     */
    private static String uncompressOkex(final byte[] bytes)
    {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                final Deflate64CompressorInputStream zin = new Deflate64CompressorInputStream(in))
        {
            final byte[] buffer = new byte[1024];
            int offset;
            while (-1 != (offset = zin.read(buffer)))
            {
                out.write(buffer, 0, offset);
            }
            return out.toString();
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 行情消息处理逻辑
     * @param message
     */
    protected void handleStringMessage(String message)
    {
    }
    
    protected void subscribe(WebSocket webSocket)
    {
    }
}
