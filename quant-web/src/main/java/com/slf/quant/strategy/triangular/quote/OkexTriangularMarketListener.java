package com.slf.quant.strategy.triangular.quote;

import java.util.Arrays;
import java.util.List;

import com.slf.quant.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.slf.quant.facade.model.OkexMarketModel;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.utils.QuantUtil;
import com.slf.quant.strategy.consts.TradeConst;
import com.slf.quant.strategy.grid.quote.AbstractMarketListener;

import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;

@Slf4j
@Service
public class OkexTriangularMarketListener extends AbstractMarketListener
{
    private static String DEPTH_SPOT = "\"spot/depth5:%s\"";
    
    @Override
    protected void subscribe(WebSocket ws)
    {
        // 订阅深度
        String topic_spot1 = getDepthTopic(DEPTH_SPOT, "BTC-USDT");
        ws.send(topic_spot1);
        String topic_spot2 = getDepthTopic(DEPTH_SPOT, "ETH-USDT");
        ws.send(topic_spot2);
        List<String> currencys = Arrays.asList(TradeConst.triangular_currencys.split(","));
        currencys.forEach(currency -> {
            String symbol_usdt = getMarketSymbol(currency, "USDT");
            if (StringUtil.isNotEmpty(symbol_usdt))
            {
                String topic_spot = getDepthTopic(DEPTH_SPOT, symbol_usdt);
                ws.send(topic_spot);
            }
            String symbol_btc = getMarketSymbol(currency, "BTC");
            if (StringUtil.isNotEmpty(symbol_btc))
            {
                String topic_spot = getDepthTopic(DEPTH_SPOT, symbol_btc);
                ws.send(topic_spot);
            }
            String symbol_eth = getMarketSymbol(currency, "ETH");
            if (StringUtil.isNotEmpty(symbol_eth))
            {
                String topic_spot = getDepthTopic(DEPTH_SPOT, symbol_eth);
                ws.send(topic_spot);
            }
        });
    }
    
    private String getMarketSymbol(String currency, String marketCurrency)
    {
        if (currency.equalsIgnoreCase(marketCurrency))
        { return ""; }
        return currency + "-" + marketCurrency;
    }
    
    private String getDepthTopic(String type, String symbol)
    {
        return "{\"args\":" + formatArgs(String.format(type, symbol)) + ",\"op\":\"subscribe\"}";
    }
    
    private static String formatArgs(String ... args)
    {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (String str : args)
        {
            if (str.isEmpty())
            {
                continue;
            }
            builder.append(str);
            if (args.length > 1 && ++count < args.length)
            {
                builder.append("\",\"");
            }
        }
        return builder.toString();
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
            OkexMarketModel model = JSON.parseObject(message, OkexMarketModel.class);
            String channel = model.getTable();
            if (isDepthModel(channel))
            {
                // 解析出深度模型类
                JSONArray json = JSON.parseArray(model.getData());
                QuoteDepth quoteDepthModel = JSON.parseObject(json.getString(0), QuoteDepth.class);
                if (CollectionUtils.isEmpty(quoteDepthModel.getAsks()) || CollectionUtils.isEmpty(quoteDepthModel.getBids()))
                { return; }
                quoteDepthModel.buildtriangularAskBid();
                String symbol = quoteDepthModel.getInstrument_id();
                TradeConst.okex_triangular_depth_map.put(symbol, quoteDepthModel);
            }
            else
            {
                // log.info("okex接收到ws订阅类消息：{}", message);
            }
        }
        catch (Exception e)
        {
            log.info("okex行情websocket客户端onmessage处理异常：{}", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    
    private boolean isDepthModel(String channel)
    {
        return StringUtils.isNotEmpty(channel) && channel.contains("depth5");
    }
    
    public static void main(String[] args)
    {
    }
}
