package com.slf.quant.strategy.hedge.quote;

import java.util.Arrays;
import java.util.List;

import com.slf.quant.facade.model.HuobiMarketModel;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.strategy.consts.TradeConst;
import com.slf.quant.strategy.grid.quote.AbstractMarketListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;

@Slf4j
@Service
public class HuobiSpotMarketListener extends AbstractMarketListener
{
    
    /**
     * websocket 操作
     */
    String            op                 = "sub";
    
    static String     DEPTH              = "market.%s.depth.%s";

    
    @Override
    protected void subscribe(WebSocket webSocket)
    {
        // 连接建立后，订阅配置指定的现货币对
        List<String> currencys = Arrays.asList(TradeConst.hedge_currencys.split(","));
        JSONObject req = new JSONObject();
        currencys.forEach(currency -> {
            String topic_spot = String.format(DEPTH, currency.toLowerCase() + "usdt", "step0");
            req.put(op, topic_spot);
            req.put("id", topic_spot);
            webSocket.send(req.toString());
        });
    }
    
    /**
     * 行情消息处理逻辑
     * @param message
     */
    @Override
    protected void handleStringMessage(String message)
    {
        try
        {
            HuobiMarketModel model = JSON.parseObject(message, HuobiMarketModel.class);
            String channel = model.getCh();
            if (isDepthModel(channel))
            {
                String symbol = getDepthSymbol(channel);
                // 解析出深度模型类
                QuoteDepth quoteDepthModel = JSON.parseObject(model.getTick(), QuoteDepth.class);
                quoteDepthModel.buildAskBid();
                quoteDepthModel.setInstrument_id(symbol);
                TradeConst.huobi_depth_map.put(symbol, quoteDepthModel);
            }
            else
            {
//                log.info("火币{}接收到订阅消息：{}", getUrl(), message);
            }
        }
        catch (Exception e)
        {
            log.info("huobi行情websocket客户端onmessage处理异常：{}", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    
    private String getDepthSymbol(String channel)
    {
        return channel.substring(7, channel.indexOf(".depth"));
    }
    
    private boolean isDepthModel(String channel)
    {
        return StringUtils.isNotEmpty(channel) && channel.contains("depth.step0");
    }
}
